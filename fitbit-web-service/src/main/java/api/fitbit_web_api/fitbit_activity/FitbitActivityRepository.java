package api.fitbit_web_api.fitbit_activity;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FitbitActivityRepository extends PagingAndSortingRepository<FitbitActivity, Long>{
    Iterable<FitbitActivity> getByFitbitUserId(Long id);
    Iterable<FitbitActivity> getByFitbitUserIdAndType(Long id, ActivitiesResource type);
    Iterable<FitbitActivity> getByType(ActivitiesResource type);


}
