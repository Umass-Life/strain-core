package api.fitbit_web_api.fitbit_activity.intraday.floors;

import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_activity.intraday.IIntradayActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FloorsActivityService implements IIntradayActivityService<FloorsActivity> {
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
        return null;
    }

    @Override
    public Optional<FloorsActivity> getById(Long id) {
        return null;
    }

    @Override
    public Iterable<FloorsActivity> getByFitbitUserId(Long id) {
        return repository.findByFitbitUserId(id);
    }

    @Override
    public Iterable<FloorsActivity> save(Iterable<FloorsActivity> entities) {
        return repository.saveAll(entities);
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
