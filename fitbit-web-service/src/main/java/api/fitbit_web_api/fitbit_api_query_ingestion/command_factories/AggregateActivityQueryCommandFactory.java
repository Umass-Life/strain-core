package api.fitbit_web_api.fitbit_api_query_ingestion.command_factories;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_activity.aggregate.AggregateActivityService;
import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResourceAggregate;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommand;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommandFactory;
import api.fitbit_web_api.fitbit_api_query_ingestion.commands.AggregateActivityQueryCommand;
import com.fasterxml.jackson.databind.JsonNode;
import util.EntityHelper;

import java.time.LocalDateTime;

public class AggregateActivityQueryCommandFactory implements IFitbitQueryCommandFactory {
    private static final String FROM_DATE_KEY = "fromDate";
    private static final String TO_DATE_KEY = "toDate";
    private static final String RESOURCE_KEY = "resource";
    @Override
    public IFitbitQueryCommand buildCommand(IFitbitQueryService service,
                                            FitbitUser fibtitUser,
                                            JsonNode queryParameters) {
        if (!(service instanceof AggregateActivityService)){
            throw new IllegalArgumentException("IFitbitQueryService should be of type AggregateActivityService in "
                    + this.getClass().getSimpleName());
        }

        AggregateActivityQueryCommand command = new AggregateActivityQueryCommand((AggregateActivityService) service);
        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;

        JsonNode fromNode = queryParameters.get(FROM_DATE_KEY);
        JsonNode toNode = queryParameters.get(TO_DATE_KEY);
        JsonNode activitiesResourceNode = queryParameters.get(RESOURCE_KEY);
        if (!fromNode.isNull()){
            fromDate = EntityHelper.epochToDate(fromNode.asLong());
        }
        if (!toNode.isNull()){
            toDate = EntityHelper.epochToDate(toNode.asLong());
        }
        if (activitiesResourceNode.isNull()){
            throw new IllegalArgumentException("queryParameter JsonNode cannot have null resource field in " +
                    AggregateActivityQueryCommandFactory.class.getSimpleName());
        }
        String resourceString = activitiesResourceNode.get(RESOURCE_KEY).asText();

        ActivitiesResourceAggregate resource = ActivitiesResourceAggregate.valueOf(resourceString);
        command.setFrom(fromDate);
        command.setTo(toDate);
        command.setResource(resource);
        return command;
    }


}
