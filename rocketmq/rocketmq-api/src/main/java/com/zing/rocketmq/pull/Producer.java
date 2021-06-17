package com.zing.rocketmq.pull;

import com.zing.rocketmq.Constant;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class Producer {

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("test_producer_pull_group");
        producer.setNamesrvAddr(Constant.NAMESRV_ADDR);
        producer.start();

        for (int i = 0; i < 10; i++) {
            Message message = new Message("test_topic_pull", "Tag1", "key", ("hello" + i).getBytes());
            SendResult result = producer.send(message);
            System.out.println(result);

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        producer.shutdown();
    }
}
