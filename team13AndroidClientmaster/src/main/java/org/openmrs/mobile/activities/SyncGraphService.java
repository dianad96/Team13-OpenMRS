package org.openmrs.mobile.activities;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

import org.openmrs.mobile.activities.fragments.ApiAuthRest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by User on 09-Apr-16.
 */
public class SyncGraphService extends IntentService{

    private DBHelper dbHelper;
    private GraphData[] graphDatas;
    private Date[] dates;
    private String[] formattedDates;

    static String username = "diana";
    static String password = "Admin123";
    static String URLBase = "http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/";

    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;

    private static final String PREFERENCE_TYPE = "HealthDataPref";
    private static final String HEALTH_BMI = "BMI";
    private static final String HEALTH_TARGET_HR = "targetHR";
    private static final String HEALTH_LAST_SYNCED = "lastSynced";
    private static final String HEALTH_IS_SYNCED_TODAY = "syncedToday";


    private final static String user = Container.user_uuid;
    private String BMI = "", heartRate = "-", targetHR = "-";


    public SyncGraphService() {
        super("SyncGraphService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do work here

        dbHelper = new DBHelper(this);

        sharedpreferences = getSharedPreferences(PREFERENCE_TYPE, 4);
        editor = sharedpreferences.edit();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ApiAuthRest.setURLBase(URLBase);
        ApiAuthRest.setUsername(username);
        ApiAuthRest.setPassword(password);

        dates = new Date[5];
        formattedDates = new String[5];
        graphDatas = new GraphData[5];
        for(int i=0; i<5; i++){
            graphDatas[i] = new GraphData();
        }

        pullData(dates, getPersonInput(user));

        ArrayList<String> arrayList = dbHelper.getHealthData();
        String todayDate = getTodayDate(System.currentTimeMillis(), Container.DATE_FORMAT);
        boolean isSyncedToday = sharedpreferences.getBoolean(HEALTH_IS_SYNCED_TODAY, false);
        if(isSyncedToday) {
            editor.remove(HEALTH_LAST_SYNCED).commit();
            editor.putString(HEALTH_LAST_SYNCED, todayDate).apply();
        }

        for(int i=0; i<5; i++){
            if(!arrayList.contains(formattedDates[i]) && isSyncedToday) {
                insertToDB(i);
            }
            if(arrayList.contains(formattedDates[i]) && isSyncedToday){
                updateDB(arrayList.indexOf(formattedDates[i]));
            }
        }


    }

    private void pullData(Date[] dates, String input) {
        int location = 0;
        String UUID;
        SharedPreferences.Editor editor = sharedpreferences.edit();

        for (int temp = 0; temp < 5; temp++) {
            dates[temp] = getDate(temp);
            formattedDates[temp] = formatDate(dates[temp]);
        }

        while ((location = input.indexOf("uuid", location)) != -1) {
            location += 7;
            int templocation = input.indexOf("\"", location);
            UUID = input.substring(location,templocation);

            int a = checkConcept(input.substring(location));
            String obs = returnObs(UUID);
            int index;
            // Check if data is from Today date
            switch (a)
            {
                // Check Steps
                case 1:
                    index = checkExerciseDate(dates,obs);
                    if(index==0 || index==1 || index==2 || index==3 || index==4 ){
                        Float value = getConceptValue(obs,"STEPS");
                        if ( value > Float.parseFloat(graphDatas[index].getSteps()) ){
                            graphDatas[index].setSteps(value.toString()) ;
                        }
                        if(index==1)
                            editor.putBoolean(HEALTH_IS_SYNCED_TODAY, true).apply();
                    }
                    break;

                case 2:
                    Log.i("BMI", BMI);
                    editor.remove(HEALTH_BMI).commit();
                    editor.putString(HEALTH_BMI, BMI).apply();
                    break;

                // Check heart rate
                case 3:
                    index = checkExerciseDate(dates,obs);
                    if(index==0 || index==1 || index==2 || index==3 || index==4 ){
                        Float value = getConceptValue(obs, "PULSE");
                        if ( value > Float.parseFloat(graphDatas[index].getHeartRate()) ){
                            graphDatas[index].setHeartRate(value.toString());
                        }
                        if(index==1)
                            editor.putBoolean(HEALTH_IS_SYNCED_TODAY, true).apply();
                    }
                    break;

                //Check exerciseMinutes
                case 4:
                    index = checkExerciseDate(dates,obs);
                    if(index==0 || index==1 || index==2 || index==3 || index==4 ){
                        Float value = getConceptValue(obs,"Active Minutes");
                        if ( value > Float.parseFloat(graphDatas[index].getActiveMinutes()) ){
                            graphDatas[index].setActiveMinutes(value.toString());
                        }
                        if(index==1)
                            editor.putBoolean(HEALTH_IS_SYNCED_TODAY, true).apply();
                    }
                    break;

                case 5:
                    Log.i("Target HR", targetHR);
                    editor.remove(HEALTH_TARGET_HR).commit();
                    editor.putString(HEALTH_TARGET_HR, targetHR).apply();
                    break;

                //Check floor
                case 6:
                    index = checkExerciseDate(dates,obs);
                    if(index==0 || index==1 || index==2 || index==3 || index==4 ){
                        Float value = getConceptValue(obs, "Floors");
                        if ( value > Float.parseFloat(graphDatas[index].getFloor()) ){
                            graphDatas[index].setFloor(value.toString());
                        }
                        if(index==1)
                            editor.putBoolean(HEALTH_IS_SYNCED_TODAY, true).apply();
                    }
                    break;

                //Check Calories burned
                case 7:
                    index = checkExerciseDate(dates,obs);
                    if(index==0 || index==1 || index==2 || index==3 || index==4){
                        Float value = getConceptValue(obs,"Calories_Burned");
                        if ( value > Float.parseFloat(graphDatas[index].getCaloriesBurned()) ){
                            graphDatas[index].setCaloriesBurned(value.toString());
                        }
                        if(index==1)
                            editor.putBoolean(HEALTH_IS_SYNCED_TODAY, true).apply();
                    }
                    break;

                //Check Calories
                case 8:
                    index = checkExerciseDate(dates,obs);
                    if(index==0 || index==1 || index==2 || index==3 || index==4 ){
                        Float value = getConceptValue(obs,"CALORIES");
                        if ( value > Float.parseFloat(graphDatas[index].getCalories()) ){
                            graphDatas[index].setCalories(value.toString());
                        }
                        if(index==1)
                            editor.putBoolean(HEALTH_IS_SYNCED_TODAY, true).apply();
                    }
                    break;

                //Check distance
                case 9:
                    index = checkExerciseDate(dates,obs);
                    if(index==0 || index==1 || index==2 || index==3 || index==4 ){
                        Float value = getConceptValue(obs,"DISTANCE");
                        if ( value > Float.parseFloat(graphDatas[index].getDistance()) ){
                            graphDatas[index].setDistance(value.toString());
                        }
                        if(index==1)
                            editor.putBoolean(HEALTH_IS_SYNCED_TODAY, true).apply();
                    }
                    break;

                default:
                    break;
            }

        }

    }

    private int checkConcept(String input) {
        /**
         Example flow :
         String input = {"results":[{"uuid":"5e5440ce-94dd-46cd-946b-391c516953ae","display":"STEPS: 0.0","links":[{"rel":"self","uri":"http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/obs/5e5440ce-94dd-46cd-946b-391c516953ae"}]}
         tempLocation = index at display + 9 (because of * display": * )
         temp = STEPS: 0.0","links":[{"rel":"self","uri":"http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/obs/5e5440ce-94dd-46cd-946b-391c516953ae"}]}
         tempLocation = 11
         temp = STEPS : 0.0

         temp.indexOf("CONCEPT_NAME + 1") will give value of concept
         **/
        int tempLocation;
        tempLocation = input.indexOf("display") + 9;
        String temp = input.substring(tempLocation + 1);
        tempLocation = temp.indexOf("\"");
        temp = temp.substring(0, tempLocation);
        if (temp.indexOf("STEPS") != -1) {
            return 1;
        }
        else if(temp.indexOf("BODY MASS INDEX") != -1) {
            BMI = temp.substring(16);
            return 2;
        }
        else if(temp.indexOf("PULSE") != -1) {
            heartRate = temp.substring(6);
            Log.i("heartRate", heartRate);
            return 3;
        }
        else if (temp.indexOf("Active Minutes") != -1) {
            return 4;
        }
        else if (temp.indexOf("TARGET HEART RATE") != -1) {
            targetHR = temp.substring(18);
            return 5;
        }
        else if (temp.indexOf("Floors") != -1) {
            return 6;
        }
        else if (temp.indexOf("Calories_Burned") != -1){
            return 7;
        }
        else if (temp.indexOf("CALORIES") != -1) {
            return 8;
        }
        else if (temp.indexOf("DISTANCE") != -1) {
            return 9;
        }
        else
            return 0;
    }

    private String getPersonInput(String patientUUID) {
        String display = null;
        try {
            display = ApiAuthRest.getRequestGet("obs?patient=" + patientUUID) + "&concept=" + Container.heart_rate_uuid;
            Log.i("OpenMRS response-GPI", display);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return display;
    }

    private String returnObs (String UUID) {
        String display = null;
        try {
            display = ApiAuthRest.getRequestGet("obs/" + UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return display;
    }


    private int checkExerciseDate(Date[] dates, String obs) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Container.DATE_FORMAT);
        int startPoint = obs.indexOf("obsDatetime") + 14;
        int endPoint = startPoint + 10;
        Date date;
        try {
            date = dateFormat.parse(obs.substring(startPoint, endPoint));
            Log.i("OpenMRS response", "Checking exercise date = " + date);
            if(date.equals(dates[0])) {
                Log.i("ActivityExercise", "Activity Date is today!!" + date);
                return 0;
            }
            if(date.equals(dates[1])) {
                return 1;
            }
            if(date.equals(dates[2])) {
                return 2;
            }
            if(date.equals(dates[3])) {
                return 3;
            }
            if(date.equals(dates[4])) {
                return 4;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private Float getConceptValue(String obs, String concept) {
        int startPoint;
        int endPoint;
        Float yPoint;
        obs = obs.substring(obs.indexOf(concept));
        startPoint = obs.indexOf(":") + 2;
        endPoint = obs.indexOf("\"");
        yPoint = Float.parseFloat(obs.substring(startPoint, endPoint));
        return yPoint;

    }

    private Date getDate (int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -index);
        Date xPoint = calendar.getTime();
        return xPoint;
    }

    public static String getTodayDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public String formatDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat(Container.DATE_FORMAT);
        return formatter.format(date);
    }

    private void insertToDB(int i) {
        String temp_steps = "8888",
                temp_distance = "0",
                temp_floors = "0",
                temp_caloriesOut = "0",
                temp_foodCalories = "0",
                temp_totalActiveMinutes = "0",
                temp_heartRate = "-";
        if(!graphDatas[i].getSteps().matches("0")){
            temp_steps = graphDatas[i].getSteps();
        }

        if (!graphDatas[i].getDistance().matches("0")) {
            temp_distance = graphDatas[i].getDistance();
        }
        if (!graphDatas[i].getFloor().matches("0")) {
            temp_floors = graphDatas[i].getFloor();
        }
        if (!graphDatas[i].getCaloriesBurned().matches("0")) {
            temp_caloriesOut = graphDatas[i].getCaloriesBurned();
        }
        if (!graphDatas[i].getCalories().matches("0")) {
            temp_foodCalories = graphDatas[i].getCalories();
        }
        if (!graphDatas[i].getActiveMinutes().matches("0")) {
            temp_totalActiveMinutes = graphDatas[i].getActiveMinutes();
        }
        if (!graphDatas[i].getHeartRate().matches("0")) {
            temp_heartRate = graphDatas[i].getHeartRate();
        }

        dbHelper.insertHealthData(formattedDates[i], temp_steps, temp_distance, temp_floors, temp_caloriesOut, temp_foodCalories, temp_totalActiveMinutes, temp_heartRate);
        Log.d("Database", "Inserting data for " + formattedDates[i]);
    }

    private void updateDB(int i) {
        dbHelper.updateHealthData(i, formattedDates[i], graphDatas[i].getSteps(), graphDatas[i].getDistance(), graphDatas[i].getFloor(),
                graphDatas[i].getCaloriesBurned(), graphDatas[i].getCalories(), graphDatas[i].getActiveMinutes(), graphDatas[i].getHeartRate());
        Log.d("Database", "Updating database = " + formattedDates[i]);
    }

}
