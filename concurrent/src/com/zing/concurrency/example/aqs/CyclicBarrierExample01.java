package com.zing.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
public class CyclicBarrierExample01 {

    private static int THREAD_POOL = 20;

    private static CyclicBarrier barrier = new CyclicBarrier(5);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();

        for (int i = 0; i < THREAD_POOL; i++) {
            final int index = i;
            Thread.sleep(1000);
            exec.execute(() -> {
                try {
                    test(index);
                } catch (Exception e) {
                    log.info("exception");
                }
            });
        }
        exec.shutdown();
    }

    private static void test(int i) throws Exception {
        Thread.sleep(1000);
        log.info("{} ready", i);
        barrier.await();
        log.info("{} continue", i);
    }

}
