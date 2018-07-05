package api.FitbitAPI.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import api.FitbitAPI.Constants.ActivitiesResourcePath;
import api.FitbitAPI.Services.FitbitAPIService;
import api.Utilities.ColorLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping("/fitbit-api/")
public class FitbitAPIController {
    Logger log = Logger.getLogger(FitbitAPIController.class.getName());
    ColorLogger colorLog = new ColorLogger(log);

    @Autowired
    FitbitAPIService fitbitAPIservice;

    @RequestMapping(value = "/authorize", method=RequestMethod.GET)
    public ResponseEntity authorizeFitbit() throws URISyntaxException{
//        Map responseJson = new HashMap<String, Object>();
        String authorization_uri = fitbitAPIservice.authorize();
////        responseJson.put("url", authorization_uri);
////        return ResponseEntity.ok(responseJson);
//        response.setHeader("Location", "http://www.google.com");

        URI uri = new URI(authorization_uri);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    @RequestMapping(value="/callback", method= RequestMethod.GET)
    public ResponseEntity<Map> api_callback(HttpServletRequest req){
        Map reqm = req.getParameterMap();
        String[] code_value= (String[]) reqm.get("code");
        String tempAccessCode = code_value[0];
        Map<String, String> json = new HashMap<>();
        fitbitAPIservice.requestAccessToken(tempAccessCode);
        return ResponseEntity.ok(reqm);
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ResponseEntity<Map> getProfile(){
        Map<String, Object> responseJson = new HashMap<>();
        JsonNode node = fitbitAPIservice.fetchProfile();
        responseJson.put("load", node);
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "/activities", method = RequestMethod.GET)
    public ResponseEntity<Map> getActivities(@RequestParam(value="resourcePath", required=false) String resourcePath,
                                             @RequestParam(value="fromDate", required=true) String fromDate,
                                             @RequestParam(value="toDate", required=false) String toDate){
        Map<String, Object> responseJson = new HashMap<>();
        colorLog.info("resourcePath=%s, fromDate=%s, toDate=%s", resourcePath, fromDate, toDate);

        try {
            LocalDateTime fromLocalDate = parseTimeParam(fromDate);
            LocalDateTime toLocalDate = null;
            if (toDate != null) {
                toLocalDate = parseTimeParam(toDate);
            }
            ActivitiesResourcePath acp = null;
            if (resourcePath != null){
                acp = ActivitiesResourcePath.valueOf(resourcePath);
            }
            JsonNode node = null;
            if (acp != null){
                node = fitbitAPIservice.fetchActivities(acp, fromLocalDate, toLocalDate);
            } else {
                node =  fitbitAPIservice.fetchActivities(fromLocalDate);
            }
            responseJson.put("load", node);
        } catch (Exception e){
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.status(400).body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "/heartrate", method = RequestMethod.GET)
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

    private LocalDateTime parseTimeParam(String date){
        // stub parse simple yyyy-MM-dd
        String[] spl = date.split("-");
        return LocalDateTime.of(new Integer(spl[0]),
                                new Integer(spl[1]),
                                new Integer(spl[2]), 0, 0);
    }

}

/// https://www.fitbit.com/oauth2/authorize?response_type=code&client_id=22CTFZ&scope=activity%20nutrition%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social%20weight

/**
 *
 POST https://api.fitbit.com/oauth2/token
 Authorization: Basic MjJDVEZaOjI1NzdjMDdkOGFmOGM3MTVhMmExOTBmYjI0MGViZjNm=
 Content-Type: application/x-www-form-urlencoded

 client_id=22CTFZ&grant_type=authorization_code&redirect_uri=http%3A%2F%2Fexample.com%2Ffitbit_auth&code=4f4e03e98a053e6812010fee04f643237cf1d014


 GET https://api.fitbit.com/1/user/-/profile.json
 Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE0MzAzNDM3MzUsInNjb3BlcyI6Indwcm8gd2xvYyB3bnV0IHdzbGUgd3NldCB3aHIgd3dlaSB3YWN0IHdzb2MiLCJzdWIiOiJBQkNERUYiLCJhdWQiOiJJSktMTU4iLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJpYXQiOjE0MzAzNDAxMzV9.z0VHrIEzjsBnjiNMBey6wtu26yHTnSWz_qlqoEpUlpc
 *
 * */