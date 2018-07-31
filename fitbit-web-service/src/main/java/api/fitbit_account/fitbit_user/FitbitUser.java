package api.fitbit_account.fitbit_user;

import domain.models.BaseEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Generated;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class FitbitUser extends BaseEntity {

    @Column(unique=true, updatable=false)
    private Long strainUserId;

    @Column(unique=true)
    private String fitbitId;

    @Column(length = 1024)
    private String accessToken;
    @Column(length = 1024)
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;

    public static final String SINGULAR = FitbitUser.class.getSimpleName();
    public static final String PLURAL = SINGULAR + "s";

    public FitbitUser(Long strainUserId, String fitbitId, String accessToken, String refreshToken, String tokenType, Long expiresIn){
        this.strainUserId = strainUserId;
        this.fitbitId = fitbitId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    public FitbitUser(){}

    public Long getStrainUserId() {
        return strainUserId;
    }

    public void setStrainUserId(Long strainUserId) {
        this.strainUserId = strainUserId;
    }

    public String getFitbitId() {
        return fitbitId;
    }

    public void setFitbitId(String fitbitId) {
        this.fitbitId = fitbitId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }


}
