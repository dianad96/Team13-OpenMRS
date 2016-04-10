package org.openmrs.mobile.activities;

/**
 * Created by User on 08-Apr-16.
 */
public class GraphData {

    private String date;
    private String steps;
    private String distance;
    private String floor;
    private String calories;
    private String caloriesBurned;
    private String heartRate;
    private String activeMinutes;
    private String asleepMinutes;

    public GraphData() {}

    public GraphData(String date, String steps, String distance, String floor, String calories, String caloriesBurned, String heartRate, String activeMinutes, String asleepMinutes) {
        this.date = date;
        this.steps = steps;
        this.distance = distance;
        this.floor = floor;
        this.calories = calories;
        this.caloriesBurned = caloriesBurned;
        this.heartRate = heartRate;
        this.activeMinutes = activeMinutes;
        this.asleepMinutes = asleepMinutes;
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

    public String getActiveMinutes() {
        return activeMinutes;
    }

    public void setActiveMinutes(String activeMinutes) {
        this.activeMinutes = activeMinutes;
    }

    public String getAsleepMinutes() {
        return asleepMinutes;
    }

    public void setAsleepMinutes(String asleepMinutes) {
        this.asleepMinutes = asleepMinutes;
    }
}


