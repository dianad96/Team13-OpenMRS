package org.openmrs.mobile.activities;

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
import android.widget.TextView;

import org.apache.http.entity.StringEntity;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.ApiAuthRest;

import java.io.UnsupportedEncodingException;


public class InputFood extends AppCompatActivity {

    TextView totalCalories;
    public static TextView breakfast_log;
    public static TextView lunch_log;
    public static TextView dinner_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_food);

        android.support.v7.app.ActionBar bar =  getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00463f")));

        breakfast_log = (TextView) findViewById(R.id.breakfast_log);
        breakfast_log.setText(Container.breakfast_input);

        lunch_log = (TextView) findViewById(R.id.lunch_log);
        lunch_log.setText(Container.lunch_input);

        dinner_log = (TextView) findViewById(R.id.dinner_log);
        dinner_log.setText(Container.diner_input);

        int total_cal = Container.food_calories_breakfast + Container.food_calories_lunch + Container.food_calories_dinner;
        totalCalories = (TextView) findViewById(R.id.total_calories);
        totalCalories.setText("Calories: " + total_cal);

        // Input Breakfast
        CardView breakfast = (CardView) findViewById(R.id.cv);
        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpBreakfast();
            }
        });

        // Input Lunch
        CardView lunch = (CardView) findViewById(R.id.cv2);
        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpLunch();
            }
        });

        // Input Dinner
        CardView dinner = (CardView) findViewById(R.id.cv3);
        dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDinner();
            }
        });

        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
                Intent i = new Intent(InputFood.this, DashboardActivity.class);
                startActivity(i);
            }
        });
    }

    void popUpBreakfast() {
        Container.meal_choice = "breakfast";
        Container.food_calories_breakfast = 0;
        Intent i = new Intent(InputFood.this, SearchFood.class);
        startActivity(i);
    }

    void popUpLunch() {
        Container.meal_choice = "lunch";
        Container.food_calories_lunch = 0;
        Intent i = new Intent(InputFood.this, SearchFood.class);
        startActivity(i);
    }

    void popUpDinner() {
        Container.food_calories_dinner = 0;
        Container.meal_choice = "dinner";
        Intent i = new Intent(InputFood.this, SearchFood.class);
        startActivity(i);
    }

    public void sendData()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ApiAuthRest.setURLBase(Container.URLBase);
        ApiAuthRest.setUsername(Container.username);
        ApiAuthRest.setPassword(Container.password);

        String date = SyncFitBitService.getDate(System.currentTimeMillis(), Container.DATE_FORMAT);

        final String JSONCalories = "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.calories_uuid + "\"" +
                ", \"value\": \"" + Container.total_calories + "\"" +
                ", \"person\": \"" + Container.user_uuid + "\"}";

        StringEntity inputCalories = null;
        try {
            inputCalories = new StringEntity(JSONCalories);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        inputCalories.setContentType("application/json");
        try {
            Log.i("OpenMRS response", "Add Calories = " + ApiAuthRest.getRequestPost("obs", inputCalories));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
