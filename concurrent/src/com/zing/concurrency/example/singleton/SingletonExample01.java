package com.zing.concurrency.example.singleton;

import com.zing.concurrency.annotation.NotThreadSafe;


@NotThreadSafe
public class SingletonExample01 {

    private SingletonExample01() {
    }

    private static SingletonExample01 instance = null;

    public static SingletonExample01 getInstance() {
        if (instance == null) {
            instance = new SingletonExample01();
        }
        return instance;
    }
}
