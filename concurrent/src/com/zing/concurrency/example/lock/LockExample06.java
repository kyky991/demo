package com.zing.concurrency.example.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
public class LockExample06 {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        new Thread(() -> {
            try {
                lock.lock();
                log.info("wait signal");
                condition.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("get signal");
            lock.unlock();
        }).start();

        new Thread(() -> {
            lock.lock();
            log.info("get lock");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            condition.signalAll();
            log.info("send signal");
            lock.unlock();
        }).start();
    }

}
