package api.fitbit_web_api.fitbit_api_query_ingestion.command_factories;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommand;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommandFactory;
import api.fitbit_web_api.fitbit_api_query_ingestion.commands.HeartrateQueryCommand;
import api.fitbit_web_api.fitbit_heartrate.FitbitHeartrateAPIService;
import com.fasterxml.jackson.databind.JsonNode;

public class HeartrateQueryCommandFactory implements IFitbitQueryCommandFactory {
    @Override
    public IFitbitQueryCommand buildCommand(IFitbitQueryService service, FitbitUser fitbitUser, JsonNode queryParameters) {
        if (!(service instanceof FitbitHeartrateAPIService)){
            throw new IllegalArgumentException("IFitbitQueryService should be of type FitbitHeartrateAPIService in "
                    + this.getClass().getSimpleName());
        }

        HeartrateQueryCommand command = new HeartrateQueryCommand((FitbitHeartrateAPIService) service);

        return command;
    }
}
