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

public class InputHeightWeight extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_height_weight);

        android.support.v7.app.ActionBar bar =  getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00463f")));

        // Input Height
        CardView heightb = (CardView) findViewById(R.id.hw1);
        heightb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heightf();
            }
        });

        // Input Weight
        CardView weightb = (CardView) findViewById(R.id.hw2);
        weightb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightf();
            }
        });


        Button submit = (Button) findViewById(R.id.hw_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendData();
                Intent i = new Intent(InputHeightWeight.this, DashboardActivity.class);
                startActivity(i);
            }
        });
    }

    void heightf() {

        final EditText input = new EditText(InputHeightWeight.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Height (cm)");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String s = input.getText().toString() + " cm";
                Container.height = input.getText().toString();
                TextView height_log = (TextView) findViewById(R.id.height_log);
                height_log.setText(s);

            }
        });
        builder.setNegativeButton("CANCEL", null);

        builder.show();
    }


    void weightf() {

        final EditText input = new EditText(InputHeightWeight.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Weight (kg)");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String s = input.getText().toString() + " kg";
                Container.weight = input.getText().toString();
                TextView weight_log = (TextView) findViewById(R.id.weight_log);
                weight_log.setText(s);
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

        String date = SyncFitBitService.getDate(System.currentTimeMillis(), Container.DATE_FORMAT);

        final String JSONHeight= "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.height_uuid + "\"" +
                ", \"value\": \"" + Container.height + "\"" +
                ", \"person\": \"" + Container.user_uuid + "\"}";

        final String JSONWeight= "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.weight_uuid + "\"" +
                ", \"value\": \"" + Container.weight + "\"" +
                ", \"person\": \"" + Container.user_uuid + "\"}";

        StringEntity heightV = null;
        StringEntity weightV = null;
        try {
            heightV = new StringEntity(JSONHeight);
            weightV = new StringEntity(JSONWeight);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        heightV.setContentType("application/json");
        weightV.setContentType("application/json");
        try {
            Log.i("OpenMRS response", "Height = " + ApiAuthRest.getRequestPost("obs", heightV));
            Log.i("OpenMRS response", "Weight = " + ApiAuthRest.getRequestPost("obs", weightV));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
