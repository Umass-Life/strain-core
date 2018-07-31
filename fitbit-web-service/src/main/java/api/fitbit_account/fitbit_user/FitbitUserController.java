package api.fitbit_account.fitbit_user;

import api.constants.AccessTokenParams;
import api.constants.AccessTokenResponseKey;
import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_profile.FitbitProfile;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import api.fitbit_web_api.fitbit_activity.FitbitActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import util.ColorLogger;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static util.Validation.parseInt;

@Controller
@RequestMapping("/users")
public class FitbitUserController {

    private final Logger log = Logger.getLogger(FitbitUserController.class.getName());
    private final ColorLogger colorLog = new ColorLogger(log);

    @Autowired
    FitbitAuthenticationService fitbitUserAuthenticationService;

    @Autowired
    FitbitUserService fitbitUserService;

    @Autowired
    FitbitProfileService fitbitProfileService;

    @Autowired
    FitbitActivityService fitbitActivityService;

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }

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

    @RequestMapping(value="/received", method= RequestMethod.GET)
    public ResponseEntity<Map> api_callback(HttpServletRequest req){
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

            JsonNode activityJson = fitbitActivityService.fetchActivities(fitbitUser, LocalDateTime.now());

            responseJson.put(FitbitUser.SINGULAR, fitbitUser);
            responseJson.put(FitbitProfile.SINGULAR, fitbitProfile);
            responseJson.put("activitySummary", activityJson);

            return ResponseEntity.ok(responseJson);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
    }

    @RequestMapping(value = "/revoke", method = RequestMethod.POST)
    public ResponseEntity<Map> revoke(@RequestParam(value="id", required=false) Long id,
                                      @RequestParam(value="fib", required=false) String fib){
        Map<String, Object> responseJson = new HashMap<>();
        try {
            FitbitUser fitbitUser = null;
            if (id != null){
                fitbitUser = fitbitUserService.getById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Cannot find FitbitUser id " + id));
            } else if (fib != null){
                fitbitUser = fitbitUserService.getByFitbitId(fib)
                        .orElseThrow(() -> new IllegalArgumentException("Cannot find FitbitUser with fitbit-id = " + fib));
            }

            if (fitbitUser == null) throw new IllegalArgumentException("Cannot find fitbitUser.");

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
    public ResponseEntity<Map> fetchFitbitUserById(@PathVariable("id") Long id) {
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
    public ResponseEntity<Map> fetchFitbitUserByParameter(@RequestParam(value="fib",required=false) String fitbitId,
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
    public ResponseEntity<Map> fetchAllFitbitUsers(){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Iterable<FitbitUser> users = fitbitUserService.list();
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

}
