package org.openmrs.mobile.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.entity.StringEntity;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.ApiAuthRest;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;


public class InputFood extends Activity {


    final CharSequence[] items = {};
    final CharSequence[] itemsBreakfast = {"Scrambled Egg Buritos...259 cal","Cornflake Crunch French Toast...149 cal","Banana Corn Muffins...199 cal ","Oatmeal...258 cal"};
    // arraylist to keep the selected items
    final ArrayList<Integer> seletedItemsBreakfast = new ArrayList<Integer>();

    final CharSequence [] itemsLunch = {"Garden Pasta Salad...345 cal","Beef Salad...376 cal", "Indian-Spiced Chicken Pitas...333cal", "Italian Vegetable Hoagies...326 cal"};
    final ArrayList<Integer> selectedItemsLunch = new ArrayList<Integer>();

    final CharSequence [] itemsDinner = {"Chopped Greek Salad with Chicken...142 cal", "Hawaiian Ginger-Chicken Stew...169 cal", "Braised Paprika Chicken...61 cal"};
    final ArrayList<Integer> SelectedItemsDinner = new ArrayList<Integer>();

    TextView totalCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_food);

        totalCalories = (TextView) findViewById(R.id.total_calories);

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
        Intent i = new Intent(InputFood.this, SearchFood.class);
        startActivity(i);
    }

    void popUpLunch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick your Lunch");
        // builder.setMessage("Which creature has one voice and yet becomes four-footed and two-footed and three-footed?");
        builder.setMultiChoiceItems(itemsLunch, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                if (isChecked) {
                    // If the user checked the item, add it to the selected items
                    selectedItemsLunch.add(indexSelected);
                } else if (selectedItemsLunch.contains(indexSelected)) {
                    // Else, if the item is already in the array, remove it
                    selectedItemsLunch.remove(Integer.valueOf(indexSelected));
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String s = "";
                for (int i : selectedItemsLunch) {
                    switch (i) {
                        case 0: {
                            s = s + "Garden Pasta Salad ";
                            Container.total_calories = Container.total_calories + 345;
                            break;
                        }
                        case 1: {
                            s = s + "Beef Salad ";
                            Container.total_calories = Container.total_calories + 376;
                            break;
                        }
                        case 2: {
                            s = s + "Indian-Spiced Chicken Pitas ";
                            Container.total_calories = Container.total_calories + 333;
                            break;
                        }
                        case 3: {
                            s = s + "Italian Vegetable Hoagies ";
                            Container.total_calories = Container.total_calories + 326;
                            break;
                        }
                    }
                }
                TextView lunch_log = (TextView) findViewById(R.id.lunch_log);
                lunch_log.setText(s);
                String TC = "Total calories: " + Container.total_calories;
                totalCalories.setText(TC);
                //  Your code when user clicked on OK
                //  You can write the code  to save the selected item here
            }
        });
        builder.setNegativeButton("CANCEL", null);

        builder.show();
    }

    void popUpDinner() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick your Dinner");
        // builder.setMessage("Which creature has one voice and yet becomes four-footed and two-footed and three-footed?");
        builder.setMultiChoiceItems(itemsDinner, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                if (isChecked) {
                    // If the user checked the item, add it to the selected items
                    SelectedItemsDinner.add(indexSelected);
                } else if (SelectedItemsDinner.contains(indexSelected)) {
                    // Else, if the item is already in the array, remove it
                    SelectedItemsDinner.remove(Integer.valueOf(indexSelected));
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String s = "";
                        for (int i : seletedItemsBreakfast) {
                            switch (i) {
                                case 0: {
                                    s = s + "Chopped Greek Salad with Chicken ";
                                    Container.total_calories = Container.total_calories + 142;
                                    break;
                                }
                                case 1: {
                                    s = s + "Hawaiian Ginger-Chicken Stew ";
                                    Container.total_calories = Container.total_calories + 169;
                                    break;
                                }
                                case 2: {
                                    s = s + "Braised Paprika Chicken ";
                                    Container.total_calories = Container.total_calories + 61;
                                    break;
                                }
                            }
                        }
                        TextView dinner_log = (TextView) findViewById(R.id.dinner_log);
                        dinner_log.setText(s);
                        String TC = "Total calories: " + Container.total_calories;
                        totalCalories.setText(TC);
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here
                    }
                }

        );
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
            Log.i("OpenMRS response", "AddCalories = " + ApiAuthRest.getRequestPost("obs", inputCalories));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
