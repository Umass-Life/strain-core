package api.fitbit_web_api.fitbit_activity;

import api.FitbitConstantEnvironment;
import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_profile.FitbitProfile;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import api.fitbit_account.fitbit_user.FitbitUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import util.ColorLogger;
import org.springframework.stereotype.Service;
import util.EntityHelper;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static api.fitbit_account.fitbit_auth.FitbitAuthenticationService.parseLongTimeParam;
import static api.fitbit_account.fitbit_auth.FitbitAuthenticationService.parseTimeParam;
import static api.fitbit_account.fitbit_auth.FitbitAuthenticationService.toRequestDateFormat;
import static util.Validation.checkNotNull;

@Service
public class FitbitActivityService {
    @Autowired
    private FitbitConstantEnvironment fitbitConstantEnvironment;

    @Autowired FitbitActivityRepository repository;

    @Autowired
    FitbitProfileService fitbitProfileService;

    @Autowired
    private FitbitAuthenticationService authenticationService;

    static Logger log = Logger.getLogger(FitbitActivityService.class.getName());
    static ColorLogger colorLog = new ColorLogger(log);

    public String FITBIT_AUTH_URI;
    public String FITBIT_ACCESS_TOKEN_URI;
    public String CLIENT_ID;
    public String CLIENT_SECRET;
    public String RESOURCE_URL;
    private Set<ActivitiesResource> finegrainSet = new HashSet<ActivitiesResource>(
            Arrays.asList(new ActivitiesResource[]{ActivitiesResource.steps,
                                                    ActivitiesResource.calories,
                                                    ActivitiesResource.distance,
                                                    ActivitiesResource.floors,
                                                    ActivitiesResource.elevation}));
    private final String DATETIME = "dateTime";
    private final String VALUE = "value";
    private final String INTRA_TIME = "time";
    private final String DATASET = "dataset";

    @PostConstruct
    public void init(){
        FITBIT_AUTH_URI = fitbitConstantEnvironment.getUserAuthorizationUri();
        FITBIT_ACCESS_TOKEN_URI = fitbitConstantEnvironment.getAccessTokenUri();
        CLIENT_ID = fitbitConstantEnvironment.getClientId();
        CLIENT_SECRET = fitbitConstantEnvironment.getClientSecret();
        RESOURCE_URL = fitbitConstantEnvironment.getFitbitAPIDomain();
    }

    public Iterable<FitbitActivity> list(){
        return repository.findAll();
    }

    public Iterable<FitbitActivity> listByResource(ActivitiesResource r){
        return repository.getByType(r);
    }

    private String getActivitiesKey(ActivitiesResource r){
        return "activities-"+r.toString();
    }

    private String getIntraDayActivitiesKey(ActivitiesResource r){
        return String.format("activities-%s-intraday", r.toString());
    }

    private String combineDateTime(String date, String time){
        return String.format("%sT%s", date, time);
    }

    public List<FitbitActivity> jsonToPOJOInBulk(Long fitbitUserId, ActivitiesResource r, JsonNode node){
        if (node.isArray()){
            List<List<FitbitActivity>> acts = new ArrayList<>();
            ArrayNode nodes = (ArrayNode)node;
            for(int i = 0; i < nodes.size(); i++){
                acts.add(_jsonToPOJOInBulk(fitbitUserId, r, nodes.get(i)));
            }
            return acts.stream().flatMap(List::stream).collect(Collectors.toList());
        } else {
            return _jsonToPOJOInBulk(fitbitUserId, r, node);
        }
    }

    public Iterable<FitbitActivity> fetchAndSave(FitbitUser fitbitUser, ActivitiesResource r, String from, String to) throws IllegalAccessException{
        JsonNode json = fetchActivities(r, fitbitUser, from, to);
        List<FitbitActivity> activities = jsonToPOJOInBulk(fitbitUser.getId(),r, json);
        Iterable<FitbitActivity> savedActivities = repository.saveAll(activities);
        return savedActivities;
    }

    public List<FitbitActivity> _jsonToPOJOInBulk(Long fitbitUserId, ActivitiesResource r, JsonNode node){
        String ACTIVITIES_KEY = getActivitiesKey(r);
        String ACTIVITIES_INTRA_KEY = getIntraDayActivitiesKey(r);

        List<FitbitActivity> activities = new ArrayList<>();
        if (!node.has(ACTIVITIES_KEY)){
            throw new IllegalArgumentException("incorrect acivities json format");
        }
        ArrayNode actJsons = (ArrayNode) node.get(ACTIVITIES_KEY);
        //{"activities-steps":[{"dateTime":"2018-06-23","value":"393"}],"activities-steps-intraday":{"dataset":[{"time":"00:00:00","value":0}...]}
        if (finegrainSet.contains(r) && node.has(ACTIVITIES_INTRA_KEY)){
            String todayString = actJsons.get(0).get(DATETIME).asText();
            JsonNode intraNode = node.get(ACTIVITIES_INTRA_KEY);
            ArrayNode node_dataset = (ArrayNode) intraNode.get(DATASET);
            for(int i = 0; i < node_dataset.size(); i++){
                JsonNode node_i = node_dataset.get(i);
                String timeText = node_i.get(INTRA_TIME).asText();
                Double value = node_i.get(VALUE).asDouble();
                String dateTimeString = combineDateTime(todayString, timeText);
                LocalDateTime dateTime=  parseLongTimeParam(dateTimeString);
                Long dateTimeEpoch = EntityHelper.toEpochMilli(dateTime);
                FitbitActivity act = new FitbitActivity(fitbitUserId, r, dateTimeEpoch, value);
                activities.add(act);
            }
        } else {
            for(int i = 0; i < actJsons.size(); i++){
                JsonNode node_i = actJsons.get(i);
                String dateTimeText = node_i.get(DATETIME).asText();
                Double value = node_i.get(VALUE).asDouble();
                LocalDateTime dateTime=  parseTimeParam(dateTimeText);
                Long dateTimeEpoch = EntityHelper.toEpochMilli(dateTime);
                FitbitActivity act = new FitbitActivity(fitbitUserId, r, dateTimeEpoch, value);
                activities.add(act);
            }
        }
        return activities;
    }

    public String buildProfileRequest(FitbitUser fitbitUser){
        String profileRequestUrl = String.format("%s/user/%s/profile.json", RESOURCE_URL, fitbitUser.getFitbitId());
        return profileRequestUrl;
    }

    public String buildActivitiesSummaryRequest(FitbitUser fitbitUser, String date){
        return String.format("%s/user/%s/activities/date/%s.json", RESOURCE_URL, fitbitUser.getFitbitId(), date);
    }

    public String buildActivitiesRequest(FitbitUser fitbitUser, ActivitiesResource resourceType,
                                                  String dateFrom, String dateTo){
        return String.format("%s/user/%s/activities/%s/date/%s/%s.json", RESOURCE_URL, fitbitUser.getFitbitId(),
                resourceType.toString(), dateFrom, dateTo);
    }

    public String buildFinegrainActivities(FitbitUser fitbitUser, ActivitiesResource resourceType, String date){
        return String.format("%s/user/%s/activities/%s/date/%s/%s/1min.json", RESOURCE_URL, fitbitUser.getFitbitId(),
                resourceType.toString(), date, date);
    }

    public String buildActivitiesTrackerTimeSeriesRequest(FitbitUser fitbitUser, ActivitiesResource resourceType,
                                                   String dateFrom, String dateTo){
        return String.format("%s/user/%s/activities/tracker/%s/date/%s/%s.json", RESOURCE_URL, fitbitUser.getFitbitId(),
                resourceType.toString(), dateFrom, dateTo);
    }

    public JsonNode fetchActivities(ActivitiesResource acp, FitbitUser fitbitUser, LocalDateTime from,
                                    LocalDateTime to) throws IllegalAccessException  {
        String fromString = null;
        String toString = null;
        if (from!=null) fromString = toRequestDateFormat(from);
        if (to!=null) toString = toRequestDateFormat(to);
        return fetchActivities(acp, fitbitUser, fromString, toString);
    }

    public JsonNode fetchFinegrainActivities(ActivitiesResource acp, FitbitUser fitbitUser, String date) throws IllegalAccessException  {
        if (!finegrainSet.contains(acp))
            throw new IllegalAccessException("No finegrain dataset exists for resource type: " + acp);

        return fetchActivities(acp, fitbitUser, date, date);
    }

    public JsonNode fetchActivities(ActivitiesResource acp, FitbitUser fitbitUser, String from, String to) throws IllegalAccessException  {
        checkNotNull(fitbitUser, "fitbitUser cannot be null");
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

        ObjectMapper objectMapper = new ObjectMapper();
        String userId = fitbitUser.getFitbitId();
        String access_token = fitbitUser.getAccessToken();
        String url = null;
        if (finegrainSet.contains(acp)){
            url = buildFinegrainActivities(fitbitUser, acp, from);
            LocalDateTime cur = parseTimeParam(from);
            LocalDateTime toDate = parseTimeParam(to);
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode nodes = mapper.createArrayNode();
            while(cur.compareTo(toDate) != 1){
                JsonNode node= authenticationService.authorizedRequest(fitbitUser, url);
                nodes.add(node);
                cur = cur.plusDays(1L);
            }
            return nodes;

        } else {
            url = buildActivitiesRequest(fitbitUser, acp, from, to);
            return authenticationService.authorizedRequest(fitbitUser, url);
        }
    }

    public JsonNode fetchActivities(FitbitUser fitbitUser, LocalDateTime date) throws IllegalAccessException{
        String dateString = toRequestDateFormat(date);
        return fetchSummarizedActivities(fitbitUser, dateString);
    }

    // without ACP we fetch summary.
    public JsonNode fetchSummarizedActivities(FitbitUser fitbitUser, String date) throws IllegalAccessException {
        String access_token = fitbitUser.getAccessToken();

        colorLog.info("FETCHING ACTIVITY SUMMARY: " + date);
        JsonNode node = null;
        ObjectMapper objectMapper = new ObjectMapper();

        String url = buildActivitiesSummaryRequest(fitbitUser, date);
        return authenticationService.authorizedRequest(fitbitUser, url);
    }

    public JsonNode fetchTrackerActivities(ActivitiesResource acp, FitbitUser fitbitUser, LocalDateTime from, LocalDateTime to) throws IllegalAccessException  {
        String fromString = toRequestDateFormat(from);
        String toString = toRequestDateFormat(to);
        return fetchTrackerActivities(acp, fitbitUser, fromString, toString);
    }

    public JsonNode fetchTrackerActivities(ActivitiesResource acp, FitbitUser fitbitUser, String from, String to) throws IllegalAccessException  {
        JsonNode node = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String userId = fitbitUser.getFitbitId();
        String access_token = fitbitUser.getAccessToken();
        String url = buildActivitiesTrackerTimeSeriesRequest(fitbitUser, acp, from, to);
       return authenticationService.authorizedRequest(fitbitUser, url);
    }


}

