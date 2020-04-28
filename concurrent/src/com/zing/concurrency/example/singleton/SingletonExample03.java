package com.zing.concurrency.example.singleton;

import com.zing.concurrency.annotation.ThreadSafe;


@ThreadSafe
public class SingletonExample03 {

    private SingletonExample03() {
    }

    private static SingletonExample03 instance = null;

    public static synchronized SingletonExample03 getInstance() {
        if (instance == null) {
            instance = new SingletonExample03();
        }
        return instance;
    }
}
