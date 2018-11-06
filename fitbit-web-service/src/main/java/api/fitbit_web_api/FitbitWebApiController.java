package api.fitbit_web_api;

import api.data_ingestion.fitbit_batch.BatchProcessingService;
import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_activity.aggregate.AggregateActivityService;
import api.fitbit_web_api.fitbit_activity.intraday.IntradayActivityService;
import api.fitbit_web_api.fitbit_heartrate.FitbitHeartrateAPIService;
import api.fitbit_web_api.fitbit_heartrate.FitbitHeartrateService;
import api.fitbit_web_api.fitbit_sleep.FitbitSleepAPIService;
import api.fitbit_web_api.fitbit_sleep.FitbitSleepController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import util.ColorLogger;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static api.fitbit_account.fitbit_auth.FitbitAuthenticationService.toRequestDateFormat;

@Controller
@RequestMapping(value="/web-api")
public class FitbitWebApiController {
    private final Logger logger = Logger.getLogger(FitbitWebApiController.class.getSimpleName());
    private final ColorLogger colorLog = new ColorLogger(logger);

    @Autowired
    private FitbitUserService fitbitUserService;

    @Autowired
    private BatchProcessingService batchProcessingService;


    @RequestMapping(value = "/fetch-batch", method = RequestMethod.GET)
    public ResponseEntity<Map> fetchAllInBatch(@RequestParam(value="fid", required=false) String fid,
                                          @RequestParam(value="all", required=false) Boolean includeAll){
        ExecutorService threadPool =  Executors.newFixedThreadPool(10);
        FitbitUser fitbitUser = null;
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Optional<FitbitUser> fitbitUserOpt = fitbitUserService.getByFitbitId(fid);
            if (fitbitUserOpt.isPresent()){
                fitbitUser = fitbitUserOpt.get();
                batchProcessingService.ingest(fitbitUser);
            } else {
                batchProcessingService.ingest();
            }
            responseMap.put("FitbitUser", fitbitUser);
            responseMap.put("includeAll", includeAll);
        } catch(Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
        return ResponseEntity.ok(responseMap);
    }


}
