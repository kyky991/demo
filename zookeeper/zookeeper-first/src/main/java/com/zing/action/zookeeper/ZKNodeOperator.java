package com.zing.action.zookeeper;

import com.zing.action.zookeeper.utils.AclUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper 操作
 */
public class ZKNodeOperator implements Watcher {

    private final static String ZK_SERVER_PATH = "192.168.110.128:2181";
    private final static Integer TIMEOUT = 5000;

    private ZooKeeper zookeeper = null;

    public ZKNodeOperator() {
    }

    public ZKNodeOperator(String connectString) {
        try {
            zookeeper = new ZooKeeper(connectString, TIMEOUT, new ZKNodeOperator());
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

    public void createZKNode(String path, byte[] data, List<ACL> acls) {

        String result;
        try {
            /**
             * 同步或者异步创建节点，都不支持子节点的递归创建，异步有一个callback函数
             * 参数：
             * path：创建的路径
             * data：存储的数据的byte[]
             * acl：控制权限策略
             * 			Ids.OPEN_ACL_UNSAFE --> world:anyone:cdrwa
             * 			CREATOR_ALL_ACL --> auth:user:password:cdrwa
             * createMode：节点类型, 是一个枚举
             * 			PERSISTENT：持久节点
             * 			PERSISTENT_SEQUENTIAL：持久顺序节点
             * 			EPHEMERAL：临时节点
             * 			EPHEMERAL_SEQUENTIAL：临时顺序节点
             */
            result = zookeeper.create(path, data, acls, CreateMode.PERSISTENT);
            System.out.println("创建节点：\t" + result + "\t成功...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process(WatchedEvent event) {

    }

    public ZooKeeper getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(ZooKeeper zookeeper) {
        this.zookeeper = zookeeper;
    }


    private static CountDownLatch countDown = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZKNodeOperator zkServer = new ZKNodeOperator(ZK_SERVER_PATH);

        /**
         * ======================  创建node start  ======================
         */
        // acl 任何人都可以访问
//		zkServer.createZKNode("/test", "test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE);

        // 自定义用户认证访问
		List<ACL> acls = new ArrayList<ACL>();
		Id test1 = new Id("digest", AclUtils.getDigestPassword("test1:123456"));
		Id test2 = new Id("digest", AclUtils.getDigestPassword("test2:123456"));
		acls.add(new ACL(ZooDefs.Perms.ALL, test1));
		acls.add(new ACL(ZooDefs.Perms.READ, test2));
		acls.add(new ACL(ZooDefs.Perms.DELETE | ZooDefs.Perms.CREATE, test2));
		zkServer.createZKNode("/test/digest", "digest".getBytes(), acls);

        // 注册过的用户必须通过addAuthInfo才能操作节点，参考命令行 addauth
//		zkServer.getZookeeper().addAuthInfo("digest", "test1:123456".getBytes());
//		zkServer.createZKNode("/test/digest/child", "child".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL);
//		Stat stat = new Stat();
//		byte[] data = zkServer.getZookeeper().getData("/test/digest", false, stat);
//		System.out.println(new String(data));
//		zkServer.getZookeeper().setData("/test/digest", "now".getBytes(), 1);

        // ip方式的acl
//		List<ACL> aclsIP = new ArrayList<ACL>();
//		Id ipId1 = new Id("ip", "192.168.0.2");
//		aclsIP.add(new ACL(ZooDefs.Perms.ALL, ipId1));
//		zkServer.createZKNode("/test/ip", "ip".getBytes(), aclsIP);

        // 验证ip是否有权限
//        zkServer.getZookeeper().setData("/test/ip", "now".getBytes(), 1);
//        Stat stat = new Stat();
//        byte[] data = zkServer.getZookeeper().getData("/test/ip", false, stat);
//        System.out.println(new String(data));
//        System.out.println(stat.getVersion());
    }
}
