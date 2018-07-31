package api.fitbit_account.fitbit_profile;

import com.fasterxml.jackson.databind.JsonNode;
import domain.models.BaseEntity;
import util.ColorLogger;

import javax.persistence.*;
import java.util.logging.Logger;

/***
 *{
 * Full Fitbit Profile json from
 "user": {
     "aboutMe":<value>, "avatar":<value>,
     "city":<value>,
     "clockTimeDisplayFormat":<12hour|24hour>,
     "country":<value>,
     "dateOfBirth":<value>,
     "displayName":<value>,
     "distanceUnit":<value>,
     "encodedId":<value>,
     "foodsLocale":<value>,
     "fullName":<value>,
     "gender":<FEMALE|MALE|NA>,
     "glucoseUnit":<value>,
     "height":<value>,
     "heightUnit":<value>,
     "locale":<value>,
     "memberSince":<value>,
     "offsetFromUTCMillis":<value>,
     "startDayOfWeek":<value>,
     "state":<value>,
     "strideLengthRunning":<value>,
     "strideLengthWalking":<value>,
     "timezone":<value>,
     "waterUnit":<value>,
     "weight":<value>,
     "weightUnit":<value>
 }
 */

@Entity
public class FitbitProfile extends BaseEntity {

    @Transient
    private static final Logger logger = Logger.getLogger(FitbitProfile.class.getSimpleName());
    @Transient
    private static final ColorLogger colorLogger = new ColorLogger(logger);
    @Transient
    public static final String SINGULAR = FitbitProfile.class.getSimpleName();
    @Transient
    public static final String PLURAL = SINGULAR + "s";


    @Column(unique=true, updatable=false, nullable = false)
    private Long fitbitUserId;
    private String displayName;
    private String dateOfBirth;
    private String locale;
    private String distanceUnit;
    private double height;
    private String gender;
    private String heightUnit;
    private String city;
    @Column(nullable = false)
    private String memberSince;
    private String rawScopes;

    public FitbitProfile(){}

    public FitbitProfile(Long fitbitUserId){
        this.fitbitUserId = fitbitUserId;
    }

    public Long getFitbitUserId() {
        return fitbitUserId;
    }

    public void setFitbitUserId(Long fitbitUserId) {
        this.fitbitUserId = fitbitUserId;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRawScopes() {
        return rawScopes;
    }

    public void setRawScopes(String rawScopes) {
        this.rawScopes = rawScopes;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeightUnit() {
        return heightUnit;
    }

    public void setHeightUnit(String heightUnit) {
        this.heightUnit = heightUnit;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(String memberSince) {
        this.memberSince = memberSince;
    }



}
