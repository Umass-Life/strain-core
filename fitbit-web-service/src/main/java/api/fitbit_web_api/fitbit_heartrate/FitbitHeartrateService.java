package api.fitbit_web_api.fitbit_heartrate;

import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
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
public class FitbitHeartrateService {

    private static final Logger logger = Logger.getLogger(FitbitHeartrateService.class.getSimpleName());
    private static final ColorLogger colorLog = new ColorLogger(logger);

    @Autowired
    private FitbitHeartrateRepository repository;

    private final String RESTING_HR = "restingHeartRate";
    private final String VALUE = "value";
    private final String SORT_KEY = "dateTime";

    public Iterable<FitbitHeartrate> list(){
        return this.repository.findAll();
    }

    public Iterable<FitbitHeartrate> listByFitbitUserId(Long id){
        return this.repository.findByFitbitUserId(id);
    }


    public Iterable<FitbitHeartrate> list(Long fitbitUserId, String from, String to){
        Specification<FitbitHeartrate> specs = whereFitbitUserId(fitbitUserId);
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

        Page<FitbitHeartrate> page = repository.findAll(specs, PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.DESC, SORT_KEY));
        return page.getContent();
    }

    public Optional<FitbitHeartrate> getById(Long id){
        return repository.findById(id);
    }

    public Optional<FitbitHeartrate> findLatest(Long fitbitUserId){
        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, SORT_KEY);
        Page<FitbitHeartrate> result = repository.findByFitbitUserId(fitbitUserId, pageable);
        List<FitbitHeartrate> content = result.getContent();

        FitbitHeartrate hr = content.isEmpty() ? null : content.get(0);
        return Optional.ofNullable(hr);
    }

    public FitbitHeartrate create(Long fitbitUserId, JsonNode node){
        FitbitHeartrate fhr = jsonToPOJO(fitbitUserId, node);
        return repository.save(fhr);
    }

    public FitbitHeartrate jsonToPOJO(Long fitbitUserId, JsonNode node){
        checkNotNull(fitbitUserId ,"fitbitUserId cannot be null in FitbitHeartrateService.jsonToPOJO");
        if (!node.has(SORT_KEY)) throw new IllegalArgumentException("incorrect heart-activities json");
        String dateTimeString = node.get(SORT_KEY).asText();
        LocalDateTime dateTime = FitbitAuthenticationService.parseTimeParam(dateTimeString);
        Long dateTimeEpoch = EntityHelper.toEpochMilli(dateTime);
        FitbitHeartrate hr = new FitbitHeartrate(fitbitUserId, dateTimeEpoch);

        Optional.ofNullable(node.get(VALUE)).ifPresent(x -> {
            Optional.ofNullable(x.get(RESTING_HR)).ifPresent(y -> {
                hr.setRestingHeartRate(y.asInt());

            });
        });

        return hr;
    }


    private Specification<FitbitHeartrate> whereFitbitUserId(Long fitbitUserId){
        return (Root<FitbitHeartrate> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            return cb.equal(root.get("fitbitUserId"), fitbitUserId);
        };
    }

    private Specification<FitbitHeartrate> greaterThanEqualToDate(Long time){
        return (Root<FitbitHeartrate> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            return cb.greaterThanOrEqualTo(root.get("dateTime"), time);
        };
    }

    private Specification<FitbitHeartrate> lessThanEqualToDate(Long time){
        return (Root<FitbitHeartrate> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            return cb.lessThanOrEqualTo(root.get("dateTime"), time);
        };
    }


}
