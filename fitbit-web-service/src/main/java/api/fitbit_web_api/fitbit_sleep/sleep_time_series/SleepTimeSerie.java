package api.fitbit_web_api.fitbit_sleep.sleep_time_series;

import api.fitbit_web_api.fitbit_sleep.SleepStages;
import domain.models.BaseEntity;

import javax.persistence.*;

@Entity
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"fitbit_sleep_id", "date_time" })}
)
public class SleepTimeSerie extends BaseEntity {
    @Transient
    public static final String SINGULAR = SleepTimeSerie.class.getSimpleName();
    @Transient
    public static final String PLURAL = SINGULAR + "List";

    @Column(name="fitbit_sleep_id", updatable=false, nullable=false)
    private Long fitbitSleepId;

    @Column(name="fitbit_user_id", updatable=false, nullable=false)
    private Long fitbitUserId;

    @Column(name="date_time", nullable=false, updatable =false)
    private Long dateTime;

    @Column(nullable=false, updatable =false)
    @Enumerated(EnumType.STRING)
    private SleepStages level;

    @Column(nullable=false, updatable =false)
    private int seconds;

    public SleepTimeSerie(Long fitbitSleepId, Long fitbitUserId, Long dateTime, SleepStages level, int seconds){
        this.fitbitUserId = fitbitUserId;
        this.fitbitSleepId = fitbitSleepId;
        this.dateTime = dateTime;
        this.level = level;
        this.seconds = seconds;
    }

    public SleepTimeSerie(){}

    public Long getFitbitUserId() {
        return fitbitUserId;
    }

    public void setFitbitUserId(Long fitbitUserId) {
        this.fitbitUserId = fitbitUserId;
    }

    public Long getFitbitSleepId() {
        return fitbitSleepId;
    }

    public void setFitbitSleepId(Long fitbitSleepId) {
        this.fitbitSleepId = fitbitSleepId;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public SleepStages getLevel() {
        return level;
    }

    public void setLevel(SleepStages level) {
        this.level = level;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
