package api.fitbit_web_api.fitbit_api_query_ingestion.command_factories;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommand;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommandFactory;
import api.fitbit_web_api.fitbit_api_query_ingestion.commands.ProfileQueryCommand;
import com.fasterxml.jackson.databind.JsonNode;

public class ProfileQueryCommandFactory implements IFitbitQueryCommandFactory {
    @Override
    public IFitbitQueryCommand buildCommand(IFitbitQueryService service,
                                            FitbitUser fitbitUser,
                                            JsonNode queryParameters) {
        if (!(service instanceof FitbitProfileService)){
            throw new IllegalArgumentException("IFitbitQueryService should be of type FitbitProfileServicein "
                    + this.getClass().getSimpleName());
        }

        ProfileQueryCommand command = new ProfileQueryCommand((FitbitProfileService) service);

        return command;

    }
}
