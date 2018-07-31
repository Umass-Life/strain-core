package api.fitbit_web_api.fitbit_activity;


import domain.models.BaseEntity;

import javax.persistence.*;

@Entity
public class FitbitActivity extends BaseEntity {
    @Transient
    public static final String SINGULAR = "FitbitActivity";
    @Transient
    public static final String PLURAL = "FitbitActivities";
    @Column(updatable = false)
    private Long fitbitUserId;

    public FitbitActivity(){ }
    public FitbitActivity(Long fitbitUserId){
        this.fitbitUserId = fitbitUserId;
    }
    public FitbitActivity(Long fitbitUserId, ActivitiesResource type, Long dateTime, Double value){
        this(fitbitUserId);
        this.type=type;
        this.dateTime=dateTime;
        this.value=value;
    }

    @Enumerated(EnumType.STRING)
    private ActivitiesResource type;

    private Double value;

    private Long dateTime;

    public Long getFitbitUserId() {
        return fitbitUserId;
    }

    public void setFitbitUserId(Long fitbitUserId) {
        this.fitbitUserId = fitbitUserId;
    }

    public ActivitiesResource getType() {
        return type;
    }

    public void setType(ActivitiesResource type) {
        this.type = type;
    }

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
}
