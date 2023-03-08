package org.example.tutorila1;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class ProducerDemoIdempotent {
  public static void main(String[] args) {
    // System.out.println("Hello world");
    String  bootstrapProperties = "127.0.0.1:9092";
    //String  bootstrapProperties = "172.20.112.198:9092";
    //    //Create producer properties
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapProperties);
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    //create safe producer (idempotent)
    properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
    properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
    properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
    properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5"); // kafka 3.4 > 1.0



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
