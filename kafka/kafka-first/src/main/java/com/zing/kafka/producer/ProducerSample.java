package com.zing.kafka.producer;

import com.zing.kafka.Factory;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

/**
 * @author Zing
 * @date 2020-10-26
 */
public class ProducerSample {

    private static final String TOPIC_NAME = "c.topic";

    public static void main(String[] args) throws Exception {
        Producer<String, String> producer = Factory.createProducer();

        for (int i = 0; i < 100; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "key-" + i, "value-" + i);
            Future<RecordMetadata> future = producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    System.out.println("callback:\t" + metadata.partition() + ", \t" + metadata.offset());
                }
            });

//            RecordMetadata metadata = future.get();
//            System.out.println(metadata.partition() + ", \t" + metadata.offset());
        }

        producer.close();
    }

}
