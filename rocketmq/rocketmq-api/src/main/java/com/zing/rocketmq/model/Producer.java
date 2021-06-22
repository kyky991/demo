package com.zing.rocketmq.model;

import com.zing.rocketmq.Constant;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class Producer {

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("test_model_producer_group");
        producer.setNamesrvAddr(Constant.NAMESRV_ADDR);
        producer.start();

        for (int i = 0; i < 10; i++) {
            String tag = i % 2 == 0 ? "Tag1" : "Tag2";
            Message message = new Message("test_tag_topic", tag, "key", ("hello" + i).getBytes());
            SendResult result = producer.send(message);
            System.out.println(result);
        }

        producer.shutdown();
    }
}
