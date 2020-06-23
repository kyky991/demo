package com.zing.action.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class Children2CallBack implements AsyncCallback.Children2Callback {

    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        for (String s : children) {
            System.out.println(s);
        }
        System.out.println("Children2CallBack:" + path);
        System.out.println((String)ctx);
        System.out.println(stat.toString());
    }
}
