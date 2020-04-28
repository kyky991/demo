package com.zing.concurrency.example.immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.zing.concurrency.annotation.ThreadSafe;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


@ThreadSafe
public class ImmutableExample02 {

    private static Map<Integer, Integer> map = Maps.newHashMap();

    private static final List<Integer> list = ImmutableList.of(1, 2, 3, 4, 5);
    private static final Set<Integer> set = ImmutableSet.copyOf(list);

    static {
        map.put(1, 2);
        map.put(2, 4);
        map.put(3, 6);
        map = Collections.unmodifiableMap(map);
    }

    public static void main(String[] args) {
        map.put(1, 6);
        list.add(7);
        set.add(7);
    }
}
