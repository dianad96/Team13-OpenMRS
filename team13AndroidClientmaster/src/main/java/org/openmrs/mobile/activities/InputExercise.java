package org.openmrs.mobile.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
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

public class InputExercise extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_exercise);


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
        // builder.setMessage("Which creature has one voice and yet becomes four-footed and two-footed and three-footed?");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String s = input.getText().toString() + " cals";
                Container.calories_burned = input.getText().toString();
                TextView caloriesBurned_log = (TextView) findViewById(R.id.calories_burned_log);
                caloriesBurned_log.setText(s);

                //  Your code when user clicked on OK
                //  You can write the code  to save the selected item here
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
        // builder.setMessage("Which creature has one voice and yet becomes four-footed and two-footed and three-footed?");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String s = input.getText().toString() + " km";
                Container.distance_covered = input.getText().toString();
                TextView distanceCovered_log = (TextView) findViewById(R.id.distance_covered_log);
                distanceCovered_log.setText(s);

                //  Your code when user clicked on OK
                //  You can write the code  to save the selected item here
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
        // builder.setMessage("Which creature has one voice and yet becomes four-footed and two-footed and three-footed?");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String s = input.getText().toString() + " steps";
                Container.total_steps = input.getText().toString();
                TextView totalSteps_log = (TextView) findViewById(R.id.total_steps_log);
                totalSteps_log.setText(s);

                //  Your code when user clicked on OK
                //  You can write the code  to save the selected item here
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
        builder.setTitle("Total Steps (steps)");
        // builder.setMessage("Which creature has one voice and yet becomes four-footed and two-footed and three-footed?");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String s = input.getText().toString() + " km";
                Container.floors_climbed = input.getText().toString();
                TextView floorsClimbed_log = (TextView) findViewById(R.id.floors_climbed_log);
                floorsClimbed_log.setText(s);

                //  Your code when user clicked on OK
                //  You can write the code  to save the selected item here
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Total Steps (steps)");
        // builder.setMessage("Which creature has one voice and yet becomes four-footed and two-footed and three-footed?");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String s = input.getText().toString() + " km";
                Container.active_minutes = input.getText().toString();
                TextView activeMinutes_log = (TextView) findViewById(R.id.active_minutes_log);
                activeMinutes_log.setText(s);

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

        final String JSONCaloriesBurned = "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.calories_burned_uuid + "\"" +
                ", \"value\": \"" + Container.calories_burned + "\"" +
                ", \"person\": \"" + Container.user_uuid + "\"}";

        final String JSONDistanceCovered = "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.distance_covered_uuid + "\"" +
                ", \"value\": \"" + Container.distance_covered + "\"" +
                ", \"person\": \"" + Container.user_uuid + "\"}";

        final String JSONTotalSteps = "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.total_steps_uuid + "\"" +
                ", \"value\": \"" + Container.total_steps + "\"" +
                ", \"person\": \"" + Container.user_uuid + "\"}";

        final String JSONFloorsClimbed = "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.floors_climbed_uuid + "\"" +
                ", \"value\": \"" + Container.floors_climbed + "\"" +
                ", \"person\": \"" + Container.user_uuid + "\"}";

        final String JSONActiveMinutes = "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.active_minutes_uuid + "\"" +
                ", \"value\": \"" + Container.active_minutes + "\"" +
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
            if (Container.calories_burned!="")
                Log.i("OpenMRS response", "AddCaloriesBurned = " + ApiAuthRest.getRequestPost("obs", inputCaloriesBurned));
            if (Container.distance_covered!="")
                Log.i("OpenMRS response", "AddDistanceCovered = " + ApiAuthRest.getRequestPost("obs", inputDistanceCovered));
            if (Container.total_steps!="")
                Log.i("OpenMRS response", "AddTotalSteps = " + ApiAuthRest.getRequestPost("obs", inputTotalSteps));
            if (Container.floors_climbed!="")
                Log.i("OpenMRS response", "AddFloorsClimbed = " + ApiAuthRest.getRequestPost("obs", inputFloorsClimbed));
            if (Container.active_minutes!="")
                Log.i("OpenMRS response", "AddActiveMinutes = " + ApiAuthRest.getRequestPost("obs", inputActiveMinutes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
