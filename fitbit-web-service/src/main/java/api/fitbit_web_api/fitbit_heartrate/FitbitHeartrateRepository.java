package api.fitbit_web_api.fitbit_heartrate;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FitbitHeartrateRepository extends PagingAndSortingRepository<FitbitHeartrate, Long> {

}
