package api.fitbit_web_api.fitbit_sleep;

import api.fitbit_account.fitbit_profile.FitbitProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FitbitSleepRepository extends PagingAndSortingRepository<FitbitSleep, Long>{
    Iterable<FitbitSleep> getByFitbitUserId(Long id);
    Page<FitbitSleep> getByFitbitUserId(Long id, Pageable page);
    Page<FitbitSleep> findAll(Specification<FitbitSleep> spec, Pageable page);
}
