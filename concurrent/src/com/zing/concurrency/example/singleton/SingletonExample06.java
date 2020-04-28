package com.zing.concurrency.example.singleton;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SingletonExample06 {

    private SingletonExample06() {
    }

    /**
     * 静态代码块的顺序，影响初始化
     */
    static {
        instance = new SingletonExample06();
    }

    private static SingletonExample06 instance = null;

    public static SingletonExample06 getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        log.info("{}", getInstance());
    }
}
