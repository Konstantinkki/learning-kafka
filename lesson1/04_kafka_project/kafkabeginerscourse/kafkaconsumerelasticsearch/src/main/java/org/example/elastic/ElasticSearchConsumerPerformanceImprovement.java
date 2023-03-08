package org.example.elastic;

import com.google.gson.JsonParser;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

public class ElasticSearchConsumerPerformanceImprovement {

  public static RestHighLevelClient createElasticSearchClient(){

    //https://app.bonsai.io/
    //https://s5ulnkb6rd:ykpza2szzj@test-567001982.us-east-1.bonsaisearch.net:443
    String hostName="test-567001982.us-east-1.bonsaisearch.net";
    String username = "s5ulnkb6rd";
    String password = "ykpza2szzj";

    //don't do if you run a local ES
    final CredentialsProvider credentialsProvider =  new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
    RestClientBuilder builder = RestClient.builder(new HttpHost(hostName, 443,"https"))
        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
          @Override
          public HttpAsyncClientBuilder customizeHttpClient(
              HttpAsyncClientBuilder httpAsyncClientBuilder) {
            return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
          }
        });

    RestHighLevelClient client = new RestHighLevelClient(builder);
    return client;

  }

  public static KafkaConsumer<String, String> createKafkaMessagesConsumer(String topic){
    String bootstrapProperties = "127.0.0.1:9092";
    String groupId = "kafka-demo-elasticsearch";
    // String topic = "twitter_tweets";

    //create consumer config
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapProperties);
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // disable autocommit
    properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10"); // disable autocommit

    //create the consumer
    KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

    consumer.subscribe(Arrays.asList(topic));
    return consumer;
  }

  private static JsonParser jsonParser =  new JsonParser();
  private static String extractIdFromTweet(String tweetJson){
    return jsonParser.parse(tweetJson)
        .getAsJsonObject()
        .get("id_str")
        .getAsString();
  }

  public static void main(String[] args) throws IOException {
    Logger logger = Logger.getLogger(RestHighLevelClient.class.getName());

    RestHighLevelClient client = createElasticSearchClient();

    KafkaConsumer<String, String> consumer = createKafkaMessagesConsumer("twitter_twits");

    //pull for new data
    while (true) {
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
      Integer recordCount = records.count();
      logger.info("Received "+recordCount+ " records");

      BulkRequest bulkRequest = new BulkRequest();

      for (ConsumerRecord<String, String> record : records) {
        //2 ways to generate unique UD
        //kafka generic id
        String id = record.topic()+"_"+record.partition()+"_"+record.offset();

        try {
          //extract from real twitter json
          //String id = extractIdFromTweet(record.value());

          //here we insert data into elastic search
          IndexRequest indexRequest = new IndexRequest(
              "twitter", "tweets", id
          ).source(record.value(), XContentType.JSON);

          bulkRequest.add(indexRequest); // we add to our bulk request (takes no time)
        }catch (Exception e){
          logger.warning("skipp bad data : "+ record.value());
        }

      }
      if(recordCount > 0) {
        BulkResponse bulkItemResponses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        logger.info(" Committing offsets ... ");
        consumer.commitSync();
        logger.info(" Offsets have been committed ");

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

      }
    }



    //close the client gracefully
   // client.close();

  }
}
