package api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.logging.Logger;

@SpringBootApplication
@EnableEurekaServer
@Controller

public class DiscoveryServiceApplication implements ApplicationListener<ServletWebServerInitializedEvent> {

    Logger log = Logger.getLogger(DiscoveryServiceApplication.class.getName());
    @Autowired
    private Environment env;

    public static void main(String[] args){
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent e){
        log.info("\n-------------\nDiscovery Server started at: " + e.getWebServer().getPort());
        log.info("eureka.instance.hostname: " + env.getProperty("eureka.instance.hostname"));
        log.info("\n" + env.getProperty("spring.cloud.config.uri"));

    }

    @RequestMapping(value="/ping", method= RequestMethod.GET)
    public ResponseEntity ping(){

        return ResponseEntity.ok("hello from " + DiscoveryServiceApplication.class.getName());
    }

}
