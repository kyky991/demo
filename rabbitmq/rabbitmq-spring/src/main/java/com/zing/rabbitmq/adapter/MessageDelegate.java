package com.zing.rabbitmq.adapter;

import com.zing.rabbitmq.entity.Order;
import com.zing.rabbitmq.entity.Packaged;

import java.io.File;
import java.util.Map;

/**
 * @author Zing
 * @date 2019-12-05
 */
public class MessageDelegate {

    public void handleMessage(byte[] messageBody) {
        System.err.println("默认方法，消息：" + new String(messageBody));
    }

    public void consumeMessage(byte[] messageBody) {
        System.err.println("字节数组方法，消息：" + new String(messageBody));
    }

    public void consumeMessage(String messageBody) {
        System.err.println("字符串方法，消息：" + messageBody);
    }

    public void method001(String messageBody) {
        System.err.println("method001方法，消息：" + messageBody);
    }

    public void method002(String messageBody) {
        System.err.println("method002方法，消息：" + messageBody);
    }

    public void consumeMessage(Map messageBody) {
        System.err.println("Map方法，消息：" + messageBody);
    }

    public void consumeMessage(Order order) {
        System.err.println("Order方法，消息：" + order);
    }

    public void consumeMessage(Packaged pack) {
        System.err.println("Packaged方法，消息：" + pack);
    }

    public void consumeMessage(File file) {
        System.err.println("File方法，消息：" + file.getAbsolutePath());
    }
}
