package api.fitbit_web_api.fitbit_api_query_ingestion.command_factories;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_activity.aggregate.AggregateActivityService;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommand;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommandFactory;
import api.fitbit_web_api.fitbit_api_query_ingestion.commands.AggregateActivityQueryCommand;
import com.fasterxml.jackson.databind.JsonNode;

public class AggregateActivityQueryCommandFactory implements IFitbitQueryCommandFactory {
    @Override
    public IFitbitQueryCommand buildCommand(IFitbitQueryService service,
                                            FitbitUser fibtitUser,
                                            JsonNode queryParameters) {
        if (!(service instanceof AggregateActivityService)){
            throw new IllegalArgumentException("IFitbitQueryService should be of type AggregateActivityService in "
                    + this.getClass().getSimpleName());
        }

        AggregateActivityQueryCommand command = new AggregateActivityQueryCommand((AggregateActivityService) service);

        return command;
    }


}
