package org.example.tutorila1;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemoAssignSeek {

  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(ConsumerDemo.class);

    String bootstrapProperties = "127.0.0.1:9092";
    String topic = "first_topic";

    //create consumer config
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapProperties);
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    //create the consumer
    KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

    //assign and seek are mostly used to reply data or fetch a specific message


    //assign
    TopicPartition partitionToReadFrom = new TopicPartition(topic, 0);
    long offsetToReadFrom = 15L;
    consumer.assign(Arrays.asList(partitionToReadFrom));

    //seek
    consumer.seek(partitionToReadFrom, offsetToReadFrom);

    int numberOfMessagesForRead = 5;
    boolean keepReading = true;
    int numberOfMessagesReadSoFar = 0;

    //pull for new data
    while (keepReading) {
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

      for (ConsumerRecord<String, String> record : records) {
        numberOfMessagesReadSoFar += 1;
        logger.info("Key : " + record.key() + ", Value : " + record.value());
        logger.info("Partition : " + record.partition() + ", Offset : " + record.offset());
        if (numberOfMessagesReadSoFar >= numberOfMessagesForRead) {
          keepReading = false;
          break;  //to exit the for loop
        }
      }
    }
    logger.info("Exiting the application");

  }

}
