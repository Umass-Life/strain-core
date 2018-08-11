package api.fitbit_web_api.fitbit_activity;

import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_activity.aggregate.AggregateActivity;
import api.fitbit_web_api.fitbit_activity.aggregate.AggregateActivityService;
import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResource;
import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResourceAggregate;
import api.fitbit_web_api.fitbit_activity.intraday.AbstractIntradayActivity;
import api.fitbit_web_api.fitbit_activity.intraday.IntradayActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import util.ColorLogger;
import util.EntityHelper;

import java.util.*;
import java.util.logging.Logger;

@RequestMapping(value = "/activities/intraday")
@Controller
public class FitbitIntradayActivityController {
    private final Logger logger = Logger.getLogger(FitbitIntradayActivityController.class.getSimpleName());
    private final ColorLogger colorLog = new ColorLogger(logger);

    @Autowired
    private FitbitUserService fitbitUserService;

    @Autowired
    private FitbitAuthenticationService fitbitAuthenticationService;

    @Autowired
    private IntradayActivityService intradayActivityService;


    @RequestMapping(value = {"/",""}, method = RequestMethod.GET)
    public ResponseEntity<Map> getActivities(@RequestParam(value="id", required=false) Long userId,
                                             @RequestParam(value="fid",required=false) String fitbitId,
                                             @RequestParam(value="r", required=true) String r,
                                             @RequestParam(value="page", required=true) Integer page,
                                             @RequestParam(value="count", required=true) Integer count){
        Map<String, Object> responseJson = new HashMap<>();
        colorLog.info("userId=%s fid=%s resourcePath=%s, page=%s, count=%s", userId, fitbitId, r, page, count);

        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser fitbitUser = getFitbitUser(fitbitId, userId);

            ActivitiesResource resource = ActivitiesResource.valueOf(r);;
            Iterable<AbstractIntradayActivity> activities = intradayActivityService.list(fitbitUser.getId(), resource, page, count);

            responseMap.put(resource.toString(), activities);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    @RequestMapping(value="/fetch-api", method = RequestMethod.GET)
    public ResponseEntity<Map> fetchAPI(@RequestParam(value="id", required=false) Long userId,
                                        @RequestParam(value="fid",required=false) String fitbitId,
                                        @RequestParam(value="save",required=false,defaultValue = "false") Boolean save,
                                        @RequestParam(value="r", required=false) String rawResourcePath[],
                                        @RequestParam(value="from", required=false) String from,
                                        @RequestParam(value="to", required=false) String to){
        Map<String, Object> responseMap = null;
        try {
            FitbitUser fitbitUser = getFitbitUser(fitbitId, userId);
            Set<ActivitiesResource> resourcePaths = getQueriedResources(rawResourcePath);

            responseMap = intradayActivityService.fetchAndSave(fitbitUser, resourcePaths, from, to, save);


            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    @RequestMapping(value = "/latest", method=RequestMethod.GET)
    public ResponseEntity<Map> getLatest(@RequestParam(value="id", required=false) Long userId,
                                         @RequestParam(value="fid",required=false) String fitbitId){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser fitbitUser = getFitbitUser(fitbitId, userId);
            Set<ActivitiesResource> resources = intradayActivityService.getActivitiesResources();
            for(ActivitiesResource r : resources){
                responseMap.put(r.toString(), intradayActivityService.findLatest(fitbitUser.getId(), r));
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

    /////////////////////////////////////////////////////////////
    /////////////////   PRIVATE UTIL METHODS  ///////////////////
    /////////////////////////////////////////////////////////////
    private Set<ActivitiesResource> getQueriedResources(String[] raw_resources) throws Exception{
        final Set<ActivitiesResource> resources = new HashSet<>();
        if (raw_resources==null){
            resources.addAll(this.intradayActivityService.getActivitiesResources());
        } else {
            Arrays.asList(raw_resources)
                    .stream()
                    .forEach(s ->{
                        try {
                            resources.add(ActivitiesResource.valueOf(s));
                        } catch(Exception e){
                            colorLog.warning("cannot find resource : " + s);
                        }
                    });
        }
        return resources;
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
