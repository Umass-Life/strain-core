package api.constants;

public enum AccessTokenParams {
    Token("token"),
    ClientId("client_id"),
    GrantType("grant_type"), // usually "authorization_code"
    Code("code"),
    RefreshToken("refresh_token"),
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
