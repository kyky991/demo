package com.zing.test.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileCopy {

    public static void main(String[] args) throws Exception {
        File source = new File("service/test/src/test/resources/bean.xml");
        File target = new File("service/test/src/test/resources/bean-file-copy.xml");

        FileInputStream fis = new FileInputStream(source);
        FileOutputStream fos = new FileOutputStream(target);

        FileChannel fisChannel = fis.getChannel();
        FileChannel fosChannel = fos.getChannel();

        ByteBuffer buf = ByteBuffer.allocate(1024);
        while ((fisChannel.read(buf)) != -1) {
            buf.flip();
            int out = 0;
            while ((out = fosChannel.write(buf)) != 0) {
                System.out.println("写入: " + out);
            }
            buf.clear();
        }
        fosChannel.force(true);

        fosChannel.close();
        fos.close();

        fisChannel.close();
        fis.close();
    }

}
