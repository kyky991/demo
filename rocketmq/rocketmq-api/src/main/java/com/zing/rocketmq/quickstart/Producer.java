package com.zing.rocketmq.quickstart;

import com.zing.rocketmq.Constant;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

public class Producer {

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("test_producer_group");
        producer.setNamesrvAddr(Constant.NAMESRV_ADDR);
        producer.start();

        for (int i = 0; i < 5; i++) {
            Message message = new Message("test_topic", "tags", "key", ("hello").getBytes());
            SendResult result = producer.send(message);
            System.out.println(result);

            producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Integer num = (Integer) arg;
                    return mqs.get(num);
                }
            }, 1);

            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println(sendResult);
                }

                @Override
                public void onException(Throwable e) {

                }
            });
        }

        producer.shutdown();
    }
}
