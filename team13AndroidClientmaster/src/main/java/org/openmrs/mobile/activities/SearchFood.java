package org.openmrs.mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.openmrs.mobile.R;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

public class SearchFood extends AppCompatActivity {

    // for FatSecret API
    private static final String ACCESS_TOKEN_MISSING = "gone";
    private static final String TAG = SearchFood.class.getName();

    // for the listview
    MyCustomAdapter dataAdapter = null;
    ArrayList<String> food_desc = new ArrayList<String>();
    ArrayList <String> food_name = new ArrayList<String>();

    ArrayList<String> food_cal = new ArrayList<String>();
    ArrayList <String> food = new ArrayList<String>();

    ArrayList <String> food_selected = new ArrayList<String>();
    int calories = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        android.support.v7.app.ActionBar bar =  getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00463f")));

       SearchView search=(SearchView) findViewById(R.id.searchView);

        search.setQueryHint("SearchView");

        //*** setOnQueryTextFocusChangeListener ***
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

                Toast.makeText(getBaseContext(), String.valueOf(hasFocus),
                        Toast.LENGTH_SHORT).show();
            }
        });

        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub

                return false;
            }
        });


        Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "";
                for (int i=0; i<food_selected.size(); i++)
                    s = s + food_selected.get(i) + " ";
                if(Container.meal_choice.equals("breakfast")) {
                    s = s + " Calories: " + Container.food_calories_breakfast;
                    Container.breakfast_input = s;
                }
                if(Container.meal_choice.equals("lunch")) {
                    s = s + " Calories: " + Container.food_calories_lunch;
                    Container.lunch_input = s;
                }
                if(Container.meal_choice.equals("dinner")) {
                    s = s + " Calories: " + Container.food_calories_dinner;
                    Container.diner_input = s;
                }

                Intent i = new Intent(SearchFood.this, InputFood.class);
                startActivity(i);
            }
        });
    }

    public void search (final String query)
    {

        food_name.clear();
        food_desc.clear();

        SharedPreferences pref = getSharedPreferences(FatSecretUtils.PREFERENCES_FILE, MODE_PRIVATE);
        String accessToken = pref.getString(FatSecretUtils.OAUTH_ACCESS_TOKEN_KEY, ACCESS_TOKEN_MISSING);

        if(accessToken.equals(ACCESS_TOKEN_MISSING)) {
            Intent login = new Intent(this, LoginActivity_FatSecret.class);
            startActivity(login);
            finish();
            return;
        }

        FatSecretUtils.setContext(this);

        // TextView loggedInText = (TextView) findViewById(R.id.loggedInText);
        // loggedInText.setText("auth token = " + pref.getString("oauth_access_token", ACCESS_TOKEN_MISSING));

        final TextView responseText = (TextView) findViewById(R.id.responseText);
        responseText.setText("Searching foods for " + query + "...");

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();

        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                try {
                    String signedFoodSearchUrl = FatSecretUtils.sign("http://platform.fatsecret.com/rest/server.api?method=foods.search&format=json&search_expression=" + query);

                    Log.d(TAG, "Signed foods.search URL = " + signedFoodSearchUrl);

                    HttpURLConnection foodSearchConnection = (HttpURLConnection) new URL(signedFoodSearchUrl).openConnection();
                    reader = new BufferedReader(new InputStreamReader(foodSearchConnection.getInputStream()));
                    final String json = reader.readLine();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Parsing the JSON
                            JsonObject response = gson.fromJson(json, JsonObject.class);
                            JsonElement jelement = new JsonParser().parse(json);
                            JsonObject  jobject = jelement.getAsJsonObject();
                            jobject = jobject.getAsJsonObject("foods");
                            JsonArray jarray = jobject.getAsJsonArray("food");

                            for (int i=0; i<jarray.size(); i++)
                            {
                                jobject = jarray.get(i).getAsJsonObject();
                                food_desc.add(jobject.get("food_description").toString());
                                food_name.add(jobject.get("food_name").toString());
                            }

                            //responseText.setText(gson.toJson(response));

                            displayListView();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void displayListView() {

        //Array list of countries
        ArrayList<Food> FoodList = new ArrayList<Food>();

        for (int i=0; i<food_name.size(); i++) {
            Food Food = new Food(food_name.get(i), food_desc.get(i), false);
            FoodList.add(Food);
        }


        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.food_info, FoodList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Food Food = (Food) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + Food.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Food> {

        private ArrayList<Food> FoodList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Food> FoodList) {
            super(context, textViewResourceId, FoodList);
            this.FoodList = new ArrayList<Food>();
            this.FoodList.addAll(FoodList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.food_info, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        TextView tv = (TextView) v;

                        Food Food = (Food) cb.getTag();
                        Toast.makeText(getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        Food.setSelected(cb.isChecked());


                        if(cb.isChecked()) { //Add food and calories to arrayList
                            food.add(Food.name);
                            calories = calories + Integer.parseInt(Food.calories.substring(Food.calories.indexOf(": ") + 1, Food.calories.indexOf("kcal")).substring(1));
                            food_cal.add(String.valueOf(calories));
                            food_selected.add(Food.name);
                            if (Container.meal_choice.equals("breakfast"))
                                Container.food_calories_breakfast = Container.food_calories_breakfast + calories;
                            if (Container.meal_choice.equals("lunch"))
                                Container.food_calories_lunch = Container.food_calories_lunch + calories;
                            if (Container.meal_choice.equals("dinner"))
                                Container.food_calories_dinner = Container.food_calories_dinner + calories;
                        }
                        else //Remove food and calories from arrayList
                        {
                            int pos = 0;
                            for (int i=0; i<food_selected.size(); i++)
                            {
                                if(food_selected.get(i).equals(Food.name)) {
                                    pos = i;
                                    break;
                                }
                            }
                            if(Container.meal_choice.equals("breakfast"))
                                Container.food_calories_breakfast = Container.food_calories_breakfast - Integer.parseInt(food_cal.get(pos));
                            if(Container.meal_choice.equals("lunch"))
                                Container.food_calories_lunch = Container.food_calories_lunch - Integer.parseInt(food_cal.get(pos));
                            if(Container.meal_choice.equals("dinner"))
                                Container.food_calories_dinner = Container.food_calories_dinner - Integer.parseInt(food_cal.get(pos));
                            food_selected.remove(pos);
                            food_cal.remove(pos);
                        }
                    }

                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Food Food = FoodList.get(position);
            holder.code.setText(" (" +  Food.getCalories() + ")");
            holder.name.setText(Food.getName());
            holder.name.setChecked(Food.isSelected());
            holder.name.setTag(Food);

            return convertView;

        }
    }

    /*/
    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<Food> FoodList = dataAdapter.FoodList;
                for(int i=0;i<FoodList.size();i++){
                    Food Food = FoodList.get(i);
                    if(Food.isSelected()){
                        responseText.append("\n" + Food.getName());
                    }
                }

                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();

            }
        });
    }
    /*/
}
