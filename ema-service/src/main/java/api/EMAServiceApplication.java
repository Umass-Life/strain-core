package api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import util.ColorLogger;

import java.util.Map;
import java.util.logging.Logger;

@SpringBootApplication
@EnableDiscoveryClient
@Controller
public class EMAServiceApplication implements ApplicationRunner {
	static final Logger log = Logger.getLogger(EMAServiceApplication.class.getName());
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
		colorLogger.info("%s started at port: %s", EMAServiceApplication.class.getName(), config.port);
	}

	@RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
	public ResponseEntity PREFLIGHT_BYPASS(){
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public ResponseEntity test_post(@RequestBody Map<String, Object> body){
		System.out.println(body);
		return ResponseEntity.ok(body);

	}



	public static void main(String[] args) {
		SpringApplication.run(EMAServiceApplication.class, args);

	}
}
