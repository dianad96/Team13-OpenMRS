package org.openmrs.mobile.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SyncData extends AppCompatActivity {

    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    private static final String AUTHORIZATION_URL = "https://www.fitbit.com/oauth2/authorize";
    private static final String ACCESS_TOKEN_URL = "https://api.fitbit.com/oauth2/token";
    private static final String CLIENT_ID = "227GHX";
    private static final String CLIENT_SECRET = "02b7cc9ffbe9dbc74bdd370631e9d2c2";
    private static final String RESPONSE_TYPE = "code";
    private static final String SCOPE_VALUE = "activity%20heartrate%20nutrition%20sleep%20weight";
    private static final String REDIRECT_URL = "openmrs://logincallback";

    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String SCOPE_TYPE_PARAM = "scope";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";

    // Read Google Fit Data

    public static final String TAG = "Team13AndroidClient";
    private static final int REQUEST_OAUTH = 1;
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private static String dateSynced;
    private static float calCount;
    private static int stepCount;

    private GoogleApiClient mClient = null;

    SharedPreferences sharedpreferences;
    private static final String PREFERENCE_TYPE = "FitbitPref";
    private static final String FITBIT_KEY = "fitbitAuth";
    private static final String FITBIT_ACCESS_KEY = "accessKey";
    private static final String FITBIT_REFRESH_KEY = "refreshKey";
    private static final String FITBIT_KEY_TIMING = "keyTiming";
    private static final String FITBIT_USER_ID = "userID";
    private static final String FITBIT_LAST_SYNCED = "lastSynced";

    public static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    CustomTabsClient mCustomTabsClient;
    CustomTabsSession mCustomTabsSession;
    CustomTabsServiceConnection mCustomTabsServiceConnection;
    CustomTabsIntent customTabsIntent;

    private HttpClient httpClient = new DefaultHttpClient();
    private HttpPost httpPost = new HttpPost(ACCESS_TOKEN_URL);
    private HttpParams myParams = new BasicHttpParams();

    private String string;
    private String ENCODED_AUTHORIZATION;
    private Button mGoogleFitBtn, mFitBitBtn, mFitBitLogoutBtn;

    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);

        android.support.v7.app.ActionBar bar =  getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00463f")));

        Button input = (Button) findViewById(R.id.sync_input);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SyncData.this, DashboardActivity.class);
                startActivity(i);
            }
        });

        final Button graph = (Button) findViewById(R.id.sync_graph);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SyncData.this, Graph.class);
                startActivity(i);
            }
        });

        Button chat = (Button) findViewById(R.id.sync_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SyncData.this, Chat.class);
                startActivity(i);
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        buildFitnessClient();

        mGoogleFitBtn = (Button) findViewById(R.id.mGoogleFitBtn);
        mFitBitBtn = (Button) findViewById(R.id.mFitbitBtn);
        mFitBitLogoutBtn = (Button) findViewById(R.id.mFitbitLogout);

        sharedpreferences = getSharedPreferences(PREFERENCE_TYPE, 4);
        if(sharedpreferences.getString(FITBIT_KEY,null) != null){
            mFitBitBtn.setText("Sync Fitbit");
            mFitBitLogoutBtn.setVisibility(View.VISIBLE);
            mFitBitLogoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.clear();
                    editor.commit();
                    finish();
                    startActivity(getIntent());
                }
            });
        }

        mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {

                //Pre-warming
                mCustomTabsClient = customTabsClient;
                mCustomTabsClient.warmup(0L);
                //Initialize a session as soon as possible.
                mCustomTabsSession = mCustomTabsClient.newSession(null);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mCustomTabsClient = null;
            }
        };

        CustomTabsClient.bindCustomTabsService(SyncData.this, CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection);

        customTabsIntent = new CustomTabsIntent.Builder(mCustomTabsSession)
                .setToolbarColor(ContextCompat.getColor(this, R.color.red))
                .setShowTitle(true)
                .build();
        /*
            End custom tabs setup
         */

        mGoogleFitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SyncData.this, SyncGoogleFitService.class);
                startService(i);
                // startActivity(i);
            }
        });

        mFitBitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sharedpreferences.getString(FITBIT_KEY, null) == null) {
                    customTabsIntent.launchUrl(SyncData.this, Uri.parse(getAuthorizationUrl()));
                }

                else {
                    Calendar calendar = new GregorianCalendar();
                    AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();
                    Long time = sharedpreferences.getLong(FITBIT_KEY_TIMING, 0);

                    if (time == 0) { //No token
                        Log.d("TAG", "access code received = " + sharedpreferences.getString(FITBIT_KEY, null));
                        asyncTaskRunner.doInBackground("authorize");
                        Intent mServiceIntent = new Intent(SyncData.this, SyncFitBitService.class);
                        startService(mServiceIntent);
                    } else if (calendar.getTimeInMillis() > time) {
                        //Refresh token then start syncing data
                        asyncTaskRunner.doInBackground("refresh");
                        Intent mServiceIntent = new Intent(SyncData.this, SyncFitBitService.class);
                        startService(mServiceIntent);
                    } else if (calendar.getTimeInMillis() < time) {
                        // Token is still valid
                        Intent mServiceIntent = new Intent(SyncData.this, SyncFitBitService.class);
                        startService(mServiceIntent);
                    }

                }
            }
        });

//        final Button mQueryBtn = (Button) findViewById(R.id.queryBtn);
//        dbHelper = new DBHelper(this);
//        mQueryBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int row = dbHelper.numberOfRows();
//                ArrayList<String> arrayList = dbHelper.getHealthData();
//                TextView mQueryTV = (TextView) findViewById(R.id.queryTextview);
//                mQueryTV.setText("Number of rows = " + row + "\n" + arrayList.get(0) );
//                TextView mQueryTV2 = (TextView) findViewById(R.id.queryTV2);
//                GraphData graphData = dbHelper.getHealthData(0);
//                if(graphData != null)
//                    mQueryTV2.setText("Steps = " + graphData.getSteps());
//            }
//        });
//
//
//        Button mInsertDataBtn = (Button) findViewById(R.id.insertDataBtn);
//        mInsertDataBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadToDB();
//            }
//        });

    }

    // GET DATA FROM GOOGLEFIT


    /**
     *  Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     *  to connect to Fitness APIs. The scopes included should match the scopes your app needs
     *  (see documentation for details). Authentication will occasionally fail intentionally,
     *  and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     *  can address. Examples of this include the user never having signed in before, or
     *  having multiple accounts on the device and needing to specify which account to use, etc.
     */
    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                new InsertAndVerifyDataTask().execute();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            SyncData.this, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(SyncData.this,
                                                REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(TAG,
                                                "Exception while starting resolution activity", e);
                                    }
                                }
                            }
                        }
                )
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        Log.i(TAG, "Connecting...");
        mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mClient.isConnected()) {
            mClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    private class InsertAndVerifyDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {


            // Begin by creating the query.
            DataReadRequest readRequest1 = calCount();
            DataReadRequest readRequest2 = stepCount();

            // [START read_dataset]
            // Invoke the History API to fetch the data with the query and await the result of
            // the read request.
            DataReadResult dataReadResult1 =
                    Fitness.HistoryApi.readData(mClient, readRequest1).await(1, TimeUnit.MINUTES);
            DataReadResult dataReadResult2 =
                    Fitness.HistoryApi.readData(mClient, readRequest2).await(1, TimeUnit.MINUTES);
            // [END read_dataset]


            // For the sake of the sample, we'll print the data so we can see what we just added.
            // In general, logging fitness information should be avoided for privacy reasons.
            printData(dataReadResult1);
            printData(dataReadResult2);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            TextView display = (TextView) findViewById(R.id.output);
            display.setText("Step Count: " + stepCount + " Calories Count: " + calCount + " Date Synced: " + dateSynced);

        }
    }


    private DataReadRequest stepCount() {
        DateTime now = new DateTime();
        DateTime midnight = now.withTimeAtStartOfDay();
        DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.google.android.gms")
                .build();

        DataReadRequest readStep = new DataReadRequest.Builder()
                .setTimeRange(midnight.getMillis(), now.getMillis(), TimeUnit.MILLISECONDS)
                .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();

        return readStep;
    }

    private DataReadRequest calCount() {
        DateTime now = new DateTime();
        DateTime midnight = now.withTimeAtStartOfDay();
        DataReadRequest readCal = new DataReadRequest.Builder()
                .setTimeRange(midnight.getMillis(), now.getMillis(), TimeUnit.MILLISECONDS)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();

        return readCal;
    }


    private void printData(DataReadResult dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: "
                    + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }

        // [END parse_read_data_result]
    }
    private void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        for(DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            dateSynced = dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS));
            for(Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                if (field.getName().equals("steps")) {
                    stepCount = dp.getValue(field).asInt();
                }
                else {
                    calCount = dp.getValue(field).asFloat();
                }
            }
        }
    }

    public static String getDate() { return dateSynced; }

    public static int getStep() {
        return stepCount;
    }

    public static double getCal() {
        return calCount;
    }

    private void buildFitBitClient(){
        mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {

                //Pre-warming
                mCustomTabsClient = customTabsClient;
                mCustomTabsClient.warmup(0L);
                //Initialize a session as soon as possible.
                mCustomTabsSession = mCustomTabsClient.newSession(null);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mCustomTabsClient = null;
            }
        };

        CustomTabsClient.bindCustomTabsService(SyncData.this, CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection);

        customTabsIntent = new CustomTabsIntent.Builder(mCustomTabsSession)
                .setToolbarColor(ContextCompat.getColor(this, R.color.red))
                .setShowTitle(true)
                .build();
        /*
            End custom tabs setup
         */
    }

    private String getBase64String(String clientID, String clientSecret) {
        return Base64.encodeToString((clientID + ":" + clientSecret).getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private static String getAuthorizationUrl() {
        return AUTHORIZATION_URL
                + QUESTION_MARK
                + RESPONSE_TYPE_PARAM + EQUALS + RESPONSE_TYPE
                + AMPERSAND
                + CLIENT_ID_PARAM + EQUALS + CLIENT_ID
                + AMPERSAND
                + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URL
                + AMPERSAND
                + SCOPE_TYPE_PARAM + EQUALS + SCOPE_VALUE
                + AMPERSAND
                + "prompt=login"; //Optional
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            int count = strings.length;
            for(int i=0; i<count; i++){
                Log.d("TAG", "String received = " + strings[i].toString());
                if(strings[i].equals("authorize")){
                    doAuthorization();
                }
                else if(strings[i].equals("refresh")) {
                    doRefreshToken();
                }
            }

            return null;
        }

    }

    private void doAuthorization() {
        ENCODED_AUTHORIZATION = getBase64String(CLIENT_ID, CLIENT_SECRET);
        Log.d("TAG", "Encoded Authorization Code = " + ENCODED_AUTHORIZATION);
        httpClient = new DefaultHttpClient(myParams);
        Log.d("TAG", "Inside sendhttpPost");
        httpPost.setHeader("Authorization", "Basic " + ENCODED_AUTHORIZATION);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("client_id", CLIENT_ID));
        nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nameValuePairs.add(new BasicNameValuePair("redirect_uri", REDIRECT_URL));
        nameValuePairs.add(new BasicNameValuePair("code", sharedpreferences.getString(FITBIT_KEY, null)));

        try {
            Log.d("TAG", "trying to sendHttp");
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            org.apache.http.HttpResponse response = httpClient.execute(httpPost);
            Toast.makeText(SyncData.this, "Executing post!!!", Toast.LENGTH_SHORT).show();
            string = EntityUtils.toString(response.getEntity());
            Log.d("TAG", string);
            JSONObject jsonObject = new JSONObject(string);
            String accessToken = jsonObject.getString("access_token");
            String refreshToken = jsonObject.getString("refresh_token");
            String userID = jsonObject.getString("user_id");

            Calendar calendar = new GregorianCalendar();
            Long time = calendar.getTimeInMillis();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(FITBIT_USER_ID, userID);
            editor.putString(FITBIT_ACCESS_KEY, accessToken);
            editor.putString(FITBIT_REFRESH_KEY, refreshToken);
            editor.putLong(FITBIT_KEY_TIMING, (time + 3600 * 1000)); // seconds to milliseconds
            editor.commit();

            Toast.makeText(SyncData.this, "Access Token received : " + accessToken, Toast.LENGTH_LONG).show();
            Log.d("TAG", "access_token = " + accessToken);
            Log.d("TAG", "refresh_token = " + refreshToken);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d("TAG", "ERROR Setting entity");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("TAG", "JSON Exception Error");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.d("TAG", "Client Protocol Error");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG", "IO Exception Error");
        }
    }

    private void doRefreshToken() {
        ENCODED_AUTHORIZATION = getBase64String(CLIENT_ID, CLIENT_SECRET);
//        Log.d("TAG", "Encoded Authorization Code = " + ENCODED_AUTHORIZATION);
        httpClient = new DefaultHttpClient(myParams);
//        Log.d("TAG", "Inside sendhttpPost");
        httpPost.setHeader("Authorization", "Basic " + ENCODED_AUTHORIZATION);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
        nameValuePairs.add(new BasicNameValuePair("refresh_token", sharedpreferences.getString(FITBIT_REFRESH_KEY, null)));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            org.apache.http.HttpResponse response = httpClient.execute(httpPost);
            Toast.makeText(SyncData.this, "Executing refresh token", Toast.LENGTH_SHORT).show();
            string = EntityUtils.toString(response.getEntity());
            Log.d("TAG", string);
            JSONObject jsonObject = new JSONObject(string);
            String accessToken = jsonObject.getString("access_token");
            String refreshToken = jsonObject.getString("refresh_token");

            Calendar calendar = new GregorianCalendar();
            Long time = calendar.getTimeInMillis();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(FITBIT_ACCESS_KEY, accessToken);
            editor.putString(FITBIT_REFRESH_KEY, refreshToken);
            editor.putLong(FITBIT_KEY_TIMING, (time + 3600 * 1000)); // seconds to milliseconds
            editor.commit();

//            Toast.makeText(SyncFitbit.this, "Access Token received : " + accessToken, Toast.LENGTH_LONG).show();
            Log.d("TAG", "access_token = " + accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadToDB() {
        String dateSynced = getDate(System.currentTimeMillis(), DATE_FORMAT);
        String temp_steps = "888",
                temp_distance = "0",
                temp_floors = "0",
                temp_caloriesOut = "0",
                temp_foodCalories = "0",
                temp_totalActiveMinutes = "0",
                temp_heartRate = "0";

            dbHelper.insertHealthData("2016-04-08", temp_steps, temp_distance, temp_floors, temp_caloriesOut, temp_foodCalories, temp_totalActiveMinutes, "75");
            dbHelper.insertHealthData("2016-04-07", "704", temp_distance, temp_floors, temp_caloriesOut, temp_foodCalories, temp_totalActiveMinutes, "69");
            dbHelper.insertHealthData("2016-04-06", "604", temp_distance, temp_floors, temp_caloriesOut, temp_foodCalories, temp_totalActiveMinutes, "75");
            dbHelper.insertHealthData("2016-04-05", "504", temp_distance, temp_floors, temp_caloriesOut, temp_foodCalories, temp_totalActiveMinutes, "72");
            Log.d("DBHelper", "Uploading dummy data ");


    }

}

