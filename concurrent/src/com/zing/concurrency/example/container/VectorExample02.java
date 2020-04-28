package com.zing.concurrency.example.container;

import com.zing.concurrency.annotation.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Vector;


@Slf4j
@NotThreadSafe
public class VectorExample02 {

    private static List<Integer> list = new Vector<>();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < list.size(); i++) {
                list.remove(i);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < list.size(); i++) {
                list.get(i);
            }
        });
        t1.start();
        t2.start();
    }

}
