package com.zing.action.zookeeper;

import org.apache.zookeeper.AsyncCallback;

public class DeleteCallBack implements AsyncCallback.VoidCallback {

    public void processResult(int rc, String path, Object ctx) {
        System.out.println("删除节点" + path);
        System.out.println((String)ctx);
    }
}
