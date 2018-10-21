package api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.logging.Logger;

@SpringBootApplication
//@EnableDiscoveryClient
@EnableZuulProxy
@Controller
public class APIGatewayService implements ApplicationRunner {
    Logger log = Logger.getLogger(APIGatewayService.class.getName());
    @Autowired
    private Environment env;

    public static void main(String[] args){
        SpringApplication.run(APIGatewayService.class, args);
    }

    @Override
    public void run(ApplicationArguments args){
        log.info(String.format("\n%s running...", APIGatewayService.class.getName()));
        log.info(String.format("\n%s PORT", env.getProperty("server.port")));
        log.info("\neureka.client.serviceUrl.defaultZone: " + env.getProperty("eureka.client.serviceUrl.defaultZone"));
    }

    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public ResponseEntity notFound(){
        return ResponseEntity.notFound().build();
    }

}
