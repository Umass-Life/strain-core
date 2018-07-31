package api.fitbit_web_api.fitbit_activity;

public enum ActivitiesResource {
    calories("calories"),
    caloriesBMR("caloriesBMR"),
    steps("steps"),
    distance("distance"),
    floors("floors"),
    elevation("elevation"),
    minutesSedentary("minutesSedentary"),
    minutesLightlyActive("minutesLightlyActive"),
    minutesFairlyActive("minutesFairlyActive"),
    minutesVeryActive("minutesVeryActive"),
    activityCalories("activityCalories");
    
    private String val;
    private ActivitiesResource(String val){ this.val = val; }
    @Override
    public String toString(){
        return this.val;
    }
}
