package api.fitbit_web_api.fitbit_api_query_ingestion.command_factories;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_activity.intraday.IntradayActivityService;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommand;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommandFactory;
import api.fitbit_web_api.fitbit_api_query_ingestion.commands.IntradayActivityQueryCommand;
import com.fasterxml.jackson.databind.JsonNode;

public class IntradayActivityQueryCommandFactory implements IFitbitQueryCommandFactory {
    @Override
    public IFitbitQueryCommand buildCommand(IFitbitQueryService service,
                                            FitbitUser fitbitUser,
                                            JsonNode queryParameters) {
        if (!(service instanceof IntradayActivityService)){
            throw new IllegalArgumentException("IFitbitQueryService should be of type IntradayActivityService in "
                    + this.getClass().getSimpleName());
        }

        IntradayActivityQueryCommand command = new IntradayActivityQueryCommand((IntradayActivityService) service);
        command.setFitbitUser(fitbitUser);
        return command;
    }
}