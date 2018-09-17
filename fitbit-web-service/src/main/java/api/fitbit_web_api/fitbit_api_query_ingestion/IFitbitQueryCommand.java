package api.fitbit_web_api.fitbit_api_query_ingestion;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_user.FitbitUser;
import com.fasterxml.jackson.databind.JsonNode;

public interface IFitbitQueryCommand {
    JsonNode executeSync();
    void setFitbitUser(FitbitUser fitbitUser);
    void setFitbitQueryService(IFitbitQueryService service);
}
