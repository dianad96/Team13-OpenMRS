package org.openmrs.mobile.activities;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.ApiAuthRest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sam on 10/03/2016.
 */
public class NotifyService extends IntentService {
    public NotifyService() {
        super("NotifyServicce");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean newMessage = false;
        try {
            newMessage = checkForNewMessage(Container.user_uuid, Container.chat_uuid);
            Log.i("myTag", Boolean.toString(newMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (newMessage) {
            Notify("OpenMRS","You have got a new message");
        }
    }

    private boolean checkForNewMessage(String userUUID, String concept) throws Exception{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ApiAuthRest.setURLBase("http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/");
        ApiAuthRest.setUsername("diana");
        ApiAuthRest.setPassword("Admin123");

        String request = "obs?patient=" + userUUID + "&concept=" + concept;
        Object obj = ApiAuthRest.getRequestGet(request);
        Log.i("myTag",obj.toString());
        JSONObject jsonObject = new JSONObject ((String) obj);
        JSONArray result = jsonObject.getJSONArray("results");
        JSONObject firstValue = (JSONObject) result.get(0);
        Log.i("myTag",firstValue.toString());
        String latestMessageUUID = firstValue.getString("uuid");
        jsonObject = new JSONObject(getMessageDetails(latestMessageUUID));
        String latestMessage = jsonObject.getString("obsDatetime");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
        Date lastMessage = dateFormat.parse(latestMessage);
        //"2016-01-29T10:01:05.000+0000"

        Calendar calendar = Calendar.getInstance();
        //calendar.set(Calendar.HOUR,-1);
        Date lastHour = calendar.getTime();
        Log.i("myTag","Latest msg: " + lastMessage.toString() + " Last hour: " + lastHour.toString());
        Long halfHour = 1800000L;
        if ((lastHour.getTime() - lastMessage.getTime()) < halfHour) {
            return true;
        }
        return false;

    }

    private String getMessageDetails(String UUID) throws Exception {
        String request = "obs/" + UUID;
        return ApiAuthRest.getRequestGet(request);
    }

    private void Notify(String notificationTitle, String notificationMessage) {
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_openmrs)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationMessage);
        int mNotificationId = 001;

        Intent resultIntent = new Intent(this, Chat.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        0
                );

        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotifyMgr.notify(mNotificationId,mBuilder.build());

    }


}
