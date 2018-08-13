package api.fitbit_web_api.fitbit_sleep;

import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_sleep.sleep_time_series.SleepTimeSerie;
import api.fitbit_web_api.fitbit_sleep.sleep_time_series.SleepTimeSerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import util.ColorLogger;
import util.EntityHelper;

import java.util.HashMap;
import java.util.List;
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
    public ResponseEntity<Map> fetchAll(@RequestParam(value="id", required=false) Long userId,
                                        @RequestParam(value="fid",required=false) String fitbitId,
                                        @RequestParam(value="r", required=false) String r,
                                        @RequestParam(value="from", required=false) String fromDate,
                                        @RequestParam(value="to", required=false) String toDate){
        Map<String, Object> responseJson = new HashMap<>();
        colorLog.info("userId=%s fid=%s resourcePath=%s, fromDate=%s, toDate=%s", userId, fitbitId, r, fromDate, toDate);
        try {
            FitbitUser fitbitUser = getFitbitUser(fitbitId, userId);
            Map<String, Object> responseMap = new HashMap<>();
            Iterable<FitbitSleep> data = fitbitSleepService.list(fitbitUser.getId(), fromDate ,toDate);
            //testing
            List<FitbitSleep> l = EntityHelper.iterableToList(data);

            responseMap.put(FitbitSleep.PLURAL, data);
            return ResponseEntity.ok(responseMap);

        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map> getById(@PathVariable("id") Long id){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitSleep fitbitSleep = fitbitSleepService.getById(id)
                    .orElseThrow(() -> new IllegalArgumentException("cannot find FitbitSleep: " + id));
            responseMap.put(FitbitSleep.SINGULAR, fitbitSleep);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    @RequestMapping(value = "/query")
    public ResponseEntity<Map> query(@RequestParam(value="fid") String fitbitId,
                                     @RequestParam(value="fuid") Long fitbitUserId){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser fitbitUser = getFitbitUser(fitbitId, fitbitUserId);
            Iterable<FitbitSleep> sleepData = fitbitSleepService.listByFitbitUser(fitbitUser.getId());
            responseMap.put(FitbitSleep.PLURAL, sleepData);
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
    public ResponseEntity<Map> fetchAPI(@RequestParam(value="id", required=false) Long userId,
                                        @RequestParam(value="fid",required=false) String fid,
                                        @RequestParam(value="save",required=false,defaultValue = "false") Boolean save,
                                        @RequestParam(value="from", required=false) String from,
                                        @RequestParam(value="to", required=false) String to){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser fitbitUser = getFitbitUser(fid, userId);
            responseMap = fitbitSleepAPIService.fetchAndSave(fitbitUser, from, to ,save);

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

    @RequestMapping(value="/{sleep_id}/serie", method = RequestMethod.GET)
    public ResponseEntity<Map> listTimeserieBySleepId(@PathVariable("sleep_id") Long id){

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

    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    public ResponseEntity<Map> getFirstDataPoint(@RequestParam(value="id", required=false) Long userId,
                                                 @RequestParam(value="fid",required=false) String fid){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser fitbitUser = getFitbitUser(fid, userId);
            Optional<FitbitSleep> sleep = fitbitSleepService.findLatest(fitbitUser.getId());
            responseMap.put(FitbitSleep.SINGULAR, sleep);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    private FitbitUser getFitbitUser(String fitbitId, Long userId) throws IllegalArgumentException{
        FitbitUser fitbitUser = null;
        if (userId!=null){
            fitbitUser = fitbitUserService.getById(userId).orElseThrow(
                    () -> new IllegalArgumentException("Unable to find FitbitUser id = " + userId)
            );
        } else if (fitbitId != null){
            fitbitUser = fitbitUserService.getByFitbitId(fitbitId).orElseThrow(
                    () -> new IllegalArgumentException("UNable to find FitbitUser fitbitId = " + fitbitId)
            );
        }

        if (fitbitUser == null){
            throw new IllegalArgumentException("No Fitbit user information provided");
        }
        return fitbitUser;
    }
}



/*
* Sample JSON


*
* */
