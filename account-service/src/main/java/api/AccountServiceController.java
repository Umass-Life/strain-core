package api;

import api.strain_user.StrainUser;
import api.strain_user.StrainUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import util.ColorLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class AccountServiceController {
    Logger logger = Logger.getLogger(AccountServiceController.class.getSimpleName());
    ColorLogger colorLogger = new ColorLogger(logger);
    @Autowired
    private StrainUserService userService;
    @Autowired
    AccountServiceApplication.ConfigWrap configWrap;
    @Autowired
    ReplyingKafkaTemplate<Integer, String, String> template;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Map> login(@RequestBody Map<String, String> loginJSON){
        Map<String, Object> resMap = new HashMap<>();
        colorLogger.info("LOGIN: " + loginJSON);
        try {
            String email = loginJSON.get("email");
            String password = loginJSON.get("password");
            StrainUser user = userService.login(email, password);
            if (user == null){
                resMap.put("error", "login failed");
                return ResponseEntity.status(401).body(resMap);
            }


            ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(configWrap.get_NEW_STRAIN_USER_TOPIC(), user.getId().toString());
            producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, configWrap.get_NEW_STRAIN_USER_TOPIC_REPLY().getBytes()));
            RequestReplyFuture<Integer, String, String> future = template.sendAndReceive(producerRecord);
            template.flush();
            ConsumerRecord<Integer, String> integerStringConsumerRecord = future.get();
            String json = integerStringConsumerRecord.value();
            colorLogger.info("[SYNCHRONOUS REPLY] " + json);
            ObjectMapper om = new ObjectMapper();
            TypeReference<HashMap<String,Object>> typeRef
                    = new TypeReference<HashMap<String,Object>>() {};
            HashMap<String, Object> replyMap = om.readValue(json, typeRef);
            resMap.putAll(replyMap);
            resMap.put(StrainUser.SINGULAR, user);
            return ResponseEntity.ok(resMap);

        } catch(Exception e){
            e.printStackTrace();
            colorLogger.severe(e.getMessage());
            resMap = new HashMap<>();
            return ResponseEntity.badRequest().body(resMap);
        }
    }

    @RequestMapping(value = "/kafka", method = RequestMethod.GET)
    public ResponseEntity kafkatest(@RequestParam(value="msg") String msg){
        try {
            colorLogger.info("/kafka SENT: " + msg);

            ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(configWrap.get_NEW_STRAIN_USER_TOPIC(), msg);
            producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, configWrap.get_NEW_STRAIN_USER_TOPIC_REPLY().getBytes()));
            RequestReplyFuture<Integer, String, String> future = template.sendAndReceive(producerRecord);
            template.flush();
//            SendResult<Integer, String> sendResult = future.getSendFuture().get();
            ConsumerRecord<Integer, String> integerStringConsumerRecord = future.get();
            String json = integerStringConsumerRecord.value();
            colorLogger.info("[SYNCHRONOUS REPLY] " + json);
            return ResponseEntity.ok(json);


//            future.addCallback(new ListenableFutureCallback<ConsumerRecord<Integer, String>>() {
//                @Override
//                public void onFailure(Throwable throwable) {
//                    colorLogger.severe(throwable.getMessage());
//                }
//
//                @Override
//                public void onSuccess(@Nullable ConsumerRecord<Integer, String> integerStringConsumerRecord) {
//                    colorLogger.info("/kafka REPLY: (%s) %s",integerStringConsumerRecord.key(), integerStringConsumerRecord.value());
//                }
//            });
//            return ResponseEntity.ok().body("ok");
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }



}
