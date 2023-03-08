package org.example.tutorila1;

import java.util.Properties;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemoWithCallback {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallback.class);
    // System.out.println("Hello world");
    String  bootstrapProperties = "127.0.0.1:9092";
    //String  bootstrapProperties = "172.20.112.198:9092";
    //Create producer properties
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapProperties);
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    // create the producer
    KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

    for(int i=0;i<10;i++) {

      // create producer record
      ProducerRecord<String, String> record =
          new ProducerRecord<>("first_topic", "Hello kafka world");

      //send data - asynchronous
      kafkaProducer.send(record, new Callback() {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
          //executes every time a record is successfully sent or an exception is thrown
          if (e == null) {
            //exception
            logger.info("Received new metadata.  \n" +
                "Topic: " + recordMetadata.topic() + "\n" +
                "Partition: " + recordMetadata.partition() + "\n" +
                "Offset: " + recordMetadata.offset() + "\n" +
                "Timestamp: " + recordMetadata.timestamp());

          } else {
            logger.error("Error while producing", e);
          }
        }
      });
    }
      //flush data and close
      kafkaProducer.flush();
      kafkaProducer.close();

  }
}
