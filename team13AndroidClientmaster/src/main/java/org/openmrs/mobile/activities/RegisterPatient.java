package org.openmrs.mobile.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.ApiAuthRest;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class RegisterPatient extends AppCompatActivity {

    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> uuids = new ArrayList<String>();
    EditText givenName, familyName;
    NumberPicker gender, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_patient);

        android.support.v7.app.ActionBar bar =  getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00463f")));

        NumberPicker gender = (NumberPicker) findViewById(R.id.numberPicker);
        gender.setMinValue(0);
        gender.setMaxValue(1);
        gender.setDisplayedValues(new String[]{"M", "F",});

        NumberPicker location = (NumberPicker) findViewById(R.id.numberPicker2);
        location.setMinValue(0);
        location.setMaxValue(4);

        //get hospital locations for picker
        try {
            getLocations();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //converting
        Object[] objNames = names.toArray();
        String[] names_values = Arrays.copyOf(objNames, objNames.length, String[].class);
        location.setDisplayedValues(names_values);

        //**register**
        Button register = (Button) findViewById(R.id.but_reg);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //create new person
                createPerson();
            }

        });
    }

    void getLocations () throws Exception
    {
     /*
	 * SET VALUE FOR CONNECT TO OPENMRS
	 */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ApiAuthRest.setURLBase("http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/");
        ApiAuthRest.setUsername("diana");
        ApiAuthRest.setPassword("Admin123");


 	/*
 	 * Example how parse json return session
 	 */

        String request = "location";
        System.out.println("########################");
        System.out.println("Search the persons that have name  JOHN");
        Object obj = ApiAuthRest.getRequestGet(request);
        JSONObject jsonObject = new JSONObject ((String) obj);
        JSONArray arrayResult = (JSONArray) jsonObject.get("results");

        System.out.println("########################");
        int itemArray = arrayResult.length();
        int iterator;
        for (iterator = itemArray-1; iterator >= 0; iterator--) {
            JSONObject data = (JSONObject) arrayResult.get(iterator);
            String uuid = (String) data.get("uuid");
            String display = (String) data.get("display");
            System.out.println("Rows " + iterator + " => Result OBS UUID:" + uuid + " Display:" + display.substring(7));

            //Only display the first 15 messages
                names.add(display);
                uuids.add(uuid);
        }
    }

    void createPerson()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ApiAuthRest.setURLBase(Container.URLBase);
        ApiAuthRest.setUsername(Container.username);
        ApiAuthRest.setPassword(Container.password);

        final String JSONComment= "{\"gender\": \"" + gender.getValue() + "\", \"names\": [{\"givenName\":\"" + givenName.getText() + "\", \"familyName\":\"" + familyName.getText() + "\"}]}";

        StringEntity input = null;
        try {
            input = new StringEntity(JSONComment);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        input.setContentType("application/json");
        try {
            Log.i("OpenMRS response", "Comment Added = " + ApiAuthRest.getRequestPost("person", input));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
