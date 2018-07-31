package api.fitbit_web_api.fitbit_sleep;

import api.FitbitConstantEnvironment;
import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_profile.FitbitProfile;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_sleep.sleep_time_series.SleepTimeSerie;
import api.fitbit_web_api.fitbit_sleep.sleep_time_series.SleepTimeSerieService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.Validation.checkNotNull;

@Service
public class FitbitSleepAPIService {
    @Autowired
    private FitbitSleepService fitbitSleepService;

    @Autowired
    private SleepTimeSerieService sleepTimeSerieService;

    @Autowired
    private FitbitProfileService fitbitProfileService;

    @Autowired
    private FitbitAuthenticationService authenticationService;

    @Autowired
    private FitbitConstantEnvironment constantEnvironment;

    public Iterable<FitbitSleep> fetchAndCreateBulk(FitbitUser fitbitUser, String from, String to){
        Map<String, Object> map = new HashMap<>();
        checkNotNull(fitbitUser, "fitbitUser cannto be null");
        JsonNode root = fetchSleepFromFitbit(fitbitUser, from, to);
        ArrayNode sleepData = (ArrayNode) root.get("sleep");
        List<FitbitSleep> sleepList = new ArrayList<>();
        for(int i = 0; i < sleepData.size(); i++){
            FitbitSleep sleep = fitbitSleepService.create(fitbitUser.getId(), sleepData.get(i));
            sleepList.add(sleep);
            JsonNode sleepTimeserieJson = sleepData.get(i).get("levels").get("data");
            Iterable<SleepTimeSerie> sleepTimeSeries = sleepTimeSerieService.createBulk(sleep.getId(), (ArrayNode) sleepTimeserieJson);
        }

        return sleepList;
    }

    public JsonNode fetchSleepFromFitbit(FitbitUser fitbitUser, String from, String to){
        checkNotNull(fitbitUser, "fitbitUser cannto be null");
        String fitbitId = fitbitUser.getFitbitId();

        if (from == null){
            FitbitProfile profile = fitbitProfileService.getByFitbitUserId(fitbitUser.getId()).orElseThrow(
                    () -> new IllegalStateException("FitbitUser "+ fitbitId + " has no profile")
            );
            from = profile.getMemberSince();
        }

        if (to == null){
            to = FitbitAuthenticationService.toRequestDateFormat(LocalDateTime.now());
        }

        String url = buildSleepQuery(fitbitId, from ,to);
        JsonNode node = authenticationService.authorizedRequest(fitbitUser, url);
        return node;
    }


    /**
     *
     *  "dateOfSleep": "2018-07-19",
     "duration": 22740000,
     "efficiency": 91,
     "endTime": "2018-07-19T09:07:30.000",
     "infoCode": 0,
     "levels": {
     data
     * */
    public void fetchAndSave(FitbitUser fitbitUser){
        JsonNode node = fetchAllSleepFromFitbit(fitbitUser);

    }

    public JsonNode fetchAllSleepFromFitbit(FitbitUser fitbitUser){
        checkNotNull(fitbitUser);
        JsonNode node = fetchSleepFromFitbit(fitbitUser, null, null);
        return node;
    }

    /*
    * Utilities
    * */
    public String buildSleepQuery(String fitbitId, LocalDateTime from, LocalDateTime to){
        String fromDate = null;
        String toDate = null;
        if(from!=null) fromDate = FitbitAuthenticationService.toRequestDateFormat(from);
        if (to!=null) toDate = FitbitAuthenticationService.toRequestDateFormat(to);
        return buildSleepQuery(fitbitId, fromDate, toDate);
    }


    public String buildSleepQuery(String fitbitId, String from, String to){
        return String.format("%s/user/%s/sleep/date/%s/%s.json",
                constantEnvironment.getSleepDomain(),
                fitbitId, from, to);
    }

}
