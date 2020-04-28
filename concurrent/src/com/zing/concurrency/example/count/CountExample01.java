package com.zing.concurrency.example.count;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


@Slf4j
public class CountExample01 {

    private static int THREAD_POOL = 200;
    private static int CLIENT_TOTAL = 5000;

    private static int count = 0;

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(THREAD_POOL);
        for (int i = 0; i < CLIENT_TOTAL; i++) {
            exec.execute(() -> {
                try {
                    semaphore.acquire();
                    add();
                    semaphore.release();
                } catch (Exception e) {
                    log.error("exception", e);
                }
            });
        }
        exec.shutdown();
        log.info("count:{}", count);
    }

    private static void add() {
        count++;
    }

}
