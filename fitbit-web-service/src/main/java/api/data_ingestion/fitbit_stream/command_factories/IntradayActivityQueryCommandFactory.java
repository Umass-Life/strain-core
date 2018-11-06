package api.data_ingestion.fitbit_stream.command_factories;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_activity.intraday.IntradayActivityService;
import api.data_ingestion.fitbit_stream.IFitbitQueryCommand;
import api.data_ingestion.fitbit_stream.IFitbitQueryCommandFactory;
import api.data_ingestion.fitbit_stream.commands.IntradayActivityQueryCommand;
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