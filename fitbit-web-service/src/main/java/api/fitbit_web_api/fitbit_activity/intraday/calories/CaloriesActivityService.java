package api.fitbit_web_api.fitbit_activity.intraday.calories;

import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_activity.intraday.IIntradayActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CaloriesActivityService implements IIntradayActivityService<CaloriesActivity> {
    @Autowired
    private FitbitUserService userService;
    @Autowired
    private CaloriesActivityRepository repository;

    @Override
    public Iterable<CaloriesActivity> list() {
        return repository.findAll();
    }

    @Override
    public Iterable<CaloriesActivity> list(Pageable page) {
        return null;
    }

    @Override
    public Optional<CaloriesActivity> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Iterable<CaloriesActivity> getByFitbitUserId(Long id) {
        return null;
    }

    @Override
    public Iterable<CaloriesActivity> save(Iterable<CaloriesActivity> entities) {
        return repository.saveAll(entities);
    }
    @Override
    public Iterable<CaloriesActivity> create(ArrayNode json) {
        return null;
    }

    @Override
    public CaloriesActivity jsonToPOJO(Long fitbitUserId, Long dateTime, Double value, JsonNode node) {
        CaloriesActivity entity = new CaloriesActivity();
        entity.setFitbitUserId(fitbitUserId);
        entity.setDateTime(dateTime);
        entity.setValue(value);
        return entity;
    }
}
