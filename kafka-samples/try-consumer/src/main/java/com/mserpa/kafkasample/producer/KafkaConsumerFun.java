package com.mserpa.kafkasample.producer;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaConsumerFun {

    private final static String URL_KAFKA = "localhost:9092,localhost:9093, localhost:9094";
    private final static String TOPIC_KAFKA = "my-replicated-topic";

    private KafkaConsumer<String, String> consumer;

    public KafkaConsumerFun() {
        Properties props = new Properties();
        props.put("bootstrap.servers", URL_KAFKA);
        props.put("group.id", "test");
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "0");

        props.put("request.timeout.ms", "60000");
        props.put("session.timeout.ms", "50000");
        //props.put("consumer.timeout.ms","30000");

        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        this.consumer = new KafkaConsumer<>(props);

        this.consumer.subscribe(Arrays.asList(TOPIC_KAFKA));
        System.out.println("configurated");
    }

    public void run(){

        while (true) {
            Long currentOffset = null;
            Boolean hasError = false;
            System.out.println("Before poll");
            ConsumerRecords<String, String> records = this.consumer.poll(100);
            System.out.println("After poll: " + records.count());
            for (ConsumerRecord<String, String> record : records) {
                currentOffset = record.offset();
                String message = record.value();
                System.out.println("Message: " + message);
                if(StringUtils.isNumeric(message)){
                    System.out.println("Sleeping for " + message + " ms.");
                    try {
                        Thread.sleep(Long.valueOf(message));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                commitOffset(hasError, record);
            }
            if (hasError) keepOnCurrentOffset(currentOffset);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void keepOnCurrentOffset(Long currentOffset) {
        TopicPartition topicPartition = new TopicPartition(URL_KAFKA, 0);
        consumer.seek(topicPartition, currentOffset);
    }

    private void commitOffset(Boolean hasError, ConsumerRecord<String, String> record) {
        Map<TopicPartition, OffsetAndMetadata> map = new HashMap<>();
        TopicPartition topicPartition = new TopicPartition(TOPIC_KAFKA, record.partition());
        map.put(topicPartition, nextOffset(record));
        consumer.commitSync(map);
    }

    private static OffsetAndMetadata nextOffset(ConsumerRecord<String, String> record) {
        return new OffsetAndMetadata(record.offset() + 1);
    }


}
