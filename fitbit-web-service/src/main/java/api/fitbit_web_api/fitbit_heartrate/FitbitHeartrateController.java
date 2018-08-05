package api.fitbit_web_api.fitbit_heartrate;

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

import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<Map> fetchAll(@RequestParam("includeZone") Boolean includeZone) {
        Map<String, Object> responseMap = new HashMap<>();
        Iterable<FitbitHeartrate> fitbitHeartrates = fitbitHeartrateService.list();
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

    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public ResponseEntity<Map> create(){
        Map<String, Object> responseJson = new HashMap<>();
        try {
            return ResponseEntity.ok(responseJson);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
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

            if (save){
                Iterable<FitbitHeartrate> hrData = heartrateAPIService.fetchAndCreateBulk(fitbitUser, from, to);
                responseMap.put(FitbitHeartrate.PLURAL, hrData);
            } else {
                JsonNode node = heartrateAPIService.fetchHeartrateFromFitbit(fitbitUser, from, to);
                responseMap.put("payload", node);
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

    @RequestMapping(value="/serie",  method=RequestMethod.GET)
    public ResponseEntity<Map> fetchTimeserieAPI(@RequestParam(value="fid",required=false) String fid,
                                        @RequestParam(value="from",required=false) String from,
                                        @RequestParam(value="to",required=false) String to,
                                        @RequestParam(value="save",required=false,defaultValue="false") Boolean save){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            FitbitUser fitbitUser = fitbitUserService.getByFitbitId(fid).orElseThrow(
                    () -> new IllegalArgumentException("cannot find fitbitId = " + fid)
            );

            if (save){

            } else {
                JsonNode hrData = heartrateAPIService.fetchFinegrainHeartrateFromFitbit(fitbitUser, from, to);
                responseMap.put("payload", hrData);
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


}
