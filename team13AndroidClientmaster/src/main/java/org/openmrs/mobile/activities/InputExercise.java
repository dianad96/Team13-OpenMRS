package org.openmrs.mobile.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InputExercise extends AppCompatActivity {

    private String calories, distance , totalSteps, floors , activeMins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_exercise);

        android.support.v7.app.ActionBar bar =  getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00463f")));

        // Input Calories Burned
        CardView calories_burned = (CardView) findViewById(R.id.c1);
        calories_burned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caloriesBurned();
            }
        });


        // Input Distance Covered
        CardView distance_covered = (CardView) findViewById(R.id.c2);
        distance_covered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distanceCovered();
            }
        });


        // Input Total Steps
        CardView total_steps = (CardView) findViewById(R.id.c3);
        total_steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalSteps();
            }
        });


        // Input Floors Climbed
        CardView floors_climbed = (CardView) findViewById(R.id.c4);
        floors_climbed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floorsClimbed();
            }
        });

        // Input Active Minutes
        CardView active_minutes = (CardView) findViewById(R.id.c5);
        active_minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeMinutes();
            }
        });


        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendData();
                Intent i = new Intent(InputExercise.this, DashboardActivity.class);
                startActivity(i);
            }
        });
    }

    void caloriesBurned() {

        final EditText input = new EditText(InputExercise.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calories Burned (cal)");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String s;
                calories = input.getText().toString();
                try{
                    float cal = Float.parseFloat(calories);
                    calories = String.valueOf((int)cal);
                    s = calories + " cals";
                } catch (Exception e) {
                    s = "Unknown value";
                    e.printStackTrace();
                }


                TextView caloriesBurned_log = (TextView) findViewById(R.id.calories_burned_log);
                caloriesBurned_log.setText(s);
                Log.d("OpenMRS", "calories = " + calories);

            }
        });
        builder.setNegativeButton("CANCEL", null);

        builder.show();
    }

    void distanceCovered() {

        final EditText input = new EditText(InputExercise.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Distance Covered (km)");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String s;
                distance = input.getText().toString();
                try{
                    float dist = Float.parseFloat(distance);
                    distance = String.valueOf((int) (dist * 1000));
                    s = distance + " m";
                } catch (Exception e) {
                    s ="Unknown value";
                    e.printStackTrace();
                }

                TextView distanceCovered_log = (TextView) findViewById(R.id.distance_covered_log);
                distanceCovered_log.setText(s);

            }
        });
        builder.setNegativeButton("CANCEL", null);

        builder.show();
    }

    void totalSteps() {

        final EditText input = new EditText(InputExercise.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Total Steps (steps)");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String s;
                totalSteps = input.getText().toString();
                try{
                    float tempSteps = Float.parseFloat(totalSteps);
                    totalSteps = String.valueOf((int) tempSteps);
                    s = totalSteps + " steps";
                } catch (Exception e) {
                    s ="Unknown value";
                    e.printStackTrace();
                }

                TextView totalSteps_log = (TextView) findViewById(R.id.total_steps_log);
                totalSteps_log.setText(s);

            }
        });
        builder.setNegativeButton("CANCEL", null);

        builder.show();
    }

    void floorsClimbed() {

        final EditText input = new EditText(InputExercise.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Floors Climbed (floors)");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String s;
                floors = input.getText().toString();
                try{
                    float tempFloors = Float.parseFloat(floors);
                    totalSteps = String.valueOf((int) tempFloors);
                    s = totalSteps + " floors";
                } catch (Exception e) {
                    s ="Unknown value";
                    e.printStackTrace();
                }

                TextView floorsClimbed_log = (TextView) findViewById(R.id.floors_climbed_log);
                floorsClimbed_log.setText(s);


            }
        });
        builder.setNegativeButton("CANCEL", null);

        builder.show();
    }

    void activeMinutes() {

        final EditText input = new EditText(InputExercise.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Active minutes (min)");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String s;
                activeMins = input.getText().toString();
                try {
                    float tempMins = Float.parseFloat(activeMins);
                    activeMins = String.valueOf((int)tempMins);
                    s = activeMins + " min";
                } catch (Exception e) {
                    s ="Unknown value";
                    e.printStackTrace();
                }

                TextView activeMinutes_log = (TextView) findViewById(R.id.active_minutes_log);
                activeMinutes_log.setText(s);

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
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        String date = SyncFitBitService.getDate(System.currentTimeMillis(), formattedDate);

        final String JSONCaloriesBurned = "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.calories_burned_uuid + "\"" +
                ", \"value\": \"" + calories + "\"" +
                ", \"person\": \"" + Container.user_uuid + "\"}";

        final String JSONDistanceCovered = "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.distance_covered_uuid + "\"" +
                ", \"value\": \"" + distance + "\"" +
                ", \"person\": \"" + Container.user_uuid + "\"}";

        final String JSONTotalSteps = "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.total_steps_uuid + "\"" +
                ", \"value\": \"" + totalSteps + "\"" +
                ", \"person\": \"" + Container.user_uuid + "\"}";

        final String JSONFloorsClimbed = "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.floors_climbed_uuid + "\"" +
                ", \"value\": \"" + floors + "\"" +
                ", \"person\": \"" + Container.user_uuid + "\"}";

        final String JSONActiveMinutes = "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.active_minutes_uuid + "\"" +
                ", \"value\": \"" + activeMins + "\"" +
                ", \"person\": \"" + Container.user_uuid + "\"}";


        StringEntity inputCaloriesBurned = null;
        StringEntity inputDistanceCovered = null;
        StringEntity inputTotalSteps = null;
        StringEntity inputFloorsClimbed = null;
        StringEntity inputActiveMinutes = null;

        try {
            inputCaloriesBurned = new StringEntity(JSONCaloriesBurned);
            inputDistanceCovered = new StringEntity(JSONDistanceCovered);
            inputTotalSteps = new StringEntity(JSONTotalSteps);
            inputFloorsClimbed = new StringEntity(JSONFloorsClimbed);
            inputActiveMinutes = new StringEntity(JSONActiveMinutes);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        inputCaloriesBurned.setContentType("application/json");
        inputDistanceCovered.setContentType("application/json");
        inputTotalSteps.setContentType("application/json");
        inputFloorsClimbed.setContentType("application/json");
        inputActiveMinutes.setContentType("application/json");
        try {
            Log.d("OpenMRS", "calories = " + calories + ", distance = " + distance + ",steps = " + totalSteps);
            if (calories != null)
                Log.i("OpenMRS response", "AddCaloriesBurned = " + ApiAuthRest.getRequestPost("obs", inputCaloriesBurned));
            if (distance != null)
                Log.i("OpenMRS response", "AddDistanceCovered = " + ApiAuthRest.getRequestPost("obs", inputDistanceCovered));
            if (totalSteps != null)
                Log.i("OpenMRS response", "AddTotalSteps = " + ApiAuthRest.getRequestPost("obs", inputTotalSteps));
            if (floors != null)
                Log.i("OpenMRS response", "AddFloorsClimbed = " + ApiAuthRest.getRequestPost("obs", inputFloorsClimbed));
            if (activeMins != null)
                Log.i("OpenMRS response", "AddActiveMinutes = " + ApiAuthRest.getRequestPost("obs", inputActiveMinutes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
