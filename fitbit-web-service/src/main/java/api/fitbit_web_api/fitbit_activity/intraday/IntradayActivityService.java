package api.fitbit_web_api.fitbit_activity.intraday;

import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_profile.FitbitProfile;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_activity.aggregate.AggregateActivity;
import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResource;
import api.fitbit_web_api.fitbit_activity.ActivityAPIService;
import api.fitbit_web_api.fitbit_activity.intraday.calories.CaloriesActivityService;
import api.fitbit_web_api.fitbit_activity.intraday.distance.DistanceActivityService;
import api.fitbit_web_api.fitbit_activity.intraday.elevation.ElevationActivityService;
import api.fitbit_web_api.fitbit_activity.intraday.floors.FloorsActivityService;
import api.fitbit_web_api.fitbit_activity.intraday.heart.HeartActivityService;
import api.fitbit_web_api.fitbit_activity.intraday.steps.StepsActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.ColorLogger;
import util.EntityHelper;
import util.StrainTimer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static api.fitbit_account.fitbit_auth.FitbitAuthenticationService.combineDateTime;
import static api.fitbit_account.fitbit_auth.FitbitAuthenticationService.parseLongTimeParam;
import static api.fitbit_account.fitbit_auth.FitbitAuthenticationService.parseTimeParam;
import static api.fitbit_web_api.fitbit_activity.ActivityAPIService.getActivitiesKey;
import static api.fitbit_web_api.fitbit_activity.ActivityAPIService.getIntraDayActivitiesKey;
import static util.Validation.checkNotNull;

@Service
public class IntradayActivityService {
    private static Logger logger = Logger.getLogger(IntradayActivityService.class.getSimpleName());
    private static ColorLogger colorLog = new ColorLogger(logger);

    @Autowired
    private StepsActivityService stepsService;
    @Autowired
    private FloorsActivityService floorsService;
    @Autowired
    private ElevationActivityService elevationService;
    @Autowired
    private CaloriesActivityService caloriesService;
    @Autowired
    private DistanceActivityService distanceService;
    @Autowired
    private HeartActivityService heartService;
    @Autowired
    private FitbitAuthenticationService authenticationService;
    @Autowired
    private ActivityAPIService activityAPIService;
    @Autowired
    private FitbitProfileService fitbitProfileService;

    private Map<ActivitiesResource, IIntradayActivityService> factory;
    private Set<ActivitiesResource> activitiesResources = new HashSet<>(
            Arrays.asList(ActivitiesResource.values())
    );

    private final String DATETIME = "dateTime";
    private final String VALUE = "value";
    private final String INTRA_TIME = "time";
    private final String DATASET = "dataset";

    public void initFactory(){
        if (this.factory==null || this.factory.isEmpty()){
            this.factory = new HashMap<>();
            this.factory.put(ActivitiesResource.calories, caloriesService);
            this.factory.put(ActivitiesResource.elevation, elevationService);
            this.factory.put(ActivitiesResource.distance, distanceService);
            this.factory.put(ActivitiesResource.floors, floorsService);
            this.factory.put(ActivitiesResource.steps, stepsService);
            this.factory.put(ActivitiesResource.steps, stepsService);
            this.factory.put(ActivitiesResource.heart, heartService);
        }
    }

    public Iterable<? extends AbstractIntradayActivity> list(ActivitiesResource r){
        initFactory();
        IIntradayActivityService service = getService(r);
        return service.list();
    }

    public Optional<? extends AbstractIntradayActivity> getById(ActivitiesResource r, Long id){
        initFactory();
        IIntradayActivityService service = getService(r);
        return service.getById(id);
    }

    public Set<ActivitiesResource> getActivitiesResources(){
        return this.activitiesResources;
    }

    public Iterable<? extends AbstractIntradayActivity> save(ActivitiesResource r, Iterable<? extends AbstractIntradayActivity> entities){
        IIntradayActivityService service = this.factory.get(r);
        return service.save(entities);
    }

    public IIntradayActivityService getService(ActivitiesResource r){
        initFactory();
        if (!this.factory.containsKey(r)){
            throw new IllegalArgumentException(String.format("activities resource %s is not intraday", r ));
        }
        return factory.get(r);
    }


    /***
     *
     * @param fitbitUser
     * @param r
     * @param from
     * @param to
     * @return
     * @throws IllegalAccessException
     */
    public Iterable<AbstractIntradayActivity> fetchAndSave(FitbitUser fitbitUser, ActivitiesResource r, String from, String to) throws IllegalAccessException{
        JsonNode json = fetchActivities(r, fitbitUser, from, to);
        IIntradayActivityService service = getService(r);
        Iterable<AbstractIntradayActivity> activities = jsonToPOJOInBulk(fitbitUser.getId(),r, json);
        colorLog.info("%s timeseries of size: %s", r, EntityHelper.iterableSize(activities));
        StrainTimer timer = new StrainTimer(colorLog);
        timer.start();
        Iterable<AbstractIntradayActivity> savedActivities = service.save(activities);
        timer.stop();
        return savedActivities;
    }

    public Map<String, Object> fetchAndSave(FitbitUser fitbitUser, Set<ActivitiesResource> resourcePaths,
                                            String from, String to, Boolean save) throws IllegalAccessException{
        Map responseMap = new HashMap<>();
        colorLog.info("%s %s %s %s", resourcePaths, from, to, save);
        for (ActivitiesResource r : resourcePaths){
            StrainTimer timer = new StrainTimer(colorLog);
            timer.start();
            Map<String, Object> aggregateInfoMap = new HashMap<>();
            try {
                Iterable<AbstractIntradayActivity> activities = null;
                if (save){
                    activities= fetchAndSave(fitbitUser, r, from, to);
                    int count = EntityHelper.iterableSize(activities);
                    aggregateInfoMap.put("count", count);
                } else {
                    JsonNode node = fetchActivities(r, fitbitUser, from, to);
                    aggregateInfoMap.put("payload", node);

                }

                aggregateInfoMap.put("success", true);
                responseMap.put(r, aggregateInfoMap);
            } catch(Exception e){
                e.printStackTrace();
                aggregateInfoMap = new HashMap<>();
                aggregateInfoMap.put("success", false);
                aggregateInfoMap.put("error", e.getMessage());
                responseMap.put(r, aggregateInfoMap);
            }
            timer.stop();
        }

        return responseMap;
    }

    /**
     * makes an HTTP request to pull data from Fitbit.
     *
     * @param r - Activities Resource Path for intraday
     * @param fitbitUser
     * @param from - YYYY-MM-dd
     * @param to
     * @return - intraday JSON data based on parameters.
     * @throws IllegalAccessException
     */
    public JsonNode fetchActivities(ActivitiesResource r, FitbitUser fitbitUser, String from, String to) throws IllegalAccessException  {
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

        String ACTIVITIES_KEY = getActivitiesKey(r);
        LocalDateTime cur = parseTimeParam(from);
        LocalDateTime toDate = parseTimeParam(to);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode nodes = mapper.createArrayNode();
        while(cur.compareTo(toDate) != 1){
            String curString = FitbitAuthenticationService.toRequestDateFormat(cur);
            String url = activityAPIService.buildFinegrainActivitiesURI(fitbitUser, r, curString);
            JsonNode node= authenticationService.authorizedRequest(fitbitUser, url);
            nodes.add(node);
            cur = cur.plusDays(1L);
        }

        return nodes;
    }

    /***
     * Wrapper on _jsonToPOJOInBulk that converts Activities Time Series JSON to POJO. where an atomic case looks like
     * node = {"activities-steps":[{"dateTime":"2018-06-23","value":"393"}],"activities-steps-intraday":{"dataset":[{"time":"00:00:00","value":0}...]
     * If JSON is an array, iterate through each atomic case and flatten it;
     * @param fitbitUserId
     * @param r
     * @param node
     * @return flattened timeserie data.
     */
    public List<AbstractIntradayActivity> jsonToPOJOInBulk(Long fitbitUserId, ActivitiesResource r, JsonNode node){
        if (node.isArray()){
            List<List<AbstractIntradayActivity>> acts = new ArrayList<>();
            ArrayNode nodes = (ArrayNode)node;
            for(int i = 0; i < nodes.size(); i++){
                acts.add(_jsonToPOJOInBulk(fitbitUserId, r, nodes.get(i)));
            }
            return acts.stream().flatMap(List::stream).collect(Collectors.toList());
        } else {
            return _jsonToPOJOInBulk(fitbitUserId, r, node);
        }
    }

    public List<AbstractIntradayActivity> _jsonToPOJOInBulk(Long fitbitUserId, ActivitiesResource r, JsonNode node){
        IIntradayActivityService service = getService(r);
        String ACTIVITIES_INTRA_KEY = getIntraDayActivitiesKey(r);
        String ACTIVITIES_KEY = getActivitiesKey(r);
        if (!(node.has(ACTIVITIES_KEY) && node.has(ACTIVITIES_INTRA_KEY))){
            throw new IllegalArgumentException("Incorrect intraday timeserie JSON");
        }
        //{"activities-steps":[{"dateTime":"2018-06-23","value":"393"}],"activities-steps-intraday":{"dataset":[{"time":"00:00:00","value":0}...]}
        ArrayNode actJsons = (ArrayNode) node.get(ACTIVITIES_KEY);
        String todayString = actJsons.get(0).get(DATETIME).asText();
        JsonNode intraNode = node.get(ACTIVITIES_INTRA_KEY);
        ArrayNode node_dataset = (ArrayNode) intraNode.get(DATASET);
        List<AbstractIntradayActivity> activities = new ArrayList<>();
        for(int i = 0; i < node_dataset.size(); i++){
            JsonNode node_i = node_dataset.get(i);
            String timeText = node_i.get(INTRA_TIME).asText();
            Double value = node_i.get(VALUE).asDouble();
            String dateTimeString = combineDateTime(todayString, timeText);

            LocalDateTime dateTime=  parseLongTimeParam(dateTimeString);
            Long dateTimeEpoch = EntityHelper.toEpochMilli(dateTime);
            AbstractIntradayActivity entity = service.jsonToPOJO(fitbitUserId, dateTimeEpoch, value, node_i);
            activities.add(entity);
        }
        return activities;
    }


}
