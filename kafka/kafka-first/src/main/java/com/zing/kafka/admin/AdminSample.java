package com.zing.kafka.admin;

import com.zing.kafka.Factory;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.ConfigResource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zing
 * @date 2020-10-22
 */
public class AdminSample {

    private static final String TOPIC_NAME = "c.topic";

    public static void main(String[] args) throws Exception {
        AdminClient client = Factory.createAdminClient();

//        NewTopic topic = new NewTopic(TOPIC_NAME, 1, (short) 1);
//        client.createTopics(Collections.singletonList(topic));

        ListTopicsResult listTopics = client.listTopics();
        System.out.println(listTopics.names().get());


        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, TOPIC_NAME);

        Map<ConfigResource, Config> configs = new HashMap<>();
        configs.put(resource, new Config(Collections.singletonList(new ConfigEntry("preallocate", "true"))));
        client.alterConfigs(configs);

        Map<String, NewPartitions> newPartitions = new HashMap<>();
        newPartitions.put(TOPIC_NAME, NewPartitions.increaseTo(2));
        client.createPartitions(newPartitions);

        DescribeConfigsResult describeConfigsResult = client.describeConfigs(Collections.singletonList(resource));
        for (Map.Entry<ConfigResource, KafkaFuture<Config>> entry : describeConfigsResult.values().entrySet()) {
            System.out.println(entry.getKey());
            Config config = entry.getValue().get();
            config.entries().forEach(System.out::println);
        }

        DescribeTopicsResult describeTopicsResult = client.describeTopics(Collections.singletonList(TOPIC_NAME));
        for (Map.Entry<String, KafkaFuture<TopicDescription>> entry : describeTopicsResult.values().entrySet()) {
            System.out.println(entry.getKey());
            TopicDescription description = entry.getValue().get();
            description.partitions().forEach(System.out::println);
        }

    }


}
