package api.fitbit_subscription;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import util.ColorLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Configuration
@EnableKafka
public class NotificationKafkaConfig {
    public static Logger defaultLog = Logger.getLogger(NotificationKafkaConfig.class.getSimpleName());
    public static ColorLogger log = new ColorLogger(defaultLog);
    // consumer config values
    public static final String DEFAULT_GROUP_ID = "default_group"; // depracated, use
    public static final String DEFAULT_AUTO_COMMIT_INTERVAL_MS_CONFIG = "100";
    public static final String DEFAULT_SESSION_TIMEOUT_MS_CONFIG = "15000";
    // producer config values
    public static final int DEFAULT_RETRY_CONFIG = 0;
    public static final int DEFAULT_BATCH_SIZE_CONFIG = 16384;
    public static final int DEFAULT_LINGER_MS_CONFIG = 1;
    public static final int DEFAULT_BUFFER_MEMORY_CONFIG = 33554432;

    @Value("${spring.kafka.bootstrap-servers}")
    public String bootstrapServersLocation;

    @Value("${spring.kafka.topics.debug}")
    public String DEBUG_TOPIC;

    @Value("${spring.kafka.topics.fitbit-notification}")
    public String FITBIT_NOTIFICATION_TOPIC;

    public final String FITBIT_NOTIFICATION_TOPIC_CONSTANT = "fitbit-notification";

    /** Consumer Kafka Configuration ------------------------------------------------------*/

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, String> kafkaListenerContainerFactory() {
        Map<String, Object> props = consumerProps();
        DefaultKafkaConsumerFactory<Long, String> cf =
                new DefaultKafkaConsumerFactory<>(props);
        ConcurrentKafkaListenerContainerFactory<Long, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        return factory;
    }

    @Bean
    public Map<String, Object> consumerProps(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersLocation);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, DEFAULT_GROUP_ID);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, DEFAULT_AUTO_COMMIT_INTERVAL_MS_CONFIG);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, DEFAULT_SESSION_TIMEOUT_MS_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    /** Producer Kafka Configuration ------------------------------------------------------*/

    @Bean
    public KafkaTemplate<Long, String> kafkaTemplate(){
        Map<String, Object> props = producerProps();
        ProducerFactory<Long, String> producerFactory = new DefaultKafkaProducerFactory<Long, String>(props);
        KafkaTemplate<Long, String> template = new KafkaTemplate<>(producerFactory);
        return template;
    }

    @Bean
    public Map<String, Object> producerProps(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersLocation);
        props.put(ProducerConfig.RETRIES_CONFIG, DEFAULT_RETRY_CONFIG);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, DEFAULT_BATCH_SIZE_CONFIG);
        props.put(ProducerConfig.LINGER_MS_CONFIG, DEFAULT_LINGER_MS_CONFIG);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, DEFAULT_BUFFER_MEMORY_CONFIG);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    /** Configuring Topics - add topics here */

    @Bean
    public KafkaAdmin admin(){
        log.info("Kafka at " + bootstrapServersLocation);
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersLocation);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic createFitbitNotificationTopic(){
        int numberOfPartitions = 1;
        short replicationFactor = 1;
        return new NewTopic(FITBIT_NOTIFICATION_TOPIC, numberOfPartitions, replicationFactor);
    }

    @Bean
    public NewTopic createDebugTopic(){
        int numberOfPartitions = 1;
        short replicationFactor = 1;
        return new NewTopic(DEBUG_TOPIC, numberOfPartitions, replicationFactor);
    }



}
