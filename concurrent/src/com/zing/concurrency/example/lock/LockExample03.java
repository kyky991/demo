package com.zing.concurrency.example.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Slf4j
public class LockExample03 {

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static final Map<String, Data> map = new TreeMap<>();

    public Data get(String key) {
        lock.readLock().lock();
        try {
            return map.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Set<String> getAllKeys() {
        lock.readLock().lock();
        try {
            return map.keySet();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Data put(String key, Data data) {
        lock.writeLock().lock();
        try {
            return map.put(key, data);
        } finally {
            lock.writeLock().unlock();
        }
    }

    class Data {

    }

}
