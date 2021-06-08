package com.zing.netty.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Zing
 * @date 2020-12-30
 */
@Slf4j
public class RpcConnectManager {

    private static volatile RpcConnectManager RPC_CONNECT_MANAGER = new RpcConnectManager();

    private RpcConnectManager() {

    }

    public static RpcConnectManager getInstance() {
        return RPC_CONNECT_MANAGER;
    }

    /**
     * 一个连接地址对应一个实际的业务处理器
     */
    private Map<InetSocketAddress, RpcClientHandler> connectedHandlerMap = new ConcurrentHashMap<>(16);

    /**
     * 连接成功的地址 所对应的任务执行器列表
     */
    private CopyOnWriteArrayList<RpcClientHandler> connectedHandlerList = new CopyOnWriteArrayList<>();

    /**
     * 用于异步提交连接请求的线程池
     */
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2000));

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    private ReentrantLock connectedLock = new ReentrantLock();

    private Condition connectedCondition = connectedLock.newCondition();

    private long connectedTimeoutMillis = 6000;

    private volatile boolean isRunning = true;

    private AtomicInteger handlerIdx = new AtomicInteger(0);

    /**
     * 异步连接 线程池 真正的发起连接，连接失败监听，连接成功监听
     * 对于连接进来的资源做一个缓存
     */
    public void connect(final String serverAddr) {
        List<String> allServerAddr = Arrays.asList(serverAddr.split(","));
        updateConnectedServer(allServerAddr);
    }

    /**
     * 更新缓存信息并异步发起连接
     */
    public void updateConnectedServer(List<String> allServerAddr) {
        if (CollectionUtils.isNotEmpty(allServerAddr)) {
            // 解析allServerAddr地址
            Set<InetSocketAddress> newAllServerNodeSet = new HashSet<>();
            for (String serverAddr : allServerAddr) {
                String[] array = serverAddr.split(":");
                if (array.length == 2) {
                    String host = array[0];
                    int port = Integer.parseInt(array[1]);
                    final InetSocketAddress socketAddress = new InetSocketAddress(host, port);
                    newAllServerNodeSet.add(socketAddress);
                }
            }

            // 建立连接 发起远程连接
            for (InetSocketAddress socketAddress : newAllServerNodeSet) {
                if (!connectedHandlerMap.containsKey(socketAddress)) {
                    connectAsync(socketAddress);
                }
            }

            // allServerAddr不存在的连接地址，需要从缓存中移除
            for (RpcClientHandler handler : connectedHandlerList) {
                SocketAddress remoteAddress = handler.getChannel().remoteAddress();
                if (!newAllServerNodeSet.contains(remoteAddress)) {
                    log.info("remove invalid server node {}", remoteAddress);
                    RpcClientHandler cachedHandler = connectedHandlerMap.get(remoteAddress);
                    if (cachedHandler != null) {
                        cachedHandler.close();
                        connectedHandlerMap.remove(remoteAddress);
                    }
                    connectedHandlerList.remove(remoteAddress);
                }
            }
        } else {
            log.error("no available server address");
            clearConnected();
        }
    }

    /**
     * 异步发起连接的方法
     */
    private void connectAsync(InetSocketAddress socketAddress) {
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap
                        .group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new RpcClientInitializer());
                connect(bootstrap, socketAddress);
            }
        });
    }

    private void connect(Bootstrap bootstrap, InetSocketAddress remoteAddress) {
        final ChannelFuture channelFuture = bootstrap.connect(remoteAddress);

        // 连接失败的时候添加监听 清除资源后进行重连
        channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                future.channel().eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        log.warn("connect fail, to reconnect {}", remoteAddress);
                        clearConnected();
                        connect(bootstrap, remoteAddress);
                    }
                }, 3, TimeUnit.SECONDS);
            }
        });

        // 连接成功的时候添加监听 新连接放入缓存中
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("successfully connect to remote server {}", remoteAddress);
                    RpcClientHandler handler = future.channel().pipeline().get(RpcClientHandler.class);
                    addHandler(handler);
                }
            }
        });
    }

    /**
     * 连接失败是 释放资源 清空缓存
     */
    private void clearConnected() {
        for (final RpcClientHandler handler : connectedHandlerList) {
            SocketAddress remoteAddress = handler.getChannel().remoteAddress();
            RpcClientHandler cachedHandler = connectedHandlerMap.get(remoteAddress);
            if (cachedHandler != null) {
                cachedHandler.close();
                connectedHandlerMap.remove(remoteAddress);
            }
        }
        connectedHandlerList.clear();
    }

    /**
     * 添加到缓存中
     */
    private void addHandler(RpcClientHandler handler) {
        connectedHandlerList.add(handler);
        connectedHandlerMap.put((InetSocketAddress) handler.getChannel().remoteAddress(), handler);

        // 唤醒可用的业务执行器
        signalAvailableHandler();
    }

    /**
     * 唤醒另一个线程有新连接接入
     */
    private void signalAvailableHandler() {
        connectedLock.lock();
        try {
            connectedCondition.signalAll();
        } finally {
            connectedLock.unlock();
        }
    }

    private boolean waitingForAvailableHandler() throws InterruptedException {
        connectedLock.lock();
        try {
            return connectedCondition.await(this.connectedTimeoutMillis, TimeUnit.MILLISECONDS);
        } finally {
            connectedLock.unlock();
        }
    }

    /**
     * 选择实际的业务处理器
     */
    public RpcClientHandler chooseHandler() {
        CopyOnWriteArrayList<RpcClientHandler> handlers = (CopyOnWriteArrayList<RpcClientHandler>) connectedHandlerList.clone();

        int size = handlers.size();

        while (isRunning && size <= 0) {
            try {
                boolean available = waitingForAvailableHandler();
                if (available) {
                    handlers = (CopyOnWriteArrayList<RpcClientHandler>) connectedHandlerList.clone();
                    size = handlers.size();
                }
            } catch (InterruptedException e) {
                log.error("waiting for available node is interrupted");
                throw new RuntimeException(e);
            }
        }

        if (!isRunning) {
            return null;
        }

        return handlers.get((handlerIdx.getAndAdd(1) + size) % size);
    }

    /**
     * 关闭
     */
    public void stop() {
        isRunning = false;

        for (RpcClientHandler handler : connectedHandlerList) {
            handler.close();
        }

        signalAvailableHandler();
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }

    /**
     * 重连
     */
    public void reconnect(final RpcClientHandler handler, final SocketAddress remoteAddress) {
        if (handler != null) {
            handler.close();
            connectedHandlerList.remove(handler);
            connectedHandlerMap.remove(remoteAddress);
        }
        connectAsync((InetSocketAddress) remoteAddress);
    }

}
