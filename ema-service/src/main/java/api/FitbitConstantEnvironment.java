package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import util.ColorLogger;

import java.util.logging.Logger;

import static util.Validation.parseInt;

@Component
public class FitbitConstantEnvironment {

    Logger logger = Logger.getLogger(FitbitConstantEnvironment.class.getName());
    ColorLogger colorLog = new ColorLogger(logger);


    @Value("${security.oauth2.client.clientId}")
    private String clientId;

    @Value("${security.oauth2.client.clientSecret}")
    private String clientSecret;

    @Value("${security.oauth2.client.accessTokenUri}")
    private String accessTokenUri;

    @Value("${security.oauth2.client.revokeTokenUri}")
    private String accessRevokeUri;

    @Value("${security.oauth2.client.userAuthorizationUri}")
    private String userAuthorizationUri;

    @Value("${security.oauth2.client.scope}")
    private String scope;

    @Value("${fitbit.api.resource.domain}")
    private String fitbitAPIDomain;

    @Value("${security.oauth2.client.accessTokenExpire}")
    private String accessTokenExpire;

    @Value("${fitbit.api.resource.sleepDomain}")
    private String sleepDomain;

    private Long defaultAccessTokenExpire = 36600L;

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAccessTokenUri() {
        return accessTokenUri;
    }

    public String getAccessRevokeUri() { return accessRevokeUri; }

    public String getUserAuthorizationUri() {
        return userAuthorizationUri;
    }

    public String[] getScopeArray(){
        return scope.split(" ");
    }

    public String getScope(){
        return scope;
    }

    public String getFitbitAPIDomain() {
        return fitbitAPIDomain;
    }

    public String getSleepDomain(){ return sleepDomain; }

    public Long getAccessTokenExpire(){
        try {
            if (accessTokenExpire.isEmpty()){
                throw new IllegalArgumentException("security.oauth2.client.accessTokenExpire is an empty string");
            }
            Integer expireInt = parseInt(accessTokenExpire, "accessTokenExpire environemnt value not an integer");
            return new Long(expireInt);
        } catch (Exception e){
            colorLog.warning("Defaulting tokenExpire value to %s due to error parsing" +
                    "security.oauth2.client.accessTokenExpire with error message: %s", defaultAccessTokenExpire, e.getMessage());
            return defaultAccessTokenExpire;
        }
    }

    @Override
    public String toString() {
        try {
            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);
            return om.writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

