package com.zing.concurrency.example.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadPoolExample03 {

    public static void main(String[] args) {
        ScheduledExecutorService exe = Executors.newScheduledThreadPool(5);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            exe.schedule(() -> {
                log.info("task:{}", index);
            }, index, TimeUnit.SECONDS);

        }
        exe.scheduleAtFixedRate(() -> {
            log.info("schedule");
        }, 1, 3, TimeUnit.SECONDS);
//        exe.shutdown();
    }

}
