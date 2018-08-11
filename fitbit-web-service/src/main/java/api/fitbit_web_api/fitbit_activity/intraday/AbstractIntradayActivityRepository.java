package api.fitbit_web_api.fitbit_activity.intraday;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface AbstractIntradayActivityRepository<T extends AbstractIntradayActivity>
        extends PagingAndSortingRepository<T, Long> {

    Iterable<T> findByFitbitUserId(Long id);
    Iterable<T> findAll(Sort sort);
    Page<T> findAll(Pageable pageable);
    Page<T> findAll(Specification<T> specs, Pageable pageble);
    
}
