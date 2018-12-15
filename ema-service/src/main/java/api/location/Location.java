package api.location;

import domain.models.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Location extends BaseEntity {
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name="fitbit_id", nullable=false)
    private String fitbitId;

    @Column(name="strain_id", nullable=false)
    private Long strainId;

    public Location(){}
    public Location(Double latitude, Double longitude, String fitbitId, Long strainId){
        this.latitude = latitude;
        this.longitude = longitude;
        this.fitbitId = fitbitId;
        this.strainId = strainId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getFitbitId() {
        return fitbitId;
    }

    public void setFitbitId(String fitbitId) {
        this.fitbitId = fitbitId;
    }

    public Long getStrainId() {
        return strainId;
    }

    public void setStrainId(Long strainId) {
        this.strainId = strainId;
    }
}

