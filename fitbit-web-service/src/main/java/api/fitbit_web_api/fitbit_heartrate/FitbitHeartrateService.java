package api.fitbit_web_api.fitbit_heartrate;

import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.ColorLogger;
import util.EntityHelper;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

import static util.Validation.checkNotNull;
@Service
public class FitbitHeartrateService {

    private static final Logger logger = Logger.getLogger(FitbitHeartrateService.class.getSimpleName());
    private static final ColorLogger colorLog = new ColorLogger(logger);

    @Autowired
    private FitbitHeartrateRepository repository;

    public Iterable<FitbitHeartrate> list(){
        return this.repository.findAll();
    }

    private final String RESTING_HR = "restingHeartRate";
    private final String VALUE = "value";

    public Optional<FitbitHeartrate> getById(Long id){
        return repository.findById(id);
    }

    public FitbitHeartrate create(Long fitbitUserId, JsonNode node){
        FitbitHeartrate fhr = jsonToPOJO(fitbitUserId, node);
        return repository.save(fhr);
    }

    public FitbitHeartrate jsonToPOJO(Long fitbitUserId, JsonNode node){
        checkNotNull(fitbitUserId ,"fitbitUserId cannot be null in FitbitHeartrateService.jsonToPOJO");
        if (!node.has("dateTime")) throw new IllegalArgumentException("incorrect heart-activities json");
        String dateTimeString = node.get("dateTime").asText();
        LocalDateTime dateTime = FitbitAuthenticationService.parseTimeParam(dateTimeString);
        Long dateTimeEpoch = EntityHelper.toEpochMilli(dateTime);
        FitbitHeartrate hr = new FitbitHeartrate(fitbitUserId, dateTimeEpoch);

        Optional.ofNullable(node.get(VALUE)).ifPresent(x -> {
            Optional.ofNullable(x.get(RESTING_HR)).ifPresent(y -> {
                colorLog.warning(y);
                hr.setRestingHeartRate(y.asInt());

            });
        });

        colorLog.info("HEART RATE POJO -----> \n" + hr);
        return hr;
    }


}
