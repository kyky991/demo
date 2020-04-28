package com.zing.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
public class CountDownLatchExample01 {

    private static int THREAD_POOL = 200;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();

        final CountDownLatch countDownLatch = new CountDownLatch(THREAD_POOL);

        for (int i = 0; i < THREAD_POOL; i++) {
            final int index = i;
            exec.execute(() -> {
                try {
                    test(index);
                } catch (InterruptedException e) {
                    log.info("exception");
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        log.info("finish");
        exec.shutdown();
    }

    private static void test(int i) throws InterruptedException {
        Thread.sleep(100);
        log.info("{}", i);
        Thread.sleep(100);
    }

}
