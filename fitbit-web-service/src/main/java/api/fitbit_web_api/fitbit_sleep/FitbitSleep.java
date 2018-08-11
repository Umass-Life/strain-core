package api.fitbit_web_api.fitbit_sleep;


import domain.models.BaseEntity;

import javax.persistence.*;

@Entity
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"fitbit_user_id", "end_time" })}
)
public class FitbitSleep extends BaseEntity{

    @Transient
    public static final String SINGULAR = FitbitSleep.class.getSimpleName();
    @Transient
    public static final String PLURAL = SINGULAR + "List";

    @Column(nullable = false, updatable = false)
    private String dateOfSleep;

    @Column(name="fitbit_user_id", updatable=false, nullable = false)
    private Long fitbitUserId;

    private Long duration;

    private Integer efficiency;

    @Column(name="end_time")
    private Long endTime;

    private String infoCode;

    FitbitSleep(){}

    public FitbitSleep(Long fitbitUserId){
        this.fitbitUserId = fitbitUserId;
    }

    public String getDateOfSleep() {
        return dateOfSleep;
    }

    public void setDateOfSleep(String dateOfSleep) {
        this.dateOfSleep = dateOfSleep;
    }

    public Long getFitbitUserId() {
        return fitbitUserId;
    }

    public void setFitbitUserId(Long fitbitUserId) {
        this.fitbitUserId = fitbitUserId;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(Integer efficiency) {
        this.efficiency = efficiency;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getInfoCode() {
        return infoCode;
    }

    public void setInfoCode(String infoCode) {
        this.infoCode = infoCode;
    }
}
