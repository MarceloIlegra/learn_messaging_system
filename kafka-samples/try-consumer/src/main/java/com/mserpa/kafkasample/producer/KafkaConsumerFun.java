package com.mserpa.kafkasample.producer;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaConsumerFun {

    private final static String URL_KAFKA = "localhost:9092,localhost:9093, localhost:9094";
    private final static String TOPIC_KAFKA = "my-replicated-topic";

    private static Logger LOG = LogManager.getLogger(KafkaConsumerFun.class);

    private KafkaConsumer<String, String> consumer;

    public KafkaConsumerFun() {
        Properties props = new Properties();
        props.put("bootstrap.servers", URL_KAFKA);
        props.put("group.id", "test");
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "0");
        props.put("request.timeout.ms", "70000");
        props.put("session.timeout.ms", "60000");
        //props.put("consumer.timeout.ms","30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        this.consumer = new KafkaConsumer<>(props);

        this.consumer.subscribe(Arrays.asList(TOPIC_KAFKA));
        LOG.info("configurated");
    }

    public void run(){

        LOG.info("Starting main loop");
        Long currentOffset = null;
        while (true) {
            Boolean hasError = false;
            LOG.info("Fetching poll messages...");
            ConsumerRecords<String, String> records = this.consumer.poll(100);
            LOG.info("Fetched " + records.count() + " messages.");
            for (ConsumerRecord<String, String> record : records) {
                String message = record.value();
                LOG.info("Processing message: "+message+" lastOffset: "+currentOffset+" newOffset: " + record.offset());

                if(currentOffset == null || currentOffset != record.offset()){
                    currentOffset = record.offset();
                    if(StringUtils.isNumeric(message)){
                        LOG.info("Sleeping for " + message + " ms.");
                        try {
                            Thread.sleep(Long.valueOf(message));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                boolean commitCompleted = commitOffset(hasError, record);
                if(!commitCompleted) break;
            }
            LOG.info("Has Error " + hasError);
            if (hasError) keepOnCurrentOffset(currentOffset);
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    private void keepOnCurrentOffset(Long currentOffset) {
        LOG.info("Keep on current offset");
        TopicPartition topicPartition = new TopicPartition(URL_KAFKA, 0);
        consumer.seek(topicPartition, currentOffset);
    }

    private Boolean commitOffset(Boolean hasError, ConsumerRecord<String, String> record) {
        LOG.info("Preparing to commit");
        Map<TopicPartition, OffsetAndMetadata> map = new HashMap<>();
        TopicPartition topicPartition = new TopicPartition(TOPIC_KAFKA, record.partition());
        map.put(topicPartition, nextOffset(record));
        LOG.info("Started commit");
        try {
            consumer.commitSync(map);
            LOG.info("Ended commit");
        }catch(CommitFailedException ex){
            LOG.info("Commit cannot be completed ");
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private static OffsetAndMetadata nextOffset(ConsumerRecord<String, String> record) {
        LOG.info("Going to next offset");
        LOG.info("Current offset " + record.offset());
        OffsetAndMetadata offset = new OffsetAndMetadata(record.offset() + 1);
        LOG.info("Next offset" + offset.offset());
        return offset;
    }


}
