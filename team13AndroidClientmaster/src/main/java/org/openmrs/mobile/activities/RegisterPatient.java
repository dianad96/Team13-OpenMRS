package org.openmrs.mobile.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.Arrays;

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

        givenName = (EditText) findViewById(R.id.editText3);
        familyName = (EditText) findViewById(R.id.editText4);

        gender = (NumberPicker) findViewById(R.id.numberPicker);
        gender.setMinValue(0);
        gender.setMaxValue(1);
        gender.setDisplayedValues(new String[]{"M", "F",});

        location = (NumberPicker) findViewById(R.id.numberPicker2);
        location.setMinValue(0);
        location.setMaxValue(4);

        //get hospital locations for picker
        try {
            getLocations();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //converting location arrayList to location string array
        Object[] objNames = names.toArray();
        String[] names_values = Arrays.copyOf(objNames, objNames.length, String[].class);
        location.setDisplayedValues(names_values);

        //**register**
        Button register = (Button) findViewById(R.id.but_reg);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (givenName.getText().toString().equals("Given Name"))
                    alertDialog("All fields are mandatory. Please include your given name.");
                else
                    if (familyName.getText().toString().equals("Family Name"))
                        alertDialog("All fields are mandatory. Please include your family name.");
                    else
                        if (gender.getValue() == 0)
                            alertDialog("All fields are mandatory. Please include your gender.");
                        else
                            if (location.getValue() == 0)
                                alertDialog("All fields are mandatory. Please include your location.");
                            else
                                //create new person
                                createPerson();
            }

        });
    }

    void alertDialog(String s)
    {
        new AlertDialog.Builder(RegisterPatient.this)
                .setTitle("Registration Failure")
                .setMessage(s)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
        Object obj = ApiAuthRest.getRequestGet(request);
        JSONObject jsonObject = new JSONObject ((String) obj);
        JSONArray arrayResult = (JSONArray) jsonObject.get("results");

        int itemArray = arrayResult.length();
        int iterator;
        names.add(" ");
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

        String gender_value;
        if(gender.getValue()==1)
            gender_value = "M";
        else
            gender_value = "F";
        final String JSONinput= "{\"gender\": \"" + gender_value + "\", \"names\": [{\"givenName\":\"" + givenName.getText() + "\", \"familyName\":\"" + familyName.getText() + "\"}]}";

        StringEntity input = null;
        try {
            input = new StringEntity(JSONinput);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        input.setContentType("application/json");
        try {
            Log.i("OpenMRS response", "Person Added = " + ApiAuthRest.getRequestPost("person", input));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
