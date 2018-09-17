package api.fitbit_web_api.fitbit_heartrate;

import api.FitbitConstantEnvironment;
import api.IFitbitQueryService;
import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_profile.FitbitProfile;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_heartrate.heartrate_zone.HeartrateZone;
import api.fitbit_web_api.fitbit_heartrate.heartrate_zone.HeartrateZoneService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.ColorLogger;
import util.EntityHelper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

import static api.fitbit_account.fitbit_auth.FitbitAuthenticationService.parseTimeParam;
import static util.Validation.checkNotNull;

@Service
public class FitbitHeartrateAPIService implements IFitbitQueryService {
    private static final Logger logger = Logger.getLogger(FitbitHeartrateAPIService.class.getSimpleName());
    private static final ColorLogger colorLog = new ColorLogger(logger);

    @Autowired
    private FitbitHeartrateService heartrateService;

    @Autowired
    private HeartrateZoneService zoneService;

    @Autowired
    private FitbitConstantEnvironment constantEnvironment;

    @Autowired
    private FitbitProfileService profileService;

    @Autowired
    private FitbitUserService userService;

    @Autowired
    private FitbitAuthenticationService authenticationService;

    private final String ACTIVITIES_HEART = "activities-heart";
    private final String HR_VALUE = "value";
    private final String HR_ZONES = "heartRateZones";

    public Map<String, Object> fetchAndSave(FitbitUser fitbitUser, String from, String to,
                                            Boolean save) throws Exception {

        Map<String, Object> responseMap = new HashMap<>();

        if (save){
            Map<String, Object> hrData = fetchAndCreateBulk(fitbitUser, from, to);
            responseMap.put(FitbitHeartrate.PLURAL, hrData);
        } else {
            JsonNode node = fetchHeartrateFromFitbit(fitbitUser, from, to);
            responseMap.put("payload", node);
        }

        return responseMap;
    }


    public Map<String, Object> fetchAndCreateBulk(FitbitUser fitbitUser, String from, String to){
        Map<String, Object> map = new HashMap<>();
        checkNotNull(fitbitUser, "fitbitUser cannto be null");

        JsonNode root = fetchHeartrateFromFitbit(fitbitUser, from, to);
        if (!root.has(ACTIVITIES_HEART)) throw new IllegalArgumentException("wrong HeartActivity json format:\n"+ root);
        ArrayNode hrData = (ArrayNode) root.get(ACTIVITIES_HEART);
        List<FitbitHeartrate> hrList = new ArrayList<>();
        int cnt = 0;
        int total = hrData.size();
        for(int i = 0; i < hrData.size(); i++){
            try {
                FitbitHeartrate hr = heartrateService.create(fitbitUser.getId(), hrData.get(i));
                hrList.add(hr);
                JsonNode node_i = hrData.get(i);
                if (!node_i.has(HR_VALUE) && !node_i.get(HR_VALUE).has(HR_ZONES)){
                    throw new IllegalArgumentException("Incorrect Heart-rate json:\n" + node_i);
                }
                ArrayNode hrZoneJsonArray = (ArrayNode) node_i.get(HR_VALUE).get(HR_ZONES);
                Iterable<HeartrateZone> sleepTimeSeries = zoneService.createBulk(hr.getId(), (ArrayNode) hrZoneJsonArray);
                cnt+=1;
            } catch(Exception e){
                colorLog.severe(e.getMessage());
                // do not want to print errors for
            }
        }
        map.put("count", cnt);
        map.put("total", total);
        map.put("error", cnt-total);
        return map;
    }

    public JsonNode fetchHeartrateFromFitbit(FitbitUser fitbitUser, String from, String to){
        checkNotNull(fitbitUser, "fitbitUser cannot be null");
        String fitbitId = fitbitUser.getFitbitId();


        if (from == null){
            Optional<FitbitHeartrate> latestHr = heartrateService.findLatest(fitbitUser.getId());
            if (latestHr.isPresent()){

                Long fromTime = latestHr.get().getDateTime();
                from = FitbitAuthenticationService.toRequestDateFormat(EntityHelper.epochToDate(fromTime));
                colorLog.info("FETCH FROM LATEST: " + from);
            } else {
                from = FitbitAuthenticationService.toRequestDateFormat(FitbitAuthenticationService.getOldestPossibleTimeForRequest());
                colorLog.info("FETCH FROM BEGINNING: " + from);
            }
        }

        if (to == null){
            to = FitbitAuthenticationService.toRequestDateFormat(LocalDateTime.now());
        }
        colorLog.info("from=%s to=%s", from, to);
        FitbitAuthenticationService.validateRequestDates(from, to);

        String url = buildHeartrateRequestURI(fitbitId, from ,to);
        JsonNode node = authenticationService.authorizedRequest(fitbitUser, url);
        return node;
    }


    public String buildHeartrateRequestURI(String fitbitId, LocalDateTime from, LocalDateTime to){
        String fromDate = null;
        String toDate = null;
        if(from!=null) fromDate = FitbitAuthenticationService.toRequestDateFormat(from);
        if (to!=null) toDate = FitbitAuthenticationService.toRequestDateFormat(to);
        return buildHeartrateRequestURI(fitbitId, fromDate, toDate);
    }

    public String buildHeartrateRequestURI(String fitbitId, String from, String to){
        return String.format("%s/user/%s/activities/heart/date/%s/%s.json",
                constantEnvironment.getFitbitAPIDomain(),
                fitbitId, from, to);
    }

    //https://api.fitbit.com/1/user/6MYGYG/activities/heart/date/2018-07-07/1d/1sec.json"
    public String buildFinegrainHeartrateRequestURI(String fitbitId, String date){
        return String.format("%s/user/%s/activities/heart/date/%s/1d/1sec.json",
                constantEnvironment.getFitbitAPIDomain(),
                fitbitId, date);
    }

}
