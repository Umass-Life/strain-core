package api.fitbit_web_api.fitbit_activity.intraday.floors;

import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_activity.intraday.IIntradayActivityService;
import api.fitbit_web_api.fitbit_activity.intraday.heart.HeartActivity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import util.ColorLogger;

import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class FloorsActivityService implements IIntradayActivityService<FloorsActivity> {
    private static final Logger logger = Logger.getLogger(FloorsActivityService.class.getSimpleName());
    private static final ColorLogger colorLog = new ColorLogger(logger);
    @Autowired
    private FloorsActivityRepository repository;
    @Autowired
    private FitbitUserService userService;

    @Override
    public Iterable<FloorsActivity> list() {
        return repository.findAll();
    }

    @Override
    public Iterable<FloorsActivity> list(Pageable page) {
        return repository.findAll(page);
    }

    @Override
    public Optional<FloorsActivity> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<FloorsActivity> findAll(Specification<FloorsActivity> specs, Pageable pageble) {
        return repository.findAll(specs, pageble);
    }

    @Override
    public Iterable<FloorsActivity> getByFitbitUserId(Long id) {
        return repository.findByFitbitUserId(id);
    }


    @Override
    public Integer save(Iterable entities) {
        Iterator<FloorsActivity> itr = entities.iterator();
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
    public Iterable<FloorsActivity> create(ArrayNode json) {
        return null;
    }

    @Override
    public FloorsActivity jsonToPOJO(Long fitbitUserId, Long dateTime, Double value, JsonNode node) {
        FloorsActivity entity = new FloorsActivity();
        entity.setFitbitUserId(fitbitUserId);
        entity.setDateTime(dateTime);
        entity.setValue(value);
        return entity;
    }
}
