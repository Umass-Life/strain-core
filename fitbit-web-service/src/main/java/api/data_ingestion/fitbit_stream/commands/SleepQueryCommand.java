package api.data_ingestion.fitbit_stream.commands;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.data_ingestion.fitbit_stream.IFitbitQueryCommand;
import api.fitbit_web_api.fitbit_sleep.FitbitSleepAPIService;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public class SleepQueryCommand implements IFitbitQueryCommand {
    private FitbitSleepAPIService fitbitSleepAPIService;
    private FitbitUser fitbitUser;
    private LocalDateTime from;
    private LocalDateTime to;
    public SleepQueryCommand(FitbitSleepAPIService sleepAPIService){
        this.fitbitSleepAPIService = sleepAPIService;
    }

    @Override
    public void setFitbitUser(FitbitUser fitbitUser) {
        this.fitbitUser = fitbitUser;
    }

    @Override
    public void setFitbitQueryService(IFitbitQueryService service) {
        if (!(service instanceof FitbitSleepAPIService)){
            throw new IllegalArgumentException("IFitbitQueryService should be of type FitbitSleepAPIService in "
                    + this.getClass().getSimpleName());
        }

        this.fitbitSleepAPIService = (FitbitSleepAPIService) service;
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
