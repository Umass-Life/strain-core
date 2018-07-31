package api.fitbit_web_api.fitbit_sleep;

import api.fitbit_account.fitbit_profile.FitbitProfile;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FitbitSleepRepository extends PagingAndSortingRepository<FitbitSleep, Long>{
    Iterable<FitbitSleep> getByFitbitUserId(Long id);
    Optional<FitbitProfile> findByDateOfSleep(String dos);
}
