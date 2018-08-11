package api.fitbit_web_api.fitbit_heartrate.heartrate_zone;

import api.fitbit_web_api.fitbit_heartrate.FitbitHeartrate;
import api.fitbit_web_api.fitbit_heartrate.FitbitHeartrateService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.ColorLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import static util.Validation.checkNotNull;

@Service
public class HeartrateZoneService {
    private static final Logger logger = Logger.getLogger(HeartrateZoneService.class.getSimpleName());
    private static final ColorLogger colorLog = new ColorLogger(logger);

    @Autowired
    private HeartrateZoneRepository repository;

    @Autowired
    private FitbitHeartrateService fitbitHeartrateService;

    public Iterable<HeartrateZone> listByFitbitHeartrateId(Long fitbitHeartrateId){
        return repository.findByFitbitHeartrateId(fitbitHeartrateId);
    }

    public Iterable<HeartrateZone> createBulk(Long fitbitHeartrateId, ArrayNode jsonArray){
        checkNotNull(fitbitHeartrateId, "fitbitheartrate-id cannot be null in HeartrateZoneService.createbulk");
        checkNotNull(jsonArray, "fitbitheartrate-id cannot be null in HeartrateZoneService.createbulk");
        List<HeartrateZone> hrZones = new ArrayList<>();
        for(int i =0 ; i < jsonArray.size(); i++){
            JsonNode hr_zone_json_i = jsonArray.get(i);
            HeartrateZone hrZone_i = jsonToPOJO(fitbitHeartrateId, hr_zone_json_i);
            hrZones.add(hrZone_i);
        }

        return repository.saveAll(hrZones);
    }

    public HeartrateZone jsonToPOJO(Long fitbitHeartrateId, JsonNode node){
        checkNotNull(fitbitHeartrateId, "fitbitheartrate-id cannot be null in HeartrateZoneService.jsonToPOJO");
        HeartrateZone zone = new HeartrateZone(fitbitHeartrateId);
        Optional.ofNullable(node.get("caloriesOut")).ifPresent((JsonNode x) -> zone.setCaloriesOut(x.asDouble()));
        Optional.ofNullable(node.get("min")).ifPresent((JsonNode x) -> zone.setMin(x.asInt()));
        Optional.ofNullable(node.get("max")).ifPresent((JsonNode x) -> zone.setMax(x.asInt()));
        Optional.ofNullable(node.get("minutes")).ifPresent((JsonNode x) -> zone.setMinutes(x.asInt()));
        Optional.ofNullable(node.get("name")).ifPresent((JsonNode x) -> zone.setName(x.asText()));

        return zone;
    }

}
