package com.zing.concurrency.example.singleton;

import com.zing.concurrency.annotation.ThreadSafe;


@ThreadSafe
public class SingletonExample02 {

    private SingletonExample02() {
    }

    private static SingletonExample02 instance = new SingletonExample02();

    public static SingletonExample02 getInstance() {
        return instance;
    }
}
