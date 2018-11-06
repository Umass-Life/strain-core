package api.fitbit_account.fitbit_auth;

import api.FitbitConstantEnvironment;
import api.constants.AccessTokenParams;
import api.constants.AuthorizeParams;
import api.constants.AccessTokenResponseKey;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_account.fitbit_user.FitbitUserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.ColorLogger;
import util.StrainTimer;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;


/**l
 POST https://api.fitbit.com/oauth2/token
 Authorization: Basic MjJDVEZaOjI1NzdjMDdkOGFmOGM3MTVhMmExOTBmYjI0MGViZjNm=
 Content-Type: application/x-www-form-urlencoded

 client_id=22CTFZ&grant_type=authorization_code&redirect_uri=http%3A%2F%2Fexample.com%2Ffitbit_auth&code=4f4e03e98a053e6812010fee04f643237cf1d014


 GET https://api.fitbit.com/1/user/-/profile.json
 Authorization: Bearer <code>
 *
 * */

@Service
public class FitbitAuthenticationService {

    private final Logger log = Logger.getLogger(FitbitAuthenticationService.class.getName());
    private ColorLogger colorLog = new ColorLogger(log);

    @Autowired
    private FitbitConstantEnvironment fitbitEnv;

    @Autowired
    private FitbitProfileService fitbitProfileService;

    @Autowired
    private FitbitUserService fitbitUserService;

    private static boolean DEBUG_TIME = true;

    private String CLIENT_ID;
    private String CLIENT_SECRET;
    private String FITBIT_AUTH_URI;
    private String FITBIT_ACCESS_TOKEN_URI;
    private String FITBIT_REVOKE_TOKEN_URI;
    private String[] SCOPE_ARRAY;
    private Integer ACCESS_EXPIRES = 3600;
    public static final int RETRY_ATTEMPTS = 4;

    @PostConstruct
    public void init() throws Exception{
        this.CLIENT_ID = fitbitEnv.getClientId();
        this.CLIENT_SECRET = fitbitEnv.getClientSecret();
        this.FITBIT_AUTH_URI =  fitbitEnv.getUserAuthorizationUri();
        this.FITBIT_ACCESS_TOKEN_URI = fitbitEnv.getAccessTokenUri();
        this.SCOPE_ARRAY  = fitbitEnv.getScopeArray();
        this.FITBIT_REVOKE_TOKEN_URI = fitbitEnv.getAccessRevokeUri();
    }

    public void test(){
        log.info("\n in AuthenticationService \n" + this.fitbitEnv.toString());
    }

    public String authorize(Long strainUserId){
        LinkedHashMap<AuthorizeParams, Object> reqMap = new LinkedHashMap<>();
        reqMap.put(AuthorizeParams.ResponseType, "code");
        reqMap.put(AuthorizeParams.ClientId, CLIENT_ID);
        reqMap.put(AuthorizeParams.Scope, SCOPE_ARRAY);
        reqMap.put(AuthorizeParams.State, strainUserId);
        String authorize_uri = FITBIT_AUTH_URI + "?" + formAuthorizeReqParams(reqMap);
        return authorize_uri;
    }

    public Map<AccessTokenResponseKey, String> requestAccessToken(String tempCode, Long strainUserId){

//        FitbitUser fitbitUser = fitbitUserService.getByStrainUserId(strainUserId)
//                .orElseThrow(() -> new IllegalArgumentException("Cannot find user of id " + strainUserId));
        Map<AccessTokenResponseKey, String> accessResponseJson = new HashMap<>();

        String authorization_token = CLIENT_ID + ":" + CLIENT_SECRET;
        byte[] authorization_token_encoded_bytes = Base64.encodeBase64(authorization_token.getBytes());
        String authorization_token_encoded = new String(authorization_token_encoded_bytes);

        LinkedHashMap<AccessTokenParams, Object> reqMap = new LinkedHashMap<>();
        reqMap.put(AccessTokenParams.ExpiresIn, ACCESS_EXPIRES);
        reqMap.put(AccessTokenParams.Code, tempCode);
        reqMap.put(AccessTokenParams.ClientId, CLIENT_ID);
        reqMap.put(AccessTokenParams.GrantType, "authorization_code");

        ObjectMapper _om = new ObjectMapper();
        _om.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            colorLog.info(String.format("request access token\n%s\n%s\n", FITBIT_ACCESS_TOKEN_URI ,_om.writeValueAsString(reqMap)));
        } catch (Exception e){ e.printStackTrace(); }
        colorLog.warning(authorization_token_encoded);
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
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            JsonNode node = mapper.readTree(responseJson);
            JsonNode accessTokenNode = node.get(AccessTokenResponseKey.ACCESS_TOKEN.toString());
//            colorLog.warning(node.toString());
            if (node.has(AccessTokenResponseKey.ACCESS_TOKEN.toString())){
                colorLog.info("has access token\n" + node.toString());
                // if access_token key is present -- authorized access token
                String access_token = node.get(AccessTokenResponseKey.ACCESS_TOKEN.toString()).asText();
                String expires_in = node.get(AccessTokenResponseKey.EXPIRE_TOKEN.toString()).asText();
                String refresh_token = node.get(AccessTokenResponseKey.REFRESH_TOKEN.toString()).asText();
                String scope = node.get(AccessTokenResponseKey.SCOPE.toString()).asText();
                String fitbit_user_id = node.get(AccessTokenResponseKey.FITBIT_USER_ID.toString()).asText();
                String token_type = node.get(AccessTokenResponseKey.TOKEN_TYPE.toString()).asText();

                accessResponseJson.put(AccessTokenResponseKey.ACCESS_TOKEN, access_token);
                accessResponseJson.put(AccessTokenResponseKey.EXPIRE_TOKEN, expires_in);
                accessResponseJson.put(AccessTokenResponseKey.REFRESH_TOKEN, refresh_token);
                accessResponseJson.put(AccessTokenResponseKey.SCOPE, scope);
                accessResponseJson.put(AccessTokenResponseKey.FITBIT_USER_ID, fitbit_user_id);
                accessResponseJson.put(AccessTokenResponseKey.TOKEN_TYPE, token_type);
            } else {
                // otherwise {success: x, errorTypes:
                JsonNode successNode = node.get(AccessTokenResponseKey.SUCCESS_TOKEN.toString());
                ArrayNode errorArrays = (ArrayNode) node.get(AccessTokenResponseKey.ERRORS_TOKEN.toString());
                accessResponseJson.put(AccessTokenResponseKey.SUCCESS_TOKEN, successNode.toString());
                accessResponseJson.put(AccessTokenResponseKey.ERRORS_TOKEN, errorArrays.toString());
            }
        } catch (Exception e){
            accessResponseJson = new HashMap<>();
            accessResponseJson.put(AccessTokenResponseKey.ERRORS_TOKEN, e.getMessage());
            e.printStackTrace();
            colorLog.severe(e.getMessage());
        } finally {
            return accessResponseJson;
        }
    }

    public FitbitUser refreshAccessToken(FitbitUser fitbitUser){
        String refreshToken = fitbitUser.getRefreshToken();
        String authorization_token = CLIENT_ID + ":" + CLIENT_SECRET;
        byte[] authorization_token_encoded_bytes = Base64.encodeBase64(authorization_token.getBytes());
        String authorization_token_encoded = new String(authorization_token_encoded_bytes);
        final String BASIC_AUTH = "Basic " + authorization_token_encoded;
        // form body

        LinkedHashMap<AccessTokenParams, String> reqMap = new LinkedHashMap<>();
        reqMap.put(AccessTokenParams.RefreshToken, fitbitUser.getRefreshToken());
        reqMap.put(AccessTokenParams.GrantType, "refresh_token");
        List<BasicNameValuePair> formList =  new ArrayList<>();

        for (Map.Entry<AccessTokenParams, String> e : reqMap.entrySet()){
            formList.add(new BasicNameValuePair(e.getKey().toString(), e.getValue()));
        }
        colorLog.info(FITBIT_ACCESS_TOKEN_URI);
        colorLog.info(formList);
        colorLog.info(BASIC_AUTH);

        try  {
            HttpPost httpPost = new HttpPost(FITBIT_ACCESS_TOKEN_URI);
            httpPost.addHeader("Authorization", BASIC_AUTH);
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
            httpPost.setEntity(new UrlEncodedFormEntity(formList));

            CloseableHttpClient client = HttpClients.createDefault();
            HttpResponse response = client.execute(httpPost);
            String responseJson = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            JsonNode bodyJson = mapper.readTree(responseJson);
            colorLog.info(bodyJson);

            if (bodyJson.has(AccessTokenResponseKey.ACCESS_TOKEN.toString())){
                String newAccessToken  = bodyJson.get(AccessTokenResponseKey.ACCESS_TOKEN.toString()).asText();
                String newRefreshToken = bodyJson.get(AccessTokenResponseKey.REFRESH_TOKEN.toString()).asText();
                return fitbitUserService.updateTokens(fitbitUser.getId(), newAccessToken, newRefreshToken);

            } else {
               throw new IllegalStateException("unsuccesful request: " + bodyJson.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            return null;
        }
    }

    public FitbitUser revokeAccessToken(FitbitUser fitbitUser){
        String refreshToken = fitbitUser.getRefreshToken();
        String authorization_token = CLIENT_ID + ":" + CLIENT_SECRET;
        byte[] authorization_token_encoded_bytes = Base64.encodeBase64(authorization_token.getBytes());
        String authorization_token_encoded = new String(authorization_token_encoded_bytes);
        final String BASIC_AUTH = "Basic " + authorization_token_encoded;

        List<BasicNameValuePair> bodyList = new ArrayList<>();
        bodyList.add(new BasicNameValuePair(AccessTokenParams.Token.toString(), fitbitUser.getAccessToken()));

        try {
            HttpPost httpPost = new HttpPost(FITBIT_REVOKE_TOKEN_URI);
            httpPost.addHeader("Authorization", BASIC_AUTH);
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
            HttpEntity httpEntity = new UrlEncodedFormEntity(bodyList);
            httpPost.setEntity(httpEntity);
            CloseableHttpClient client = HttpClients.createDefault();

            HttpResponse response = client.execute(httpPost);
            colorLog.info(response.getStatusLine());

            String entity = EntityUtils.toString(response.getEntity());

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            JsonNode node = mapper.readTree(entity);

            return fitbitUser;

        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe(e.getMessage());
            return null;
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

    public JsonNode authorizedPOSTRequest(FitbitUser fitbitUser, String url, JsonNode inputBody){
        return authorizedPOSTRequest(fitbitUser, url, null, inputBody, 0);
    }

    public JsonNode authorizedPOSTRequest(FitbitUser fitbitUser, String url, Map<String, String> headers, JsonNode inputBody){
        return authorizedPOSTRequest(fitbitUser, url, headers, inputBody, 0);
    }

    public JsonNode authorizedPOSTRequest(FitbitUser fitbitUser, String url, Map<String, String> headers, JsonNode inputBody, int attempt){
        if (attempt == RETRY_ATTEMPTS){
            throw new IllegalStateException(String.format("request attempt more than %s times", RETRY_ATTEMPTS-1));
        }
        JsonNode node = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String userId = fitbitUser.getFitbitId();
        String access_token = fitbitUser.getAccessToken();

        colorLog.info("POST " + url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Authorization", String.format("Bearer %s", access_token));
        if (headers!=null){
            headers.entrySet().forEach((Map.Entry<String, String> e) ->{
                System.out.println(e.getKey() + " : "  + e.getValue());
                httpPost.addHeader(e.getKey(), e.getValue());
            });
        }

        StrainTimer timer = new StrainTimer(colorLog, "POST " + url);
        timer.start();
        try {
            if (inputBody!=null){
                httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
                StringEntity requestEntity = new StringEntity(
                        inputBody.toString(),
                        "application/json",
                        "UTF-8");
                httpPost.setEntity(requestEntity);
            }
//            debug_body(body);
//            colorLog.info(body);
            CloseableHttpClient client = HttpClients.createDefault();

            HttpResponse res = client.execute(httpPost);
            Integer statusCode = res.getStatusLine().getStatusCode();
            colorLog.info(res.getStatusLine());

            HttpEntity entity = res.getEntity();
            String body = EntityUtils.toString(entity);
            node = objectMapper.readTree(body);
            if (statusCode >= 300) {
                String SUCCESS_TOKEN = AccessTokenResponseKey.SUCCESS_TOKEN.toString();
                String ERRORS_TOKEN = AccessTokenResponseKey.ERRORS_TOKEN.toString();
                colorLog.warning(node);
                if (node.has(SUCCESS_TOKEN) && !node.get(SUCCESS_TOKEN).asBoolean()){
                    colorLog.warning(node.get(SUCCESS_TOKEN).asBoolean());
                    if (node.has(ERRORS_TOKEN)){
                        ArrayNode errorArrayNode = (ArrayNode) node.get(ERRORS_TOKEN);
                        colorLog.warning(String.format("Token might need to be refreshed for user\n%s\nwith err:\n %s",
                                fitbitUser, errorArrayNode));
                        JsonNode errNode = errorArrayNode.get(0);
                        String errorType = errNode.get(AccessTokenResponseKey.ERROR_TYPE.toString()).asText();
                        if(errorType.equals("expired_token")){
                            colorLog.warning("errorType : " + errorType);
                            if ((fitbitUser = refreshAccessToken(fitbitUser)) != null){
                                colorLog.warning("refreshed token for user:\n%s\non attempt %s", fitbitUser, attempt+1);
                                node = authorizedRequest(fitbitUser, url, attempt + 1);
                            } else {
                                throw new IllegalStateException("failed to refresh token on unauthorized request " +
                                        "for user-id = " + fitbitUser.getId());
                            }
                        }

                    }
                }

                colorLog.severe(node);
                throw new IllegalArgumentException(res.getStatusLine().toString());
            }

        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe("Got code: " + e.getMessage());
            throw new IllegalAccessError(e.getMessage());
        } finally {
            timer.stop();
            return node;
        }
    }
    /***
     * Generalized GET Request with Fitbit Authentication
     * @param fitbitUser
     * @param url
     * @return
     */
    public JsonNode authorizedRequest(FitbitUser fitbitUser, String url){
        return authorizedRequest(fitbitUser, url, 0);
    }

    public JsonNode authorizedRequest(FitbitUser fitbitUser, String url, int attempt){
        if (attempt == RETRY_ATTEMPTS){
            throw new IllegalStateException(String.format("request attempt more than %s times", RETRY_ATTEMPTS-1));
        }
        JsonNode node = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String userId = fitbitUser.getFitbitId();
        String access_token = fitbitUser.getAccessToken();

        colorLog.info("fetching " + url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Authorization", String.format("Bearer %s", access_token));

        CloseableHttpClient client = HttpClients.createDefault();
        StrainTimer timer = new StrainTimer(colorLog, "GET " + url);
        timer.start();

        try {
            HttpResponse res = client.execute(httpGet);
            Integer statusCode = res.getStatusLine().getStatusCode();
            colorLog.info(res.getStatusLine());

            HttpEntity entity = res.getEntity();
            String body = EntityUtils.toString(entity);
            node = objectMapper.readTree(body);
            if (statusCode != 200) {
                String SUCCESS_TOKEN = AccessTokenResponseKey.SUCCESS_TOKEN.toString();
                String ERRORS_TOKEN = AccessTokenResponseKey.ERRORS_TOKEN.toString();
                colorLog.warning(node);
                if (node.has(SUCCESS_TOKEN) && !node.get(SUCCESS_TOKEN).asBoolean()){
                    colorLog.warning(node.get(SUCCESS_TOKEN).asBoolean());
                    if (node.has(ERRORS_TOKEN)){
                        ArrayNode errorArrayNode = (ArrayNode) node.get(ERRORS_TOKEN);
                        colorLog.warning(String.format("Token might need to be refreshed for user\n%s\nwith err:\n %s",
                                fitbitUser, errorArrayNode));
                        JsonNode errNode = errorArrayNode.get(0);
                        String errorType = errNode.get(AccessTokenResponseKey.ERROR_TYPE.toString()).asText();
                        if(errorType.equals("expired_token")){
                            colorLog.warning("errorType : " + errorType);
                            if ((fitbitUser = refreshAccessToken(fitbitUser)) != null){
                                colorLog.warning("refreshed token for user:\n%s\non attempt %s", fitbitUser, attempt+1);
                                node = authorizedRequest(fitbitUser, url, attempt + 1);
                                return node; // skip to finall  y clause;
                            } else {
                                throw new IllegalStateException("failed to refresh token on unauthorized request " +
                                        "for user-id = " + fitbitUser.getId());
                            }
                        }
                    }
                }
                colorLog.severe(node);
                throw new IllegalArgumentException(res.getStatusLine().toString());
            }

        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe("Got code: " + e.getMessage());
            throw new IllegalAccessError(e.getMessage());
        } finally {
            timer.stop();
            return node;
        }
    }

    public JsonNode authorizedDELETERequest(FitbitUser fitbitUser, String url){
        return authorizedDELETERequest(fitbitUser, url, 0);
    }

    public JsonNode authorizedDELETERequest(FitbitUser fitbitUser, String url, int attempt){
        if (attempt == RETRY_ATTEMPTS){
            throw new IllegalStateException(String.format("request attempt more than %s times", RETRY_ATTEMPTS-1));
        }
        JsonNode node = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String userId = fitbitUser.getFitbitId();
        String access_token = fitbitUser.getAccessToken();

        colorLog.info("DELETE " + url);
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.addHeader("Authorization", String.format("Bearer %s", access_token));

        CloseableHttpClient client = HttpClients.createDefault();
        StrainTimer timer = new StrainTimer(colorLog, "GET " + url);
        timer.start();

        try {
            HttpResponse res = client.execute(httpDelete);
            Integer statusCode = res.getStatusLine().getStatusCode();
            colorLog.info(res.getStatusLine());

            HttpEntity entity = res.getEntity();
            if (entity!=null){
                String body = EntityUtils.toString(entity);
                node = objectMapper.readTree(body);
            }

            if (statusCode >= 300) {
                String SUCCESS_TOKEN = AccessTokenResponseKey.SUCCESS_TOKEN.toString();
                String ERRORS_TOKEN = AccessTokenResponseKey.ERRORS_TOKEN.toString();
                colorLog.warning(node);
                if (node.has(SUCCESS_TOKEN) && !node.get(SUCCESS_TOKEN).asBoolean()){
                    colorLog.warning(node.get(SUCCESS_TOKEN).asBoolean());
                    if (node.has(ERRORS_TOKEN)){
                        ArrayNode errorArrayNode = (ArrayNode) node.get(ERRORS_TOKEN);
                        colorLog.warning(String.format("Token might need to be refreshed for user\n%s\nwith err:\n %s",
                                fitbitUser, errorArrayNode));
                        JsonNode errNode = errorArrayNode.get(0);
                        String errorType = errNode.get(AccessTokenResponseKey.ERROR_TYPE.toString()).asText();
                        if(errorType.equals("expired_token")){
                            colorLog.warning("errorType : " + errorType);
                            if ((fitbitUser = refreshAccessToken(fitbitUser)) != null){
                                colorLog.warning("refreshed token for user:\n%s\non attempt %s", fitbitUser, attempt+1);
                                node = authorizedRequest(fitbitUser, url, attempt + 1);
                                return node; // skip to finall  y clause;
                            } else {
                                throw new IllegalStateException("failed to refresh token on unauthorized request " +
                                        "for user-id = " + fitbitUser.getId());
                            }
                        }
                    }
                }
                colorLog.severe(node);
                throw new IllegalArgumentException(res.getStatusLine().toString());
            }

        } catch (Exception e){
            e.printStackTrace();
            colorLog.severe("Got code: " + e.getMessage());
            throw new IllegalAccessError(e.getMessage());
        } finally {
            timer.stop();
            return node;
        }
    }

    public static LocalDateTime parseTimeParam(String date){
        // stub parse simple yyyy-MM-dd
        String[] spl = date.split("-");
        return LocalDateTime.of(new Integer(spl[0]),
                new Integer(spl[1]),
                new Integer(spl[2]), 0, 0);
    }

    public static LocalDateTime parseLongTimeParam(String date){
        //"endTime": "2018-07-19T09:07:30.000",
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern);
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return dateTime;
    }

    public static String toRequestDateFormat(LocalDateTime date){
        return String.format("%s-%02d-%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public static LocalDateTime getOldestPossibleTimeForRequest(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxPossibleTime = now.minusDays(14);
        return maxPossibleTime;
    }

    public static void validateRequestDates(String from, String to){
        LocalDateTime fromDate = parseTimeParam(from);
        LocalDateTime toDate = parseTimeParam(to);
        validateRequestDates(fromDate, toDate);
    }

    public static void validateRequestDates(LocalDateTime fromDate, LocalDateTime toDate){
        if (!fromDate.isBefore(toDate)) {
            throw new IllegalArgumentException(String.format("From date (%s) cannot be after To Date (%s)", fromDate, toDate));
        }
    }


    private void debug_body(HttpEntity body) throws IOException {
        System.out.println("=========");
        IOUtils.copy(body.getContent(), System.out);
        System.out.println();
    }

    public static String combineDateTime(String date, String time){
        return String.format("%sT%s", date, time);
    }

    public static void main(String[] args){
        System.out.println(LocalDateTime.parse("2018-05-05"));
    }

}
