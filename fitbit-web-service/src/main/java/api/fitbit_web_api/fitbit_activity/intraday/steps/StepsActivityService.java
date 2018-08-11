package api.fitbit_web_api.fitbit_activity.intraday.steps;

import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_activity.intraday.IIntradayActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import util.ColorLogger;
import util.EntityHelper;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Logger;

import static api.fitbit_account.fitbit_auth.FitbitAuthenticationService.parseLongTimeParam;

@Service
public class StepsActivityService implements IIntradayActivityService<StepsActivity> {
    private static final Logger logger = Logger.getLogger(StepsActivityService.class.getSimpleName());
    private static final ColorLogger colorLog = new ColorLogger(logger);
    @Autowired
    private StepsActivityRepository repository;
    @Autowired
    private FitbitUserService userService;

    @Override
    public Iterable<StepsActivity> list() {
        return repository.findAll();
    }

    @Override
    public Iterable<StepsActivity> list(Pageable page) {
        return repository.findAll(page);
    }

    @Override
    public Page<StepsActivity> findAll(Specification<StepsActivity> specs, Pageable pageble) {
        return repository.findAll(specs, pageble);
    }

    @Override
    public Optional<StepsActivity> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Iterable<StepsActivity> getByFitbitUserId(Long id) {
        return repository.findByFitbitUserId(id);
    }

    @Override
    public Integer save(Iterable entities) {
        Iterator<StepsActivity> itr = entities.iterator();
        int cnt = 0;
        int totalCount = 0;
        while(itr.hasNext()){
            try {
                repository.save(itr.next());
                cnt+=1;
            } catch(Exception e){
//                colorLog.severe(e.getStackTrace()[0]);
            } finally {
                totalCount+=1;
            }
        }
        colorLog.info("%d/%d", cnt, totalCount);
        return cnt;
    }

    @Override
    public Iterable<StepsActivity> create(ArrayNode json) {
        return null;
    }



    @Override
    public StepsActivity jsonToPOJO(Long fitbitUserId, Long dateTime, Double value, JsonNode node) {
        StepsActivity entity = new StepsActivity();
        entity.setFitbitUserId(fitbitUserId);
        entity.setDateTime(dateTime);
        entity.setValue(value);
        return entity;
    }
}
