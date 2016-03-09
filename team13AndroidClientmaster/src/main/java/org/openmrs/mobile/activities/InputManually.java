package org.openmrs.mobile.activities;

import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import org.apache.http.entity.StringEntity;


import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.ApiAuthRest;

import java.io.UnsupportedEncodingException;


// PAGE ADDED BY TEAM 13
//** HARD CODED VERSION - TO BE CHANGED!!! **//
public class InputManually extends Activity {

    static String username = "admin";
    static String password = "Admin123";
    static String URLBase = "http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/";

    public static final String USER_GIVENNAME = "givenName";
    public static final String USER_FAMILYNAME = "familyName";
    public static final String GENDER = "gender";

    public void createPatient ()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_manually);

        final EditText mgivenName = (EditText) findViewById(R.id.id_givenName);
        final EditText mfamilyName = (EditText) findViewById(R.id.id_familyName);
        final EditText mgender = (EditText) findViewById(R.id.id_gender);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ApiAuthRest.setURLBase(URLBase);
        ApiAuthRest.setUsername(username);
        ApiAuthRest.setPassword(password);

        final Button button = (Button) findViewById(R.id.id_submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String JSON = "{\"names\": [{\"givenName\": \"" + mgivenName.getText().toString() + "\", \"familyName\":\"" + mfamilyName.getText().toString() + "\"}],\"gender\":\"" + mgender.getText().toString() + "\"}";
                StringEntity inputAddPerson = null;
                try {
                    inputAddPerson = new StringEntity(JSON);
                } catch (UnsupportedEncodingException e) {
                   // System.out.println("NOOO");
                    e.printStackTrace();
                }

                inputAddPerson.setContentType("application/json");
                try {
                    System.out.println("AddPerson = " + ApiAuthRest.getRequestPost("person", inputAddPerson));
                } catch (Exception e) {
                 //   System.out.println("Whaat?");
                    e.printStackTrace();
                }

            }
        });
    }


}
