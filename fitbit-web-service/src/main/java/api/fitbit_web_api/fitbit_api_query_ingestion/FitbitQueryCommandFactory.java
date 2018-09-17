package api.fitbit_web_api.fitbit_api_query_ingestion;

import api.IFitbitQueryService;
import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.fitbit_activity.aggregate.AggregateActivityService;
import api.fitbit_web_api.fitbit_activity.intraday.IntradayActivityService;
import api.fitbit_web_api.fitbit_api_query_ingestion.command_factories.*;
import api.fitbit_web_api.fitbit_heartrate.FitbitHeartrate;
import api.fitbit_web_api.fitbit_heartrate.FitbitHeartrateAPIService;
import api.fitbit_web_api.fitbit_sleep.FitbitSleepAPIService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.Validation;

import java.util.HashMap;
import java.util.Map;

@Service
public class FitbitQueryCommandFactory {
    @Autowired
    private FitbitProfileService profileService;
    @Autowired
    private AggregateActivityService aggregateActivityService;
    @Autowired
    private IntradayActivityService intradayActivityService;
    @Autowired
    private FitbitHeartrateAPIService heartrateAPIService;
    @Autowired
    private FitbitSleepAPIService sleepAPIService;
    @Autowired
    private FitbitUserService fitbitUserService;

    private Map<FitbitQueryTypes, IFitbitQueryService> typeToQueryService = null;
    private Map<FitbitQueryTypes, IFitbitQueryCommandFactory> typeToCommandFactory = null;

    private final Map<FitbitQueryTypes, IFitbitQueryService> initQueryServices(){
        Map<FitbitQueryTypes, IFitbitQueryService> map = new HashMap<>();
        map.put(FitbitQueryTypes.PROFILE, profileService);
        map.put(FitbitQueryTypes.AGGREGATE, aggregateActivityService);
        map.put(FitbitQueryTypes.INTRADAY, intradayActivityService);
        map.put(FitbitQueryTypes.SLEEP, sleepAPIService);
        map.put(FitbitQueryTypes.HEART, heartrateAPIService);
        return map;
    }

    private final Map<FitbitQueryTypes, IFitbitQueryCommandFactory> initQueryCommandFactories(){
        Map<FitbitQueryTypes, IFitbitQueryCommandFactory> map = new HashMap<>();
        map.put(FitbitQueryTypes.PROFILE, new ProfileQueryCommandFactory());
        map.put(FitbitQueryTypes.AGGREGATE, new AggregateActivityQueryCommandFactory());
        map.put(FitbitQueryTypes.INTRADAY, new IntradayActivityQueryCommandFactory());
        map.put(FitbitQueryTypes.SLEEP, new SleepQueryCommandFactory());
        map.put(FitbitQueryTypes.HEART, new HeartrateQueryCommandFactory());
        return map;
    }

    public IFitbitQueryService getQueryService(String queryType){
        FitbitQueryTypes type = FitbitQueryTypes.valueOf(queryType.toUpperCase());
        return getQueryService(type);

    }

    public IFitbitQueryService getQueryService(FitbitQueryTypes queryTypes){
        if (this.typeToQueryService == null){
            this.typeToQueryService = initQueryServices();
        }
        return this.typeToQueryService.get(queryTypes);
    }

    public IFitbitQueryCommandFactory getQueryCommandFactory(String queryType){
        FitbitQueryTypes type = FitbitQueryTypes.valueOf(queryType.toUpperCase());
        return getQueryCommandFactory(type);
    }

    public IFitbitQueryCommandFactory getQueryCommandFactory(FitbitQueryTypes queryType){
        if (this.typeToCommandFactory == null){
            this.typeToCommandFactory = initQueryCommandFactories();
        }
        return this.typeToCommandFactory.get(queryType);
    }

    public IFitbitQueryCommand buildQueryCommand(FitbitUser fitbitUser,
                                            FitbitQueryTypes type,
                                            JsonNode parameters){
        Validation.checkNotNull(fitbitUser, "fitbitUser cannot be null");
        Validation.checkNotNull(type, "FitbitQueryType cannot be null in buildQueryCommand");
        IFitbitQueryCommandFactory factory = getQueryCommandFactory(type);
        IFitbitQueryService service = getQueryService(type);
        return factory.buildCommand(service, fitbitUser,parameters);

    }

}
