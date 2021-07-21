package com.zing.test;

import com.alibaba.fastjson.JSON;
import com.zing.test.domain.Person;
import com.zing.test.domain.TestEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Data
public class TestSample {

    public static void main(String[] args) throws Exception {

        CompletableFuture.supplyAsync(() -> "hello")
                .thenApplyAsync(s -> s + " world")
                .whenCompleteAsync((s, e) -> System.out.println(s));

        CompletableFuture.completedFuture("hehe")
                .thenApplyAsync(String::toUpperCase)
                .whenComplete((s, e) -> System.out.println(s))
                .thenAccept(System.out::println);

        CompletableFuture<Void> allOf = CompletableFuture.allOf(CompletableFuture.completedFuture("te"), CompletableFuture.completedFuture("a"));
        System.out.println(allOf.get());

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(CompletableFuture.completedFuture("te"), CompletableFuture.completedFuture("a"));
        System.out.println(anyOf.get());

        System.out.println(Integer[].class);
        System.out.println(int[].class);

        EnumSet<TestEnum> set = EnumSet.of(TestEnum.T1, TestEnum.T2);
        System.out.println(set.contains(TestEnum.T2));

        List<String> stringList = new ArrayList<>();
        stringList.add("aa");
        stringList.add("bb");
        Method add = stringList.getClass().getMethod("add", Object.class);
        add.invoke(stringList, 111);
        System.out.println(stringList);
//        System.out.println(stringList.get(2));

        Person person = new Person();
        System.out.println(JSON.parseObject(null, Person.class));

        Object o = 1;
        System.out.println(person.getAge() == o);
//        System.out.println(person.getAge() == 1); // NullPointerException
        System.out.println(Objects.equals(person.getAge(), 1));

        System.out.println(List.class.isAssignableFrom(ArrayList.class));

        byte[] b = new byte[]{
                1, 2, 3, 4
        };
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        int i = dis.readInt();
        System.out.println(Integer.toHexString(i));

        Demo demo = new Demo();
        Thread thread = new Thread(() -> {
            try {
                demo.lockInterruptibly();
                log.info("demo lockInterruptibly");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread thread2 = new Thread(() -> {
            demo.lock();
            log.info("demo lock");
        });

        thread.start();
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        thread2.start();

        thread.join();
        thread2.join();

    }

    static class Demo {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        public void lock() {
            log.info("lock start");
            lock.lock();
            try {
                try {
                    log.info("lock sleep 5000");
                    Thread.sleep(5000);

                    log.info("lock signal");
                    condition.signal();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                log.info("lock unlock before");
                lock.unlock();
                log.info("lock unlock after");
            }
        }

        public void lockInterruptibly() throws Exception {
            log.info("lockInterruptibly start");
            lock.lockInterruptibly();
            try {
                try {
                    log.info("lockInterruptibly sleep 5000");
                    Thread.sleep(5000);

                    log.info("lockInterruptibly await");
                    condition.await();

                    log.info("lockInterruptibly sleep 5000");
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                log.info("lockInterruptibly unlock before");
                lock.unlock();
                log.info("lockInterruptibly unlock after");
            }
        }
    }

}
