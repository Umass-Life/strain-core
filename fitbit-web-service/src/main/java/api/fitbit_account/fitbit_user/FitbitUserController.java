package api.fitbit_account.fitbit_user;

import api.FileLogger;
import api.constants.AccessTokenResponseKey;
import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_profile.FitbitProfile;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import api.fitbit_web_api.fitbit_activity.aggregate.AggregateActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import util.ColorLogger;
import util.EntityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

import static util.Validation.checkNotNull;
import static util.Validation.parseInt;

@Controller
@RequestMapping("/users")
public class FitbitUserController {
    // will reverse order Users so first user (me) comes first.
    final Boolean TESTING_MODE = true;
    private final Logger log = Logger.getLogger(FitbitUserController.class.getName());
    private final ColorLogger colorLog = new ColorLogger(log);

    @Autowired
    FitbitAuthenticationService fitbitUserAuthenticationService;

    @Autowired
    FitbitUserService fitbitUserService;

    @Autowired
    FitbitProfileService fitbitProfileService;

    @Autowired
    AggregateActivityService aggregateActivityService;

    @Value("${view.uri}")
    String WEBAPP_URI_HOME;

    @RequestMapping(value = "/authorize", method= RequestMethod.GET)
    public ResponseEntity authorizeFitbit(@RequestParam(value="strainUserId") String strainUserIdString) throws URISyntaxException {
        Map<String, Object> responseJson = new HashMap<>();
        try {
            Integer strainUserIdInt = parseInt(strainUserIdString, "Query string \'strainUserIdString\' is not a number");
            Long strainUserId = new Long(strainUserIdInt);
            String authorization_uri = fitbitUserAuthenticationService.authorize(strainUserId);
            colorLog.info(authorization_uri);
            URI uri = new URI(authorization_uri);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(uri);
            ResponseEntity responseEntity = new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
            return responseEntity;
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
    }

    /***
     * User Registration function
     *
     * @param req
     * @param res
     */
    @RequestMapping(value="/received", method= RequestMethod.GET)
    public void api_callback(HttpServletRequest req, HttpServletResponse res){
        Map<String, Object> responseJson = new HashMap<>();
        try {

            Map reqm = req.getParameterMap();
            colorLog.info(reqm.toString());
            String[] code_value= (String[]) reqm.get("code");
            String[] strainUserIdStrings = (String[]) reqm.get("state");
            Long strainUserId = new Long(parseInt(strainUserIdStrings[0], "Query string \'strainUserId\' is not a number"));
            String tempAccessCode = code_value[0];

            colorLog.info("1)Received Fitbit authorize callback! Access Code for StrainUserId= %s\ntempAccessCode=%s\n", strainUserId, tempAccessCode);
            Map<AccessTokenResponseKey, String> accessJson = fitbitUserAuthenticationService.requestAccessToken(tempAccessCode, strainUserId);
            colorLog.info("2) accessJson:\n%s\n", accessJson);
            if (!accessJson.containsKey(AccessTokenResponseKey.FITBIT_USER_ID)){
                String access_success = accessJson.get(AccessTokenResponseKey.SUCCESS_TOKEN);
                String access_errors = accessJson.get(AccessTokenResponseKey.ERRORS_TOKEN);
                String err_msg = access_success + " : " + access_errors;
                throw new IllegalStateException(err_msg);
            }
            Long expiresIn = new Long(parseInt(accessJson.get(AccessTokenResponseKey.EXPIRE_TOKEN), "cannot parse expireIn"));

            colorLog.info("3) fetching FitbitUser for strain wtih accessJson: \n" + accessJson);
            Optional<FitbitUser> fitbitUserOpt = fitbitUserService.getByStrainUserId(strainUserId);
            FitbitUser fitbitUser = null;
            if(fitbitUserOpt.isPresent()){
                fitbitUser = fitbitUserService.updateTokens(fitbitUserOpt.get().getId(),
                        accessJson.get(AccessTokenResponseKey.ACCESS_TOKEN),
                        accessJson.get(AccessTokenResponseKey.REFRESH_TOKEN));
                colorLog.info("--user updated");
            } else {
                colorLog.info("3.1) not found creating new user..");
                fitbitUser = fitbitUserService.create(strainUserId,
                        accessJson.get(AccessTokenResponseKey.FITBIT_USER_ID),
                        accessJson.get(AccessTokenResponseKey.ACCESS_TOKEN),
                        accessJson.get(AccessTokenResponseKey.REFRESH_TOKEN),
                        accessJson.get(AccessTokenResponseKey.TOKEN_TYPE),
                        expiresIn);
                colorLog.info("--user created");
            }

            colorLog.info("4) obtained FitbitUser --- \n%s\n", fitbitUser);
            JsonNode profileJsonNode = fitbitProfileService.fetchProfileFromWebAPI(fitbitUser);
            if (profileJsonNode == null){
                throw new Exception("null profile json");
            }
            colorLog.info("Fetching FitbitProfile...");
            Optional<FitbitProfile> fitbitProfileOpt = fitbitProfileService.getByFitbitUserId(fitbitUser.getId());
            FitbitProfile fitbitProfile = null;
            if (fitbitProfileOpt.isPresent()) {
                fitbitProfile = fitbitProfileService
                        .update(fitbitProfileOpt.get().getId(),
                        profileJsonNode,
                        accessJson.get(AccessTokenResponseKey.SCOPE));
            } else{
                fitbitProfile = fitbitProfileService
                        .create(fitbitUser.getId(),
                        profileJsonNode,
                        accessJson.get(AccessTokenResponseKey.SCOPE));
            }

            colorLog.info(" obtained FitbitProfile --- \n%s\n", fitbitProfile);

            JsonNode activityJson = aggregateActivityService.fetchActivities(fitbitUser, LocalDateTime.now());

            responseJson.put(FitbitUser.SINGULAR, fitbitUser);
            responseJson.put(FitbitProfile.SINGULAR, fitbitProfile);
            responseJson.put("activitySummary", activityJson);

        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseJson.put("error", e.getMessage());
//            return ResponseEntity.badRequest().body(responseJson);
        }
        res.setHeader("Location", WEBAPP_URI_HOME);
        res.setStatus(302);
    }

    @RequestMapping(value = "/renew", method = RequestMethod.POST)
    public ResponseEntity<Map> renewAccessToken(@RequestParam(value="id", required=false) Long id,
                                      @RequestParam(value="fid", required=false) String fid){
        Map<String, Object> responseJson = new HashMap<>();
        try {
            FitbitUser fitbitUser = getFitbitUser(fid, id);
            fitbitUser =  fitbitUserAuthenticationService.refreshAccessToken(fitbitUser);
            responseJson.put(FitbitUser.SINGULAR, fitbitUser);
            return ResponseEntity.ok(responseJson);
        } catch (Exception e){
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
    }

    @RequestMapping(value = "/revoke", method = RequestMethod.POST)
    public ResponseEntity<Map> revoke(@RequestParam(value="id", required=false) Long id,
                                      @RequestParam(value="fid", required=false) String fid){
        Map<String, Object> responseJson = new HashMap<>();
        try {
            FitbitUser fitbitUser = getFitbitUser(fid, id);
            fitbitUserAuthenticationService.revokeAccessToken(fitbitUser);

            return ResponseEntity.ok(responseJson);
        } catch (Exception e){
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map> fetchFitbitUserById(@PathVariable("id") Long id,
                                                   @RequestParam(value="includeProfile", required=false) Boolean includeProfile) {
        Map<String, Object> responseMap = new HashMap<>();
        try {

            FitbitUser user = fitbitUserService.getById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find FibtitUser with ID: " + id));
            responseMap.put(FitbitUser.SINGULAR, user);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<Map> fetchFitbitUserByParameter(@RequestParam(value="fid",required=false) String fitbitId,
                                                         @RequestParam(value="sid",required=false) Long strainUserId){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser user = null; //fitbitUserService.getByFitbitId()
            if (fitbitId!=null){
                user = fitbitUserService.getByFitbitId(fitbitId)
                        .orElseThrow(() -> new IllegalArgumentException("can't find fib=" + fitbitId));
            }
            if (strainUserId!=null){
                user = fitbitUserService.getByStrainUserId(strainUserId)
                        .orElseThrow(() -> new IllegalArgumentException("can't find sid=" + strainUserId));
            }
            responseMap.put(FitbitUser.SINGULAR, user);
            return ResponseEntity.ok(responseMap);
        } catch(Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    /***
     * CREATE NEW FITBIT USER
     * - manually creating will refresh token and store profile
     */
    @RequestMapping(value = {"create", "/", ""}, method = RequestMethod.POST)
    public ResponseEntity<Map> createUser(@RequestBody JsonNode jsonBody){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            checkNotNull(jsonBody, "no body in POST request in FitbitUserController.create");
            FitbitUser fitbitUser = fitbitUserService.create(jsonBody);
            fitbitUser = fitbitUserAuthenticationService.refreshAccessToken(fitbitUser);
            String fitbitId = fitbitUser.getFitbitId();
            FileLogger manualLogger = new FileLogger(fitbitId, "[FitbitId] " + fitbitId);
            manualLogger.log(fitbitUser.toString());
            FitbitProfile profile = fitbitProfileService.fetchAndSave(fitbitUser);

            responseMap.put(FitbitUser.SINGULAR, fitbitUser);
            responseMap.put(FitbitProfile.SINGULAR, profile);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());

        }
        return ResponseEntity.ok(responseMap);
    }

    @RequestMapping(value = "/updateTokens/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> updateUser(@PathVariable("id") Long id,
                                          @RequestBody JsonNode jsonBody){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser fitbitUser = fitbitUserService.updateTokens(id,
                    jsonBody.get(AccessTokenResponseKey.ACCESS_TOKEN.toString()).asText(),
                    jsonBody.get(AccessTokenResponseKey.REFRESH_TOKEN.toString()).asText());
            responseMap.put(FitbitUser.SINGULAR, fitbitUser);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());

        }
        return ResponseEntity.ok(responseMap);
    }


    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public ResponseEntity<Map> fetchAllFitbitUsers(@RequestParam(value="includeProfile", required=false) Boolean includeProfile){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Iterable<FitbitUser> usersItr = fitbitUserService.list();
            List<FitbitUser> users = EntityHelper.iterableToList(usersItr);

            if(TESTING_MODE) Collections.sort(users, Comparator.comparing(FitbitUser::getId));

            if (includeProfile!=null && includeProfile.booleanValue()){
                List<FitbitProfile> profiles = new ArrayList<>();
                for(FitbitUser user : users){
                    profiles.add(fitbitProfileService.getByFitbitUserId(user.getId()).get());
                }
                responseMap.put(FitbitProfile.PLURAL, profiles);
            }
            responseMap.put(FitbitUser.PLURAL, users);
            return ResponseEntity.ok(responseMap);
        } catch(Exception e){
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            e.printStackTrace();
            colorLog.severe(e.getMessage());
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

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }
}
