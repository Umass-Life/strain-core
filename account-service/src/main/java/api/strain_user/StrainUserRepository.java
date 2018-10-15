package api.strain_user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface StrainUserRepository extends PagingAndSortingRepository<StrainUser, Long> {
    Optional<StrainUser> getByEmail(String email);
}
