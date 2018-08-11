package api.fitbit_web_api.fitbit_activity.intraday.elevation;

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

import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class ElevationActivityService implements IIntradayActivityService<ElevationActivity>{
    private static final Logger logger = Logger.getLogger(ElevationActivityService.class.getSimpleName());
    private static final ColorLogger colorLog = new ColorLogger(logger);
    @Autowired
    private ElevationActivityRepository repository;
    @Autowired
    private FitbitUserService userService;

    @Override
    public Iterable<ElevationActivity> list() {
        return repository.findAll();
    }

    @Override
    public Iterable<ElevationActivity> list(Pageable page) {
        return repository.findAll(page);
    }

    @Override
    public Page<ElevationActivity> findAll(Specification<ElevationActivity> specs, Pageable pageble) {
        return repository.findAll(specs, pageble);
    }

    @Override
    public Optional<ElevationActivity> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Iterable<ElevationActivity> getByFitbitUserId(Long id) {
        return repository.findByFitbitUserId(id);
    }

    @Override
    public Integer save(Iterable entities) {
        Iterator<ElevationActivity> itr = entities.iterator();
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
    public Iterable<ElevationActivity> create(ArrayNode json) {
        return null;
    }

    @Override
    public ElevationActivity jsonToPOJO(Long fitbitUserId, Long dateTime, Double value, JsonNode node) {
        ElevationActivity entity = new ElevationActivity();
        entity.setFitbitUserId(fitbitUserId);
        entity.setDateTime(dateTime);
        entity.setValue(value);
        return entity;
    }
}
