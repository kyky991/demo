package com.zing.concurrency.example.publish;

import com.zing.concurrency.annotation.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;


@Slf4j
@NotThreadSafe
public class UnsafePublish {

    private String[] states = {"a", "b", "c"};

    public String[] getStates() {
        return states;
    }

    public static void main(String[] args) {
        UnsafePublish publish = new UnsafePublish();
        log.info("{}", Arrays.toString(publish.getStates()));

        publish.getStates()[0] = "d";
        log.info("{}", Arrays.toString(publish.getStates()));
    }
}
