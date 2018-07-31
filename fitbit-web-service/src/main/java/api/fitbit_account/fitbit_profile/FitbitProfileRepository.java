package api.fitbit_account.fitbit_profile;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FitbitProfileRepository extends PagingAndSortingRepository<FitbitProfile, Long>{
    Optional<FitbitProfile> findByFitbitUserId(Long id);

}
