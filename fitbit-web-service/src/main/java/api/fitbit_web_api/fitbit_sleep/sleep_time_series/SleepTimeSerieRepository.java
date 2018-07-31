package api.fitbit_web_api.fitbit_sleep.sleep_time_series;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SleepTimeSerieRepository extends PagingAndSortingRepository<SleepTimeSerie, Long> {
    Iterable<SleepTimeSerie> findByFitbitSleepId(Long id);


}
