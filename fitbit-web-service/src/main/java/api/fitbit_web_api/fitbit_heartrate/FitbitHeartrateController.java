package api.fitbit_web_api.fitbit_heartrate;

import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_heartrate.heartrate_zone.HeartrateZone;
import api.fitbit_web_api.fitbit_heartrate.heartrate_zone.HeartrateZoneService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import util.ColorLogger;
import util.EntityHelper;

import javax.xml.ws.Response;
import java.util.*;
import java.util.logging.Logger;

@Controller
@RequestMapping("/hr")
public class FitbitHeartrateController {

    private final Logger logger = Logger.getLogger(FitbitHeartrateController.class.getName());
    private final ColorLogger colorLog = new ColorLogger(logger);
    @Autowired
    private FitbitHeartrateService fitbitHeartrateService;
    @Autowired
    private HeartrateZoneService zoneService;
    @Autowired
    private FitbitHeartrateAPIService heartrateAPIService;
    @Autowired
    private FitbitUserService fitbitUserService;

    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public ResponseEntity<Map> fetchAll(@RequestParam(value="includeZone",required=false,defaultValue="true") Boolean includeZone,
                                        @RequestParam(value="fid", required=false) String fid,
                                        @RequestParam(value="id", required=false) Long id,
                                        @RequestParam(value="from",required=false) String from,
                                        @RequestParam(value="to",required=false) String to) {
        Map<String, Object> responseMap = new HashMap<>();

        FitbitUser fitbitUser = fitbitUserService.getByFitbitId(fid).orElseThrow(
                () -> new IllegalArgumentException("cannot find fitbitId = " + fid)
        );

        Iterable<FitbitHeartrate> fitbitHeartrates = fitbitHeartrateService.list(fitbitUser.getId(), from ,to);
        List<FitbitHeartrate> mm = EntityHelper.iterableToList(fitbitHeartrates);
        colorLog.info("SIZE: %s from=%s to=%s\n", mm.size(),
                EntityHelper.epochToDateString(mm.get(mm.size()-1).getDateTime()),
                EntityHelper.epochToDateString(mm.get(0).getDateTime()));
        if (includeZone){
            ObjectMapper om = new ObjectMapper();
            List<Iterable<HeartrateZone>> zones =new ArrayList<>();
            for(FitbitHeartrate hr : fitbitHeartrates){
                Iterable<HeartrateZone> zone = zoneService.listByFitbitHeartrateId(hr.getId());
                zones.add(zone);
            }
            responseMap.put(HeartrateZone.PLURAL, zones);
        }
        responseMap.put(FitbitHeartrate.PLURAL, fitbitHeartrates);
        return ResponseEntity.ok(responseMap);
    }

    @RequestMapping(value = "/fetch-api", method = RequestMethod.GET)
    public ResponseEntity<Map> fetchAPI(@RequestParam(value="fid",required=false) String fid,
                                        @RequestParam(value="from",required=false) String from,
                                        @RequestParam(value="to",required=false) String to,
                                        @RequestParam(value="save",required=false,defaultValue="false") Boolean save){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser fitbitUser = fitbitUserService.getByFitbitId(fid).orElseThrow(
                    () -> new IllegalArgumentException("cannot find fitbitId = " + fid)
            );
            responseMap = heartrateAPIService.fetchAndSave(fitbitUser, from, to, save);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    public ResponseEntity<Map> getFirstDataPoint(@RequestParam(value="id", required=false) Long userId,
                                                 @RequestParam(value="fid",required=false) String fid){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser fitbitUser = getFitbitUser(fid, userId);
            Optional<FitbitHeartrate> hr = fitbitHeartrateService.findLatest(fitbitUser.getId());
            responseMap.put(FitbitHeartrate.SINGULAR, hr);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    @RequestMapping(value = "/{id}/zones", method = RequestMethod.GET)
    public ResponseEntity<Map> listZonesById(@PathVariable("id") Long id){
        Map<String, Object> map = new HashMap<>();
        try {
            FitbitHeartrate hr = fitbitHeartrateService.getById(id).orElseThrow(
                    () -> new IllegalArgumentException("cannot find Fitbitheartrate id " + id)
            );
            Iterable<HeartrateZone> hrZoneList = zoneService.listByFitbitHeartrateId(hr.getId());
            map.put(HeartrateZone.PLURAL, hrZoneList);
            return ResponseEntity.ok(map);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            map = new HashMap<>();
            map.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(map);
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


}
