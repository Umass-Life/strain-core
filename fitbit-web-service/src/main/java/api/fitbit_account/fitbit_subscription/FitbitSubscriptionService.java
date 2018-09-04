package api.fitbit_account.fitbit_subscription;

import api.FitbitConstantEnvironment;
import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_user.FitbitUser;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.Validation;

import java.util.*;

@Service
public class FitbitSubscriptionService {
    @Autowired
    private FitbitAuthenticationService authenticationService;

    @Autowired
    private FitbitConstantEnvironment constantEnvironment;

    public static final String SUBSCRIPTION_ID = "320";
    public static final List<CollectionType> subscriptionList = Arrays.asList(CollectionType.activities, CollectionType.sleep);

    public List<JsonNode> subscribeUser(FitbitUser user){
        Validation.checkNotNull(user, "Fitbit user cannot be null in subscribeUser");
        List<JsonNode> nodes = new ArrayList<>();
        subscriptionList.forEach(type -> {
            String subscriptionURI = buildSubscriptionURI(user, type);
            JsonNode node = authenticationService.authorizedPOSTRequest(user, subscriptionURI, null);
            nodes.add(node);
        });

        return nodes;
    }
    /**
     * Subscribe to Fitbit Web notification endpoint
     * @param user
     * @param collectionType
     * @return
     */
    public JsonNode subscribeUser(FitbitUser user, CollectionType collectionType){
        Validation.checkNotNull(user, "Fitbit user cannot be null in subscribeUser");
        String subscriptionURI = buildSubscriptionURI(user, collectionType);
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept-Language", "en_US");
        headers.put("X-Fitbit-Subscriber-id", SUBSCRIPTION_ID);
        JsonNode node = authenticationService.authorizedPOSTRequest(user, subscriptionURI, headers, null);
        return node;
    }

    public JsonNode listSubscriptionByUser(FitbitUser user){
        Validation.checkNotNull(user, "Fitbit user cannot be null in subscribeUser");
        String listingURI = buildSubscriptionListingURI(user);
        JsonNode node = authenticationService.authorizedRequest(user, listingURI);
        return node;
    }

    public JsonNode deleteSubscriptionByUser(FitbitUser user, CollectionType collectionType){
        Validation.checkNotNull(user, "Fitbit user cannot be nul in subscribeUser");
        String deleteSubscriptionURI = buildSubscriptionURI(user, collectionType);
        JsonNode node = authenticationService.authorizedDELETERequest(user, deleteSubscriptionURI);
        return node;
    }

    // https://api.fitbit.com/1/user/-/[collection-path]/apiSubscriptions/[subscription-id].json
    public String buildSubscriptionURI(FitbitUser user, CollectionType type){
        String FITBIT_DOMAIN = constantEnvironment.getFitbitAPIDomain();
        String fitbitId = user.getFitbitId();
        if (type == null){
            return String.format("%s/user/-/%s/apiSubscriptions.json");
        }
        return String.format("%s/user/-/%s/apiSubscriptions/1-%s.json", FITBIT_DOMAIN, type , type);
    }

    public String buildSubscriptionListingURI(FitbitUser user){
        String FITBIT_DOMAIN = constantEnvironment.getFitbitAPIDomain();
        String fitbitId = user.getFitbitId();
        String subscriptionUrl = String.format("%s/user/%s/apiSubscriptions.json",
                FITBIT_DOMAIN,
                fitbitId);
        return subscriptionUrl;
    }
}
