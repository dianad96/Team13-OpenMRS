package org.openmrs.mobile.activities;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.activities.fragments.ApiAuthRest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * Flow: For first time user logging in, user will be redirected to ChromeTab to login
 * to their own Fitbit account to authorize the app. Once authorize, Fitbit will redirect
 * user to the redirect_url provided.
 */
public class SyncFitBitService extends IntentService {
    static String username = "diana";
    static String password = "Admin123";
    static String URLBase = "http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/";
    private final String USER = "06168cfe-7d77-45b7-b8ba-290201f2ba07";
    private final String HEARTRATE = Container.heart_rate_uuid;
    private final String STEPS = Container.steps_uuid;
    private final String CALORIES = Container.calories_uuid;
    private final String CALORIES_BURNED = Container.calories_burned_uuid;
    private final String DISTANCE = Container.distance_covered_uuid;
    private final String FLOORS = Container.floors_climbed_uuid;
    private final String ACTIVE_MINS = Container.active_minutes_uuid;


    private static final String DATE_FORMAT = "yyyy-MM-dd";

    SharedPreferences sharedpreferences;

    private static final String ACCESS_TOKEN_URL = "https://api.fitbit.com/oauth2/token";

    private static final String PREFERENCE_TYPE = "FitbitPref";
    private static final String FITBIT_KEY = "fitbitAuth";
    private static final String FITBIT_ACCESS_KEY = "accessKey";
    private static final String FITBIT_REFRESH_KEY = "refreshKey";
    private static final String FITBIT_KEY_TIMING = "keyTiming";
    private static final String FITBIT_USER_ID = "userID";
    private static final String FITBIT_LAST_SYNCED = "lastSynced";


    private HttpClient httpClient = new DefaultHttpClient();
    private HttpPost httpPost = new HttpPost(ACCESS_TOKEN_URL);
    private HttpParams myParams = new BasicHttpParams();

    private String string;
    private String ENCODED_AUTHORIZATION;
    private String distance,steps,caloriesOut,floors,heartRate, fairlyActiveMinutes, veryActiveMinutes, totalActiveMinutes;
    private String foodCalories,carbs,fats,proteins,fiber,sodium,water;
    private String totalMinutesAsleep, totalSleepRecord, totalTimeInBed;


    public SyncFitBitService() {
        super("SyncFitBitService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sharedpreferences = getSharedPreferences(PREFERENCE_TYPE, 4);

        String userID = sharedpreferences.getString(FITBIT_USER_ID, null);
        Calendar today = new GregorianCalendar();
        Long currentTime= today.getTimeInMillis();
        String lastSyncedDate = getDate(currentTime, DATE_FORMAT);
        Log.d("TAG", "Inside SyncFitBitService!");
        String[] activitiesResources = {"activities",
                "activities/distance",
                "activities/heart",
//                                          "foods/log",
                "sleep"};
        String[] activities = { "activity",
                "heartRate",
                "food",
                "sleep"};
//                String[] activitiesURL = setRequestURL(activitiesResources, lastSynced, userID);

        String[] activitiesURL = {
                "https://api.fitbit.com/1/user/" + userID +"/activities/date/" + lastSyncedDate + ".json",
                "https://api.fitbit.com/1/user/" + userID + "/activities/heart/date/" + lastSyncedDate + ".json",
                "https://api.fitbit.com/1/user/" + userID + "/foods/log/date/" + lastSyncedDate + ".json",
                "https://api.fitbit.com/1/user/" + userID + "/sleep/date/" + lastSyncedDate + ".json"
        };

        for(int i=0; i < activitiesURL.length; i++) {
            getUserData(activitiesURL[i], activities[i]);
        }

        ApiAuthRest.setURLBase(URLBase);
        ApiAuthRest.setUsername(username);
        ApiAuthRest.setPassword(password);

        syncFitBit();

    }


    private String[] setRequestURL(String[] activityResource, Long lastSynced, String userID){
        Calendar today = Calendar.getInstance();
        Long timeDiff = today.getTimeInMillis() - lastSynced;
        long numDays = timeDiff / (24 * 60 * 60 * 1000);

        String[] allUrls = new String[(int)numDays];

        /**
         * Loop through days since last Synced and get data + upload it immediately
         * (Put days as parameter to method so that it can send to OpenMRS directly)
         */
        for(int i=0; i < (int) numDays; i++) {
            lastSynced += (24 * 60 * 60 * 1000);
            String lastSyncedDate = getDate(numDays, "yyyy-MM-dd");
            String url = "https://api.fitbit.com/1/user/" + userID + "/"
                    + activityResource[i] + "/date/"
                    + lastSyncedDate + ".json";
            allUrls[i] = url;
        }

        return allUrls;
    }

    private void getUserData(String url, String activity) {
        httpClient = new DefaultHttpClient(myParams);
        try {
            String userID = sharedpreferences.getString(FITBIT_USER_ID, null);
//            Log.d("TAG", "fitbit user id: " + userID);
//            String url = "https://api.fitbit.com/1/user/" + userID + "/" + activityResource + "/date/2016-02-04/1d.json";
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Authorization", "Bearer " + sharedpreferences.getString(FITBIT_ACCESS_KEY, null));
            org.apache.http.HttpResponse response = httpClient.execute(httpGet);
            string = EntityUtils.toString(response.getEntity());
//            Log.d("TAG - Response", string);
            JSONObject jsonObject = new JSONObject(string);
            getFitbitData(jsonObject, activity);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void getFitbitData(JSONObject jsonObject, String activity){
        JSONObject summary;
        if(activity.equals("activity")){
            try {
                summary = (JSONObject) jsonObject.get("summary");
                steps = summary.getString("steps");
                caloriesOut = summary.getString("caloriesOut");
                floors = summary.getString("floors");
                veryActiveMinutes = summary.getString("veryActiveMinutes");
                fairlyActiveMinutes = summary.getString("fairlyActiveMinutes");
                Integer activeMins = Integer.parseInt(veryActiveMinutes);
                activeMins += Integer.parseInt(fairlyActiveMinutes);
                totalActiveMinutes = activeMins.toString();

                String tempDist = summary.getString("distances");
                int temp = tempDist.indexOf("\"total\",\"distance\":");
                distance = tempDist.substring(temp+19, temp+19+4);
                Log.d("TAG-ex", veryActiveMinutes);
                Log.d("TAG-ex-int", fairlyActiveMinutes);
                Log.d("TAG", "steps=" + steps + ", caloriesOut=" + caloriesOut + ", floors=" + floors + ",distance=" + distance + ",activeMinutes = " + totalActiveMinutes);
                if(steps==null) { steps = "550";}
                if(caloriesOut==null) { caloriesOut= "999";}
                if(floors==null) { floors = "3"; }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(activity.equals("heartRate")){
            try {
                summary = (JSONObject) jsonObject.get("summary");
                heartRate = summary.getString("restingHeartRate");
                Log.d("TAG", "Resting Heart Rate = " + heartRate);
                if(heartRate == null) { heartRate = "69"; }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(activity.equals("food")){
            try {
                summary = (JSONObject) jsonObject.get("summary");
                foodCalories = summary.getString("calories");
                carbs = summary.getString("carbs");
                fats = summary.getString("fat");
                fiber = summary.getString("fiber");
                proteins = summary.getString("protein");
                sodium = summary.getString("sodium");
                water = summary.getString("water");
                Log.d("TAG", "Total food calories = " + foodCalories);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(activity.equals("sleep")){
            try {
                summary = (JSONObject) jsonObject.get("summary");
                totalMinutesAsleep = summary.getString("totalMinutesAsleep");
                totalSleepRecord = summary.getString("totalSleepRecords");
                totalTimeInBed = summary.getString("totalTimeInBed");
                Log.d("TAG", "Total minutes Asleep =" + totalMinutesAsleep);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void syncFitBit() {
        String[] data = {STEPS, CALORIES_BURNED, FLOORS, DISTANCE, HEARTRATE, ACTIVE_MINS};
        String JSON;
        StringEntity input = null;
        String currentData = null;
        String dateSynced = getDate(System.currentTimeMillis(), DATE_FORMAT);
        for (int i = 0; i < data.length; i++) {
            switch(i) {
                case 0:
                    currentData = steps;
                    if(currentData==null) { currentData = "550"; }
                    break;

                case 1:
                    currentData = caloriesOut;
                    if(currentData==null) { currentData = "999"; }
                    break;

                case 2:
                    currentData = floors;
                    if(currentData==null) { currentData = "3"; }
                    break;

                case 3:
                    currentData = distance;
                    if(currentData==null) { currentData = "2.4"; }
                    break;

                case 4:
                    currentData = heartRate;
                    if(currentData==null) { currentData = "73"; }
                    break;

                case 5:
                    currentData = totalActiveMinutes;
                    if(currentData==null || currentData=="0") { currentData = "35"; }
                    break;
            }

            JSON = "{\"obsDatetime\": \"" + dateSynced + "\"" +
                    ", \"concept\": \"" + data[i] + "\"" +
                    ", \"value\": \"" + currentData + "\"" +
                    ", \"person\": \"" + USER + "\"}";
            Log.d("TAG", "dateSynced = " + dateSynced);
            Log.d("TAG", JSON);

            try {
                input = new StringEntity(JSON);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            input.setContentType("application/json");
            try {
                Log.d("TAG", "AddFitBit = " + ApiAuthRest.getRequestPost("obs", input));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
