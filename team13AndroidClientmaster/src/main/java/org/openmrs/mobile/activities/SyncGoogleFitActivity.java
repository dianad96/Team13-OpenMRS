package org.openmrs.mobile.activities;

/**
 * Created by Diana on 02/02/2016.
 */
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.entity.StringEntity;
import org.openmrs.mobile.activities.fragments.ApiAuthRest;

import java.io.UnsupportedEncodingException;

public class SyncGoogleFitActivity extends Activity {
    static String username = "diana";
    static String password = "Admin123";
    static String URLBase = "http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/";
    private final String Raizelb = Container.user_uuid;
    private final String CALORIES = Container.calories_uuid;
    private final String STEPS = Container.steps_uuid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ApiAuthRest.setURLBase(URLBase);
        ApiAuthRest.setUsername(username);
        ApiAuthRest.setPassword(password);

        final String JSONSteps = "{\"obsDatetime\": \"" + SyncData.getDate() + "\"" +
                ", \"concept\": \"" + STEPS + "\"" +
                ", \"value\": \"" + SyncData.getStep() + "\"" +
                ", \"person\": \"" + Raizelb + "\"}";
        final String JSONCalories = "{\"obsDatetime\": \"" + SyncData.getDate() + "\"" +
                ", \"concept\": \"" + CALORIES + "\"" +
                ", \"value\": \"" + Math.round(SyncData.getCal()) + "\"" +
                ", \"person\": \"" + Raizelb + "\"}";

        StringEntity inputSteps = null;
        StringEntity inputCalories = null;
        try {
            inputSteps = new StringEntity(JSONSteps);
            inputCalories = new StringEntity(JSONCalories);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        inputSteps.setContentType("application/json");
        inputCalories.setContentType("application/json");
        try {
            Log.i("OpenMRS response","AddSteps = " + ApiAuthRest.getRequestPost("obs", inputSteps));
            Log.i("OpenMRS response","AddCalories = " + ApiAuthRest.getRequestPost("obs", inputCalories));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
