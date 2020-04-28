package com.zing.concurrency.example.immutable;

import com.google.common.collect.Maps;
import com.zing.concurrency.annotation.NotThreadSafe;

import java.util.Map;


@NotThreadSafe
public class ImmutableExample01 {

    private static final Integer a = 1;
    private static final String b = "2";
    private static final Map<Integer, Integer> map = Maps.newHashMap();

    static {
        map.put(1, 2);
        map.put(2, 4);
        map.put(3, 6);
    }

    public static void main(String[] args) {
        map.put(1, 6);
    }
}
