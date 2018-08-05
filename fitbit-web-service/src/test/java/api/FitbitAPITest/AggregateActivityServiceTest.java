package api.FitbitAPITest;

import api.fitbit_web_api.fitbit_activity.aggregate.AggregateActivityService;
import util.ColorLogger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AggregateActivityServiceTest {
    static Logger log = Logger.getLogger(AggregateActivityServiceTest.class.getName());
    static ColorLogger colorLog = new ColorLogger(log);
    @Autowired
    AggregateActivityService fitbitActivityService;
//    @Test
//    public void test_authorize_string(){
//        Function<Void, Boolean> test = (Void t) -> {
//            fitbitActivityService.authorize();
//            LinkedHashMap<AggregateActivityService.AuthorizeParams, Object> reqMap = new LinkedHashMap<>();
//            reqMap.put(AggregateActivityService.AuthorizeParams.ResponseType, "code");
//            reqMap.put(AggregateActivityService.AuthorizeParams.ClientId, AggregateActivityService.CLIENT_ID);
//            reqMap.put(AggregateActivityService.AuthorizeParams.Scope, new String[]{"activity","nutrition", "heart",
//                    "profile", "settings", "sleep", "social", "weight"});
//            log.info("\n");
//            log.info(fitbitActivityService.formAuthorizeReqParams(reqMap));
//            return true;
//        };
//        assertThat(test.apply(null), is(true));
//    }
//
//    @Test
//    public void test_test_controller(){
//
//    }
//
//    @Test
//    public void test_authorize(){
//        String auth = this.fitbitActivityService.authorize();
//        try {
//            Assert.assertTrue(auth.length() > 0);
//        } catch (Exception e){
//            e.printStackTrace();
//            Assert.assertFalse(true);
//        }
//    }
//
//    @Test
//    public void test_request_builder(){
//        FitbitAPIRequestBuilder builder = new FitbitAPIRequestBuilder("T3ST");
//        String profileString = builder.buildProfileRequest();
//        LocalDateTime from = LocalDateTime.of(2018, 6, 1, 0, 0);
//        LocalDateTime to = LocalDateTime.of(2018, 6, 11, 0, 0);
//        String activitySummary = builder.buildActivitiesSummaryRequestURI(from);
//        List<String> acts = new ArrayList<>();
//        for(ActivitiesResource arp : ActivitiesResource.values()){
//            String activityTS = builder.buildActivitiesTimeSeriesRequest(arp, from, to);
//            acts.add(activityTS);
//        }
//        colorLog.info(profileString);
//        colorLog.info(activitySummary);
//        for (String resource_url : acts){
//            colorLog.warning(resource_url);
//        }
//    }
}
