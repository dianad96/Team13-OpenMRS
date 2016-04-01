package org.openmrs.mobile.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.openmrs.mobile.R;

public class LoginPatient extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_patient);

        android.support.v7.app.ActionBar bar =  getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00463f")));

        Button sub = (Button) findViewById(R.id.subpat);
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText mEdit = (EditText) findViewById(R.id.editsub);
                String s = mEdit.getText().toString();
                if (!s.equals(""))
                    Container.user_uuid = s;
                Intent i = new Intent(LoginPatient.this, DashboardActivity.class);
                startActivity(i);
            }
        });

        Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginPatient.this, RegisterPatient.class);
                startActivity(i);
            }

        });
        startService(new Intent(this, AlarmManagerService.class));
    }


}
