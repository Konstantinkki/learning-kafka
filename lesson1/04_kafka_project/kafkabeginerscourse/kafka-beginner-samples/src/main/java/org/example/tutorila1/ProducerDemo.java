package org.example.tutorila1;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class ProducerDemo {
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

    // create producer record
    ProducerRecord<String, String> record = new ProducerRecord<>("first_topic", "Hello kafka world");

    //send data - asynchronous
    kafkaProducer.send(record);

    //flush data and close
    kafkaProducer.flush();
    kafkaProducer.close();
  }
}
