package api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.logging.Logger;

@SpringBootApplication
@EnableDiscoveryClient
@Controller
public class AccountServiceApplication implements ApplicationRunner {

    Logger log = Logger.getLogger(AccountServiceApplication.class.getName());

    @Autowired
    KafkaTemplate<Integer, String> template;

    public static void main(String[] args){
        SpringApplication.run(AccountServiceApplication.class, args);

    }

    @Override
    public void run(ApplicationArguments args){
        log.info(String.format("\n%s running...", AccountServiceApplication.class.getName()));
        template.send(KafkaSandbox.NEW_KAFKA_USER_TOPIC, 33, "hello");
        template.flush();
        try {
            log.info("\n----> sent");
            KafkaSandbox.Listener.latch.await();
            log.info("counted.");
        } catch (Exception e){

        }
    }

    @RequestMapping(value="/ping", method = RequestMethod.GET)
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }


}
