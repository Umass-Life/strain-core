package api.fitbit_web_api.fitbit_sleep.sleep_time_series;

import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_web_api.fitbit_sleep.FitbitSleep;
import api.fitbit_web_api.fitbit_sleep.FitbitSleepService;
import api.fitbit_web_api.fitbit_sleep.SleepStages;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.ColorLogger;
import util.EntityHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class SleepTimeSerieService {

    private static final Logger logger = Logger.getLogger(SleepTimeSerie.class.getSimpleName());
    private static final ColorLogger colorLog = new ColorLogger(logger);
    @Autowired
    private SleepTimeSerieRepository repository;

    @Autowired
    private FitbitSleepService fitbitSleepService;


    public Iterable<SleepTimeSerie> list(){
        return repository.findAll();
    }
    public Iterable<SleepTimeSerie> listBySleep(Long sleepSessionId){
        return repository.findByFitbitSleepId(sleepSessionId);
    }

    public Iterable<SleepTimeSerie> createBulk(Long sleepSessionId, Long fitbitUserId,  ArrayNode sleepJsonData){
        List<SleepTimeSerie> sleepList = new ArrayList<>();
        for(int i = 0; i < sleepJsonData.size(); i++){
            JsonNode sleep_i = sleepJsonData.get(i);
            SleepTimeSerie sleepTimeSerie_i = jsonToPOJO(sleepSessionId, fitbitUserId, sleep_i);
            sleepList.add(sleepTimeSerie_i);
//            colorLog.info("created\n"+sleepTimeSerie_i);
        }
        return repository.saveAll(sleepList);
    }

    public SleepTimeSerie jsonToPOJO(Long sleepSessionId, Long fitbitUserId, JsonNode json){
        if (!json.has("dateTime")) {
            throw new IllegalArgumentException("invalid SleepTimeSerie json: \n" +json);
        }
        String dateTimeString = json.get("dateTime").asText();
        LocalDateTime dateTime = FitbitAuthenticationService.parseLongTimeParam(dateTimeString);
        Long dateTimeEpoch = EntityHelper.toEpochMilli(dateTime);
        String levelString = json.get("level").asText();
        SleepStages sleepStage = SleepStages.valueOf(levelString);
        Integer seconds = json.get("seconds").asInt();

        SleepTimeSerie sleep = new SleepTimeSerie(sleepSessionId, fitbitUserId, dateTimeEpoch, sleepStage, seconds);
        return sleep;
    }

}
