package api.fitbit_web_api.fitbit_sleep;

import api.FitbitConstantEnvironment;
import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import api.fitbit_account.fitbit_user.FitbitUser;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import util.ColorLogger;
import util.EntityHelper;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    private final String SORT_KEY = "endTime";

    private static final Logger logger = Logger.getLogger(FitbitSleepService.class.getSimpleName());
    private static final ColorLogger colorLog = new ColorLogger(logger);

    public Iterable<FitbitSleep> list(){
        return fitbitSleepRepository.findAll();
    }

    public Iterable<FitbitSleep> list(Long fitbitUserId, String from, String to){
        Specification<FitbitSleep> specs = whereFitbitUserId(fitbitUserId);
        if (from!=null){
            LocalDateTime dt = FitbitAuthenticationService.parseTimeParam(from);
            Long time = EntityHelper.toEpochMilli(dt);
            specs = specs.and(greaterThanEqualToDate(time));
        }

        if(to!=null){
            LocalDateTime dt = FitbitAuthenticationService.parseTimeParam(to);
            Long time = EntityHelper.toEpochMilli(dt);
            specs = specs.and(lessThanEqualToDate(time));
        }

        colorLog.info("[Sleep] id=%s from=%s to=%s", fitbitUserId, from, to);
        Page<FitbitSleep> page = fitbitSleepRepository.findAll(specs, PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.DESC, SORT_KEY));
        return page.getContent();
    }


    public Iterable<FitbitSleep> listByFitbitSleep(Long fitbitSleepid){
        return null;
    }

    public Iterable<FitbitSleep> listByFitbitUser(Long fitbitUser){
        return null;
    }

    public Optional<FitbitSleep> getById(Long id){
        return fitbitSleepRepository.findById(id);
    }

    public Optional<FitbitSleep> findLatest(Long fitbitUserId){
        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, SORT_KEY);
        Page<FitbitSleep> page = fitbitSleepRepository.getByFitbitUserId(fitbitUserId, pageable);
        List<FitbitSleep> content = page.getContent();
        FitbitSleep sl = content.isEmpty() ? null : content.get(0);
        return Optional.ofNullable(sl);
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

    private Specification<FitbitSleep> whereFitbitUserId(Long fitbitUserId){
        return (Root<FitbitSleep> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            return cb.equal(root.get("fitbitUserId"), fitbitUserId);
        };
    }

    private Specification<FitbitSleep> greaterThanEqualToDate(Long time){
        return (Root<FitbitSleep> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            return cb.greaterThanOrEqualTo(root.get(SORT_KEY), time);
        };
    }

    private Specification<FitbitSleep> lessThanEqualToDate(Long time){
        return (Root<FitbitSleep> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            return cb.lessThanOrEqualTo(root.get(SORT_KEY), time);
        };
    }

}
