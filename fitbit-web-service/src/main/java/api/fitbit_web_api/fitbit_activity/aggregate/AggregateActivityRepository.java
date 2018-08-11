package api.fitbit_web_api.fitbit_activity.aggregate;


import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResourceAggregate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregateActivityRepository extends PagingAndSortingRepository<AggregateActivity, Long>{
    Iterable<AggregateActivity> getByFitbitUserId(Long id);
    Iterable<AggregateActivity> getByFitbitUserIdAndType(Long id, ActivitiesResourceAggregate type);
    Iterable<AggregateActivity> getByType(ActivitiesResourceAggregate type);
    Page<AggregateActivity> findAll(Specification<AggregateActivity> specs, Pageable pageble);

//

}
