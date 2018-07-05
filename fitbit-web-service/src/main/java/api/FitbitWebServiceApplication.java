package api;

import api.Utilities.ColorLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@SpringBootApplication
@EnableDiscoveryClient
public class FitbitWebServiceApplication implements ApplicationRunner {
	static final Logger log = Logger.getLogger(FitbitWebServiceApplication.class.getName());
	static final ColorLogger colorLogger = new ColorLogger(log);

	@Autowired
	private ApplicationConfig config;

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
	}

	public static void main(String[] args) {
		SpringApplication.run(FitbitWebServiceApplication.class, args);

	}
}
