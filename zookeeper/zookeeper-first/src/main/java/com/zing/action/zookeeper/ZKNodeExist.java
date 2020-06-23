package com.zing.action.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper 判断阶段是否存在
 */
public class ZKNodeExist implements Watcher {

    private final static String ZK_SERVER_PATH = "192.168.110.128:2181";
    private final static Integer TIMEOUT = 5000;

    private ZooKeeper zookeeper = null;

    public ZKNodeExist() {
    }

    public ZKNodeExist(String connectString) {
        try {
            zookeeper = new ZooKeeper(connectString, TIMEOUT, new ZKNodeExist());
        } catch (IOException e) {
            e.printStackTrace();
            if (zookeeper != null) {
                try {
                    zookeeper.close();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeCreated) {
            System.out.println("节点创建");
            countDown.countDown();
        } else if (event.getType() == Event.EventType.NodeDataChanged) {
            System.out.println("节点数据改变");
            countDown.countDown();
        } else if (event.getType() == Event.EventType.NodeDeleted) {
            System.out.println("节点删除");
            countDown.countDown();
        }
    }

    public ZooKeeper getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(ZooKeeper zookeeper) {
        this.zookeeper = zookeeper;
    }


    private static CountDownLatch countDown = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZKNodeExist zkServer = new ZKNodeExist(ZK_SERVER_PATH);

        /**
         * 参数：
         * path：节点路径
         * watch：watch
         */
        Stat stat = zkServer.getZookeeper().exists("/test", true);
        if (stat != null) {
            System.out.println("查询的节点版本为dataVersion：" + stat.getVersion());
        } else {
            System.out.println("该节点不存在...");
        }

        countDown.await();
    }
}
