package org.openmrs.mobile.activities.fragments;




        import android.util.Log;

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
        import org.openmrs.mobile.activities.Container;

/**
 *
 * @author Victor Aravena victor.aravena@ciochile.cl
 *
 */

public class ApiAuthRest {
    static String username = null;
    static String password = null;
    static String URLBase = null;
    /**
     * HTTP POST
     * @param URLPath
     * @param input
     * @return
     * @throws Exception
     */
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
//            response = httpclient.execute(httpGet,responseHandler);
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
    /**
     * HTTP GET
     * @param URLPath
     * @return
     * @throws Exception
     */
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


    // Create a new person
    public static Boolean getResponsePost (String URLPath, StringEntity input) throws Exception {
        String URL = URLBase + URLPath;
        Boolean response = false;
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
//            response = httpclient.execute(httpGet,responseHandler);
            HttpResponse responseRequest = httpclient.execute(httpPost);

            //Getting request response
            String s = org.apache.http.util.EntityUtils.toString(responseRequest.getEntity());
            Log.i("OpenMRS response", s);

            int curly = s.indexOf("\"");
            int curly2 = s.indexOf("\"", curly+1);
            int curly3 = s.indexOf("\"", curly2+1);
            int curly4 = s.indexOf("\"", curly3+1);

            s = s.substring(curly3+1,curly4);
            if (responseRequest.getStatusLine().getStatusCode() != 200 && responseRequest.getStatusLine().getStatusCode() != 201) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + responseRequest.getStatusLine().getStatusCode());
            }


            httpclient.getConnectionManager().shutdown();
            response = true;
            Log.i("OpenMRS response", s);
            Container.person_uuid = s;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return response;
    }


    //Create new patient
    public static Boolean getResponsePost2 (String URLPath, StringEntity input) throws Exception {
        String URL = URLBase + URLPath;
        Boolean response = false;
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
//            response = httpclient.execute(httpGet,responseHandler);
            HttpResponse responseRequest = httpclient.execute(httpPost);

            //Getting request response
            String s = org.apache.http.util.EntityUtils.toString(responseRequest.getEntity());
            Log.i("OpenMRS response", s);

            int curly = s.indexOf("\"");
            int curly2 = s.indexOf("\"", curly+1);
            int curly3 = s.indexOf("\"", curly2+1);
            int curly4 = s.indexOf("\"", curly3+1);

            s = s.substring(curly3+1,curly4);
            if (responseRequest.getStatusLine().getStatusCode() != 200 && responseRequest.getStatusLine().getStatusCode() != 201) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + responseRequest.getStatusLine().getStatusCode());
            }


            httpclient.getConnectionManager().shutdown();
            response = true;
            Log.i("OpenMRS response", s);
            Container.patient_uuid = s;
            Container.user_uuid = s;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return response;
    }

    public static void setUsername(String username) {
        ApiAuthRest.username = username;
    }


    public static void setPassword(String password) {
        ApiAuthRest.password = password;
    }


    public static void setURLBase(String uRLBase) {
        URLBase = uRLBase;
    }

}
