package com.zing.concurrency.example.count;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


@Slf4j
public class CountExample02 {

    private static int THREAD_POOL = 1;
    private static int CLIENT_TOTAL = 5000;

    private static Map<Integer, Integer> map = Maps.newHashMap();

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(THREAD_POOL);
        for (int i = 0; i < CLIENT_TOTAL; i++) {
            final int num = i;
            exec.execute(() -> {
                try {
                    semaphore.acquire();
                    func(num);
                    semaphore.release();
                } catch (Exception e) {
                    log.error("exception", e);
                }
            });
        }
        exec.shutdown();
        log.info("size:{}", map.size());
    }

    private static void func(int num) {
        map.put(num, num);
    }

}
