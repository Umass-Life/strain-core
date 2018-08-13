package api.fitbit_account.fitbit_subscription;

import api.FitbitConstantEnvironment;
import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_user.FitbitUser;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.Validation;

@Service
public class FitbitSubscriptionService {
    @Autowired
    private FitbitAuthenticationService authenticationService;

    @Autowired
    private FitbitConstantEnvironment constantEnvironment;

    public static final String SUBSCRIPTION_ID = "STRAIN";



    public JsonNode subscribeUser(FitbitUser user){
        Validation.checkNotNull(user, "Fitbit user cannot be null in subscribeUser");
        String subscriptionURI = buildSubscriptionURI(user);
        JsonNode node = authenticationService.authorizedPOSTRequest(user, subscriptionURI, null);
        return node;
    }

    // https://api.fitbit.com/1/user/-/[collection-path]/apiSubscriptions/[subscription-id].json
    public String buildSubscriptionURI(FitbitUser user){
        String FITBIT_DOMAIN = constantEnvironment.getFitbitAPIDomain();
        String fitbitId = user.getFitbitId();
        String subscriptionUrl = String.format("%s/user/%s/apiSubscriptions/%s.json",
                FITBIT_DOMAIN,
                fitbitId,
                SUBSCRIPTION_ID);
        return subscriptionUrl;
    }
}
