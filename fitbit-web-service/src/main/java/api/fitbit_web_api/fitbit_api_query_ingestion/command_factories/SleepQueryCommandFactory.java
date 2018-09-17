package api.fitbit_web_api.fitbit_api_query_ingestion.command_factories;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommand;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommandFactory;
import api.fitbit_web_api.fitbit_api_query_ingestion.commands.SleepQueryCommand;
import api.fitbit_web_api.fitbit_sleep.FitbitSleepAPIService;
import com.fasterxml.jackson.databind.JsonNode;

public class SleepQueryCommandFactory implements IFitbitQueryCommandFactory {
    @Override
    public IFitbitQueryCommand buildCommand(IFitbitQueryService service,
                                            FitbitUser fitbitUser,
                                            JsonNode queryParameters) {
        if (!(service instanceof FitbitSleepAPIService)){
            throw new IllegalArgumentException("IFitbitQueryService should be of type FitbitSleepAPIService in "
                    + this.getClass().getSimpleName());
        }

        SleepQueryCommand command = new SleepQueryCommand((FitbitSleepAPIService) service);
        return command;

    }
}
