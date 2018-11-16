package api.fitbit_web_api;

import api.fitbit_subscription.FitbitSubscriptionService;
import api.fitbit_subscription.NotificationKafkaConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import util.ColorLogger;
import util.EntityHelper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
public class FitbitNotificationService {
    private Logger defaultLogger = Logger.getLogger(FitbitNotificationService.class.getSimpleName());
    private ColorLogger log = new ColorLogger(defaultLogger);
    @Autowired
    private NotificationKafkaConfig notificationKafkaConfig;
//    @Autowired
//    private KafkaTemplate<Long, String> kafkaTemplate;

    private FitbitSubscriptionService subscriptionService;

    /**
     * Notification Ingestion pipeline, receives notification and makes the appropriate API call.
     * Backlogs any view limits to delayed Kafka queues.
     * -
     * */

    public void notifySubscription(JsonNode notificationJson) throws JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
//        String serializedJson = mapper.writeValueAsString(notificationJson);
//        Long key = EntityHelper.toEpochMilli(LocalDateTime.now());
//        this.kafkaTemplate.send(notificationKafkaConfig.FITBIT_NOTIFICATION_TOPIC, key, serializedJson);
//        this.kafkaTemplate.flush();
    }

    public void notifyDebug(JsonNode test) throws JsonProcessingException{
//        Long key = EntityHelper.toEpochMilli(LocalDateTime.now());
//        ObjectMapper mapper = new ObjectMapper();
//        String serializedJson = mapper.writeValueAsString(test);
//        this.kafkaTemplate.send(notificationKafkaConfig.DEBUG_TOPIC, key, serializedJson);
//        this.kafkaTemplate.flush();
    }

//    @KafkaListener(topics = "${spring.kafka.topics.fitbit-notification}")
//    public void notificationListener(ConsumerRecord<Long, String> msg) throws IOException {
//        String json = msg.value();
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode node = mapper.readTree(json);
//        log.info("RECV: " + node);
//    }
//
//    @KafkaListener(topics = "${spring.kafka.topics.debug}")
//    public void debugListener(ConsumerRecord<Long, String> msg) throws IOException {
//        String json = msg.value();
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode node = mapper.readTree(json);
//        log.info("[KAFKA(debug)]: " + node);
//    }

}

