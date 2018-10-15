package api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.ConfigServerApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.InetAddress;
import java.util.logging.Logger;

@SpringBootApplication
@EnableConfigServer
@Controller
public class ConfigServiceApplication implements ApplicationRunner {
    Logger log = Logger.getLogger(ConfigServiceApplication.class.getName());
    public static void main(String[] args){
        SpringApplication.run(ConfigServiceApplication.class, args);
    }

    @Autowired
    private Environment env;

    @Override
    public void run(ApplicationArguments args){

        log.info(String.format("\n%s running...", ConfigServerApplication.class.getName()));
        log.info(env.getProperty("server.port"));
        log.info(env.getProperty("spring.cloud.config.server.git.uri"));
        try {
            log.info("Inet: " + InetAddress.getLocalHost().getHostName());
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @RequestMapping(value="/ping", method= RequestMethod.GET)
    public ResponseEntity ping(){
        return ResponseEntity.ok("hello from " + ConfigServerApplication.class.getName());
    }

}
