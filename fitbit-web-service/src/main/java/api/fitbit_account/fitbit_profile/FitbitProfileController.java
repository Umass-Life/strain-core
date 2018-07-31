package api.fitbit_account.fitbit_profile;

import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_account.fitbit_user.FitbitUserService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "profiles")
public class FitbitProfileController {

    @Autowired
    private FitbitProfileService fitbitProfileService;

    @Autowired
    private FitbitUserService fitbitUserService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<Map> list(){
        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put(FitbitProfile.PLURAL, fitbitProfileService.list());
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<Map> getProfile(@RequestParam("fitbitUserId") Long id){
        Map<String, Object> responseJson = new HashMap<>();
        FitbitUser fitbitUser = fitbitUserService.getById(id).get();
        JsonNode node = fitbitProfileService.fetchProfileFromWebAPI(fitbitUser);
        responseJson.put("load", node);
        return ResponseEntity.ok(responseJson);
    }



}
