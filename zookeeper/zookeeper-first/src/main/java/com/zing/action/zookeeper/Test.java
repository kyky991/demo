package com.zing.action.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Zing
 * @date 2020-06-24
 */
public class Test implements Watcher {

    private static CountDownLatch latch = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();

    @Override
    public void process(WatchedEvent event) {
        if (event.getState() == Event.KeeperState.SyncConnected) {
            if (Event.EventType.None == event.getType()) {
                latch.countDown();
            } else if (Event.EventType.NodeDataChanged == event.getType()) {
                System.out.println("node changed");

                try {
                    System.out.println(new String(zk.getData(event.getPath(), true, stat)));
                    System.out.println(stat);
                } catch (Exception e) {

                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        zk = new ZooKeeper("hadooooop:2181", 5000, new Test());

        latch.await();

        System.out.println(new String(zk.getData("/test", true, stat)));
        System.out.println(stat);

        TimeUnit.SECONDS.sleep(1000);
    }
}
