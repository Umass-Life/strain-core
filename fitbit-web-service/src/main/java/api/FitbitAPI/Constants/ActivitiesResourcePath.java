package api.FitbitAPI.Constants;

public enum ActivitiesResourcePath {
    Calories("calories"),
    CaloriesBMR("caloriesBMR"),
    Steps("steps"),
    Distance("distance"),
    Floors("floors"),
    Elevation("elevation"),
    MinutesSedentary("minutesSedentary"),
    MinutesLightlyActive("minutesLightlyActive"),
    MinutesFairlyActive("minutesFairlyActive"),
    MinutesVeryActive("minutesVeryActive"),
    ActivityCalories("activityCalories"),
    Heart("heart");

    private String val;
    private ActivitiesResourcePath(String val){ this.val = val; }
    @Override
    public String toString(){
        return this.val;
    }

}
