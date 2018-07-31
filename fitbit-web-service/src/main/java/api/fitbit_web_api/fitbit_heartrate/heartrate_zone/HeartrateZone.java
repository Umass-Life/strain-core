package api.fitbit_web_api.fitbit_heartrate.heartrate_zone;

import domain.models.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;


/**
 *
 "caloriesOut": 740.15264,
 "max": 94,
 "min": 30,
 "minutes": 593,
 "name": "Out of Range"
 *
 */

@Entity
public class HeartrateZone extends BaseEntity {
    @Transient
    public static final String SINGULAR = HeartrateZone.class.getSimpleName();
    @Transient
    public static final String PLURAL = SINGULAR + "s";

    @Column(nullable=false)
    private Long fitbitHeartrateId;

    @Column(nullable=false)
    private Integer max;

    @Column(nullable=false)
    private Integer min;

    private Integer minutes;

    @Column(nullable=false)
    private String name;

    private Double caloriesOut;

    public HeartrateZone(){
        this.min = -1;
        this.max = -1;
        this.minutes = -1;
    }

    public HeartrateZone(Long fitbitHeartrateId) {
        this();
        this.fitbitHeartrateId = fitbitHeartrateId;

    }

    public Long getFitbitHeartrateId() {
        return fitbitHeartrateId;
    }

    public void setFitbitHeartrateId(Long fitbitHeartrateId) {
        this.fitbitHeartrateId = fitbitHeartrateId;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCaloriesOut() {
        return caloriesOut;
    }

    public void setCaloriesOut(Double caloriesOut) {
        this.caloriesOut = caloriesOut;
    }
}
