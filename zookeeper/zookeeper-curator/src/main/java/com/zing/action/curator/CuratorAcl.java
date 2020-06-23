package com.zing.action.curator;

import com.zing.action.curator.utils.AclUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;


public class CuratorAcl {

    private CuratorFramework client = null;
    private static final String ZK_SERVER_PATH = "192.168.110.128:2181";
    private final static Integer TIMEOUT = 10000;

    public CuratorAcl() {
        RetryPolicy retryPolicy = new RetryNTimes(3, 5000);

        client = CuratorFrameworkFactory.builder()
                .authorization("digest", "test1:123456".getBytes())
                .connectString(ZK_SERVER_PATH)
                .sessionTimeoutMs(TIMEOUT)
                .retryPolicy(retryPolicy)
                .namespace("workspace")
                .build();
        client.start();
    }

    public void closeZKClient() {
        if (client != null) {
            this.client.close();
        }
    }

    public static void main(String[] args) throws Exception {
        // 实例化
        CuratorAcl cto = new CuratorAcl();
        boolean isZkCuratorStarted = cto.client.isStarted();
        System.out.println("当前客户的状态：" + (isZkCuratorStarted ? "连接中" : "已关闭"));

        String nodePath = "/acl/father/child/sub";

        List<ACL> acls = new ArrayList<ACL>();
        Id test1 = new Id("digest", AclUtils.getDigestPassword("test1:123456"));
        Id test2 = new Id("digest", AclUtils.getDigestPassword("test2:123456"));
        acls.add(new ACL(Perms.ALL, test1));
        acls.add(new ACL(Perms.READ, test2));
        acls.add(new ACL(Perms.DELETE | Perms.CREATE, test2));

        // 创建节点
        byte[] data = "acl".getBytes();
        cto.client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(acls, true)
                .forPath(nodePath, data);


//        cto.client.setACL().withACL(acls).forPath("/curatorNode");

        // 更新节点数据
//        byte[] newData = "batman".getBytes();
//        cto.client.setData().withVersion(0).forPath(nodePath, newData);

        // 删除节点
//        cto.client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(0).forPath(nodePath);

        // 读取节点数据
//        Stat stat = new Stat();
//        byte[] data = cto.client.getData().storingStatIn(stat).forPath(nodePath);
//        System.out.println("节点" + nodePath + "的数据为: " + new String(data));
//        System.out.println("该节点的版本号为: " + stat.getVersion());


        cto.closeZKClient();
        boolean isZkCuratorStarted2 = cto.client.isStarted();
        System.out.println("当前客户的状态：" + (isZkCuratorStarted2 ? "连接中" : "已关闭"));
    }

}
