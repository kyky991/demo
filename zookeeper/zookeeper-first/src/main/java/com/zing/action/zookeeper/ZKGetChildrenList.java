package com.zing.action.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper 获取子节点数据
 */
public class ZKGetChildrenList implements Watcher {

    private final static String ZK_SERVER_PATH = "192.168.110.128:2181";
    private final static Integer TIMEOUT = 5000;

    private ZooKeeper zookeeper = null;

    public ZKGetChildrenList() {
    }

    public ZKGetChildrenList(String connectString) {
        try {
            zookeeper = new ZooKeeper(connectString, TIMEOUT, new ZKGetChildrenList());
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
            if (event.getType() == Event.EventType.NodeChildrenChanged) {
                System.out.println("NodeChildrenChanged");

                ZKGetChildrenList zkServer = new ZKGetChildrenList(ZK_SERVER_PATH);

                List<String> strChildList = zkServer.getZookeeper().getChildren(event.getPath(), false);
                for (String s : strChildList) {
                    System.out.println(s);
                }

                countDown.countDown();
            } else if (event.getType() == Event.EventType.NodeCreated) {
                System.out.println("NodeCreated");
            } else if (event.getType() == Event.EventType.NodeDataChanged) {
                System.out.println("NodeDataChanged");
            } else if (event.getType() == Event.EventType.NodeDeleted) {
                System.out.println("NodeDeleted");
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

        ZKGetChildrenList zkServer = new ZKGetChildrenList(ZK_SERVER_PATH);

        /**
         * 参数：
         * path：父节点路径
         * watch：true或者false，注册一个watch事件
         */
		List<String> strChildList = zkServer.getZookeeper().getChildren("/test", true);
		for (String s : strChildList) {
			System.out.println(s);
		}

        // 异步调用
//        String ctx = "{'callback':'ChildrenCallback'}";
//        zkServer.getZookeeper().getChildren("/test", true, new ChildrenCallBack(), ctx);
//        zkServer.getZookeeper().getChildren("/test", true, new Children2CallBack(), ctx);

        countDown.await();
    }
}
