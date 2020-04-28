package com.zing.concurrency.example.singleton;

import com.zing.concurrency.annotation.ThreadSafe;


@ThreadSafe
public class SingletonExample05 {

    private SingletonExample05() {
    }

    /**
     * volatile + 双重检测模式 禁止 指令重排
     */
    private static volatile SingletonExample05 instance = null;

    public static SingletonExample05 getInstance() {
        if (instance == null) {
            synchronized (SingletonExample05.class) {
                if (instance == null) {
                    instance = new SingletonExample05();
                }
            }
        }
        return instance;
    }
}
