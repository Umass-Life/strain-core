package api;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.ConfigServerApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.logging.Logger;

@SpringBootApplication
@EnableConfigServer
@Controller
public class ConfigServiceApplication implements ApplicationRunner {
    Logger log = Logger.getLogger(ConfigServiceApplication.class.getName());
    public static void main(String[] args){
        SpringApplication.run(ConfigServiceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args){
        log.info(String.format("\n%s running...", ConfigServerApplication.class.getName()));
    }

    @RequestMapping(value="ping", method= RequestMethod.GET)
    public ResponseEntity ping(){
        return ResponseEntity.ok("hello from " + ConfigServerApplication.class.getName());
    }

}
