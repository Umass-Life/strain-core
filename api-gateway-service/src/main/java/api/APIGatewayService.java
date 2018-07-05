package api;


import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.logging.Logger;

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@Controller
public class APIGatewayService implements ApplicationRunner {
    Logger log = Logger.getLogger(APIGatewayService.class.getName());
    public static void main(String[] args){
        SpringApplication.run(APIGatewayService.class, args);
    }

    @Override
    public void run(ApplicationArguments args){
        log.info(String.format("\n%s running...", APIGatewayService.class.getName()));
    }

    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public ResponseEntity notFound(){
        return ResponseEntity.notFound().build();
    }

}
