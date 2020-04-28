package com.zing.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


@Slf4j
public class SemaphoreExample03 {

    private static int THREAD_POOL = 20;

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();

        final Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < THREAD_POOL; i++) {
            final int index = i;
            exec.execute(() -> {
                try {
                    if (semaphore.tryAcquire()) {
                        test(index);
                        semaphore.release();
                    }
                } catch (InterruptedException e) {
                    log.info("exception");
                }
            });
        }
        exec.shutdown();
    }

    private static void test(int i) throws InterruptedException {
        log.info("{}", i);
        Thread.sleep(1000);
    }

}
