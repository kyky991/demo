package com.zing.action.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper 获取节点数据
 */
public class ZKGetNodeData implements Watcher {

    private final static String ZK_SERVER_PATH = "hadooooop:2181";
    private final static Integer TIMEOUT = 5000;
    private static Stat stat = new Stat();

    private ZooKeeper zookeeper = null;

    public ZKGetNodeData() {
    }

    public ZKGetNodeData(String connectString) {
        try {
            zookeeper = new ZooKeeper(connectString, TIMEOUT, new ZKGetNodeData());
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
        try {
            if (event.getType() == Event.EventType.NodeDataChanged) {
                ZKGetNodeData zkServer = new ZKGetNodeData(ZK_SERVER_PATH);

                byte[] resByte = zkServer.getZookeeper().getData("/test", false, stat);

                String result = new String(resByte);

                System.out.println("更改后的值:" + result);
                System.out.println("版本号变化dversion:" + stat.getVersion());

                countDown.countDown();
            } else if (event.getType() == Event.EventType.NodeCreated) {

            } else if (event.getType() == Event.EventType.NodeChildrenChanged) {

            } else if (event.getType() == Event.EventType.NodeDeleted) {

            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

        ZKGetNodeData zkServer = new ZKGetNodeData(ZK_SERVER_PATH);

        /**
         * 参数：
         * path：节点路径
         * watch：true或者false，注册一个watch事件
         * stat：状态
         */
        byte[] resByte = zkServer.getZookeeper().getData("/test", true, stat);

        String result = new String(resByte);
        System.out.println("当前值:" + result);

        countDown.await();
    }
}
