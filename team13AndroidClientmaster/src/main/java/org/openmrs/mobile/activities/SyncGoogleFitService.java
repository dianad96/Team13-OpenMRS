package org.openmrs.mobile.activities;

/**
 * Created by Diana on 02/02/2016.
 */
import android.app.IntentService;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.entity.StringEntity;
import org.openmrs.mobile.activities.fragments.ApiAuthRest;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SyncGoogleFitService extends IntentService {
    static String username = "diana";
    static String password = "Admin123";
    static String URLBase = "http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/";
    private final String Raizelb = Container.user_uuid;
    private final String CALORIES = Container.calories_uuid;
    private final String STEPS = Container.steps_uuid;


    private static final String DATE_FORMAT = "yyyy-MM-dd";
    static String UUID = null;


    public SyncGoogleFitService() { super("SyncGoogleFitActivity"); }

    @Override
    protected void onHandleIntent(Intent intent){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ApiAuthRest.setURLBase(URLBase);
        ApiAuthRest.setUsername(username);
        ApiAuthRest.setPassword(password);

        /*if (hasSubmittedToday("STEPS"))
            syncGoogleFit(2);
        else
            syncGoogleFit(1);

        if (hasSubmittedToday("CALORIES"))
            syncGoogleFit(4);
        else
            syncGoogleFit(3);*/
        syncGoogleFit(1);
        syncGoogleFit(3);


    }

    private boolean hasSubmittedToday(String concept) {
        String result = null;
        String obsResult = null;
        try {
            result = ApiAuthRest.getRequestGet("obs?patient=" + Raizelb);
        }catch (Exception e) {
            e.printStackTrace();
        }
        int startPoint = 0;
        int endPoint = 0;
        while ((startPoint = result.indexOf("uuid",startPoint)) != -1) {
            startPoint += 7;
            endPoint = result.indexOf("\"", startPoint);
            UUID = result.substring(startPoint,endPoint);
            try {
                obsResult = ApiAuthRest.getRequestGet("obs/" + UUID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isCorrectConcept(obsResult,concept)) {
                if (syncedToday(obsResult))
                    return true;
                else
                    return false;
            }
        }
        return false;
    }

    private boolean syncedToday(String obsResult) {
        Date todayDate = new Date();
        Date date = new Date();
        int startPoint = obsResult.indexOf("obsDatetime") + 14;
        int endPoint = startPoint + 10;
        String obsDate = obsResult.substring(startPoint,endPoint);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            date = dateFormat.parse(obsDate);
            todayDate = dateFormat.parse(dateFormat.format(todayDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (todayDate.equals(date)) {
            return true;
        }
        return false;
    }

    private boolean isCorrectConcept(String obsResult, String concept) {
        if (obsResult.indexOf(concept) != -1) {
            return true;
        }
        return false;
    }

    private void syncGoogleFit(int type) {
        String JSON;
        StringEntity input = null;
        switch (type) {
            case 1:
                JSON = "{\"obsDatetime\": \"" + SyncData.getDate() + "\"" +
                        ", \"concept\": \"" + STEPS + "\"" +
                        ", \"value\": \"" + SyncData.getStep() + "\"" +
                        ", \"person\": \"" + Raizelb + "\"}";
                try {
                    input = new StringEntity(JSON);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                input.setContentType("application/json");
                try {
                    Log.i("OpenMRS response", "AddGoogleFit = " + ApiAuthRest.getRequestPost("obs", input));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                JSON = "{\"value\" : \"" + SyncData.getStep() + "\"}";
                try {
                    input = new StringEntity(JSON);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                input.setContentType("application/json");
                try {
                    Log.i("OpenMRS response", "AddGoogleFit = " + ApiAuthRest.getRequestPost("obs/" + UUID, input));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                JSON = "{\"obsDatetime\": \"" + SyncData.getDate() + "\"" +
                        ", \"concept\": \"" + CALORIES + "\"" +
                        ", \"value\": \"" + Math.round(SyncData.getCal()) + "\"" +
                        ", \"person\": \"" + Raizelb + "\"}";
                try {
                    input = new StringEntity(JSON);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                input.setContentType("application/json");
                try {
                    Log.i("OpenMRS response", "AddGoogleFit = " + ApiAuthRest.getRequestPost("obs", input));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                JSON = "{\"value\" : \"" + Math.round(SyncData.getCal()) + "\"}";
                try {
                    input = new StringEntity(JSON);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                input.setContentType("application/json");
                try {
                    Log.i("OpenMRS response", "AddGoogleFit = " + ApiAuthRest.getRequestPost("obs/" + UUID, input));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }
}
