package org.openmrs.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.openmrs.mobile.R;

public class LoginPatient extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_patient);

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
        startService(new Intent(this, AlarmManagerService.class));
    }

}
