package api.strain_user;

import api.strain_role.StrainRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static util.Validation.checkNotNull;

@Service
public class StrainUserService {
    @Autowired
    StrainUserRepository userRepository;

    public Iterable<StrainUser> list() {
        return userRepository.findAll();
    }

    public Optional<StrainUser> getById(Long id){
        return userRepository.findById(id);
    }

    public Optional<StrainUser> getByEmail(String email){
        return userRepository.getByEmail(email);
    }

    public StrainUser create(String email, String password){
        checkNotNull(email);
        checkNotNull(password);
        return userRepository.save(new StrainUser(email, password));
    }

    public StrainUser createWithUniqueEmail(String email, String password){
        return null;
    }

    public static Map<String, Object> buildAuthenticationResponse(boolean isAuthenticated, String username,
                                                                  StrainUser user, List<StrainRole> roles,
                                                                  Integer status, String error) {
        Map<String, Object> m = new HashMap<>();
        roles = roles == null ? new ArrayList<StrainRole>() : roles;
        m.put("isAuthenticated", isAuthenticated);
        m.put("username", username);
        m.put("roles", roles);
        if (user!=null) m.put("user", user);
        m.put("status", status);
        if (error!=null) m.put("error", error);
        return m;

    }



}
