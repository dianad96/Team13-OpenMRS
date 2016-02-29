package org.openmrs.mobile.activities;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Flow: For first time user logging in, user will be redirected to ChromeTab to login
 * to their own Fitbit account to authorize the app. Once authorize, Fitbit will redirect
 * user to the redirect_url provided.
 */
public class SyncFitBitActivity extends IntentService {

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
    private String distance,steps,caloriesOut,floors,heartRate;
    private String foodCalories,carbs,fats,proteins,fiber,sodium,water;
    private String totalMinutesAsleep, totalSleepRecord, totalTimeInBed;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SyncFitBitActivity(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        sharedpreferences = getSharedPreferences(PREFERENCE_TYPE, 4);

        String userID = sharedpreferences.getString(FITBIT_USER_ID, null);
        Long lastSynced = sharedpreferences.getLong(FITBIT_KEY_TIMING, 0);
        Calendar today = new GregorianCalendar();
        Long currentTime= today.getTimeInMillis();
        String lastSyncedDate = getDate(currentTime, "yyyy-MM-dd");
        Log.d("TAG", "getting activitiesURL");
        String[] activitiesResources = {"activities",
                "activities/distance",
                "activities/heart",
//                                                "foods/log",
                "sleep"};
        String[] activities = { "activity",
                "distance",
                "heartRate",
                "food",
                "sleep"};
//                String[] activitiesURL = setRequestURL(activitiesResources, lastSynced, userID);

        String[] activitiesURL = {
                "https://api.fitbitcom/1/user/" + userID +"/activities/date/" + lastSyncedDate + ".json",
                "https://api.fitbitcom/1/user/" + userID + "/activities/distance/date/today.json",
                "https://api.fitbit.com/1/user/" + userID + "/activities/heart/date/" + lastSyncedDate + ".json",
                "https://api.fitbit.com/1/user/" + userID + "/foods/log/date/" + lastSyncedDate + ".json",
                "https://api.fitbit.com/1/user/" + userID + "/sleep/date/" + lastSyncedDate + ".json"
        };

        for(int i=0; i < activitiesURL.length; i++) {
            getUserData(activitiesURL[i], activities[i]);
        }
    }



//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        sharedpreferences = getSharedPreferences(PREFERENCE_TYPE, 4);
//
//            String userID = sharedpreferences.getString(FITBIT_USER_ID, null);
//            Long lastSynced = sharedpreferences.getLong(FITBIT_KEY_TIMING, 0);
//            Calendar today = new GregorianCalendar();
//            Long currentTime= today.getTimeInMillis();
//            String lastSyncedDate = getDate(currentTime,"yyyy-MM-dd");
////
////            Log.d("TAG", "lastSynced = " + lastSynced);
////            Log.d("TAG", "currentTime = " + currentTime);
////            if(lastSynced > currentTime){ //Access token has expire
////                Log.d("TAG", "access token expired, refreshing token");
////                asyncTaskRunner.execute("refresh");
////            }
////            else {
////                /**
////                 * ActivityResource : "activities", "activities/heart" , "foods/log", "sleep"
////                 * "https://api.fitbitcom/1/user/[user-id]/activities/date/[date].json"
////                 * "https://api.fitbitcom/1/user/[user-id]/activities/distance/date/today.json"
////                 * "https://api.fitbit.com/1/user/[user-id]/activities/heart/date/[base-date]/[end-date].json
////                 * "https://api.fitbit.com/1/user/[user-id]/foods/log/date/[date].json"
////                 * "https://api.fitbit.com/1/user/[user-id]/sleep/date/[date].json"
////                 */
////                String activityURL = setRequestURL("activities" , lastSynced, userID);
////                String distanceURL = setRequestURL("activities/distance", lastSynced, userID);
////                String activityHeartURL = setRequestURL("activities/heart", lastSynced, userID);
////                String foodURL = setRequestURL("foods/log", lastSynced, userID);
////                String sleepURL = setRequestURL("sleep", lastSynced, userID);
//                Log.d("TAG", "getting activitiesURL");
//                String[] activitiesResources = {"activities",
//                        "activities/distance",
//                        "activities/heart",
////                                                "foods/log",
//                        "sleep"};
//                String[] activities = { "activity",
//                        "distance",
//                        "heartRate",
//                        "food",
//                        "sleep"};
////                String[] activitiesURL = setRequestURL(activitiesResources, lastSynced, userID);
//
//                 String[] activitiesURL = {
//                "https://api.fitbitcom/1/user/" + userID +"/activities/date/" + lastSyncedDate + ".json",
//                "https://api.fitbitcom/1/user/" + userID + "/activities/distance/date/today.json",
//                "https://api.fitbit.com/1/user/" + userID + "/activities/heart/date/" + lastSyncedDate + ".json",
//                "https://api.fitbit.com/1/user/" + userID + "/foods/log/date/" + lastSyncedDate + ".json",
//                "https://api.fitbit.com/1/user/" + userID + "/sleep/date/" + lastSyncedDate + ".json"
//                 };
//
//                for(int i=0; i < activitiesURL.length; i++) {
//                    getUserData(activitiesURL[i], activities[i]);
//                }
//
//
////            }
////        }
//    }



    private String getBase64String(String clientID, String clientSecret) {
        return Base64.encodeToString((clientID + ":" + clientSecret).getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
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
            Log.d("TAG", "fitbit user id: " + userID);
//            String url = "https://api.fitbit.com/1/user/" + userID + "/" + activityResource + "/date/2016-02-04/1d.json";
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Authorization", "Bearer " + sharedpreferences.getString(FITBIT_ACCESS_KEY, null));
            org.apache.http.HttpResponse response = httpClient.execute(httpGet);
            string = EntityUtils.toString(response.getEntity());
            Log.d("TAG", string);
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
                Log.d("TAG", "steps=" + steps + ", caloriesBMR=" + ", caloriesOut=" + caloriesOut + ", floors=" + floors);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(activity.equals("distance")){
            try {
                summary = (JSONObject) jsonObject.get("summary");
                distance = summary.getString("value");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(activity.equals("heartRate")){
            try {
                summary = (JSONObject) jsonObject.get("summary");
                heartRate = summary.getString("restingHeartRate");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(activity.equals("food")){
            try {
                summary = (JSONObject) jsonObject.get("summary");
                foodCalories = summary.getString("calories");
                carbs = summary.getString("carbs");
                fats = summary.getString("fats");
                fiber = summary.getString("fiber");
                proteins = summary.getString("protein");
                sodium = summary.getString("sodium");
                water = summary.getString("water");
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
