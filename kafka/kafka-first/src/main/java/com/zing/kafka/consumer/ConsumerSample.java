package com.zing.kafka.consumer;

import com.zing.kafka.Factory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.Collections;

/**
 * @author Zing
 * @date 2020-11-04
 */
public class ConsumerSample {

    private static final String TOPIC_NAME = "c.topic";

    public static void main(String[] args) {
        Consumer<String, String> consumer = Factory.createConsumer();
        consumer.subscribe(Collections.singleton(TOPIC_NAME));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("p = %d, offset = %d, key = %s, value = %s%n", record.partition(), record.offset(), record.key(), record.value());
            }

            consumer.commitAsync();
        }
    }

}
