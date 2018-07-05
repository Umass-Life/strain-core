package api.FitbitAPI.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import api.FitbitAPI.Constants.ActivitiesResourcePath;
import api.FitbitAPI.FitbitAPIRequestBuilder;
import api.Utilities.ColorLogger;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

@Service
public class FitbitAPIService {
    static Logger log = Logger.getLogger(FitbitAPIService.class.getName());
    public static final String FITBIT_AUTH_URI = "https://www.fitbit.com/oauth2/authorize";
    public static final String FITBIT_ACCESS_TOKEN_URI = "https://api.fitbit.com/oauth2/token";
    public static final String FITBIT_REVOKE_ACCESS_RUI = "https://api.fitbit.com/oauth2/revoke";
    public static final String CLIENT_ID = "22CTFZ";
    public static final String CLIENT_SECRET = "2577c07d8af8c715a2a190fb240ebf3f";
    public Map<String, String> accessJson = new HashMap<>();

    static ColorLogger colorLog = new ColorLogger(log);
    public String authorize(){
        LinkedHashMap<AuthorizeParams, Object> reqMap = new LinkedHashMap<>();
        reqMap.put(AuthorizeParams.ResponseType, "code");
        reqMap.put(AuthorizeParams.ClientId, CLIENT_ID);
        reqMap.put(AuthorizeParams.Scope, new String[]{"activity","nutrition", "heartrate",
                                                        "profile", "settings", "sleep", "social", "weight"});
        String authorize_uri = FITBIT_AUTH_URI + "?" + formAuthorizeReqParams(reqMap);
        return authorize_uri;
    }

    public void requestAccessToken(String tempCode){
        String authorization_token = CLIENT_ID + ":" + CLIENT_SECRET;
        byte[] authorization_token_encoded_bytes = Base64.encodeBase64(authorization_token.getBytes());
        String authorization_token_encoded = new String(authorization_token_encoded_bytes);
        LinkedHashMap<AccessTokenParams, Object> reqMap = new LinkedHashMap<>();
        reqMap.put(AccessTokenParams.Code, tempCode);
        reqMap.put(AccessTokenParams.ClientId, CLIENT_ID);
        reqMap.put(AccessTokenParams.GrantType, "authorization_code");
        reqMap.put(AccessTokenParams.ExpiresIn, 600);
        List<BasicNameValuePair> formlist = new ArrayList<>();
        for (Map.Entry<AccessTokenParams, Object> e : reqMap.entrySet()){
            formlist.add(new BasicNameValuePair(e.getKey().toString(), e.getValue().toString()));
        }
        try {
            HttpPost httpPost = new HttpPost(FITBIT_ACCESS_TOKEN_URI);
            httpPost.addHeader("Authorization", "Basic " + authorization_token_encoded);
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
            HttpEntity body = new UrlEncodedFormEntity(formlist);
            httpPost.setEntity(body);
            HttpClient client = HttpClients.createDefault();
            HttpResponse response = client.execute(httpPost);
            String responseJson = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(responseJson);
            String access_token = node.get("access_token").asText();
            String expires_in = node.get("expires_in").asText();
            String refresh_token = node.get("refresh_token").asText();
            String scope = node.get("scope").asText();
            String fitbit_user_id = node.get("user_id").asText();
            String token_type = node.get("token_type").asText();
            accessJson.put("access_token", access_token);
            accessJson.put("expires_in", expires_in);
            accessJson.put("refresh_token", refresh_token);
            accessJson.put("scope", scope);
            accessJson.put("user_id", fitbit_user_id);
            accessJson.put("token_type", token_type);

            colorLog.info(accessJson);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void refreshAccessToken(){

    }

    public void revokeAccessToken(){

    }

    public JsonNode fetchProfile(){
        JsonNode node = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String userId = accessJson.get("user_id");
        String access_token = accessJson.get("access_token");
        FitbitAPIRequestBuilder fitbitAPIBuilder = new FitbitAPIRequestBuilder(userId);
        String profileUrl = fitbitAPIBuilder.buildProfileRequest();
        colorLog.info(profileUrl);
        HttpGet httpGet = new HttpGet(profileUrl);
        httpGet.addHeader("Authorization", String.format("Bearer %s", this.accessJson.get("access_token")));

        CloseableHttpClient client = HttpClients.createDefault();

        try {
            HttpResponse res = client.execute(httpGet);
            Integer statusCode = res.getStatusLine().getStatusCode();
            colorLog.info(res.getStatusLine());
            if (statusCode != 200) throw new IllegalArgumentException(res.getStatusLine().toString());
            HttpEntity entity = res.getEntity();
            String body = EntityUtils.toString(entity);
            Object json = objectMapper.writeValueAsString(body);
            node = objectMapper.readTree(body);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
            System.out.println(prettyJson);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe("Got code: " + e.getMessage());
        } finally {
            return node;
        }
    }

    public JsonNode fetchActivities(LocalDateTime date) throws IllegalAccessException {
        colorLog.info("FETCHING ACTIVITY SUMMARY: " + date);
        JsonNode node = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String userId = accessJson.get("user_id");
        String access_token = accessJson.get("access_token");
        FitbitAPIRequestBuilder fitbitAPIBuilder = new FitbitAPIRequestBuilder(userId);
        String url = fitbitAPIBuilder.buildActivitiesSummaryRequest(date);
        colorLog.info(url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Authorization", String.format("Bearer %s", this.accessJson.get("access_token")));

        CloseableHttpClient client = HttpClients.createDefault();

        try {
            HttpResponse res = client.execute(httpGet);
            Integer statusCode = res.getStatusLine().getStatusCode();
            colorLog.info(res.getStatusLine());
            if (statusCode != 200) throw new IllegalArgumentException(res.getStatusLine().toString());
            HttpEntity entity = res.getEntity();
            String body = EntityUtils.toString(entity);
            Object json = objectMapper.writeValueAsString(body);
            node = objectMapper.readTree(body);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
            colorLog.info(prettyJson);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe("Got code: " + e.getMessage());
            throw new IllegalAccessError(e.getMessage());
        } finally {
            return node;
        }
    }

    public JsonNode fetchActivities(ActivitiesResourcePath acp, LocalDateTime from, LocalDateTime to) throws IllegalAccessException  {
        JsonNode node = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String userId = accessJson.get("user_id");
        String access_token = accessJson.get("access_token");
        FitbitAPIRequestBuilder fitbitAPIBuilder = new FitbitAPIRequestBuilder(userId);
        String url = fitbitAPIBuilder.buildActivitiesTimeSeriesRequest(acp, from, to);
        colorLog.info(url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Authorization", String.format("Bearer %s", this.accessJson.get("access_token")));

        CloseableHttpClient client = HttpClients.createDefault();

        try {
            HttpResponse res = client.execute(httpGet);
            Integer statusCode = res.getStatusLine().getStatusCode();
            colorLog.info(res.getStatusLine());
            if (statusCode != 200) throw new IllegalArgumentException(res.getStatusLine().toString());
            HttpEntity entity = res.getEntity();
            String body = EntityUtils.toString(entity);
            Object json = objectMapper.writeValueAsString(body);
            node = objectMapper.readTree(body);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            colorLog.info(prettyJson);
        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe("Got code: " + e.getMessage());
            throw new IllegalAccessError(e.getMessage());
        } finally {
            return node;
        }
    }

    public String formAuthorizeReqParams(LinkedHashMap<AuthorizeParams, Object> json) throws IllegalStateException{
        StringBuffer buf = new StringBuffer("");
        int paramsCount = json.entrySet().size()-1;
        if (!json.containsKey(AuthorizeParams.ResponseType) || !(json.get(AuthorizeParams.ResponseType) instanceof String)){
            throw new IllegalStateException(String.format("request param doesn't have \'response_type\'or should be of type String"));
        }
        if (!json.containsKey(AuthorizeParams.ClientId) || !(json.get(AuthorizeParams.ClientId) instanceof String)){
            throw new IllegalStateException(String.format("request param doesn't have \'client_id\' or should be of type String"));
        }
        if (!json.containsKey(AuthorizeParams.Scope) || !(json.get(AuthorizeParams.Scope) instanceof String[])){
            throw new IllegalStateException(String.format("request param doesn't have \'scope\' or should be of type String[]"));
        }

        for (Map.Entry<AuthorizeParams, Object> e : json.entrySet()){
            try {
                String tuple = "";
                if (e.getKey().equals(AuthorizeParams.Scope)){
                    StringBuffer tupBuf = new StringBuffer(String.format("%s=", e.getKey()));
                    String[] val = (String[]) e.getValue();
                    for (int i = 0; i < val.length; i++) {
                        tupBuf.append(val[i]);
                        if (i < val.length - 1) {
                            tupBuf.append("%20");
                        }
                    }
                    tuple = tupBuf.toString();
                } else {
                    tuple = String.format("%s=%s", e.getKey(), e.getValue().toString());
                }

                buf.append(tuple);
                if (paramsCount-- > 0){
                    buf.append("&");
                }
            } catch (Exception exception){};
        }
        return buf.toString();
    }

    public String formAccessTokenReqParams(LinkedHashMap<AccessTokenParams, Object> json) throws IllegalStateException {
        StringBuffer buf = new StringBuffer("");
        int paramCount = json.entrySet().size() - 1;
        for(Map.Entry<AccessTokenParams, Object> e : json.entrySet()){
            String key = e.getKey().toString();
            Object value = e.getValue();
            buf.append(String.format("%s=%s",key, value));
            if (paramCount-- > 0){
                buf.append("&");
            }
        }
        return buf.toString();
    }

    public static enum AuthorizeParams {
        ResponseType("response_type"),
        Scope("scope"),
        ClientId("client_id"),
        RedirectURI("redirect_uri");

        private final String val;
        private AuthorizeParams(String val){
            this.val = val;
        }
        @Override
        public String toString(){
            return this.val;
        }
    }

    public static enum AccessTokenParams {
        ClientId("client_id"),
        GrantType("grant_type"), // usually "authorization_code"
        Code("code"),
        State("state"),
        ExpiresIn("expires_in"),
        RedirectURI("redirect_uri"),
        CodeVerifier("code_verifier");

        private final String val;
        private AccessTokenParams(String val){
            this.val = val;
        }
        @Override
        public String toString(){ return this.val; }
    }
}

