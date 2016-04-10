package org.openmrs.mobile.activities;

/**
 * Created by User on 08-Apr-16.
 */
public class GraphData {

    private String date = "-";
    private String steps = "0";
    private String distance = "0";
    private String floor = "0";
    private String calories = "0";
    private String caloriesBurned = "0";
    private String heartRate = "0";
    private String activeMinutes = "0";

    public GraphData() {}

    public GraphData(String date, String steps, String distance, String floor, String calories, String caloriesBurned, String heartRate, String activeMinutes) {
        this.date = date;
        this.steps = steps;
        this.distance = distance;
        this.floor = floor;
        this.calories = calories;
        this.caloriesBurned = caloriesBurned;
        this.heartRate = heartRate;
        this.activeMinutes = activeMinutes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(String caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getActiveMinutes() { return activeMinutes; }

    public void setActiveMinutes(String activeMinutes) { this.activeMinutes = activeMinutes; }

}


