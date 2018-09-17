package api.fitbit_web_api.fitbit_api_query_ingestion.commands;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_activity.aggregate.AggregateActivityService;
import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResource;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommand;
import api.fitbit_web_api.fitbit_api_query_ingestion.command_factories.AggregateActivityQueryCommandFactory;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public class AggregateActivityQueryCommand implements IFitbitQueryCommand {
    private AggregateActivityService activityService;
    private FitbitUser fitbitUser;
    private ActivitiesResource resource;
    private LocalDateTime from;
    private LocalDateTime to;

    public AggregateActivityQueryCommand(AggregateActivityService activityService){
        this.activityService = activityService;
    }

    @Override
    public void setFitbitUser(FitbitUser fitbitUser) {
        this.fitbitUser = fitbitUser;
    }

    @Override
    public void setFitbitQueryService(IFitbitQueryService service) {
        if (!(service instanceof AggregateActivityService)){
            throw new IllegalArgumentException("IFitbitQueryService should be of type AggregateActivityService in "
                    + AggregateActivityQueryCommandFactory.class.getSimpleName());
        }
        this.activityService = (AggregateActivityService) service;
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
