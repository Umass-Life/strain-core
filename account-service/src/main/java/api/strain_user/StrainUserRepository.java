package api.strain_user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StrainUserRepository extends CrudRepository<StrainUser, Long> {
    Optional<StrainUser> getByEmail(String email);
}
