package api.ema;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StressLevelRepository extends PagingAndSortingRepository<StressLevel, Long>{

}
