package org.openmrs.mobile.activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import org.openmrs.mobile.R;

import java.io.UnsupportedEncodingException;


//** HARD CODED VERSION - TO BE CHANGED!!! **//
public class InputManually extends Activity {

    static String username = "diana";
    static String password = "Admin123";
    static String URLBase = "http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/person";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_manually);

        EditText mgivenName = (EditText) findViewById(R.id.id_givenName);
        EditText mfamilyName = (EditText) findViewById(R.id.id_givenName);
        EditText mgender = (EditText) findViewById(R.id.id_gender);

        final Button button = (Button) findViewById(R.id.id_submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StringEntity inputAddPerson = null;
                try {
                    inputAddPerson = new StringEntity("{\"names\":[{\"givenName\": \"John\",\"familyName\":\"Nash\"}],\"gender\":\"F\",\"age\":40}");
                } catch (UnsupportedEncodingException e) {
                    System.out.println("DAAAMN");
                    e.printStackTrace();
                }

                inputAddPerson.setContentType("application/json");
                try {
                    System.out.println("AddPerson = " + getRequestPost("person", inputAddPerson));
                } catch (Exception e) {
                    System.out.println("WTF?");
                    e.printStackTrace();
                }

            }
        });
    }

    public static Boolean getRequestPost(String URLPath, StringEntity input) throws Exception {
        String URL = URLBase + URLPath;
        Boolean response =  false;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httpPost = new HttpPost(URL);
            System.out.println(URL);
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            BasicScheme scheme = new BasicScheme();
            Header authorizationHeader = scheme.authenticate(credentials, httpPost);
            httpPost.setHeader(authorizationHeader);
            httpPost.setEntity(input);
            //System.out.println("Executing request: " + httpGet.getRequestLine());
            //System.out.println(response);
            //response = httpclient.execute(httpGet,responseHandler);
            HttpResponse responseRequest = httpclient.execute(httpPost);

            if (responseRequest.getStatusLine().getStatusCode() != 200 && responseRequest.getStatusLine().getStatusCode() != 201) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + responseRequest.getStatusLine().getStatusCode());
            }

            httpclient.getConnectionManager().shutdown();
            response = true;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return response;
    }

    public static String getRequestGet(String URLPath) throws Exception {
        String URL = URLBase + URLPath;
        String response =  "";
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpGet = new HttpGet(URL);

            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            BasicScheme scheme = new BasicScheme();
            Header authorizationHeader = scheme.authenticate(credentials, httpGet);
            httpGet.setHeader(authorizationHeader);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            //System.out.println("Executing request: " + httpGet.getRequestLine());
            //System.out.println(response);
            response = httpclient.execute(httpGet,responseHandler);


        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return response;
    }

}
