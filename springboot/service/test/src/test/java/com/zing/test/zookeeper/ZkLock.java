package com.zing.test.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ZkLock implements Lock {

    private static final String PATH = "/test/lock";
    private static final String PREFIX = PATH + "/";
    private static final long WAIT_TIME = 1000;

    private AtomicInteger lockCount = new AtomicInteger(0);

    CuratorFramework client;
    private String lockedSeqPath;
    private String lockedPath;
    private String priorPath;
    private Thread thread;

    public ZkLock(CuratorFramework client) {
        try {
            if (client.checkExists().forPath(PATH) == null) {
                client.create().forPath(PATH);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.client = client;
    }

    @Override
    public boolean lock() {
        synchronized (this) {
            if (lockCount.get() == 0) {
                thread = Thread.currentThread();
                lockCount.incrementAndGet();
            } else {
                if (thread != Thread.currentThread()) {
                    return false;
                }
                lockCount.incrementAndGet();
                return true;
            }
        }

        log.info("thread {}", thread.getName());

        try {
            boolean locked = tryLock();
            if (locked) {
                return true;
            }

            while (!locked) {
                await();

                List<String> waiters = getWaiters();
                if (checkLocked(waiters)) {
                    locked = true;
                }
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            unlock();
        }
        return false;
    }

    private void await() throws Exception {
        if (priorPath == null) {
            throw new Exception();
        }

        final CountDownLatch latch = new CountDownLatch(1);

        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                log.info("[WatchedEvent]节点删除");
                latch.countDown();
            }
        };
        client.getData().usingWatcher(watcher).forPath(priorPath);

//        TreeCache treeCache = new TreeCache(client, priorPath);
//        TreeCacheListener listener = new TreeCacheListener() {
//            @Override
//            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
//                ChildData data = event.getData();
//                if (data != null) {
//                    if (event.getType() == TreeCacheEvent.Type.NODE_REMOVED) {
//                        latch.countDown();
//                    }
//                }
//            }
//        };
//        treeCache.getListenable().addListener(listener);
//        treeCache.start();

        latch.await(WAIT_TIME, TimeUnit.SECONDS);
    }


    @Override
    public boolean unlock() {
        if (thread != Thread.currentThread()) {
            return false;
        }

        int lockedCount = lockCount.decrementAndGet();
        if (lockedCount < 0) {
            throw new IllegalStateException();
        }
        if (lockedCount != 0) {
            return true;
        }

        try {
            if (client.checkExists().forPath(lockedPath) != null) {
                client.delete().forPath(lockedPath);
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private boolean tryLock() throws Exception {
        lockedPath = client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(PREFIX);

        log.info("create seq {}", lockedPath);

        if (lockedPath == null) {
            throw new Exception();
        }
        lockedSeqPath = getSeqPath(lockedPath);

        List<String> waiters = getWaiters();
        if (checkLocked(waiters)) {
            return true;
        }

        int index = Collections.binarySearch(waiters, lockedSeqPath);
        if (index < 0) {
            throw new Exception();
        }

        priorPath = PREFIX + waiters.get(index - 1);

        log.info("is not first {} {}", lockedSeqPath, priorPath);

        return false;
    }

    private String getSeqPath(String path) {
        int index = path.lastIndexOf(PREFIX);
        if (index >= 0) {
            index += PREFIX.length();
            return index <= path.length() ? path.substring(index) : "";
        }
        return null;
    }

    private List<String> getWaiters() {
        List<String> children = null;
        try {
            children = client.getChildren().forPath(PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return children;
    }

    private boolean checkLocked(List<String> waiters) {
        Collections.sort(waiters);
        return lockedSeqPath.equals(waiters.get(0));
    }

}
