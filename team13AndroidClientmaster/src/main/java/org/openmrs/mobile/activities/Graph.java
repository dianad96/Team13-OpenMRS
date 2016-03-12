package org.openmrs.mobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.ApiAuthRest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Graph extends Activity {

    static String username = Container.username;
    static String password = Container.password;
    static String URLBase = Container.URLBase;

    final static String Raizelb = Container.user_uuid;
    final static String Chevy = "06168cfe-7d77-45b7-b8ba-290201f2ba07";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String GRAPH_DATE_FORMAT = "dd/MM";

    private String exerciseMinutes ="0", BMI = "", heartRate = "-", targetHR = "-", actualHR = "-";
    private TextView heartRate_TV, targetHR_TV, bmi_TV, abnorm_TV, exerciseMin_TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ApiAuthRest.setURLBase(URLBase);
        ApiAuthRest.setUsername(username);
        ApiAuthRest.setPassword(password);



        GraphView graphView = (GraphView) findViewById(R.id.graphView);
        Float[] values = new Float[]{0f, 0f, 0f, 0f, 0f};
        Date[] dates = new Date[5];
        SimpleDateFormat dateFormat = new SimpleDateFormat(GRAPH_DATE_FORMAT);
        createGraph(values, dates, getPersonInput(Chevy), "STEPS");
        logBS(dates, values);
        setTextView();

        BarGraphSeries<DataPoint> series1 = new BarGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(1,values[4]),
                new DataPoint(2,values[3]),
                new DataPoint(3,values[2]),
                new DataPoint(4,values[1]),
                new DataPoint(5,values[0])
        });

        graphView.addSeries(series1);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
        staticLabelsFormatter.setHorizontalLabels(new String[]{dateFormat.format(dates[4]), dateFormat.format(dates[3]), dateFormat.format(dates[2]), dateFormat.format(dates[1]), dateFormat.format(dates[0]) });
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
        series1.setValuesOnTopColor(Color.RED);

    }


    private void createGraph(Float[] values, Date[] dates, String input, String concept) {
        int location = 0;
        int i;
        String UUID;
        for (int temp = 0; temp < 5; temp++) {
            dates[temp] = getDate(temp);
        }
        while ((location = input.indexOf("uuid", location)) != -1) {
            location += 7;
            int templocation = input.indexOf("\"", location);
            UUID = input.substring(location,templocation);
//            Log.d("OpenMRS response UUID:", UUID);
//            if (checkConcept(input.substring(location), concept)) {
//                String obs = returnObs(UUID);
//                if ((i = checkObsDate(dates, obs)) != -1) {
//                    if (getConceptValue(obs,concept) > values[i]) {
//                        values[i] = getConceptValue(obs, concept);
//                        Log.d("Values : ", values[i].toString());
//                    }
//                }
//                else break;
//
//                //dates[i] = getDate(returnObs(UUID,concept),concept);
//            }
            int a = checkConcept(input.substring(location), concept);
            String obs = returnObs(UUID);
//            Log.i("A value", "" + a);
            switch (a)
            {
                case 1:
                    if ((i = checkObsDate(dates, obs)) != -1) {
                        if (getConceptValue(obs,concept) > values[i]) {
                            values[i] = getConceptValue(obs, concept);
                            Log.d("Steps Values : ", values[i].toString());
                        }
                    }
                    break;


                case 2:
                    Log.i("BMI", BMI);
                    break;

                case 3:
                    if((checkExerciseDate(dates, obs)) == -1){
                        heartRate = "N.A";
                    }
                    else {
                        actualHR = heartRate;
                        Log.i("heartRate", "heartRate found for today");
                    }
                    break;

                case 4:
                    if ((checkExerciseDate(dates, obs)) == -1) {
                        exerciseMinutes = "0";
                    }
                    else {
                        Log.i("ActivityMinutes", exerciseMinutes);
                    }
                    break;

                case 5:
                    Log.i("Target HR", targetHR);
                    break;

                default:
                    break;


            }

        }

    }

    private int checkConcept(String input, String concept) {
        int tempLocation;
        tempLocation = input.indexOf("display") + 9;
        String temp = input.substring(tempLocation + 1);
        tempLocation = temp.indexOf("\"");
        temp = temp.substring(0, tempLocation);
//        Log.i("OpenMRS response", temp);
        if (temp.indexOf(concept) != -1) {
            return 1;
        }
        else if(temp.indexOf("BODY MASS INDEX") != -1) {
            BMI = temp.substring(16);
//            Log.i("BMI", BMI);
            return 2;
        }
        else if(temp.indexOf("PULSE") != -1) {
            heartRate = temp.substring(6);
            Log.i("heartRate", heartRate);
            return 3;
        }
        else if (temp.indexOf("Active Minutes") != -1) {
            exerciseMinutes = temp.substring(15);
            return 4;
        }
        else if (temp.indexOf("TARGET HEART RATE") != -1) {
            targetHR = temp.substring(18);
            return 5;
        }
        else
            return 0;
    }

    private String getPersonInput(String patientUUID) {
        String display = null;
        try {
            display = ApiAuthRest.getRequestGet("obs?patient=" + patientUUID) + "&concept=" + Container.heart_rate_uuid;
            Log.i("OpenMRS response-GPI", display);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return display;
    }

    private String returnObs (String UUID) {
        String display = null;
        try {
            display = ApiAuthRest.getRequestGet("obs/" + UUID);
            //Log.i("OpenMRS response", display);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return display;
    }

    private int checkObsDate(Date[] dates, String obs) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        int startPoint = obs.indexOf("obsDatetime") + 14;
        int endPoint = startPoint + 10;
        Date date = new Date();
        try {
            date = dateFormat.parse(obs.substring(startPoint, endPoint));
//            Log.i("OpenMRS response", date.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 5; i++) {
//            Log.i("OpenMRS response", Boolean.toString(date.equals(dates[i])));
            if (date.equals(dates[i])) {
                return i;
            }
        }
        return -1;
    }

    private Float getConceptValue (String obs, String concept) {
        int startPoint;
        int endPoint;
        float yPoint;
        obs = obs.substring(obs.indexOf(concept));
        startPoint = obs.indexOf(":") + 2;
        endPoint = obs.indexOf("\"");
        yPoint = Float.parseFloat(obs.substring(startPoint, endPoint));
        return yPoint;

    }


    /*private Date getDate (String obs, String concept) {
        int startPoint = obs.indexOf("obsDatetime") + 14;
        int endPoint = startPoint + 10;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date xPoint = new Date();
        try {
            xPoint = dateFormat.parse(obs.substring(startPoint,endPoint));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return xPoint;
    }*/
    private Date getDate (int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -index);
        Date xPoint = calendar.getTime();
        return xPoint;
    }

    private void logBS (Date[] dates, Float[] values) {
        for (int i = 0; i < 5; i++) {
//            Log.i("OpenMRS response", dates[i].toString() + ":" + Float.toString(values[i]));
        }
    }


    private int checkExerciseDate(Date[] dates, String obs) {
//        Log.i("Obs", obs);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        int startPoint = obs.indexOf("obsDatetime") + 14;
        int endPoint = startPoint + 10;
        Date date;
//        Date date = new Date();
        try {
            date = dateFormat.parse(obs.substring(startPoint,endPoint));
//            Log.i("OpenMRS response", "Checking exercise date = " + date);
            if(date.equals(dates[0])) {
//                Log.i("ActivityExercise", "Activity Date is today!!" + date);
                return 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private void setTextView() {

        double heart_rate = 0, target_HR = 0;
        int i=0;

        try {
            heart_rate = Double.parseDouble(actualHR);
            heartRate_TV.setText((long)heart_rate + " bpm");
            i++;
        } catch(NumberFormatException e) {
            heartRate_TV.setText("-- bpm");
        }

        try {
            target_HR = Double.parseDouble(targetHR);
            targetHR_TV.setText((long)target_HR + " bpm");
            i++;
        } catch(NumberFormatException e) {
            targetHR_TV.setText("-- bpm");
        }

        if(i==2) {
            if (heart_rate > target_HR) {
                abnorm_TV.setText("High Average HR");
            } else if (heart_rate <= target_HR) {
                abnorm_TV.setText("Normal HR");
            }
        } else {
            abnorm_TV.setText("Insufficient data");
        }


        exerciseMin_TV.setText(exerciseMinutes + " Mins");


        if(!BMI.equals("")) {
            bmi_TV.setText(BMI);
        } else { bmi_TV.setText("N.A"); }

    }
}
