package api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//@Configuration
//@EnableWebMvc
//public class WebConfig extends WebMvcConfigurerAdapter {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**");
//    }
//}
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    private static String publicDomain = "54.86.8.246";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("????:  " + String.format("Domain: http://%s:3000", publicDomain));
        registry.addMapping("/**")
                .allowedOrigins(String.format("http://%s:3000", publicDomain))
                .allowedMethods("POST", "GET", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type",
                        "Access-Control-Allow-Origin",
                        "Access-Control-Allow-Headers",
                        "Access-Control-Allow-Credentials",
                        "withCredentials",
                        "Authorization",
                        "X-Requested-With",
                        "requestId",
                        "Correlation-Id",
                        "Set-Cookie");
    }
}
