package api.fitbit_web_api.fitbit_heartrate;

import domain.models.BaseEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import util.ColorLogger;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Entity
public class FitbitHeartrate extends BaseEntity{
    @Transient
    private static final Logger logger = Logger.getLogger(FitbitHeartrate.class.getName());
    @Transient
    private static final ColorLogger colorLogger = new ColorLogger(logger);

    @Transient
    static final String SINGULAR = FitbitHeartrate.class.getSimpleName();
    @Transient
    static final String PLURAL = SINGULAR + "s";

    @Column(nullable=false, updatable=false)
    private Long fitbitUserId;

    @Column(nullable=false, updatable=false)
    private Long dateTime;

    private Integer restingHeartRate;

    public FitbitHeartrate(){}
    public FitbitHeartrate(Long fitbitUserId, Long dateTime){
        this.fitbitUserId = fitbitUserId;
        this.dateTime = dateTime;
    }

    public Long getFitbitUserId() {
        return fitbitUserId;
    }

    public void setFitbitUserId(Long fitbitUserId) {
        this.fitbitUserId = fitbitUserId;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getRestingHeartRate() {
        return restingHeartRate;
    }

    public void setRestingHeartRate(Integer restingHeartRate) {
        this.restingHeartRate = restingHeartRate;
    }
}
