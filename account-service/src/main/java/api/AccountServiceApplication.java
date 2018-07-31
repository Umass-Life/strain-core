package api;
import api.strain_user.StrainUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;
import java.util.logging.Logger;

//@SpringBootApplication(scanBasePackages= {"api", "domain.models.account_service"})
//@EntityScan(basePackages = {"domain.models.account_service"})
@SpringBootApplication
@EnableDiscoveryClient
@Controller
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AccountServiceApplication implements ApplicationRunner {

    Logger log = Logger.getLogger(AccountServiceApplication.class.getName());

    @Autowired
    ReplyingKafkaTemplate<Integer, String, String> template;

    @Autowired
    ConfigWrap configWrap;

    @Autowired
    StrainUserService service;

    public static void main(String[] args){
        SpringApplication.run(AccountServiceApplication.class, args);

    }

    @Override
    public void run(ApplicationArguments args){
        log.info(String.format("\n%s running...", AccountServiceApplication.class.getName()));
        log.info("\n"+service.create("hello", "world"));


//        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(configWrap.get_NEW_STRAIN_USER_TOPIC(), "HELLO");
//        producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, configWrap.get_NEW_STRAIN_USER_TOPIC_REPLY().getBytes()));
//        RequestReplyFuture<Integer, String, String> future = template.sendAndReceive(producerRecord);
//        template.flush();
//
//        future.addCallback(new ListenableFutureCallback<ConsumerRecord<Integer, String>>() {
//            @Override
//            public void onFailure(Throwable throwable) {
//                log.warning(throwable.getMessage());
//            }
//
//            @Override
//            public void onSuccess(@Nullable ConsumerRecord<Integer, String> integerStringSendResult) {
//                log.info("\nFUTURE found!-------- "+ integerStringSendResult.toString());
//            }
//        });
//
//        try {
//            log.info("\n----> sent");
////            KafkaSandbox.Listener.latch.await();
////            log.info("counted.");
//        } catch (Exception e){
//
//        }
    }

    @Bean
    public AuditorAware<String> auditorAware(){
        return new AuditorAwareImpl();
    }



    @RequestMapping(value="/ping", method = RequestMethod.GET)
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }


    public static class AuditorAwareImpl implements AuditorAware<String>{
        @Override
        public Optional<String> getCurrentAuditor() {
            return Optional.of("admin");
        }
    }

    @Component
    public static class ConfigWrap {
        @Value("${spring.kafka.topics.new-strain-user}")
        private String NEW_STRAIN_USER_TOPIC;

        @Value("${spring.kafka.topics.new-strain-user-reply}")
        private String NEW_STRAIN_USER_TOPIC_REPLY;

        public String get_NEW_STRAIN_USER_TOPIC(){ return NEW_STRAIN_USER_TOPIC; }
        public String get_NEW_STRAIN_USER_TOPIC_REPLY(){ return NEW_STRAIN_USER_TOPIC_REPLY; }
    }



}
