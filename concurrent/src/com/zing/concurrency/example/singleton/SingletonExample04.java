package com.zing.concurrency.example.singleton;

import com.zing.concurrency.annotation.NotThreadSafe;


@NotThreadSafe
public class SingletonExample04 {

    private SingletonExample04() {
    }

    private static SingletonExample04 instance = null;

    // 指令重排

    public static SingletonExample04 getInstance() {
        if (instance == null) {
            synchronized (SingletonExample04.class) {
                if (instance == null) {
                    instance = new SingletonExample04();
                }
            }
        }
        return instance;
    }
}
