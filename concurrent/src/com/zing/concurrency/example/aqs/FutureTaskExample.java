package com.zing.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@Slf4j
public class FutureTaskExample {

    public static void main(String[] args) throws Exception {
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            log.info("do something");
            Thread.sleep(5000);
            return "done";
        });

        ExecutorService exec = Executors.newCachedThreadPool();
        exec.submit(futureTask);
        log.info("future");
        Thread.sleep(1000);
        String result = futureTask.get();
        log.info("result:{}", result);
        exec.shutdown();
    }

}
