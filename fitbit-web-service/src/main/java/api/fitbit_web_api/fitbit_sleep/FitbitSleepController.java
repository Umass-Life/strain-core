package api.fitbit_web_api.fitbit_sleep;

import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_sleep.sleep_time_series.SleepTimeSerie;
import api.fitbit_web_api.fitbit_sleep.sleep_time_series.SleepTimeSerieService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import util.ColorLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Controller
@RequestMapping("/sleep")
public class FitbitSleepController {

    private static final Logger logger = Logger.getLogger(FitbitSleepController.class.getName());
    private static final ColorLogger colorLog = new ColorLogger(logger);

    @Autowired
    FitbitSleepService fitbitSleepService;
    @Autowired
    FitbitUserService fitbitUserService;
    @Autowired
    SleepTimeSerieService sleepTimeSerieService;
    @Autowired
    FitbitSleepAPIService fitbitSleepAPIService;

    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public ResponseEntity<Map> fetchAll(){
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put(FitbitSleep.PLURAL, fitbitSleepService.list());
        return ResponseEntity.ok(responseMap);
    }

    @RequestMapping(value = "/query")
    public ResponseEntity<Map> query(@RequestParam(value="fid") String fitbitId,
                                     @RequestParam(value="fuid") Long fitbitUserId){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            return ResponseEntity.ok(responseMap);
        } catch(Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    @RequestMapping(value = "/fetch-api")
    public ResponseEntity<Map> fetchAPI(@RequestParam(value="fid",required=false) String fid,
                                        @RequestParam(value="from",required=false) String from,
                                        @RequestParam(value="to",required=false) String to,
                                        @RequestParam(value="all",required=false) Boolean fetchAll){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser fitbitUser = fitbitUserService.getByFitbitId(fid).orElseThrow(
                    () -> new IllegalArgumentException("cannot find fitbitId = " + fid)
            );

            JsonNode node = fitbitSleepAPIService.fetchSleepFromFitbit(fitbitUser, from, to);
            responseMap.put("payload", node);

            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    @RequestMapping(value = "/fetch-api-save")
    public ResponseEntity<Map> fetchAPISave(@RequestParam(value="fid",required=false) String fid,
                                        @RequestParam(value="from",required=false) String from,
                                        @RequestParam(value="to",required=false) String to,
                                        @RequestParam(value="all",required=false) Boolean fetchAll){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser fitbitUser = fitbitUserService.getByFitbitId(fid).orElseThrow(
                    () -> new IllegalArgumentException("cannot find fitbitId = " + fid)
            );

            Iterable<FitbitSleep> sleepData = fitbitSleepAPIService.fetchAndCreateBulk(fitbitUser, from, to);
            responseMap.put(FitbitSleep.PLURAL, sleepData);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    @RequestMapping(value="/serie", method = RequestMethod.GET)
    public ResponseEntity<Map> listTimeserie(){
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put(SleepTimeSerie.PLURAL, sleepTimeSerieService.list());
        return ResponseEntity.ok(responseMap);
    }

    @RequestMapping(value="/{id}/serie", method = RequestMethod.GET)
    public ResponseEntity<Map> listTimeserieBySleepId(@PathVariable("id") Long id){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Iterable<SleepTimeSerie> timeserie = sleepTimeSerieService.listBySleep(id);
            responseMap.put(SleepTimeSerie.PLURAL, timeserie);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }
}


/*
* Sample JSON


*
* */
