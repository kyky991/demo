package com.zing.test.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TestZookeeper {

    private CuratorFramework client;

    @Before
    public void start() {
        client = CuratorFrameworkFactory.newClient(
                "zookeeper.zing:2181",
                new ExponentialBackoffRetry(1000, 3)
        );
        client.start();
    }

    @After
    public void stop() {
        client.close();
    }

    @Test
    public void testCreate() throws Exception {
        String path = "/test/seq_";
        byte[] data = "1122".getBytes(StandardCharsets.UTF_8);

        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                .forPath(path, data);
    }

    @Test
    public void testDelete() throws Exception {
        client.delete()
                .deletingChildrenIfNeeded()
                .forPath("/test");
    }

    @Test
    public void testGetData() throws Exception {
        String path = "/test";
        Stat stat = client.checkExists().forPath(path);
        if (stat != null) {
            byte[] data = client.getData().forPath(path);
            System.out.println(new String(data, StandardCharsets.UTF_8));

            List<String> list = client.getChildren().forPath(path);
            System.out.println(list);
        }
    }

    @Test
    public void testSetData() throws Exception {
        String path = "/test";
        byte[] data = "Hello".getBytes(StandardCharsets.UTF_8);
        client.setData().forPath(path, data);
    }

    @Test
    public void testAsyncSetData() throws Exception {
        String path = "/test";
        byte[] data = "Hello123456".getBytes(StandardCharsets.UTF_8);
        client.setData()
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        System.out.println("processResult");
                        System.out.println(curatorEvent);
                    }
                })
                .forPath(path, data);
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void testAsyncSetData2() throws Exception {
        String path = "/test";
        byte[] data = "Hello789".getBytes(StandardCharsets.UTF_8);
        client.setData()
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                        System.out.println(event);
                    }
                })
                .forPath(path, data);
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void testWatcher() throws Exception {
        String path = "/test";
        byte[] data = "watcher".getBytes(StandardCharsets.UTF_8);
        client.getData()
                .usingWatcher(new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        System.out.println("usingWatcher: " + event);
                    }
                })
                .forPath(path);

        client.setData().forPath(path, data);

        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void testNodeCache() throws Exception {
        String path = "/test";
        NodeCache nodeCache = new NodeCache(client, path);
        NodeCacheListener listener = new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                ChildData childData = nodeCache.getCurrentData();
                System.out.println(childData);
                System.out.println(new String(childData.getData(), StandardCharsets.UTF_8));
            }
        };

        nodeCache.getListenable().addListener(listener);
        nodeCache.start();

        client.setData().forPath(path, "111111".getBytes(StandardCharsets.UTF_8));
        TimeUnit.SECONDS.sleep(1);
        client.setData().forPath(path, "222222".getBytes(StandardCharsets.UTF_8));
        TimeUnit.SECONDS.sleep(1);
        client.setData().forPath(path, "333333".getBytes(StandardCharsets.UTF_8));
        TimeUnit.SECONDS.sleep(10);

        nodeCache.close();
    }

    @Test
    public void testPathCache() throws Exception {
        String path = "/test";
        String childPath = "/test/id-";
        PathChildrenCache cache = new PathChildrenCache(client, path, true);
        PathChildrenCacheListener listener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println(event);
                System.out.println(event.getType());
                switch (event.getType()) {
                    case CHILD_ADDED:
                    case CHILD_UPDATED:
                    case CHILD_REMOVED:
                        System.out.println(new String(event.getData().getData(), StandardCharsets.UTF_8));
                        break;
                    default:
                        break;
                }
            }
        };

        cache.getListenable().addListener(listener);
        cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

        for (int i = 0; i < 5; i++) {
            client.create().forPath(childPath + i);
        }
        TimeUnit.SECONDS.sleep(1);

        for (int i = 0; i < 5; i++) {
            client.delete().forPath(childPath + i);
        }
        TimeUnit.SECONDS.sleep(10);

        cache.close();
    }

    @Test
    public void testTreeCache() throws Exception {
        String path = "/test";
        String childPath = "/test/id-";
        TreeCache cache = new TreeCache(client, path);
        TreeCacheListener listener = new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                System.out.println(event);
                System.out.println(event.getType());
                switch (event.getType()) {
                    case NODE_ADDED:
                    case NODE_UPDATED:
                    case NODE_REMOVED:
                        System.out.println(new String(event.getData().getData(), StandardCharsets.UTF_8));
                        break;
                    default:
                        break;
                }
            }
        };

        cache.getListenable().addListener(listener);
        cache.start();

        for (int i = 0; i < 5; i++) {
            client.create().forPath(childPath + i);
        }
        TimeUnit.SECONDS.sleep(1);

        for (int i = 0; i < 5; i++) {
            client.delete().forPath(childPath + i);
        }
        TimeUnit.SECONDS.sleep(10);

        cache.close();
    }


    int count = 0;

    @Test
    public void testLock() throws Exception {
        CountDownLatch latch = new CountDownLatch(20);
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < 20; i++) {
            executor.submit(() -> {
                Lock lock = new ZkLock(client);
                lock.lock();
                count++;
                log.info("count {}", count);
                lock.unlock();
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();
    }

    @Test
    public void testMutex() throws Exception {
        CountDownLatch latch = new CountDownLatch(20);
        ExecutorService executor = Executors.newCachedThreadPool();
        InterProcessMutex mutex = new InterProcessMutex(client, "/test/mutex");
        for (int i = 0; i < 20; i++) {
            executor.submit(() -> {
                try {
                    mutex.acquire();
                    count++;
                    log.info("count {}", count);
                    mutex.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();
    }

}
