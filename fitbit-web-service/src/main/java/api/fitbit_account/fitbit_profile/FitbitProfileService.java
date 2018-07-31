package api.fitbit_account.fitbit_profile;

import api.FitbitConstantEnvironment;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_web_api.fitbit_activity.FitbitActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.ColorLogger;
import util.EntityHelper;

import java.util.Optional;
import java.util.logging.Logger;

import static util.Validation.checkNotNull;
import static util.EntityHelper.updateBlock;
@Service
public class FitbitProfileService {

    private final Logger logger = Logger.getLogger(FitbitProfileService.class.getName());
    private final ColorLogger colorLogger = new ColorLogger(logger);

    @Autowired
    private FitbitConstantEnvironment fitbitConstantEnvironment;

    @Autowired
    private FitbitProfileRepository fitbitProfileRepository;

    @Autowired
    private FitbitActivityService fitbitActivityService;

    public Optional<FitbitProfile> getById(Long id){
        checkNotNull(id, "fitbit profile id cannot be null in FitbitProfileSerivce.getById");
        return fitbitProfileRepository.findById(id);
    }

    public Optional<FitbitProfile> getByFitbitUserId(Long fitbitUserId){
        checkNotNull(fitbitUserId, "fitbit user id cannot be null in FitbitProfileService.getByFitbitUserId");
        return fitbitProfileRepository.findByFitbitUserId(fitbitUserId);
    }

    public Iterable<FitbitProfile> list(){
        return fitbitProfileRepository.findAll();
    }

    public FitbitProfile create(Long fitbitUserId, JsonNode json, String scope){
        checkNotNull(fitbitUserId, "fitbitUserId cannot be null in FitbitProfileService.create");
        checkNotNull(scope, "scope cannot be null in FitbitProfileService.create");
        checkNotNull(json, "jsonNode cannot be null");
        FitbitProfile profile = new FitbitProfile(fitbitUserId);

        JsonNode jsonNode = json.get("user");
        if (jsonNode.isNull()){
            throw new IllegalArgumentException("The follwing Fitbit Profile Json is incorrect: " +
                    "must have user field.\n" + json);
        }

        JsonNode displayNameNode = jsonNode.get("displayName");
        JsonNode dobNode = jsonNode.get("dateOfBirth");
        JsonNode localeNode = jsonNode.get("locale");
        JsonNode distanceUnitNode = jsonNode.get("distanceUnit");
        JsonNode heightNode = jsonNode.get("height");
        JsonNode genderNode = jsonNode.get("gender");
        JsonNode heightUnitNode = jsonNode.get("heightUnit");
        JsonNode memberSinceNode = jsonNode.get("memberSince");
        JsonNode cityNode = jsonNode.get("timezone");

        profile.setDisplayName(displayNameNode.asText());
        profile.setDateOfBirth(dobNode.asText());
        profile.setLocale(localeNode.asText());
        profile.setDistanceUnit(distanceUnitNode.asText());
        profile.setHeight(heightNode.asDouble());
        profile.setGender(genderNode.asText());
        profile.setHeightUnit(heightUnitNode.asText());
        profile.setMemberSince(memberSinceNode.asText());
        profile.setCity(cityNode.asText());
        profile.setRawScopes(scope);

        return fitbitProfileRepository.save(profile);
    }

    public FitbitProfile update(Long id, JsonNode json, String scope){
        FitbitProfile profile = getById(id).orElseThrow(
                () -> new IllegalArgumentException("Cannot update FitbitProfile id = " + id)
        );
        JsonNode jsonNode = json.get("user");
        colorLogger.warning(jsonNode);
        Optional.ofNullable(jsonNode.get("displayName")).ifPresent((JsonNode x) -> profile.setDisplayName(x.asText()));
        Optional.ofNullable(jsonNode.get("dateOfBirth")).ifPresent((JsonNode x) -> profile.setDateOfBirth(x.asText()));
        Optional.ofNullable(jsonNode.get("locale")).ifPresent((JsonNode x)      -> profile.setLocale(x.asText()));
        Optional.ofNullable(jsonNode.get("distanceUnit")).ifPresent((JsonNode x) -> profile.setDistanceUnit(x.asText()));
        Optional.ofNullable(jsonNode.get("height")).ifPresent((JsonNode x)      -> profile.setHeight(x.asDouble()));
        Optional.ofNullable(jsonNode.get("gender")).ifPresent((JsonNode x)      -> profile.setGender(x.asText()));
        Optional.ofNullable(jsonNode.get("heightUnit")).ifPresent((JsonNode x)  -> profile.setHeightUnit(x.asText()));
        Optional.ofNullable(jsonNode.get("memberSince")).ifPresent((JsonNode x) -> profile.setMemberSince(x.asText()));
        Optional.ofNullable(jsonNode.get("timezone")).ifPresent((JsonNode x)    -> profile.setCity(x.asText()));
        if (scope != null){
            profile.setRawScopes(scope);
        }
        return fitbitProfileRepository.save(profile);
    }


    public JsonNode fetchProfileFromWebAPI(FitbitUser fitbitUser){
        JsonNode node = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String fitbitId = fitbitUser.getFitbitId();
        String access_token = fitbitUser.getAccessToken();
        String profileUrl = fitbitActivityService.buildProfileRequest(fitbitUser);
        colorLogger.info("fetching.. " + profileUrl);
        HttpGet httpGet = new HttpGet(profileUrl);
        httpGet.addHeader("Authorization", String.format("Bearer %s", access_token));

        CloseableHttpClient client = HttpClients.createDefault();

        try {
            HttpResponse res = client.execute(httpGet);
            Integer statusCode = res.getStatusLine().getStatusCode();
            colorLogger.info(res.getStatusLine());

            HttpEntity entity = res.getEntity();
            String body = EntityUtils.toString(entity);
            Object json = objectMapper.writeValueAsString(body);
            node = objectMapper.readTree(body);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
            if (statusCode != 200) {
                colorLogger.severe(prettyJson);
                throw new IllegalArgumentException(res.getStatusLine().toString());
            }
            colorLogger.info("\n--> profile json\n%s\n", prettyJson);
        } catch (Exception e){
            e.printStackTrace();
            node = null;
            colorLogger.severe("Got code: " + e.getMessage());
        } finally {
            return node;
        }
    }
}
