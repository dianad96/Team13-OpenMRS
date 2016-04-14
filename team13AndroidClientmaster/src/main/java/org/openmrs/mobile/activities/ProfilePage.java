package org.openmrs.mobile.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.openmrs.mobile.R;

public class ProfilePage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        android.support.v7.app.ActionBar bar =  getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00463f")));

        TextView patientName = (TextView) findViewById(R.id.patient_name);
        patientName.setText(Container.patient_name);

        TextView patientAge = (TextView) findViewById(R.id.patient_age);
        patientAge.setText("Age: " + Container.patient_age);

        TextView patientBirthdate = (TextView) findViewById(R.id.patient_birthdate);
        String bday = Container.patient_birthdate;
        if(!bday.matches("")) {
            patientBirthdate.setText("Birthdate: " + Container.patient_birthdate);
        } else {
            patientBirthdate.setText("Not Available");
        }

        TextView patientGender = (TextView) findViewById(R.id.patient_gender);
        patientGender.setText("Gender: " + Container.patient_gender);

        TextView patientUuid = (TextView) findViewById(R.id.patient_uuid);
        patientUuid.setText("Patient UUID: " + Container.user_uuid);
    }

}
