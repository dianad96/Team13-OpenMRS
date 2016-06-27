package org.openmrs.mobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

/**
 * Used to catch the redirect_url from Fitbit Server and store the essential authorisation code into the sharedpreferences for usage in other activity
 */
public class SyncFitBitAuthorizationActivity extends Activity {

    private String intentString;
    SharedPreferences sharedpreferences;

    private static final String CLIENT_ID = "227GHX";
    private static final String CLIENT_SECRET = "02b7cc9ffbe9dbc74bdd370631e9d2c2";
    private static final String PREFERENCE_TYPE = "FitbitPref";
    private static final String FITBIT_KEY = "fitbitAuth";
    private static final String AUTHORIZATION_ENCODED = "auth_encode";

    @Override
    protected void onNewIntent(Intent intent) {
        intentString = intent.getDataString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());

        sharedpreferences = getSharedPreferences(PREFERENCE_TYPE, 4);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(FITBIT_KEY, intentString.substring(intentString.indexOf("code=") + 5));
        editor.putString(AUTHORIZATION_ENCODED, getBase64String(CLIENT_ID, CLIENT_SECRET));
        editor.commit();

        Log.d("TAG", "Inside FitbitAuthorizationActivity");

        startActivity(new Intent(getApplicationContext(), SyncData.class));
    }

    private String getBase64String(String clientID, String clientSecret) {
        return Base64.encodeToString((clientID + ":" + clientSecret).getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
    }

}
