package com.zing.action.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.*;

import java.util.List;

public class CuratorOperator {

    private final static String ZK_SERVER_PATH = "192.168.110.128:2181";
    private final static Integer TIMEOUT = 10000;
    private final static String ADD_PATH = "/curator/test/add";

    public CuratorFramework client = null;

    /**
     * 实例化zk客户端
     */

    public CuratorOperator() {
        /**
         * 同步创建zk示例，原生api是异步的
         *
         * curator链接zookeeper的策略:ExponentialBackoffRetry
         * baseSleepTimeMs：初始sleep的时间
         * maxRetries：最大重试次数
         * maxSleepMs：最大重试时间
         */
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);

        /**
         * curator链接zookeeper的策略:RetryNTimes
         * n：重试的次数
         * sleepMsBetweenRetries：每次重试间隔的时间
         */
//        RetryPolicy retryPolicy = new RetryNTimes(3, 5000);

        /**
         * curator链接zookeeper的策略:RetryOneTime
         * sleepMsBetweenRetry:每次重试间隔的时间
         */
//        RetryPolicy retryPolicy2 = new RetryOneTime(3000);

        /**
         * 永远重试，不推荐使用
         */
//        RetryPolicy retryPolicy3 = new RetryForever(retryIntervalMs);

        /**
         * curator链接zookeeper的策略:RetryUntilElapsed
         * maxElapsedTimeMs:最大重试时间
         * sleepMsBetweenRetries:每次重试间隔
         * 重试时间超过maxElapsedTimeMs后，就不再重试
         */
//        RetryPolicy retryPolicy4 = new RetryUntilElapsed(2000, 3000);

        client = CuratorFrameworkFactory.builder()
                .connectString(ZK_SERVER_PATH)
                .sessionTimeoutMs(TIMEOUT)
                .retryPolicy(retryPolicy)
                .namespace("workspace")
                .build();
        client.start();
    }

    /**
     * 关闭zk客户端连接
     */
    public void closeZKClient() {
        if (client != null) {
            this.client.close();
        }
    }

    public static void main(String[] args) throws Exception {
        // 实例化
        CuratorOperator cto = new CuratorOperator();
        boolean isZkCuratorStarted = (cto.client.getState() == CuratorFrameworkState.STARTED);
        System.out.println("当前客户的状态：" + (isZkCuratorStarted ? "连接中" : "已关闭"));

        // 创建节点
        String nodePath = "/curator/test";
//        byte[] data = "curator".getBytes();
//        cto.client.create().creatingParentsIfNeeded()
//                .withMode(CreateMode.PERSISTENT)
//                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
//                .forPath(nodePath, data);

        // 更新节点数据
//        byte[] newData = "hahahaha".getBytes();
//        cto.client.setData().withVersion(0).forPath(nodePath, newData);

        // 删除节点
//        cto.client.delete()
//                .guaranteed()                   // 如果删除失败，那么在后端还是继续会删除，直到成功
//                .deletingChildrenIfNeeded()     // 如果有子节点，就删除
//                .withVersion(0)
//                .forPath(nodePath);

        // 读取节点数据
//        Stat stat = new Stat();
//        byte[] data = cto.client.getData().storingStatIn(stat).forPath(nodePath);
//        System.out.println("节点" + nodePath + "的数据为: " + new String(data));
//        System.out.println("该节点的版本号为: " + stat.getVersion());

        // 查询子节点
//        List<String> childNodes = cto.client.getChildren().forPath(nodePath);
//        System.out.println("开始打印子节点：");
//        for (String s : childNodes) {
//            System.out.println(s);
//        }

        // 判断节点是否存在,如果不存在则为空
//        Stat statExist = cto.client.checkExists().forPath(nodePath + "/aaa");
//        System.out.println(statExist);

        // watcher 事件  当使用usingWatcher的时候，监听只会触发一次，监听完毕后就销毁
//        cto.client.getData().usingWatcher(new MyCuratorWatcher()).forPath(nodePath);
//        cto.client.getData().usingWatcher(new MyWatcher()).forPath(nodePath);

        // 为节点添加watcher
        // NodeCache: 监听数据节点的变更，会触发事件
//        final NodeCache nodeCache = new NodeCache(cto.client, nodePath);
        // buildInitial : 初始化的时候获取node的值并且缓存
//        nodeCache.start(true);
//        if (nodeCache.getCurrentData() != null) {
//            System.out.println("节点初始化数据为：" + new String(nodeCache.getCurrentData().getData()));
//        } else {
//            System.out.println("节点初始化数据为空...");
//        }
//        nodeCache.getListenable().addListener(new NodeCacheListener() {
//            public void nodeChanged() throws Exception {
//                if (nodeCache.getCurrentData() == null) {
//                    System.out.println("空");
//                    return;
//                }
//                String data = new String(nodeCache.getCurrentData().getData());
//                System.out.println("节点路径：" + nodeCache.getCurrentData().getPath() + "数据：" + data);
//            }
//        });

        // 为子节点添加watcher
        // PathChildrenCache: 监听数据节点的增删改，会触发事件
        String childNodePathCache = nodePath;
        // cacheData: 设置缓存节点的数据状态
        final PathChildrenCache childrenCache = new PathChildrenCache(cto.client, childNodePathCache, true);
        /**
         * StartMode: 初始化方式
         * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
         * NORMAL：异步初始化
         * BUILD_INITIAL_CACHE：同步初始化
         */
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        List<ChildData> childDataList = childrenCache.getCurrentData();
        for (ChildData childData : childDataList) {
            String data = new String(childData.getData());
            System.out.println(data);
        }

        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case INITIALIZED: {
                        System.out.println("子节点初始化ok...");
                        break;
                    }
                    case CHILD_ADDED: {
                        String path = event.getData().getPath();
                        if (path.equals(ADD_PATH)) {
                            System.out.println("添加子节点:" + event.getData().getPath());
                            System.out.println("子节点数据:" + new String(event.getData().getData()));
                        } else if (path.equals("/curator/test/other")) {
                            System.out.println("添加不正确...");
                        }
                        break;
                    }
                    case CHILD_UPDATED: {
                        System.out.println("修改子节点路径:" + event.getData().getPath());
                        System.out.println("修改子节点数据:" + new String(event.getData().getData()));
                        break;
                    }
                    case CHILD_REMOVED: {
                        System.out.println("删除子节点:" + event.getData().getPath());
                        break;
                    }
                }
            }
        });

        Thread.sleep(100000);

        cto.closeZKClient();
        boolean isZkCuratorStarted2 = (cto.client.getState() == CuratorFrameworkState.STARTED);
        System.out.println("当前客户的状态：" + (isZkCuratorStarted2 ? "连接中" : "已关闭"));
    }
}
