package api.strain_user;


import domain.models.BaseEntity;

import javax.persistence.*;


@Table(
        name="strain_user"
)
@Entity
public class StrainUser extends BaseEntity{

    public static final String SINGULAR = StrainUser.class.getSimpleName();
    public static final String PLIURAL = StrainUser.class.getSimpleName() + "s";

    @Column(name="email", unique = true, nullable = false)
    private String email;
    @Column(name="password", nullable = false)
    private String password;

    public StrainUser(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public StrainUser(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}



