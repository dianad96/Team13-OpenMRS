/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONObject;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.ApiAuthRest;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;

public class DashboardActivity extends ACBaseActivity {

    private SparseArray<Bitmap> mBitmapCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));

        android.support.v7.app.ActionBar bar =  getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00463f")));

        Button input = (Button) findViewById(R.id.dash_input);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, DashboardActivity.class);
                startActivity(i);
            }
        });

        Button graph = (Button) findViewById(R.id.dash_graph);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, Graph.class);
                startActivity(i);
            }
        });

        Button chat = (Button) findViewById(R.id.dash_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, Chat.class);
                startActivity(i);
            }
        });

        // Get Patient Information if it has not been loaded before
        if(Container.patient_name.matches("") || Container.patient_gender.matches("") || Container.patient_age.matches("") || Container.patient_birthdate.matches("")) {
            try {
                getPatientData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onInputFood(View v) {
        Intent i = new Intent(this, InputFood.class);
        startActivity(i);
    }

    public void onInputExercise(View v) {
        Intent i = new Intent(this, InputExercise.class);
        startActivity(i);
    }

    public void onInputHeartRate(View v) {
        Intent i = new Intent(this, InputHeartRate.class);
        startActivity(i);
    }

    public void onInputHeightWeight(View v) {
        Intent i = new Intent(this, InputHeightWeight.class);
        startActivity(i);
    }

    public void onSyncData(View v) {
        Intent i = new Intent(this, SyncData.class);
        startActivity(i);
    }

    public void onFindPatientCallback(View v) {
        Intent i = new Intent(this, FindPatientsActivity.class);
        startActivity(i);
    }

    public void onActiveVisitsCallback(View v) {
        Intent intent = new Intent(this, FindActiveVisitsActivity.class);
        startActivity(intent);
    }

    public void onCaptureVitalsCallback(View v) {
        Intent intent = new Intent(this, CaptureVitalsActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        bindDrawableResources();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawableResources();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void bindDrawableResources() {
        mBitmapCache = new SparseArray<Bitmap>();
        ImageView inputFood = (ImageView) findViewById(R.id.findPatientButton);
        ImageView inputHeight = (ImageView) findViewById(R.id.registryPatientButton);
        ImageView inputExercise = (ImageView) findViewById(R.id.activeVisitsButton);
        ImageView captureVitalsImageButton = (ImageView) findViewById(R.id.captureVitalsButton);
        ImageView syncData = (ImageView) findViewById(R.id.syncData);

        createImageBitmap(R.drawable.ico_food, inputFood.getLayoutParams());
        createImageBitmap(R.drawable.ico_scale, inputHeight.getLayoutParams());
        createImageBitmap(R.drawable.ico_exercise, inputExercise.getLayoutParams());
        createImageBitmap(R.drawable.ico_vitals, captureVitalsImageButton.getLayoutParams());
        createImageBitmap(R.drawable.sync, syncData.getLayoutParams());

        inputFood.setImageBitmap(mBitmapCache.get(R.drawable.ico_food));
        inputHeight.setImageBitmap(mBitmapCache.get(R.drawable.ico_scale));
        inputExercise.setImageBitmap(mBitmapCache.get(R.drawable.ico_exercise));
        captureVitalsImageButton.setImageBitmap(mBitmapCache.get(R.drawable.ico_vitals));
        syncData.setImageBitmap(mBitmapCache.get(R.drawable.sync));
    }

    private void createImageBitmap(Integer key, ViewGroup.LayoutParams layoutParams) {
        if (mBitmapCache.get(key) == null) {
            mBitmapCache.put(key, ImageUtils.decodeBitmapFromResource(getResources(), key,
                    layoutParams.width, layoutParams.height));
        }
    }

    private void unbindDrawableResources() {
        if (null != mBitmapCache) {
            for (int i = 0; i < mBitmapCache.size(); i++) {
                Bitmap bitmap = mBitmapCache.valueAt(i);
                bitmap.recycle();
            }
        }
    }

    // Get Patient Information
    void getPatientData() throws Exception {
    /*
	 * SET VALUE FOR CONNECT TO OPENMRS
	 */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ApiAuthRest.setURLBase("http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/person/");
        ApiAuthRest.setUsername("diana");
        ApiAuthRest.setPassword("Admin123");


 	/*
 	 * Example how parse json return session
 	 */

        String request = Container.user_uuid;
        Object obj = ApiAuthRest.getRequestGet(request);
        JSONObject jsonObject = new JSONObject ((String) obj);
        Log.d("Profile page", (String) obj);

        Container.patient_name = jsonObject.getString("display");
        Container.patient_gender = jsonObject.getString("gender");

        if(Container.patient_gender.matches("M"))
        { Container.patient_gender = "Male"; }
        else { Container.patient_gender = "Female"; }

        Container.patient_age =  jsonObject.getString("age");
        Container.patient_birthdate = jsonObject.getString("birthdate");
        Container.patient_birthdate  = Container.patient_birthdate.substring(0,10);
    }
}
