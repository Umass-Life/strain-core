package api.fitbit_web_api.fitbit_activity;

import api.FitbitConstantEnvironment;
import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResource;
import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResourceAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class ActivityAPIService {
    @Autowired
    private FitbitConstantEnvironment fitbitConstantEnvironment;
    public String FITBIT_AUTH_URI;
    public String FITBIT_ACCESS_TOKEN_URI;
    public String CLIENT_ID;
    public String CLIENT_SECRET;
    public String RESOURCE_URL;

    public static final String MIN_1 = "1min";
    public static final String SEC_1 = "1sec";
    public static final Set<ActivitiesResource> ONE_SEC_TIMESERIE_SET =
            new HashSet<>(Arrays.asList(ActivitiesResource.heart));

    @PostConstruct
    public void init(){
        FITBIT_AUTH_URI = fitbitConstantEnvironment.getUserAuthorizationUri();
        FITBIT_ACCESS_TOKEN_URI = fitbitConstantEnvironment.getAccessTokenUri();
        CLIENT_ID = fitbitConstantEnvironment.getClientId();
        CLIENT_SECRET = fitbitConstantEnvironment.getClientSecret();
        RESOURCE_URL = fitbitConstantEnvironment.getFitbitAPIDomain();
    }

    public String buildProfileRequest(FitbitUser fitbitUser){
        String profileRequestUrl = String.format("%s/user/%s/profile.json", RESOURCE_URL, fitbitUser.getFitbitId());
        return profileRequestUrl;
    }


    public String buildActivitiesSummaryRequestURI(FitbitUser fitbitUser, String date){
        return String.format("%s/user/%s/activities/date/%s.json", RESOURCE_URL, fitbitUser.getFitbitId(), date);
    }

    public String buildActivitiesRequestURI(FitbitUser fitbitUser, ActivitiesResourceAggregate resourceType,
                                            String dateFrom, String dateTo){
        return String.format("%s/user/%s/activities/%s/date/%s/%s.json", RESOURCE_URL, fitbitUser.getFitbitId(),
                resourceType.toString(), dateFrom, dateTo);
    }

    public String buildFinegrainActivitiesURI(FitbitUser fitbitUser, ActivitiesResource resourceType, String date){
        if (is1SecTimeSerie(resourceType)){
            return buildFinegrainActivitiesURI(fitbitUser, resourceType, date, SEC_1);
        }
        return buildFinegrainActivitiesURI(fitbitUser, resourceType, date, MIN_1);
    }

    public String buildFinegrainActivitiesURI(FitbitUser fitbitUser, ActivitiesResource resourceType, LocalDateTime date){
        if (is1SecTimeSerie(resourceType)){
            return buildFinegrainActivitiesURI(fitbitUser, resourceType, date, SEC_1);
        }
        return buildFinegrainActivitiesURI(fitbitUser, resourceType, date, MIN_1);
    }

    public String buildFinegrainActivitiesURI(FitbitUser fitbitUser, ActivitiesResource resourceType, String date, String detailType){
        return String.format("%s/user/%s/activities/%s/date/%s/1d/%s.json", RESOURCE_URL, fitbitUser.getFitbitId(),
                resourceType.toString(), date, detailType);
    }

    //GET https://api.fitbit.com/1/user/-/[resource-path]/date/[date]/1d/[detail-level]/time/[start-time]/[end-time].json
    public String buildFinegrainActivitiesURI(FitbitUser fitbitUser, ActivitiesResource resourceType, LocalDateTime date, String detailType){
        String dateString = FitbitAuthenticationService.toRequestDateFormat(date);
        return String.format("%s/user/%s/activities/%s/date/%s/1d/%s/time/%s:%s/23:59.json",
                RESOURCE_URL, fitbitUser.getFitbitId(), resourceType.toString(), dateString,
                detailType, date.getHour(), date.getMinute());

    }

    public String buildActivitiesTrackerTimeSeriesRequest(FitbitUser fitbitUser, ActivitiesResourceAggregate resourceType,
                                                          String dateFrom, String dateTo){
        return String.format("%s/user/%s/activities/tracker/%s/date/%s/%s.json", RESOURCE_URL, fitbitUser.getFitbitId(),
                resourceType.toString(), dateFrom, dateTo);
    }

    public static String getIntraDayActivitiesKey(ActivitiesResource r){
        return String.format("activities-%s-intraday", r.toString());
    }

    public static String getActivitiesKey(String r){
        return "activities-"+r.toString();
    }
    public static String getActivitiesKey(ActivitiesResource r){
        return getActivitiesKey(r.toString());
    }
    public static String getActivitiesKey(ActivitiesResourceAggregate r){
        return getActivitiesKey(r.toString());
    }

    private boolean is1SecTimeSerie(ActivitiesResource r){
        return ONE_SEC_TIMESERIE_SET.contains(r);
    }
}
