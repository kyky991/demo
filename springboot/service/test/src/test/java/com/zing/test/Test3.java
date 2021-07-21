package com.zing.test;

import java.nio.ByteBuffer;

public class Test3 {

    public static void main(String[] args) {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.println(buf);

        buf.putInt(1);
        buf.putInt(2);

        int limit = buf.position();
        for (int i = 0; i < limit; i++) {
            System.out.println("--------------------------");

            System.out.println(buf);
            buf.flip();
            System.out.println(buf);

            System.out.println(buf.get());
            System.out.println(buf);

            buf.compact();

            System.out.println("--------------------------");
        }
    }

}
