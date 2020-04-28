package com.zing.concurrency.example.sync;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
public class SynchronizedExample01 {

    public void test1(int j) {
        synchronized (this) {
            for (int i = 0; i < 10; i++) {
                log.info("test 1:{}---{}", i, j);
            }
        }
    }

    public synchronized void test2(int j) {
        for (int i = 0; i < 10; i++) {
            log.info("test 2:{}---{}", i, j);
        }
    }

    public static void main(String[] args) {
        SynchronizedExample01 example = new SynchronizedExample01();
        SynchronizedExample01 example2 = new SynchronizedExample01();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(() -> example.test1(1));
        executorService.execute(() -> example2.test1(2));

        executorService.execute(() -> example.test2(1));
        executorService.execute(() -> example2.test2(2));
    }

}
