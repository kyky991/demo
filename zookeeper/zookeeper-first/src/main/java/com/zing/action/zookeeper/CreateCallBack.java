package com.zing.action.zookeeper;

import org.apache.zookeeper.AsyncCallback;

public class CreateCallBack implements AsyncCallback.StringCallback {

    public void processResult(int rc, String path, Object ctx, String name) {
        System.out.println("创建节点: " + path);
        System.out.println((String)ctx);
    }
}
