package org.openmrs.mobile.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.ApiAuthRest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Graph extends Activity {

    static String username = Container.username;
    static String password = Container.password;
    static String URLBase = Container.URLBase;

    final static String Raizelb = Container.user_uuid;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String GRAPH_DATE_FORMAT = "dd/MM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

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
        Float[] values = new Float[5];
        Date[] dates = new Date[5];
        SimpleDateFormat dateFormat = new SimpleDateFormat(GRAPH_DATE_FORMAT);
        createGraph(values, dates, getPersonInput(Raizelb), "STEPS");
        logBS(dates, values);

        BarGraphSeries<DataPoint> series1 = new BarGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(dates[0],values[0]),
                new DataPoint(dates[1],values[1]),
                new DataPoint(dates[2],values[2]),
                new DataPoint(dates[3],values[3]),
                new DataPoint(dates[4],values[4])
        });

        graphView.addSeries(series1);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
        staticLabelsFormatter.setHorizontalLabels(new String[] {dateFormat.format(dates[4]),dateFormat.format(dates[3]),dateFormat.format(dates[2]),dateFormat.format(dates[1]),dateFormat.format(dates[0]) });
        graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        graphView.getGridLabelRenderer().setNumHorizontalLabels(5);

        series1.setSpacing(30);

        graphView.getViewport().setMinX(dates[4].getTime());
        graphView.getViewport().setMaxX(dates[0].getTime());
        graphView.getViewport().setXAxisBoundsManual(true);

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
        int i = 0;
        String UUID = null;
        while ((location = input.indexOf("uuid", location)) != -1) {
            location += 7;
            int templocation = input.indexOf("\"", location);
            UUID = input.substring(location,templocation);
            if (returnObs(UUID,concept) != null ) {
                values[i] = getConceptValue(returnObs(UUID,concept),concept);
                dates[i] = getDate(returnObs(UUID,concept),concept);
                i++;
            }
            if (i > 4)
                break;
        }

    }

    private String getPersonInput(String patientUUID) {
        String display = null;
        try {
            display = ApiAuthRest.getRequestGet("obs?patient=" + patientUUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return display;
    }

    private String returnObs (String UUID, String concept) {
        String display = null;
        try {
            display = ApiAuthRest.getRequestGet("obs/" + UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (display.indexOf(concept) != -1) {
            return display;
        }
        else
            return null;
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

    private Date getDate (String obs, String concept) {
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
    }

    private void logBS (Date[] dates, Float[] values) {
        for (int i = 0; i < 5; i++) {
            Log.i("OpenMRS response", dates[i].toString() + ":" + Float.toString(values[i]));
        }
    }

}
