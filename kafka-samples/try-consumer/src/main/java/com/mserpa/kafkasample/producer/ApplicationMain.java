package com.mserpa.kafkasample.producer;

public class ApplicationMain {

    public static void main(String[] args){
        KafkaConsumerFun consumerFun = new KafkaConsumerFun();
        consumerFun.run();
    }

}
