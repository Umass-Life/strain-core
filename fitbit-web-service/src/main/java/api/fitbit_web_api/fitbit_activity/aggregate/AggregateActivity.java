package api.fitbit_web_api.fitbit_activity.aggregate;


import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResourceAggregate;
import domain.models.BaseEntity;

import javax.persistence.*;

@Table(
        name="aggregate_activity",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"fitbit_user_id", "date_time", "type" })}
)
@Entity
public class AggregateActivity extends BaseEntity {
    @Transient
    public static final String SINGULAR = "AggregateActivity";
    @Transient
    public static final String PLURAL = "FitbitActivities";

    @Column(name="fitbit_user_id", updatable = false)
    private Long fitbitUserId;

    @Column(updatable = false)
    private Double value;

    @Column(name="date_time", updatable = false)
    private Long dateTime;

    @Enumerated(EnumType.STRING)
    @Column(name="type", updatable = false)
    private ActivitiesResourceAggregate type;

    public AggregateActivity(){ }
    public AggregateActivity(Long fitbitUserId){
        this.fitbitUserId = fitbitUserId;
    }
    public AggregateActivity(Long fitbitUserId, ActivitiesResourceAggregate type, Long dateTime, Double value){
        this(fitbitUserId);
        this.type=type;
        this.dateTime=dateTime;
        this.value=value;
    }


    public Long getFitbitUserId() {
        return fitbitUserId;
    }

    public void setFitbitUserId(Long fitbitUserId) {
        this.fitbitUserId = fitbitUserId;
    }

    public ActivitiesResourceAggregate getType() {
        return type;
    }

    public void setType(ActivitiesResourceAggregate type) {
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
