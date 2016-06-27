package org.openmrs.mobile.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.entity.StringEntity;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.ApiAuthRest;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class InputHeartRate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_heart_rate);

        android.support.v7.app.ActionBar bar =  getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00463f")));

        // Input Heart Rate
        CardView calories_burned = (CardView) findViewById(R.id.hr1);
        calories_burned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heartRate();
            }
        });

        Button submit = (Button) findViewById(R.id.hr_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendData();
                Intent i = new Intent(InputHeartRate.this, DashboardActivity.class);
                startActivity(i);
            }
        });
    }

    void heartRate() {

        final EditText input = new EditText(InputHeartRate.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Heart Rate (BPM)");
        // builder.setMessage("Which creature has one voice and yet becomes four-footed and two-footed and three-footed?");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String s = input.getText().toString() + " BPM (Beats Per Minute)";
                Container.heart_rate = input.getText().toString();
                TextView caloriesBurned_log = (TextView) findViewById(R.id.heart_rate_log);
                caloriesBurned_log.setText(s);

                //  Your code when user clicked on OK
                //  You can write the code  to save the selected item here
            }
        });
        builder.setNegativeButton("CANCEL", null);

        builder.show();
    }

    public void sendData()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ApiAuthRest.setURLBase(Container.URLBase);
        ApiAuthRest.setUsername(Container.username);
        ApiAuthRest.setPassword(Container.password);

        Calendar c = Calendar.getInstance();
        String date = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" +c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

        final String JSONHeartRate= "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.heart_rate_uuid + "\"" +
                ", \"value\": \"" + Container.heart_rate + "\"" +
                ", \"person\": \"" + Container.user_uuid + "\"}";

        StringEntity heartRate = null;
        try {
            heartRate = new StringEntity(JSONHeartRate);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        heartRate.setContentType("application/json");
        try {
            Log.i("OpenMRS response", "AddCalories = " + ApiAuthRest.getRequestPost("obs", heartRate));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
