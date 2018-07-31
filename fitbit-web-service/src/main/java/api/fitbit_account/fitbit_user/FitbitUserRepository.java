package api.fitbit_account.fitbit_user;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface FitbitUserRepository extends PagingAndSortingRepository<FitbitUser, Long>{
    Optional<FitbitUser> findByStrainUserId(Long strainUserId);
    Optional<FitbitUser> findByFitbitId(String fitbitId);
}
