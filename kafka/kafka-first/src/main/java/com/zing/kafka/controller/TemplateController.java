package com.zing.kafka.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.UUID;

/**
 * @author Zing
 * @date 2020-11-02
 */
@RestController
public class TemplateController {

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    @GetMapping("/send")
    public String send() {
        ProducerRecord<String, String> record = new ProducerRecord<>("c.topic",
                UUID.randomUUID().toString(), JSON.toJSONString(Arrays.asList(RandomStringUtils.randomAlphanumeric(5))));
        kafkaProducer.send(record);
        return "";
    }

}
