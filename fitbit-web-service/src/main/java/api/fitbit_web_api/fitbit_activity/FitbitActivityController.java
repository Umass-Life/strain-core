package api.fitbit_web_api.fitbit_activity;

import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_activity.aggregate.AggregateActivity;
import api.fitbit_web_api.fitbit_activity.aggregate.AggregateActivityService;
import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResource;
import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResourceAggregate;
import api.fitbit_web_api.fitbit_activity.intraday.IntradayActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import util.ColorLogger;
import util.EntityHelper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

import static api.fitbit_account.fitbit_auth.FitbitAuthenticationService.parseTimeParam;
/***
 * Controller for aggregates data
 * */
@Controller
@RequestMapping("/activities")
public class FitbitActivityController {
    private final Logger logger = Logger.getLogger(FitbitActivityController.class.getSimpleName());
    private final ColorLogger colorLog = new ColorLogger(logger);

    @Autowired
    private AggregateActivityService aggregateActivityService;

    @Autowired
    private FitbitUserService fitbitUserService;

    @Autowired
    private FitbitAuthenticationService fitbitAuthenticationService;


    @RequestMapping(value = {"/",""}, method = RequestMethod.GET)
    public ResponseEntity<Map> getActivities(@RequestParam(value="id", required=false) Long userId,
                                             @RequestParam(value="fid",required=false) String fitbitId,
                                             @RequestParam(value="r", required=false) String r,
                                             @RequestParam(value="from", required=false) String fromDate,
                                             @RequestParam(value="to", required=false) String toDate){
        Map<String, Object> responseJson = new HashMap<>();
        colorLog.info("userId=%s fid=%s resourcePath=%s, fromDate=%s, toDate=%s", userId, fitbitId, r, fromDate, toDate);

        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser fitbitUser = getFitbitUser(fitbitId, userId);
            ActivitiesResourceAggregate resource = r == null ? null : ActivitiesResourceAggregate.valueOf(r);;
            Iterable<AggregateActivity> activities = aggregateActivityService.list(fitbitUser.getId(), resource, fromDate, toDate);

            responseMap.put(AggregateActivity.PLURAL, activities);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    @RequestMapping(value="/bob-api", method = RequestMethod.GET)
    public ResponseEntity bob(@RequestParam(value="id", required=false) Long userId,
                                        @RequestParam(value="fid",required=false) String fitbitId,
                                        @RequestParam(value="r", required=false) String[] resourcePathStrings,
                                        @RequestParam(value="save",required=false,defaultValue = "false") Boolean save,
                                        @RequestParam(value="from", required=false) String from,
                                        @RequestParam(value="to", required=false) String to) {
        Page p = aggregateActivityService.test(getFitbitUser(fitbitId, userId).getId());
        return ResponseEntity.ok(p.getContent());
    }

    @RequestMapping(value="/fetch-api", method = RequestMethod.GET)
    public ResponseEntity<Map> fetchAPI(@RequestParam(value="id", required=false) Long userId,
                                        @RequestParam(value="fid",required=false) String fitbitId,
                                        @RequestParam(value="r", required=false) String[] resourcePathStrings,
                                        @RequestParam(value="save",required=false,defaultValue = "false") Boolean save,
                                        @RequestParam(value="from", required=false) String from,
                                        @RequestParam(value="to", required=false) String to){
        Map<String, Object> responseMap = null;
        try {
            FitbitUser fitbitUser = getFitbitUser(fitbitId, userId);
            Set<ActivitiesResourceAggregate> resourcesPaths = getQueriedResources(resourcePathStrings);
            responseMap = aggregateActivityService.fetchAndSave(fitbitUser, resourcesPaths, from ,to, save);

            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);

        }
    }


    @RequestMapping(value = "/levels", method = RequestMethod.GET)
    public ResponseEntity<Map> getAggregates(@RequestParam(value="id", required=false) Long userId,
                                             @RequestParam(value="fid",required=false) String fitbitId,
                                             @RequestParam(value="from", required=false) String from,
                                             @RequestParam(value="to", required=false) String to){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser fitbitUser = getFitbitUser(fitbitId, userId);
            List<Map> aggregate = aggregateActivityService.listAggregates(fitbitUser.getId(), from, to);
            responseMap.put("aggregate", aggregate);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }


    @RequestMapping(value = {"/test"}, method = RequestMethod.POST)
    public ResponseEntity<Map> test(@RequestParam(value="fid", required=false) String fitbitId,
                                    @RequestBody Map<String, Object> body){
        Map<String, Object> responseJson = new HashMap<>();
        try {
            FitbitUser fitbitUser = null;
            fitbitUser = fitbitUserService.getByFitbitId(fitbitId).orElseThrow(
                    () -> new Exception("UNable to find FitbitUser fitbitId = " + fitbitId)
            );

            colorLog.info("found user = " + fitbitUser);

            if (fitbitUser == null){
                throw new IllegalStateException("No Fitbit user information provided");
            }

            String url = (String) body.get("url");

            JsonNode node = null;
            node = fitbitAuthenticationService.authorizedRequest(fitbitUser, url);

            responseJson.put("payload", node);
        } catch (Exception e){
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.status(400).body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = {"/tracker"}, method = RequestMethod.GET)
    public ResponseEntity<Map> getTrackerActivities(@RequestParam(value="id", required=false) Long userId,
                                                    @RequestParam(value="fid",required=false) String fitbitId,
                                                    @RequestParam(value="r", required=false) String resourcePath,
                                                    @RequestParam(value="from", required=true) String fromDate,
                                                    @RequestParam(value="to", required=false) String toDate){
        Map<String, Object> responseJson = new HashMap<>();
        colorLog.info("userId=%s resourcePath=%s, fromDate=%s, toDate=%s", userId, resourcePath, fromDate, toDate);

        try {
            FitbitUser fitbitUser = getFitbitUser(fitbitId, userId);

            LocalDateTime fromLocalDate = parseTimeParam(fromDate);
            LocalDateTime toLocalDate = null;
            if (toDate != null) {
                toLocalDate = parseTimeParam(toDate);
            }

            ActivitiesResourceAggregate acp = null;
            if (resourcePath != null){
                acp = ActivitiesResourceAggregate.valueOf(resourcePath);

            }
            JsonNode node = null;
            if (acp != null){
                node = aggregateActivityService.fetchTrackerActivities(acp, fitbitUser, fromLocalDate, toLocalDate);
            } else {
                node =  aggregateActivityService.fetchActivities(fitbitUser, fromLocalDate);
            }
            responseJson.put("payload", node);
        } catch (Exception e){
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.status(400).body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    private Set<ActivitiesResourceAggregate> getQueriedResources(String[] raw_resources) throws Exception{
        final Set<ActivitiesResourceAggregate> resources = new HashSet<>();
        if (raw_resources==null){
            resources.addAll(this.aggregateActivityService.getActivitiesResources());
        } else {
            Arrays.asList(raw_resources)
                    .stream()
                    .forEach(s ->{
                        try {
                            resources.add(ActivitiesResourceAggregate.valueOf(s));
                        } catch(Exception e){
                            colorLog.warning("cannot find resource : " + s);
                        }
                    });
        }
        return resources;
    }

    @RequestMapping(value = "/latest", method=RequestMethod.GET)
    public ResponseEntity<Map> getLatest(@RequestParam(value="id", required=false) Long userId,
                                              @RequestParam(value="fid",required=false) String fitbitId){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser fitbitUser = getFitbitUser(fitbitId, userId);
            Set<ActivitiesResourceAggregate> resources = aggregateActivityService.getActivitiesResources();
            for(ActivitiesResourceAggregate r : resources){
                responseMap.put(r.toString(), aggregateActivityService.findLatest(fitbitUser.getId(), r));
            }
            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    @RequestMapping(value = {"/ping"}, method = RequestMethod.GET)
    public ResponseEntity ping(){
        return ResponseEntity.ok("poing");
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

//        colorLog.info("found user = " + fitbitUser);

        if (fitbitUser == null){
            throw new IllegalArgumentException("No Fitbit user information provided");
        }
        return fitbitUser;
    }
}
