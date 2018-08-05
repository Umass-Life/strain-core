package api.fitbit_web_api.fitbit_activity.intraday;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import domain.models.BaseEntity;

import javax.persistence.*;


@MappedSuperclass
public class AbstractIntradayActivity extends BaseEntity{
    @Column(name="value", nullable=false, updatable = false)
    private Double value;
    @Column(name="date_time", nullable=false, updatable = false)
    private Long dateTime;
    @Column(name="fitbit_user_id", nullable=false, updatable = false)
    private Long fitbitUserId;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public Long getFitbitUserId() {
        return fitbitUserId;
    }

    public void setFitbitUserId(Long fitbitUserId) {
        this.fitbitUserId = fitbitUserId;
    }

    @Transient
    @JsonIgnore
    public static String key(){
        return AbstractIntradayActivity.class.getSimpleName();
    }
}
