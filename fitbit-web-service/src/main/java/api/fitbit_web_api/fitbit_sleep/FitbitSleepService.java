package api.fitbit_web_api.fitbit_sleep;

import api.FitbitConstantEnvironment;
import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_profile.FitbitProfile;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import api.fitbit_account.fitbit_user.FitbitUser;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import util.ColorLogger;
import util.EntityHelper;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import static util.Validation.checkNotNull;

@Service
public class FitbitSleepService {
    @Autowired
    FitbitSleepRepository fitbitSleepRepository;
    @Autowired
    FitbitConstantEnvironment constantEnvironment;
    @Autowired
    FitbitAuthenticationService authenticationService;
    @Autowired
    FitbitProfileService fitbitProfileService;

    private static final Logger logger = Logger.getLogger(FitbitSleepService.class.getSimpleName());
    private static final ColorLogger colorLog = new ColorLogger(logger);

    public Iterable<FitbitSleep> list(){
        return fitbitSleepRepository.findAll();
    }

    public Iterable<FitbitSleep> listByFitbitSleep(Long fitbitSleepid){
        return null;
    }

    public Iterable<FitbitSleep> listByFitbitUser(Long fitbitUser){
        return null;
    }


    public FitbitSleep create(Long fitbitUserId, JsonNode json){
        checkNotNull(fitbitUserId, "FitbitUser.id cannot be null when creating FitbitSleep");
        if (!json.has("dateOfSleep")) throw new IllegalArgumentException("incorrect sleep json format: " + json);
        FitbitSleep sleep = new FitbitSleep(fitbitUserId);
        String dateOfSleep = json.get("dateOfSleep").asText();
        Long duration = json.get("duration").asLong();
        Integer efficiency = json.get("efficiency").asInt();
        String endTimeString = json.get("endTime").asText();
        LocalDateTime endTimeDT = FitbitAuthenticationService.parseLongTimeParam(endTimeString);
        Long endTime = EntityHelper.toEpochMilli(endTimeDT);
        String infoCode = json.get("infoCode").asText();

        sleep.setDateOfSleep(dateOfSleep);
        sleep.setDuration(duration);
        sleep.setEfficiency(efficiency);
        sleep.setEndTime(endTime);
        sleep.setInfoCode(infoCode);

        return fitbitSleepRepository.save(sleep);
    }



}
