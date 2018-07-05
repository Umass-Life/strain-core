package api.strain_user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrainUserRepository extends PagingAndSortingRepository<StrainUser, Long> {

}
