package org.openmrs.mobile.activities;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
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

        TextView patientName = (TextView) findViewById(R.id.patient_name);
        patientName.setText(Container.patient_name);

        TextView patientAge = (TextView) findViewById(R.id.patient_age);
        patientAge.setText("Age: " + Container.patient_age);

        TextView patientBirthdate = (TextView) findViewById(R.id.patient_birthdate);
        patientBirthdate.setText("Birthdate: " + Container.patient_birthdate);

        TextView patientGender = (TextView) findViewById(R.id.patient_gender);
        patientGender.setText("Gender: " + Container.patient_gender);
        ImageView image  = (ImageView) findViewById(R.id.user_profile_photo);
        Resources res = getResources(); /** from an Activity */

        if(Container.patient_gender.equals("F"))
            image.setImageDrawable(res.getDrawable(R.drawable.profilef));
        else
            image.setImageDrawable(res.getDrawable(R.drawable.profilem));

        TextView patientUuid = (TextView) findViewById(R.id.user_uuid);
        patientUuid.setText("Patient UUID: " + Container.user_uuid);
    }

}
