package api.fitbit_account.fitbit_user;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import util.ColorLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

@Configuration
@EnableKafka
public class FitbitUserKafkaConsumerConfig {
    static Logger log = Logger.getLogger(FitbitUserKafkaConsumerConfig.class.getName());
    static ColorLogger colorLogger = new ColorLogger(log);

    @Value("${spring.kafka.topics.new-strain-user}")
    public String NEW_STRAIN_USER_TOPIC;

    @Value("${spring.kafka.topics.new-strain-user-reply}")
    public String NEW_STRAIN_USER_REPLY_TOPIC;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServersLocation;

    private static final String GROUP_ID_CONFIG = "strain-user-group";

    @Bean
    public ConsumerFactory<Integer, String> fitbitUserConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(fitbitUserConsumerConfigs());
    }

    @Bean
    public Map<String, Object> fitbitUserConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersLocation);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONFIG);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<Integer, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersLocation);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 0);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public KafkaTemplate<Integer, String> kafkaTemplate() {
        return new KafkaTemplate<Integer, String>(producerFactory());
    }

    @Bean
    public ReplyingKafkaTemplate<Integer, String, String> replyingKafkaTemplate
            (ProducerFactory<Integer, String> pf,
             KafkaMessageListenerContainer<Integer, String> replyContainer){
        return new ReplyingKafkaTemplate<>(pf, replyContainer);
    }

    @Bean
    public KafkaMessageListenerContainer<Integer, String> replyContainer(ConsumerFactory<Integer, String> cf){
        ContainerProperties cp = new ContainerProperties(NEW_STRAIN_USER_REPLY_TOPIC);
        return new KafkaMessageListenerContainer<Integer, String>(cf, cp);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(fitbitUserConsumerFactory());
        factory.setReplyTemplate(kafkaTemplate());
        return factory;
    }

    @Bean
    public KafkaAdmin admin(){
        log.info("[bootstrapServerLocation] ----> " + bootstrapServersLocation);
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersLocation);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic newStrainUserTopic(){
        return new NewTopic(NEW_STRAIN_USER_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic newStrainUserReplyTopic(){
        return new NewTopic(NEW_STRAIN_USER_REPLY_TOPIC, 1, (short) 1);
    }



}
