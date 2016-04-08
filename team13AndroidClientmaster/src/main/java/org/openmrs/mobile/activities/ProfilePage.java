package org.openmrs.mobile.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.openmrs.mobile.R;

public class ProfilePage extends AppCompatActivity {

    String uuid, name, age, gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        android.support.v7.app.ActionBar bar =  getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00463f")));

//        // Get Patient Information
//        try {
//            getData();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        TextView patientName = (TextView) findViewById(R.id.patient_name);
        patientName.setText(Container.patient_name);

        TextView patientAge = (TextView) findViewById(R.id.patient_age);
        patientAge.setText("Age: " + Container.patient_age);

        TextView patientBirthdate = (TextView) findViewById(R.id.patient_birthdate);
        patientBirthdate.setText("Birthdate: " + Container.patient_birthdate);

        TextView patientGender = (TextView) findViewById(R.id.patient_gender);
        patientGender.setText("Gender: " + Container.patient_gender);

        TextView patientUuid = (TextView) findViewById(R.id.patient_uuid);
        patientUuid.setText("Patient UUID: " + Container.user_uuid);
    }


//    // Get Patient Information
//    void getData() throws Exception {
//    /*
//	 * SET VALUE FOR CONNECT TO OPENMRS
//	 */
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        ApiAuthRest.setURLBase("http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/person/");
//        ApiAuthRest.setUsername("diana");
//        ApiAuthRest.setPassword("Admin123");
//
//
// 	/*
// 	 * Example how parse json return session
// 	 */
//
//        String request = Container.user_uuid;
//        Object obj = ApiAuthRest.getRequestGet(request);
//        JSONObject jsonObject = new JSONObject ((String) obj);
//
//        uuid = (String) jsonObject.get("uuid");
//        name = (String) jsonObject.get("display");
//        gender = (String) jsonObject.get("gender");
//        age = (String) jsonObject.get("age");
//    }

}
