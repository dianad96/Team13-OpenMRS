package org.openmrs.mobile.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.openmrs.mobile.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Graph extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    DBHelper dbHelper;
    SharedPreferences sharedpreferences;
    GraphView graphView;
    SwipeRefreshLayout swipeLayout;

    private static final String PREFERENCE_TYPE = "HealthDataPref";
    private static final String HEALTH_BMI = "BMI";
    private static final String HEALTH_TARGET_HR = "targetHR";
    private static final String HEALTH_SYNCED_TIME = "syncTiming";
    private static final String HEALTH_IS_SYNCED_TODAY = "syncedToday";


    private static final String GRAPH_DATE_FORMAT = "dd/MM";

    private String exerciseMinutes = "0", BMI = "", targetHR = "-", actualHR = "-";
    private TextView heartRate_TV, targetHR_TV, bmi_TV, abnorm_TV, exerciseMin_TV;
    private int averageHR, averageHRCount = 0;

    private Date[] dates;
    private Float[] values;
    private String[] syncDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00463f")));

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        heartRate_TV = (TextView) findViewById(R.id.average_heart_rate_log);
        targetHR_TV = (TextView) findViewById(R.id.target_heart_rate_log);
        bmi_TV = (TextView) findViewById(R.id.bmi_log);
        abnorm_TV = (TextView) findViewById(R.id.abnormalities_log);
        exerciseMin_TV = (TextView) findViewById(R.id.exercise_min_log);

        Button input = (Button) findViewById(R.id.graph_input);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Graph.this, DashboardActivity.class);
                startActivity(i);
            }
        });

        Button graph = (Button) findViewById(R.id.graph_graph);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Graph.this, Graph.class);
                startActivity(i);
            }
        });

        Button chat = (Button) findViewById(R.id.graph_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Graph.this, Chat.class);
                startActivity(i);
            }
        });

        sharedpreferences = getSharedPreferences(PREFERENCE_TYPE, 4);
        BMI = sharedpreferences.getString(HEALTH_BMI, "N.A");
        targetHR = sharedpreferences.getString(HEALTH_TARGET_HR, "--");

        graphView = (GraphView) findViewById(R.id.graphView);
        values = new Float[]{0f, 0f, 0f, 0f, 0f};
        dates = new Date[5];
        syncDates = new String[5];


        loadHealthData();
        setGraphView();
        setTextView();
    }



    private Date getDate(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -index);
        Date xPoint = calendar.getTime();
        return xPoint;
    }

    private void loadHealthData() {
        dbHelper = new DBHelper(this);
        SimpleDateFormat db_dateFormat = new SimpleDateFormat(Container.DATE_FORMAT);
        for (int temp = 0; temp < 5; temp++) {
            // 0 = Today, 1 = Yesterday, etc..
            dates[temp] = getDate(temp);
            syncDates[temp] = db_dateFormat.format(dates[temp]);
            Log.d("SyncDate", "date[temp] = " + dates[temp] + "syncDates[temp] = " + syncDates[temp]);
        }
        ArrayList<String> arrayList = dbHelper.getHealthData();
        Log.d("Database", "arrayList = " + arrayList.size());


            if (arrayList.contains(syncDates[0])) {
                // Load data for today
                int index = arrayList.indexOf(syncDates[0]);
                Log.d("Database", "arrayList contains syncDate[0] = " + syncDates[0] + "  and index = " + index);
                GraphData graphData = dbHelper.getHealthData(index);
                if (graphData != null) {
                    values[0] = Float.valueOf(graphData.getSteps());
                    float temp = Float.valueOf(graphData.getHeartRate());
                    averageHR += temp;
                    if( temp != 0.0) {
                        averageHRCount++;
                    }
                    //actualHR = graphData.getHeartRate();
                    exerciseMinutes = graphData.getActiveMinutes();
                    exerciseMinutes = String.valueOf((int) Float.parseFloat(exerciseMinutes));
                    Log.d("Data", "Inside syncdate[0] and average HR = " + averageHR);
                }
            }
            if (arrayList.contains(syncDates[1])) {
                // Load and save steps data to show on graph
                int index = arrayList.indexOf(syncDates[1]);
                GraphData graphData = dbHelper.getHealthData(index);
                if (graphData != null){
                    values[1] = Float.valueOf(graphData.getSteps());
                    float temp = Float.valueOf(graphData.getHeartRate());
                    averageHR += temp;
                    if( temp != 0.0) {
                        averageHRCount++;
                    }
                    Log.d("Data", "Inside syncdate[1] and average HR = " + averageHR);
                }
                Log.d("GraphDB", "Syncdate[1] = " + values[1]);
            }
            if (arrayList.contains(syncDates[2])) {
                // Load and save steps data to show on graph
                int index = arrayList.indexOf(syncDates[2]);
                GraphData graphData = dbHelper.getHealthData(index);
                if (graphData != null) {
                    values[2] = Float.valueOf(graphData.getSteps());
                    float temp = Float.valueOf(graphData.getHeartRate());
                    averageHR += temp;
                    if( temp != 0.0) {
                        averageHRCount++;
                    }
                    Log.d("Data", "Inside syncdate[2] and average HR = " + averageHR);
                }
                Log.d("GraphDB", "Syncdate[2] = " + values[2]);
            }
            if (arrayList.contains(syncDates[3])) {
                // Load and save steps data to show on graph
                int index = arrayList.indexOf(syncDates[3]);
                GraphData graphData = dbHelper.getHealthData(index);
                if (graphData != null) {
                    values[3] = Float.valueOf(graphData.getSteps());
                    float temp = Float.valueOf(graphData.getHeartRate());
                    averageHR += temp;
                    if( temp != 0.0) {
                        averageHRCount++;
                    }
                    Log.d("Data", "Inside syncdate[3] and average HR = " + averageHR);
                }
                Log.d("GraphDB", "Syncdate[3] = " + values[3]);
            }
            if (arrayList.contains(syncDates[4])) {
                // Load and save steps data to show on graph
                int index = arrayList.indexOf(syncDates[4]);
                GraphData graphData = dbHelper.getHealthData(index);
                if (graphData != null) {
                    values[4] = Float.valueOf(graphData.getSteps());
                    float temp = Float.valueOf(graphData.getHeartRate());
                    averageHR += temp;
                    if( temp != 0.0) {
                        averageHRCount++;
                    }
                    Log.d("Data", "Inside syncdate[4] and average HR = " + averageHR);
                }
                Log.d("GraphDB", "Syncdate[4] = " + values[4]);
            }

        }
//    }

    private void setTextView() {
        double target_HR = 0;
        int i = 0;

        try {
            //heart_rate = Double.parseDouble(actualHR);
            heartRate_TV.setText( (averageHR / averageHRCount) + " bpm");
            i++;
        } catch (NumberFormatException e) {
            heartRate_TV.setText("-- bpm");
        }

        try {
            target_HR = Double.parseDouble(targetHR);
            targetHR_TV.setText((long) target_HR + " bpm");
            i++;
        } catch (NumberFormatException e) {
            targetHR_TV.setText("-- bpm");
        }

        if (i == 2) {
            if ( (averageHR / averageHRCount) > target_HR) {
                abnorm_TV.setText("High Average HR");
            } else if ((averageHR / averageHRCount) <= target_HR) {
                abnorm_TV.setText("Normal HR");
            }
        } else {
            abnorm_TV.setText("Insufficient data");
        }


        exerciseMin_TV.setText(exerciseMinutes + " Mins");


        if (!BMI.equals("")) {
            bmi_TV.setText(BMI);
        } else {
            bmi_TV.setText("N.A");
        }

    }

    private void setGraphView(){
        BarGraphSeries<DataPoint> series1 = new BarGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(1, values[4]),
                new DataPoint(2, values[3]),
                new DataPoint(3, values[2]),
                new DataPoint(4, values[1]),
                new DataPoint(5, values[0])
        });

        graphView.addSeries(series1);
        SimpleDateFormat dateFormat = new SimpleDateFormat(GRAPH_DATE_FORMAT);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
        staticLabelsFormatter.setHorizontalLabels(new String[]{dateFormat.format(dates[4]), dateFormat.format(dates[3]), dateFormat.format(dates[2]), dateFormat.format(dates[1]), dateFormat.format(dates[0])});
        graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        graphView.getGridLabelRenderer().setNumHorizontalLabels(5);


        series1.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
            }
        });


        // draw values on top
        series1.setDrawValuesOnTop(true);
        series1.setValuesOnTopColor(Color.BLUE);

        //Legends
        //graphView.getGridLabelRenderer().setVerticalAxisTitle("Step");
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Date");
        series1.setTitle("Step");
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setTextColor(Color.BLUE);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        graphView.removeAllSeries();
        boolean isSyncedToday = sharedpreferences.getBoolean(HEALTH_IS_SYNCED_TODAY, false);
        Long syncTiming = sharedpreferences.getLong(HEALTH_SYNCED_TIME, 0);
        Long currentTime = System.currentTimeMillis();
        Log.d("Time", "syncTime = " + syncTiming + " currentTime = " + currentTime);

        /** Check if data has been synced today and prevent user from sending multiple request to overwhelm server!
            Currently user has to wait 2min before app will send another request
         **/

        if(isSyncedToday && (syncTiming+ 120000 < currentTime ) ){
            Intent i = new Intent(Graph.this, SyncGraphService.class);
            startService(i);
            Toast.makeText(this, "Syncing data from OpenMRS Server...Please wait before refreshing again", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong(HEALTH_SYNCED_TIME, currentTime).commit();
        }
        loadHealthData();
        setGraphView();
        setTextView();
        swipeLayout.setRefreshing(false);
    }
}