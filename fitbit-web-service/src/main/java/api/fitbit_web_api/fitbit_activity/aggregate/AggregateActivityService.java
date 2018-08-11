package api.fitbit_web_api.fitbit_activity.aggregate;

import api.FitbitConstantEnvironment;
import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_profile.FitbitProfile;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResource;
import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResourceAggregate;
import api.fitbit_web_api.fitbit_activity.ActivityAPIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import util.ColorLogger;
import org.springframework.stereotype.Service;
import util.EntityHelper;
import util.StrainTimer;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static api.fitbit_account.fitbit_auth.FitbitAuthenticationService.parseTimeParam;
import static api.fitbit_account.fitbit_auth.FitbitAuthenticationService.toRequestDateFormat;
import static api.fitbit_web_api.fitbit_activity.ActivityAPIService.getActivitiesKey;
import static util.Validation.checkNotNull;

@Service
public class AggregateActivityService {
    @Autowired
    private FitbitConstantEnvironment fitbitConstantEnvironment;

    @Autowired
    AggregateActivityRepository repository;

    @Autowired
    FitbitProfileService fitbitProfileService;

    @Autowired
    private FitbitAuthenticationService authenticationService;

    @Autowired
    private ActivityAPIService util;
    static Logger log = Logger.getLogger(AggregateActivityService.class.getName());
    static ColorLogger colorLog = new ColorLogger(log);

    public String FITBIT_AUTH_URI;
    public String FITBIT_ACCESS_TOKEN_URI;
    public String CLIENT_ID;
    public String CLIENT_SECRET;
    public String RESOURCE_URL;

    private Set<ActivitiesResourceAggregate> activityAggregateMinuteSet = new HashSet(
            Arrays.asList(ActivitiesResourceAggregate.minutesFairlyActive,
                    ActivitiesResourceAggregate.minutesLightlyActive,
                    ActivitiesResourceAggregate.minutesSedentary,
                    ActivitiesResourceAggregate.minutesVeryActive)
    );

    private Set<ActivitiesResourceAggregate> activitiesResourceAggregates = new HashSet<ActivitiesResourceAggregate>(
            Arrays.asList(ActivitiesResourceAggregate.values())
    );

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

    public Iterable<AggregateActivity> list(){
        return repository.findAll();
    }

    public Iterable<AggregateActivity> list(Long fitbitUserId, ActivitiesResourceAggregate resource, String from, String to){

        Specification<AggregateActivity> specs = whereFitbitUserId(fitbitUserId);
        if (resource != null){
            colorLog.info("--> Resource: " + resource);
            specs = specs.and(whereActivitiesResource(resource));
        }


        if (from!=null){
            LocalDateTime fromDateTime = FitbitAuthenticationService.parseTimeParam(from);
            Long time = EntityHelper.toEpochMilli(fromDateTime);
            colorLog.info("--> FRom: " + from);
            specs = specs.and(greaterThanEqualToDate(time));
        }

        if (to!=null){
            LocalDateTime toDateTime = FitbitAuthenticationService.parseTimeParam(to);
            Long time = EntityHelper.toEpochMilli(toDateTime);
            colorLog.info("--> TO: " + to);
            specs = specs.and(lessThanEqualToDate(time));
        }

        Page page = repository.findAll(specs, PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.DESC, "dateTime"));
        return page.getContent();
    }

    public Iterable<AggregateActivity> listByResource(Long fitbitUserId, ActivitiesResourceAggregate r){
        Specification<AggregateActivity> specs = null;
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.DESC, "date_time");

        Iterable<AggregateActivity> results = repository.getByFitbitUserIdAndType(fitbitUserId, r);
        List<AggregateActivity> a = EntityHelper.iterableToList(results);
        Collections.sort(a,Collections.reverseOrder((x, y) -> x.getDateTime().compareTo(y.getDateTime())));
        Collections.reverse(a);
        return a;
//        Page page = repository.findAll(specs, pageable);
//        return page.getContent();
    }

    public Iterable<AggregateActivity> listByResource(ActivitiesResourceAggregate r){
        return repository.getByType(r);
    }

    public Set<ActivitiesResourceAggregate> getActivitiesResources(){
        return activitiesResourceAggregates;
    }

    ///////////////////////////////////
    ////////// WEB API ///////////////
    ///////////////////////////////////


    public Map<String, Object> fetchAndSave(FitbitUser fitbitUser, Set<ActivitiesResourceAggregate> resourcePaths,
                                            String from, String to, Boolean save) throws IllegalAccessException{
        Map responseMap = new HashMap<>();
        colorLog.info("%s %s %s %s", resourcePaths, from, to, save);
        for (ActivitiesResourceAggregate r : resourcePaths){
            StrainTimer timer = new StrainTimer(colorLog);
            timer.start();
            Map<String, Object> aggregateInfoMap = new HashMap<>();
            try {
                Iterable<AggregateActivity> activities = null;
                if (save){
                    activities = fetchAndSave(fitbitUser, r, from, to);
                    int count = EntityHelper.iterableSize(activities);
                    aggregateInfoMap.put("count", count);
                } else {
                    JsonNode node = fetchActivities(r, fitbitUser, from, to);
                    activities= jsonToPOJOInBulk(fitbitUser.getId(), r, node);
                    aggregateInfoMap.put(AggregateActivity.PLURAL, activities);
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

    public List<AggregateActivity> jsonToPOJOInBulk(Long fitbitUserId, ActivitiesResourceAggregate r, JsonNode node){
        if (node.isArray()){
            List<List<AggregateActivity>> acts = new ArrayList<>();
            ArrayNode nodes = (ArrayNode)node;
            for(int i = 0; i < nodes.size(); i++){
                acts.add(_jsonToPOJOInBulk(fitbitUserId, r, nodes.get(i)));
            }
            return acts.stream().flatMap(List::stream).collect(Collectors.toList());
        } else {
            return _jsonToPOJOInBulk(fitbitUserId, r, node);
        }
    }

    public Iterable<AggregateActivity> fetchAndSave(FitbitUser fitbitUser, ActivitiesResourceAggregate r, String from, String to) throws IllegalAccessException{
        JsonNode json = fetchActivities(r, fitbitUser, from, to);
        List<AggregateActivity> activities = jsonToPOJOInBulk(fitbitUser.getId(),r, json);
        Iterable<AggregateActivity> savedActivities = repository.saveAll(activities);
        return savedActivities;
    }
    /*
    * Converts fetched json to Entities
    * **/
    public List<AggregateActivity> _jsonToPOJOInBulk(Long fitbitUserId, ActivitiesResourceAggregate r, JsonNode node){
        String ACTIVITIES_KEY = getActivitiesKey(r);

        List<AggregateActivity> activities = new ArrayList<>();
        if (!node.has(ACTIVITIES_KEY)){
            throw new IllegalArgumentException("incorrect acivities json format");
        }
        ArrayNode actJsons = (ArrayNode) node.get(ACTIVITIES_KEY);
        for(int i = 0; i < actJsons.size(); i++) {
            JsonNode node_i = actJsons.get(i);
            String dateTimeText = node_i.get(DATETIME).asText();
            Double value = node_i.get(VALUE).asDouble();
            LocalDateTime dateTime = parseTimeParam(dateTimeText);
            Long dateTimeEpoch = EntityHelper.toEpochMilli(dateTime);
            AggregateActivity act = new AggregateActivity(fitbitUserId, r, dateTimeEpoch, value);
            activities.add(act);
        }
        return activities;
    }

    public JsonNode fetchActivities(ActivitiesResourceAggregate acp, FitbitUser fitbitUser, LocalDateTime from,
                                    LocalDateTime to) throws IllegalAccessException  {
        String fromString = null;
        String toString = null;
        if (from!=null) fromString = toRequestDateFormat(from);
        if (to!=null) toString = toRequestDateFormat(to);
        return fetchActivities(acp, fitbitUser, fromString, toString);
    }

    public JsonNode fetchActivities(ActivitiesResourceAggregate acp, FitbitUser fitbitUser, String from, String to) throws IllegalAccessException  {
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
        String url = util.buildActivitiesRequestURI(fitbitUser, acp, from, to);
        return authenticationService.authorizedRequest(fitbitUser, url);
    }

    public JsonNode fetchActivities(FitbitUser fitbitUser, LocalDateTime date) throws IllegalAccessException{
        String dateString = toRequestDateFormat(date);
        return fetchSummarizedActivities(fitbitUser, dateString);
    }

    // without ACP we fetch summary.
    public JsonNode fetchSummarizedActivities(FitbitUser fitbitUser, String date) throws IllegalAccessException {
        String url = util.buildActivitiesSummaryRequestURI(fitbitUser, date);
        return authenticationService.authorizedRequest(fitbitUser, url);
    }

    public JsonNode fetchTrackerActivities(ActivitiesResourceAggregate acp, FitbitUser fitbitUser, LocalDateTime from, LocalDateTime to) throws IllegalAccessException  {
        String fromString = toRequestDateFormat(from);
        String toString = toRequestDateFormat(to);
        return fetchTrackerActivities(acp, fitbitUser, fromString, toString);
    }

    public JsonNode fetchTrackerActivities(ActivitiesResourceAggregate acp, FitbitUser fitbitUser, String from, String to) throws IllegalAccessException  {
        JsonNode node = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String userId = fitbitUser.getFitbitId();
        String access_token = fitbitUser.getAccessToken();
        String url = util.buildActivitiesTrackerTimeSeriesRequest(fitbitUser, acp, from, to);
       return authenticationService.authorizedRequest(fitbitUser, url);
    }

    /***
     * TODO: filter by User.
     * @return
     */
    public List<Map> listAggregates(Long fitbitUserId, String from, String to){
        TreeMap<Long, Map> aggs = new TreeMap<>();
        for(ActivitiesResourceAggregate r : this.activityAggregateMinuteSet){
            Iterable<AggregateActivity> acts = list(fitbitUserId, r, from ,to);
            Iterator<AggregateActivity> itr = acts.iterator();
            while(itr.hasNext()){
                AggregateActivity act = itr.next();
                Long key = act.getDateTime();
                if (aggs.containsKey(key)){
                    aggs.get(key).put(r.toString(), act.getValue());
                } else {
                    Map<String, Number> datum = new HashMap<>();
                    datum.put(r.toString(), act.getValue());
                    aggs.put(key, datum);
                }
            }
        }

        List<Map> Out = new ArrayList<>();
        for(Map.Entry<Long, Map> e : aggs.entrySet()){
            Map json = e.getValue();
            json.put("dateTime", e.getKey());
            Out.add(json);
        }

        Collections.sort(Out, Collections.reverseOrder((a,b) -> {
            Long al = (Long) a.get("dateTime");
            Long bl = (Long) b.get("dateTime");
            return al.compareTo(bl);
        }));

        return Out;

    }

    public Page test(Long id){
        return repository.findAll(whereFitbitUserId(id), PageRequest.of(0,Integer.MAX_VALUE));
    }


    public Optional<AggregateActivity> findLatest(Long fitbitUserId, ActivitiesResourceAggregate ara){
        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, "dateTime");
        Specification<AggregateActivity> specs = whereFitbitUserId(fitbitUserId)
                                            .and(whereActivitiesResource(ara));
        Page<AggregateActivity> page = repository.findAll(specs, pageable);
        List<AggregateActivity> content = page.getContent();
        AggregateActivity sl = content.isEmpty() ? null : content.get(0);
        return Optional.ofNullable(sl);
    }


    private Specification<AggregateActivity> whereFitbitUserId(Long fitbitUserId){
        return (Root<AggregateActivity> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
                return cb.equal(root.get("fitbitUserId"), fitbitUserId);
        };
    }

    private Specification<AggregateActivity> whereActivitiesResource(ActivitiesResourceAggregate resource){
        return (Root<AggregateActivity> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            return cb.equal(root.get("type"), resource);
        };
    }

    private Specification<AggregateActivity> greaterThanEqualToDate(Long time){
        return (Root<AggregateActivity> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            return cb.greaterThanOrEqualTo(root.get("dateTime"), time);
        };
    }

    private Specification<AggregateActivity> lessThanEqualToDate(Long time){
        return (Root<AggregateActivity> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            return cb.lessThanOrEqualTo(root.get("dateTime"), time);
        };
    }




}

