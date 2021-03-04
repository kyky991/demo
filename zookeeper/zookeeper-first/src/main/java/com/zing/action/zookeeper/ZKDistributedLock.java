package com.zing.action.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Zing
 * @date 2020-06-24
 */
public class ZKDistributedLock implements Watcher {

    private static final String HOST = "hadooooop:2181";
    private static final int SESSION_TIMEOUT = 5000;

    private ZooKeeper zk;
    private String lock;
    private String id;
    private String waitNode;
    private String lockNode;
    private CountDownLatch latch;
    private CountDownLatch connectedLatch;

    public ZKDistributedLock(String id) {
        this.id = id;

        try {
            zk = new ZooKeeper(HOST, SESSION_TIMEOUT, this);
            connectedLatch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getState() == Event.KeeperState.SyncConnected) {
            connectedLatch.countDown();
        }
        latch.countDown();
    }

    public boolean tryLock() {
        try {
            lockNode = zk.create(lock + "/" + id, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            List<String> list = zk.getChildren(lock + "/" + lockNode, false);
            Collections.sort(list);

            if (lockNode.equals(lock + "/" + list.get(0))) {
                return true;
            }

            int pre = -1;
            for (int i = 1; i < list.size(); i++) {
                if (lockNode.equals(lock + "/" + list.get(i))) {
                    pre = i - 1;
                    break;
                }
            }

            waitNode = list.get(pre);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void lock() {
        try {
            if (!tryLock()) {
                waitForLock(waitNode, SESSION_TIMEOUT);
            }
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    public void unlock() {
        try {
            System.out.println(lockNode);
            zk.delete(lockNode, -1);
            lockNode = null;
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    private boolean waitForLock(String waitNode, long waitTime) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(lock + "/" + waitNode, true);
        if (stat != null) {
            latch = new CountDownLatch(1);
            latch.await(waitTime, TimeUnit.MILLISECONDS);
            latch = null;
        }
        return true;
    }
}
