package api;

import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_profile.FitbitProfile;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_activity.ActivityAPIService;
import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResource;
import api.fitbit_web_api.fitbit_heartrate.FitbitHeartrate;
import api.fitbit_web_api.fitbit_heartrate.FitbitHeartrateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import util.ColorLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;
import util.EntityHelper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@EnableDiscoveryClient
@Controller
public class FitbitWebServiceApplication implements ApplicationRunner {
	static final Logger log = Logger.getLogger(FitbitWebServiceApplication.class.getName());
	static final ColorLogger colorLogger = new ColorLogger(log);

	@Autowired
	private ApplicationConfig config;

	@Autowired
	FitbitAuthenticationService service;

	@Autowired
	ActivityAPIService apiService;

	@Autowired
	FitbitUserService userService;

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
		LocalDateTime d =  FitbitAuthenticationService.parseLongTimeParam("2018-08-16T08:15:00.000");
		LocalDateTime d2 =  FitbitAuthenticationService.parseLongTimeParam("2018-08-05T08:15:00.000");
		LocalDateTime d3 = FitbitAuthenticationService.parseLongTimeParam("2018-08-16T08:15:30");
		LocalDateTime d4 = FitbitAuthenticationService.parseLongTimeParam("2018-08-05T20:34:59");
		FitbitUser user = EntityHelper.iterableToList(userService.list()).get(0);
		String s= apiService.buildFinegrainActivitiesURI(user, ActivitiesResource.heart, d4, "1min");
		colorLogger.info(s);


	}

	@RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
	public ResponseEntity PREFLIGHT_BYPASS(){
		colorLogger.info("PREFLIGHT initiated----");
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public ResponseEntity test_post(@RequestBody Map<String, Object> body){
		System.out.println(body);
		return ResponseEntity.ok(body);

	}



	public static void main(String[] args) {
		SpringApplication.run(FitbitWebServiceApplication.class, args);

	}
}
