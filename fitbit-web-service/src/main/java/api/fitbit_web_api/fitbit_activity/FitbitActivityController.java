package api.fitbit_web_api.fitbit_activity;

import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_account.fitbit_user.FitbitUserService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import util.ColorLogger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static api.fitbit_account.fitbit_auth.FitbitAuthenticationService.parseTimeParam;

@Controller
@RequestMapping("/activities")
public class FitbitActivityController {
    private final Logger logger = Logger.getLogger(FitbitActivityController.class.getSimpleName());
    private final ColorLogger colorLog = new ColorLogger(logger);

    @Autowired
    private FitbitActivityService fitbitActivityService;

    @Autowired
    private FitbitUserService fitbitUserService;

    @Autowired
    private FitbitAuthenticationService fitbitAuthenticationService;

    public ResponseEntity<Map> fetchAll(){
        return null;
    }

    @RequestMapping(value = {"/",""}, method = RequestMethod.GET)
    public ResponseEntity<Map> getActivities(@RequestParam(value="id", required=false) Long userId,
                                             @RequestParam(value="fid",required=false) String fitbitId,
                                             @RequestParam(value="r", required=false) String resourcePath,
                                             @RequestParam(value="from", required=false) String fromDate,
                                             @RequestParam(value="to", required=false) String toDate){
        Map<String, Object> responseJson = new HashMap<>();
        colorLog.info("userId=%s resourcePath=%s, fromDate=%s, toDate=%s", userId, resourcePath, fromDate, toDate);

        try {
            FitbitUser fitbitUser = null;
            if (userId!=null){
                fitbitUser = fitbitUserService.getById(userId).orElseThrow(
                        () -> new Exception("Unable to find FitbitUser id = " + userId)
                );
            } else if (fitbitId != null){
                fitbitUser = fitbitUserService.getByFitbitId(fitbitId).orElseThrow(
                        () -> new Exception("UNable to find FitbitUser fitbitId = " + fitbitId)
                );
            }

            colorLog.info("found user = " + fitbitUser);

            if (fitbitUser == null){
                throw new IllegalStateException("No Fitbit user information provided");
            }
            LocalDateTime fromLocalDate = parseTimeParam(fromDate);

            LocalDateTime toLocalDate = null;
            if (toDate != null) {
                toLocalDate = parseTimeParam(toDate);
            }

            ActivitiesResource acp = null;
            if (resourcePath != null){
                acp = ActivitiesResource.valueOf(resourcePath);

            }
            JsonNode node = null;
            if (acp != null){
                node = fitbitActivityService.fetchActivities(acp, fitbitUser, fromLocalDate, toLocalDate);
            } else {
                node =  fitbitActivityService.fetchActivities(fitbitUser, fromLocalDate);
            }
            responseJson.put("payload", node);
        } catch (Exception e){
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.status(400).body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = {"/query"}, method = RequestMethod.GET)
    public ResponseEntity<Map> query(@RequestParam(value="r") String r){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            ActivitiesResource resource = ActivitiesResource.valueOf(r);
            Iterable<FitbitActivity> activities = fitbitActivityService.listByResource(resource);
            responseMap.put(FitbitActivity.PLURAL, activities);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
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
            FitbitUser fitbitUser = null;
            if (userId!=null){
                fitbitUser = fitbitUserService.getById(userId).orElseThrow(
                        () -> new Exception("Unable to find FitbitUser id = " + userId)
                );
            } else if (fitbitId != null){
                fitbitUser = fitbitUserService.getByFitbitId(fitbitId).orElseThrow(
                        () -> new Exception("UNable to find FitbitUser fitbitId = " + fitbitId)
                );
            }

            colorLog.info("found user = " + fitbitUser);

            if (fitbitUser == null){
                throw new IllegalStateException("No Fitbit user information provided");
            }
            LocalDateTime fromLocalDate = parseTimeParam(fromDate);
            LocalDateTime toLocalDate = null;
            if (toDate != null) {
                toLocalDate = parseTimeParam(toDate);
            }

            ActivitiesResource acp = null;
            if (resourcePath != null){
                acp = ActivitiesResource.valueOf(resourcePath);

            }
            JsonNode node = null;
            if (acp != null){
                node = fitbitActivityService.fetchTrackerActivities(acp, fitbitUser, fromLocalDate, toLocalDate);
            } else {
                node =  fitbitActivityService.fetchActivities(fitbitUser, fromLocalDate);
            }
            responseJson.put("payload", node);
        } catch (Exception e){
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.status(400).body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }


    @RequestMapping(value="/fetch-api", method = RequestMethod.GET)
    public ResponseEntity<Map> fetchAPI(@RequestParam(value="id", required=false) Long userId,
                                        @RequestParam(value="fid",required=false) String fitbitId,
                                        @RequestParam(value="r", required=false) String resourcePath,
                                        @RequestParam(value="from", required=false) String from,
                                        @RequestParam(value="to", required=false) String to){
        Map<String, Object> responseMap = null;
        try {
            FitbitUser fitbitUser = null;
            if (userId!=null){
                fitbitUser = fitbitUserService.getById(userId).orElseThrow(
                        () -> new Exception("Unable to find FitbitUser id = " + userId)
                );
            } else if (fitbitId != null){
                fitbitUser = fitbitUserService.getByFitbitId(fitbitId).orElseThrow(
                        () -> new Exception("UNable to find FitbitUser fitbitId = " + fitbitId)
                );
            }

            colorLog.info("found user = " + fitbitUser);

            if (fitbitUser == null){
                throw new IllegalStateException("No Fitbit user information provided");
            }

            ActivitiesResource acp = null;
            if (resourcePath != null){
                acp =  ActivitiesResource.valueOf(resourcePath);
            }
            responseMap = new HashMap<>();
            JsonNode node = fitbitActivityService.fetchActivities(acp, fitbitUser, from, to);
            List<FitbitActivity> activityList= fitbitActivityService.jsonToPOJOInBulk(fitbitUser.getId(), acp, node);
            colorLog.info(activityList);

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

    @RequestMapping(value="/fetch-api-save", method = RequestMethod.GET)
    public ResponseEntity<Map> fetchSaveAPI(@RequestParam(value="id", required=false) Long userId,
                                        @RequestParam(value="fid",required=false) String fitbitId,
                                        @RequestParam(value="r", required=false) String resourcePath,
                                        @RequestParam(value="from", required=false) String from,
                                        @RequestParam(value="to", required=false) String to){
        Map<String, Object> responseMap = null;
        try {
            FitbitUser fitbitUser = null;
            if (userId!=null){
                fitbitUser = fitbitUserService.getById(userId).orElseThrow(
                        () -> new Exception("Unable to find FitbitUser id = " + userId)
                );
            } else if (fitbitId != null){
                fitbitUser = fitbitUserService.getByFitbitId(fitbitId).orElseThrow(
                        () -> new Exception("UNable to find FitbitUser fitbitId = " + fitbitId)
                );
            }

            colorLog.info("found user = " + fitbitUser);

            if (fitbitUser == null){
                throw new IllegalStateException("No Fitbit user information provided");
            }

            ActivitiesResource acp = null;
            responseMap = new HashMap<>();

            if (resourcePath != null){
                acp =  ActivitiesResource.valueOf(resourcePath);
                Iterable<FitbitActivity> activities = fitbitActivityService.fetchAndSave(fitbitUser, acp, from, to);
                responseMap.put(FitbitActivity.PLURAL, activities);
            } else {

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



    @RequestMapping(value = {"/ping"}, method = RequestMethod.GET)
    public ResponseEntity ping(){
        return ResponseEntity.ok("poing");
    }


}
