package api.FitbitAPITest;

import api.FitbitAPI.Constants.ActivitiesResourcePath;
import api.FitbitAPI.FitbitAPIRequestBuilder;
import api.FitbitAPI.Services.FitbitAPIService;
import api.Utilities.ColorLogger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(SpringRunner.class)
@SpringBootTest
public class FitbitAPIServiceTest {
    static Logger log = Logger.getLogger(FitbitAPIServiceTest.class.getName());
    static ColorLogger colorLog = new ColorLogger(log);
    @Autowired
    FitbitAPIService fitbitAPIService;
    @Test
    public void test_authorize_string(){
        Function<Void, Boolean> test = (Void t) -> {
            fitbitAPIService.authorize();
            LinkedHashMap<FitbitAPIService.AuthorizeParams, Object> reqMap = new LinkedHashMap<>();
            reqMap.put(FitbitAPIService.AuthorizeParams.ResponseType, "code");
            reqMap.put(FitbitAPIService.AuthorizeParams.ClientId, FitbitAPIService.CLIENT_ID);
            reqMap.put(FitbitAPIService.AuthorizeParams.Scope, new String[]{"activity","nutrition", "heartrate",
                    "profile", "settings", "sleep", "social", "weight"});
            log.info("\n");
            log.info(fitbitAPIService.formAuthorizeReqParams(reqMap));
            return true;
        };
        assertThat(test.apply(null), is(true));
    }

    @Test
    public void test_test_controller(){

    }

    @Test
    public void test_authorize(){
        String auth = this.fitbitAPIService.authorize();
        try {
            Assert.assertTrue(auth.length() > 0);
        } catch (Exception e){
            e.printStackTrace();
            Assert.assertFalse(true);
        }
    }

    @Test
    public void test_request_builder(){
        FitbitAPIRequestBuilder builder = new FitbitAPIRequestBuilder("T3ST");
        String profileString = builder.buildProfileRequest();
        LocalDateTime from = LocalDateTime.of(2018, 6, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2018, 6, 11, 0, 0);
        String activitySummary = builder.buildActivitiesSummaryRequest(from);
        List<String> acts = new ArrayList<>();
        for(ActivitiesResourcePath arp : ActivitiesResourcePath.values()){
            String activityTS = builder.buildActivitiesTimeSeriesRequest(arp, from, to);
            acts.add(activityTS);
        }
        colorLog.info(profileString);
        colorLog.info(activitySummary);
        for (String resource_url : acts){
            colorLog.warning(resource_url);
        }
    }
}
