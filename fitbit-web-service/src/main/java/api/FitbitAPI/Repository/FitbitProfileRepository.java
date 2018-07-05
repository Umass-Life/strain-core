package api.FitbitAPI.Repository;

import api.FitbitAPI.Models.FitbitProfile;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FitbitProfileRepository extends PagingAndSortingRepository<FitbitProfile, Long>{
    public FitbitProfile getProfileByUserId(Long userId);
}
