package org.example.elastic;

import java.util.Date;
import java.util.Properties;
import java.util.Random;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class TwitterMessagesEmulator {
  public static void main(String[] args) {
    // System.out.println("Hello world");
    String  bootstrapProperties = "127.0.0.1:9092";
    //String  bootstrapProperties = "172.20.112.198:9092";
    //    //Create producer properties
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapProperties);
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    // create the producer

    KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

    for(int i=0;i<10;i++) {
      //KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
      // create producer record
      ProducerRecord<String, String> record =
          new ProducerRecord<>("twitter_twits", "{\"messageID\":\""+i+"_"+ new Date().getTime()+"\", \"messageType\":\"fakeTwit\"}");

      //send data - asynchronous
      kafkaProducer.send(record);

      //flush data and close
      kafkaProducer.flush();

    }
    kafkaProducer.close();
  }
}
