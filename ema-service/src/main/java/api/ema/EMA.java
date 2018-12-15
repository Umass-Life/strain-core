package api.ema;

import domain.models.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"fitbit_id", "date_time", "ema_type"})
})
public class EMA extends BaseEntity {
    public static final String SINGULAR = EMA.class.getSimpleName();
    public static final String PLURAL = SINGULAR + "s";

    @Column(nullable = false)
    private Long strainId;

    @Column(name="fitbit_id", nullable = false)
    private String fitbitId;

    @Column(name="date_time", nullable = false)
    private Long dateTime;

    @Column(nullable = false)
    private String emaValue;

    @Column(name="ema_type", nullable = false)
    private String emaType;

    public EMA() {
    }

    public EMA(Long strainId, String fitbitId, Long dateTime, String emaType, String emaValue) {
        this.dateTime = dateTime;
        this.emaValue = emaValue;
        this.emaType = emaType;
        this.strainId = strainId;
        this.fitbitId = fitbitId;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public Long getStrainId() {
        return strainId;
    }

    public void setStrainId(Long strainId) {
        this.strainId = strainId;
    }

    public String getFitbitId() {
        return fitbitId;
    }

    public void setFitbitId(String fitbitId) {
        this.fitbitId = fitbitId;
    }

    public String getEmaValue() {
        return emaValue;
    }

    public void setEmaValue(String emaValue) {
        this.emaValue = emaValue;
    }

    public String getEmaType() {
        return emaType;
    }

    public void setEmaType(String emaType) {
        this.emaType = emaType;
    }
}
