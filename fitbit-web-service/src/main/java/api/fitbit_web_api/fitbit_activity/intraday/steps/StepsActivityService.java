package api.fitbit_web_api.fitbit_activity.intraday.steps;

import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_activity.intraday.IIntradayActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import util.EntityHelper;

import java.time.LocalDateTime;
import java.util.Optional;

import static api.fitbit_account.fitbit_auth.FitbitAuthenticationService.parseLongTimeParam;

@Service
public class StepsActivityService implements IIntradayActivityService<StepsActivity> {
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
        return null;
    }

    @Override
    public Optional<StepsActivity> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Iterable<StepsActivity> getByFitbitUserId(Long id) {
        return null;
    }

    @Override
    public Iterable<StepsActivity> save(Iterable<StepsActivity> entities) {
        return repository.saveAll(entities);
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
