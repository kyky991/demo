package com.zing.concurrency.example.publish;

import com.zing.concurrency.annotation.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@NotThreadSafe
public class Escape {

    private int escape = 0;

    public Escape() {
        new InnerEscape();
    }

    private class InnerEscape {
        public InnerEscape() {
            log.info("{}", Escape.this.escape);
        }
    }

    public static void main(String[] args) {
        new Escape();
    }
}
