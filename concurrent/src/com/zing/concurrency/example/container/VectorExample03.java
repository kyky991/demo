package com.zing.concurrency.example.container;

import java.util.Iterator;
import java.util.Vector;


public class VectorExample03 {

    private static void test1(Vector<Integer> list) {
        for (Integer i : list) {
            if (i.equals(3)) {
                list.remove(i);
            }
        }
    }

    private static void test2(Vector<Integer> list) {
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(3)) {
                iterator.remove();
            }
        }
    }

    private static void test3(Vector<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(3)) {
                list.remove(i);
            }
        }
    }

    public static void main(String[] args) {
        Vector<Integer> list = new Vector<>();
        list.add(1);
        list.add(2);
        list.add(3);
        test1(list);
    }

}
