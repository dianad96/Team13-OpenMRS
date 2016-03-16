package org.openmrs.mobile.activities;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Sam on 09/03/2016.
 */
public class AlarmManagerService extends IntentService {

    public AlarmManagerService() {
        super("AlarmManagerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("myTag","On Alarm Manager");
        Container.latest_message = Calendar.getInstance().getTime();
        Log.i("myTag",Container.latest_message.toString());

        Intent notifyIntent = new Intent(this, NotifyService.class);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, notifyIntent, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0,
                60000, pendingIntent);

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        //WakefulBroadcastReceiver.completeWakefulIntent(intent);



    }

}
