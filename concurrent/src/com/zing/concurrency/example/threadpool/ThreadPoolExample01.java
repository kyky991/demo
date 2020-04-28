package com.zing.concurrency.example.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ThreadPoolExample01 {

    public static void main(String[] args) {
        ExecutorService exe = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            exe.execute(() -> {
                log.info("task:{}", index);
            });

        }
        exe.shutdown();
    }

}
