package org.openmrs.mobile.activities;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

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

        Intent notifyIntent = new Intent(this, NotifyService.class);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, notifyIntent, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0,
                1800000, pendingIntent);

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        //WakefulBroadcastReceiver.completeWakefulIntent(intent);



    }

}
