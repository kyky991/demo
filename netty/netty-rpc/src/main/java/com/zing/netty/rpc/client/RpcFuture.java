package com.zing.netty.rpc.client;

import com.zing.netty.rpc.codec.RpcRequest;
import com.zing.netty.rpc.codec.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Zing
 * @date 2021-01-02
 */
@Slf4j
public class RpcFuture implements Future<Object> {

    private static final long THRESHOLD = 5000;

    private RpcRequest request;

    private RpcResponse response;

    private long startTime;

    private List<RpcCallback> pendingCallbacks = new ArrayList<>();

    private Sync sync;

    private ReentrantLock lock = new ReentrantLock();

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(16, 16, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(65536));

    public RpcFuture(RpcRequest request) {
        this.request = request;
        this.startTime = System.currentTimeMillis();
        this.sync = new Sync();
    }

    public void done(RpcResponse response) {
        this.response = response;
        boolean success = sync.release(1);
        if (success) {
            invokeCallbacks();
        }

        long costTime = System.currentTimeMillis() - startTime;
        if (costTime > THRESHOLD) {
            log.warn("rpc response cost [{}]ms, request[{}]", costTime, request);
        }
    }

    private void invokeCallbacks() {
        lock.lock();
        try {
            for (final RpcCallback callback : pendingCallbacks) {
                doCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    private void doCallback(RpcCallback callback) {
        final RpcResponse response = this.response;
        executor.submit(new Runnable() {
            @Override
            public void run() {
                if (response.getThrowable() != null) {
                    callback.success(response.getResult());
                } else {
                    callback.failure(response.getThrowable());
                }
            }
        });
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        if (response != null) {
            return response.getResult();
        }
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (response != null) {
                return response.getResult();
            }
            return null;
        }
        throw new RuntimeException("Timeout request[{}]" + request);
    }

    class Sync extends AbstractQueuedSynchronizer {

        private final int done = 1;

        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == pending) {
                if (compareAndSetState(pending, done)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isDone() {
            return getState() == done;
        }
    }

    public RpcFuture addCallback(RpcCallback callback) {
        lock.lock();
        try {
            if (isDone()) {
                doCallback(callback);
            } else {
                pendingCallbacks.add(callback);
            }
            return this;
        } finally {
            lock.unlock();
        }
    }
}
