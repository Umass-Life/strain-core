package api.constants;

public enum AccessTokenResponseKey {
    ACCESS_TOKEN("access_token"),
    REFRESH_TOKEN("refresh_token"),
    EXPIRE_TOKEN("expires_in"),
    SCOPE("scope"),
    FITBIT_USER_ID("user_id"),
    TOKEN_TYPE("token_type"),
    ERRORS_TOKEN("errors"),
    SUCCESS_TOKEN("success"),
    ERROR_TYPE("errorType"),
    ERROR_MSG("message");


    private String val;
    private AccessTokenResponseKey(String val){ this.val = val; }

    @Override
    public String toString() {
        return this.val;
    }

    /*
    accessJson.put("access_token", access_token);
    accessJson.put("expires_in", expires_in);
    accessJson.put("refresh_token", refresh_token);
    accessJson.put("scope", scope);
    accessJson.put("user_id", fitbit_user_id);
    accessJson.put("token_type", token_type);
    */
}
