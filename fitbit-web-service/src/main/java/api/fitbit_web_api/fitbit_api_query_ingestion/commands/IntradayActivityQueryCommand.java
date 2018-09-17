package api.fitbit_web_api.fitbit_api_query_ingestion.commands;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResource;
import api.fitbit_web_api.fitbit_activity.intraday.IntradayActivityService;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommand;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public class IntradayActivityQueryCommand implements IFitbitQueryCommand {
    private IntradayActivityService activityService;
    private FitbitUser fitbitUser;
    private ActivitiesResource resource;
    private LocalDateTime from;
    private LocalDateTime to;
    public IntradayActivityQueryCommand(IntradayActivityService activityService){
        this.activityService = activityService;
    }

    @Override
    public void setFitbitUser(FitbitUser fitbitUser) {
        this.fitbitUser = fitbitUser;
    }

    @Override
    public void setFitbitQueryService(IFitbitQueryService service) {
        if (!(service instanceof IntradayActivityService)){
            throw new IllegalArgumentException("IFitbitQueryService should be of type IntradayActivityService in "
                    + this.getClass().getSimpleName());
        }
        this.activityService = (IntradayActivityService) service;
    }

    public void setResource(ActivitiesResource resource) {
        this.resource = resource;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    @Override
    public JsonNode executeSync() {
        return null;
    }
}
