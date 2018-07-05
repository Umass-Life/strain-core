package api.FitbitAPI;

import api.FitbitAPI.Constants.ActivitiesResourcePath;

import java.time.LocalDateTime;

public class FitbitAPIRequestBuilder {
    public static final String RESOURCE_URL = "https://api.fitbit.com";
    private String fitbitUserID;
    public FitbitAPIRequestBuilder(String fitbitUserID){
        this.fitbitUserID = fitbitUserID;
    }

    public String buildProfileRequest(){
        String profileRequestUrl = String.format("%s/1/user/%s/profile.json", RESOURCE_URL, this.fitbitUserID);
        return profileRequestUrl;
    }

    public String buildActivitiesSummaryRequest(LocalDateTime date){
        String dateString = toRequestDateFormat(date);
        return String.format("%s/1/user/%s/activities/date/%s.json", RESOURCE_URL, fitbitUserID, date);
    }

    public String buildActivitiesTimeSeriesRequest(ActivitiesResourcePath resourceType, LocalDateTime dateFrom, LocalDateTime dateTo){
        String dateFromString = toRequestDateFormat(dateFrom);
        String dateToString = toRequestDateFormat(dateTo);
        return String.format("%s/1/user/%s/activities/%s/date/%s/%s.json", RESOURCE_URL, this.fitbitUserID,
                                                                         resourceType.toString(), dateToString, dateFromString);
    }

    private String toRequestDateFormat(LocalDateTime date){
        return String.format("%s-%s-%s", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }
}
