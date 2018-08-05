package api.fitbit_web_api.fitbit_activity.intraday.elevation;

import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_activity.intraday.IIntradayActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ElevationActivityService implements IIntradayActivityService<ElevationActivity>{
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
        return null;
    }

    @Override
    public Optional<ElevationActivity> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Iterable<ElevationActivity> getByFitbitUserId(Long id) {
        return null;
    }

    @Override
    public Iterable<ElevationActivity> save(Iterable<ElevationActivity> entities) {
        return repository.saveAll(entities);
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
