package api.data_ingestion.fitbit_stream.commands;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.data_ingestion.fitbit_stream.IFitbitQueryCommand;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public class ProfileQueryCommand implements IFitbitQueryCommand {
    private FitbitProfileService profileService;
    private FitbitUser fitbitUser;
    private LocalDateTime from;
    private LocalDateTime to;

    public ProfileQueryCommand(FitbitProfileService profileService){
        this.profileService = profileService;
    }

    @Override
    public void setFitbitUser(FitbitUser fitbitUser) {
        this.fitbitUser = fitbitUser;
    }

    @Override
    public void setFitbitQueryService(IFitbitQueryService service) {
        if (!(service instanceof FitbitProfileService)){
            throw new IllegalArgumentException("IFitbitQueryService should be of type FitbitProfileServicein "
                    + this.getClass().getSimpleName());
        }
        this.profileService = (FitbitProfileService) service;
    }

    public void setProfileService(FitbitProfileService profileService) {
        this.profileService = profileService;
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
