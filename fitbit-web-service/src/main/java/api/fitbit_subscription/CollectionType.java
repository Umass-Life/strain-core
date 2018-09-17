package api.fitbit_subscription;

public enum CollectionType {
    activities("activities"),
    foods("foods"),
    sleep("sleep"),
    body("body");
    private String val;
    CollectionType(String val){
        this.val = val;
    }
    @Override
    public String toString(){
        return this.val;
    }
}
