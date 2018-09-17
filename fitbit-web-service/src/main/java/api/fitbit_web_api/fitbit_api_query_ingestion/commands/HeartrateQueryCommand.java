package api.fitbit_web_api.fitbit_api_query_ingestion.commands;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_api_query_ingestion.IFitbitQueryCommand;
import api.fitbit_web_api.fitbit_heartrate.FitbitHeartrateAPIService;
import api.fitbit_web_api.fitbit_heartrate.FitbitHeartrateService;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public class HeartrateQueryCommand implements IFitbitQueryCommand {
    private FitbitHeartrateAPIService heartrateAPIService;
    private FitbitUser fitbitUser;
    private LocalDateTime from;
    private LocalDateTime to;
    public HeartrateQueryCommand(FitbitHeartrateAPIService hrService){
        this.heartrateAPIService = hrService;
    }

    @Override
    public void setFitbitUser(FitbitUser fitbitUser) {
        this.fitbitUser = fitbitUser;
    }

    @Override
    public void setFitbitQueryService(IFitbitQueryService service) {

    }

    public void setHeartrateAPIService(FitbitHeartrateAPIService heartrateAPIService) {
        this.heartrateAPIService = heartrateAPIService;
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
