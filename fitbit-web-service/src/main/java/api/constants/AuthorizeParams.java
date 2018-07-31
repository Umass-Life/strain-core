package api.constants;

public enum AuthorizeParams {
    ResponseType("response_type"),
    Scope("scope"),
    ClientId("client_id"),
    RedirectURI("redirect_uri"),
    State("state");

    private final String val;
    private AuthorizeParams(String val){
        this.val = val;
    }
    @Override
    public String toString(){
        return this.val;
    }
}
