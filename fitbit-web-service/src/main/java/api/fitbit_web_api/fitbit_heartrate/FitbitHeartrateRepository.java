package api.fitbit_web_api.fitbit_heartrate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FitbitHeartrateRepository extends PagingAndSortingRepository<FitbitHeartrate, Long> {
    Iterable<FitbitHeartrate> findByFitbitUserId(Long id);
    Page<FitbitHeartrate> findByFitbitUserId(Long id, Pageable page);
    Page<FitbitHeartrate> findAll(Specification<FitbitHeartrate> specs, Pageable page);

}
