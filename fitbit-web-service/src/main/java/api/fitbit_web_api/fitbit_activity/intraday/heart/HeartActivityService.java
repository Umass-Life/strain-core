package api.fitbit_web_api.fitbit_activity.intraday.heart;

import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_activity.intraday.IIntradayActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HeartActivityService implements IIntradayActivityService{
    @Autowired
    private HeartActivityRepository repository;
    @Autowired
    private FitbitUserService userService;

    public static final String ACTIVITIES_HEART = "activities-heart";
    public static final String ACTIVITIES_HEART_INTRADAY = "activities-heart";

    public Iterable<HeartActivity> list(){
        return repository.findAll();
    }

    public Iterable<HeartActivity> list(Pageable page){
        return repository.findAll(page);
    }

    @Override
    public Optional<HeartActivity> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Iterable<HeartActivity> getByFitbitUserId(Long id) {
        return repository.findByFitbitUserId(id);
    }

    @Override
    public Iterable<HeartActivity> save(Iterable entities) {
        return repository.saveAll(entities);
    }

    @Override
    public Iterable<HeartActivity> create(ArrayNode json) {
        return null;
    }

    @Override
    public HeartActivity jsonToPOJO(Long fitbitUserId, Long dateTime, Double value, JsonNode node) {
        HeartActivity entity = new HeartActivity();
        entity.setFitbitUserId(fitbitUserId);
        entity.setDateTime(dateTime);
        entity.setValue(value);
        return entity;
    }
}

/*
*
*     /*
    * [ activities-heart: [
            {
                dateTime: "2018-06-11",
                value: {
                    customHeartRateZones: [ ],
                    heartRateZones: [
                        {
                            caloriesOut: 1922.55066,
                            max: 98,
                            min: 30,
                            minutes: 982,
                            name: "Out of Range"
                        },....
                    ],
                    restingHeartRate: 61
                }
            }
        ],
        activities-heart-intraday: {
            dataset: [{}],
            datasetInterval: 1,
            datasetType: "second"
        }, .... ]

* */