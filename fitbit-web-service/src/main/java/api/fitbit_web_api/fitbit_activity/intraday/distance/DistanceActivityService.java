package api.fitbit_web_api.fitbit_activity.intraday.distance;

import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_activity.intraday.IIntradayActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class DistanceActivityService implements IIntradayActivityService<DistanceActivity>{
    @Autowired
    private FitbitUserService userService;
    @Autowired
    private DistanceActivityRepository repository;
    @Override
    public Iterable<DistanceActivity> list() {
        return repository.findAll();
    }

    @Override
    public Iterable<DistanceActivity> list(Pageable page) {
        return null;
    }

    @Override
    public Optional<DistanceActivity> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Iterable<DistanceActivity> getByFitbitUserId(Long id) {
        return null;
    }

    @Override
    public Iterable<DistanceActivity> save(Iterable<DistanceActivity> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public Iterable<DistanceActivity> create(ArrayNode json) {
        return null;
    }

    @Override
    public DistanceActivity jsonToPOJO(Long fitbitUserId, Long dateTime, Double value, JsonNode node) {
        DistanceActivity entity = new DistanceActivity();
        entity.setFitbitUserId(fitbitUserId);
        entity.setDateTime(dateTime);
        entity.setValue(value);
        return entity;
    }
}
