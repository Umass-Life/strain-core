package api.fitbit_web_api;

import api.fitbit_web_api.fitbit_activity.aggregate.AggregateActivityService;
import util.ColorLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping("/fitbit-api/")
public class FitbitAPIController {
    Logger log = Logger.getLogger(FitbitAPIController.class.getName());
    ColorLogger colorLog = new ColorLogger(log);

    @Autowired
    AggregateActivityService fitbitAPIservice;

    @RequestMapping(value = "/heart", method = RequestMethod.GET)
    public ResponseEntity<Map> getHeartRate(){
        Map<String, Object> responseJson = new HashMap<>();
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value ="", method = RequestMethod.GET)
    public ResponseEntity<Map> dummy(){
        Map<String, String> json = new HashMap<>();
        json.put("test", "ok");
        return ResponseEntity.ok(json);
    }

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }


    @Value("${hello}")
    private String hello;

    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public ResponseEntity<String> config(){
        return ResponseEntity.ok(hello);
    }


}

