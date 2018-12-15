package api.ema;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EMARepository extends PagingAndSortingRepository<EMA, Long> {
}
