package com.zing.rocketmq.tx;

import com.zing.rocketmq.Constant;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TransactionProducer {

    public static void main(String[] args) throws Exception {
        TransactionMQProducer producer = new TransactionMQProducer("test_tx_producer_group");
        producer.setNamesrvAddr(Constant.NAMESRV_ADDR);
        producer.setExecutorService(new ThreadPoolExecutor(2, 5, 600, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("tx_producer-thread");
                return t;
            }
        }));
        producer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                System.out.println("executeLocalTransaction");

                String s = (String) arg;
                System.out.println(s);

                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                System.out.println("checkLocalTransaction");
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });
        producer.start();

        Message message = new Message("test_tx_topic", "Tags", "key", ("hello tx").getBytes());
        SendResult result = producer.sendMessageInTransaction(message, "arg");
        System.out.println(result);

        try {
            Thread.sleep(600000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        producer.shutdown();
    }
}
