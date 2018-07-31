package api;

import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_profile.FitbitProfile;
import api.fitbit_web_api.fitbit_heartrate.FitbitHeartrate;
import api.fitbit_web_api.fitbit_heartrate.FitbitHeartrateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import util.ColorLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@EnableDiscoveryClient
public class FitbitWebServiceApplication implements ApplicationRunner {
	static final Logger log = Logger.getLogger(FitbitWebServiceApplication.class.getName());
	static final ColorLogger colorLogger = new ColorLogger(log);

	@Autowired
	private ApplicationConfig config;

	@Autowired
	FitbitAuthenticationService service;

	@Component
	public static class ApplicationConfig {
		public String port;

		@Autowired
		public ApplicationConfig(@Value("${server.port}") String port){
			this.port = port;
		}
	}

	@Override
	public void run(ApplicationArguments args){
		colorLogger.info("%s started at port: %s", FitbitWebServiceApplication.class.getName(), config.port);
		new FitbitProfile(1L);
        System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");


        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.impl.conn", "DEBUG");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.impl.client", "DEBUG");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.client", "DEBUG");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "DEBUG");

//        java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(Level.ALL);
//        java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(Level.ALL);
//        java.util.logging.Logger.getLogger("org.apache.http").setLevel(Level.ALL);

	}



	public static void main(String[] args) {
		SpringApplication.run(FitbitWebServiceApplication.class, args);

	}
}
