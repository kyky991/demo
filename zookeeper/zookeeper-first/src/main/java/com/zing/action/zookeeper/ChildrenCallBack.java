package com.zing.action.zookeeper;

import org.apache.zookeeper.AsyncCallback;

import java.util.List;

public class ChildrenCallBack implements AsyncCallback.ChildrenCallback {

    public void processResult(int rc, String path, Object ctx, List<String> children) {
        for (String s : children) {
            System.out.println(s);
        }
        System.out.println("ChildrenCallback:" + path);
        System.out.println((String)ctx);
    }
}
