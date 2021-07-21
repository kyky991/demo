package com.zing.test.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class TestKafka {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka.zing:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        AdminClient client = AdminClient.create(properties);

        System.out.println(client.listTopics().names().get());

        CountDownLatch latch = new CountDownLatch(1);

        Producer<String, Object> producer = new KafkaProducer<>(properties);

        ProducerRecord<String, Object> record = new ProducerRecord<>("test_topic", 0, "key", "value");
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                System.out.println(metadata);

                latch.countDown();
            }
        });

        latch.await();
    }

}
