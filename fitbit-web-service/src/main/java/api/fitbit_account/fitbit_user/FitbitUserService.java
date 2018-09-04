package api.fitbit_account.fitbit_user;

import api.FitbitConstantEnvironment;
import api.fitbit_account.fitbit_profile.FitbitProfile;
import api.fitbit_account.fitbit_profile.FitbitProfileService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.ColorLogger;
import util.Validation;

import static util.Validation.checkNotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class FitbitUserService {
    private static final Logger logger = Logger.getLogger(FitbitUserService.class.getSimpleName());
    private static final ColorLogger colorLogger = new ColorLogger(logger);

    public static final String SINGULAR = FitbitUser.class.getName();
    public static final String PLURAL = SINGULAR + "s";

    @Autowired
    FitbitUserRepository fitbitUserRepository;

    @Autowired
    FitbitConstantEnvironment fitbitConstantEnvironment;

    @Autowired
    FitbitProfileService fitbitProfileService;


    public Iterable<FitbitUser> list(){
        return fitbitUserRepository.findAll();
    }

    public Optional<FitbitUser> getById(Long id){
        checkNotNull(id, "FitbitUser id cannot be null in FitbitUserService.getById");
        return fitbitUserRepository.findById(id);
    }

    public Optional<FitbitUser> getByStrainUserId(Long strainUserId){
        checkNotNull(strainUserId, "strainUserId cannot be null in FitbitUserService.getByStrainUserId");
        return fitbitUserRepository.findByStrainUserId(strainUserId);
    }

    public Optional<FitbitUser> getByFitbitId(String fitbitId){
        checkNotNull(fitbitId, "fitbitId cannot be Null in FitbitUserService.getByFitbitId");
        return fitbitUserRepository.findByFitbitId(fitbitId);
    }

    public FitbitUser create(JsonNode json){
        // randomly generates UUID
        final String STRAIN_ID = "strainUserId";
        final String FITBIT_ID = "fitbitId";
        final String ACCESS_TOKEN_ID = "accessToken";
        final String REFRESH_TOKEN_ID = "refreshToken";
        final String TOKEN_TYPE_ID = "tokenType";
        final String EXPIRE_ID = "expiresIn";

        Long strainId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        if (json.has(STRAIN_ID)){
            strainId = json.get(STRAIN_ID).asLong();
        }

        String fitbitId = json.get(FITBIT_ID).asText();
        String accessToken = json.get(ACCESS_TOKEN_ID).asText();
        String refreshToken = json.get(REFRESH_TOKEN_ID).asText();
        String tokenType = json.get(TOKEN_TYPE_ID).asText();
        Long expiresIn = json.get(EXPIRE_ID).asLong();

        FitbitUser fitbitUser = create(strainId, fitbitId, accessToken, refreshToken, tokenType, expiresIn);
        return fitbitUser;

    }



    public FitbitUser create(Long strainUserId, String fitbitId, String accessToken, String refreshToken,
                             String tokenType, Long expiresIn){
        checkNotNull(strainUserId, "strainUserId cannot be null");
        checkNotNull(fitbitId, "fitbitId cannot be null");
        checkNotNull(accessToken, "access token cannot be null");
        checkNotNull(refreshToken, "refresh token cannot be null");
        if (expiresIn == null){
            expiresIn = fitbitConstantEnvironment.getAccessTokenExpire();
        }

        FitbitUser fitbitUser = new FitbitUser(strainUserId, fitbitId, accessToken,
                                                refreshToken, tokenType, expiresIn);


        return fitbitUserRepository.save(fitbitUser);
    }

    public FitbitUser createWithProfile(Long strainUserId, String fitbitId, String accessToken, String refreshToken,
                             String tokenType, Long expiresIn){
        FitbitUser fitbitUser = create(strainUserId,fitbitId,accessToken, refreshToken, tokenType,expiresIn);
        FitbitProfile profile = fitbitProfileService.fetchAndSave(fitbitUser);

        colorLogger.info("new profile \n%s", profile.toString());
        return fitbitUser;
    }

    public FitbitUser updateTokens(Long id, String accessToken, String refreshToken){
        checkNotNull(accessToken, "access-token cannot be null");
        checkNotNull(refreshToken, "refresh-token cannot be null");
        FitbitUser fitbitUser = getById(id).orElseThrow(() -> new IllegalArgumentException("cannot find fitbit user" +
                " with id = " + id));
        fitbitUser.setAccessToken(accessToken);
        fitbitUser.setRefreshToken(refreshToken);
        return fitbitUserRepository.save(fitbitUser);
    }

}
