package api.data_ingestion.fitbit_stream.command_factories;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.data_ingestion.fitbit_stream.IFitbitQueryCommand;
import api.data_ingestion.fitbit_stream.IFitbitQueryCommandFactory;
import api.data_ingestion.fitbit_stream.commands.ProfileQueryCommand;
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
