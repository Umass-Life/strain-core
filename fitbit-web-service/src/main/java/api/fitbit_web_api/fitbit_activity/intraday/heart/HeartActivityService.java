package api.fitbit_web_api.fitbit_activity.intraday.heart;

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
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class HeartActivityService implements IIntradayActivityService{
    private static final Logger logger = Logger.getLogger(HeartActivityService.class.getSimpleName());
    private static final ColorLogger colorLog = new ColorLogger(logger);

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
    public Page findAll(Specification specs, Pageable pageble) {
        return repository.findAll(specs, pageble);
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
    public Integer save(Iterable entities) {
        Iterator<HeartActivity> itr = entities.iterator();
        int cnt = 0;
        int totalCount = 0;
        while(itr.hasNext()){
            try {
                HeartActivity hr = repository.save(itr.next());
                cnt+=1;
//                colorLog.info("saved " + hr.getDateTime());
            } catch(Exception e){
//                e.printStackTrace();
//                System.exit(1);
            } finally {
                totalCount+=1;
            }
        }
        colorLog.info("%d/%d", cnt, totalCount);
        return cnt;
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