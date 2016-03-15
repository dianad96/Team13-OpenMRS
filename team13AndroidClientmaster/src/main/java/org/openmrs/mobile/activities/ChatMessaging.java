package org.openmrs.mobile.activities;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.ApiAuthRest;



import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatMessaging extends Activity {

    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ChatArrayAdapter2 chatArrayAdapter2;
    private ListView listView;
    private ListView listView2;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;
    String comment = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_messaging);

        Button input = (Button) findViewById(R.id.chat_input);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatMessaging.this, DashboardActivity.class);
                startActivity(i);
            }
        });

        Button graph = (Button) findViewById(R.id.chat_graph);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatMessaging.this, Graph.class);
                startActivity(i);
            }
        });

        Button chat = (Button) findViewById(R.id.chat_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatMessaging.this, Chat.class);
                startActivity(i);
            }
        });

        buttonSend = (Button) findViewById(R.id.send);

        listView = (ListView) findViewById(R.id.msgview);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        listView2 = (ListView) findViewById(R.id.msgview2);
        chatArrayAdapter2 = new ChatArrayAdapter2(getApplicationContext(), R.layout.left);
        listView2.setAdapter(chatArrayAdapter2);

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
                sendData();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        listView2.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView2.setAdapter(chatArrayAdapter2);

        //to scroll the list view to bottom on data change
        chatArrayAdapter2.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView2.setSelection(chatArrayAdapter2.getCount() - 1);
            }
        });

        try {
            getData();
            getData2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean sendChatMessage() {
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        comment=chatText.getText().toString();
        chatText.setText("");
        side = !side;
        return true;
    }

    private boolean getChatMessage(String s) {
        chatArrayAdapter.add(new ChatMessage(side, s));
        //comment=chatText.getText().toString();
        chatText.setText("");
        side = !side;
        return true;
    }

    private boolean getChatMessage2(String s) {
        chatArrayAdapter2.add(new ChatMessage(side, s));
        //comment=chatText.getText().toString();
        side = !side;
        return true;
    }

    public void sendData()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ApiAuthRest.setURLBase(Container.URLBase);
        ApiAuthRest.setUsername(Container.username);
        ApiAuthRest.setPassword(Container.password);

        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        //String date = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" +c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(today);
        Log.i("myTag",date + " vs " + today.toString());

        final String JSONComment= "{\"obsDatetime\": \"" + date + "\"" +
                ", \"concept\": \"" + Container.chat_uuid + "\"" +
                ", \"value\": \"" + comment + "\"" +
                ", \"person\": \"" + Container.user_uuid + "\"}";

        StringEntity inputComment = null;
        try {
            inputComment = new StringEntity(JSONComment);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        inputComment.setContentType("application/json");
        try {
            Log.i("OpenMRS response", "Comment Added = " + ApiAuthRest.getRequestPost("obs", inputComment));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // get patient's messages
    void getData() throws Exception {
    /*
	 * SET VALUE FOR CONNECT TO OPENMRS
	 */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ApiAuthRest.setURLBase("http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/");
        ApiAuthRest.setUsername("diana");
        ApiAuthRest.setPassword("Admin123");


 	/*
 	 * Example how parse json return session
 	 */

        String request = "obs?patient=" + Container.user_uuid + "&concept=" + Container.chat_uuid;
        System.out.println("########################");
        System.out.println("Search the persons that have name  JOHN");
        Object obj = ApiAuthRest.getRequestGet(request);
        JSONObject jsonObject = new JSONObject ((String) obj);
        JSONArray arrayResult = (JSONArray) jsonObject.get("results");

        System.out.println("########################");
        int itemArray = arrayResult.length();
        int iterator;
        ArrayList<String> array = new ArrayList<String>();
        for (iterator = itemArray-1; iterator >= 0; iterator--) {
            JSONObject data = (JSONObject) arrayResult.get(iterator);
            String uuid = (String) data.get("uuid");
            String display = (String) data.get("display");
            System.out.println("Rows " + iterator + " => Result OBS UUID:" + uuid + " Display:" + display.substring(7));

            //Only display the first 15 messages
            if(array.size()<15) {
                array.add(display.substring(7));
                getChatMessage(display.substring(7));
            }


            /*/
            //Show ROWS LINKS
            JSONArray arrayResultLinks = (JSONArray) data.get("links");
            int largoArrayLinks = arrayResultLinks.length();
            int contadorLinks;
            for (contadorLinks = 0; contadorLinks < largoArrayLinks; contadorLinks++) {
                JSONObject registroLink = (JSONObject) arrayResultLinks.get(contadorLinks);
                String uri = (String) registroLink.get("uri");
                String rel = (String) registroLink.get("rel");
                System.out.println("==>Record Row " + iterator + "." + contadorLinks
                        + " =>  URI:" + uri + " REL:" + rel);

            }
            /*/
            System.out.println("########################");
        }
    }


    // get doctor's messages
    void getData2() throws Exception {
    /*
	 * SET VALUE FOR CONNECT TO OPENMRS
	 */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ApiAuthRest.setURLBase("http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/");
        ApiAuthRest.setUsername("diana");
        ApiAuthRest.setPassword("Admin123");


 	/*
 	 * Example how parse json return session
 	 */

        String request = "obs?patient=" + Container.doctor_uuid + "&concept=" + Container.chat_uuid;
        System.out.println("########################");
        System.out.println("Search the persons that have name  JOHN");
        Object obj = ApiAuthRest.getRequestGet(request);
        JSONObject jsonObject = new JSONObject ((String) obj);
        JSONArray arrayResult = (JSONArray) jsonObject.get("results");

        System.out.println("########################");
        int itemArray = arrayResult.length();
        int iterator;
        ArrayList<String> array = new ArrayList<String>();
        for (iterator = itemArray-1; iterator >= 0; iterator--) {
            JSONObject data = (JSONObject) arrayResult.get(iterator);
            String uuid = (String) data.get("uuid");
            String display = (String) data.get("display");
            System.out.println("Rows " + iterator + " => Result OBS UUID:" + uuid + " Display:" + display.substring(7));

            //Only display the first 15 messages
            if(array.size()<15) {
                array.add(display.substring(7));
                getChatMessage2(display.substring(7));
            }


            /*/
            //Show ROWS LINKS
            JSONArray arrayResultLinks = (JSONArray) data.get("links");
            int largoArrayLinks = arrayResultLinks.length();
            int contadorLinks;
            for (contadorLinks = 0; contadorLinks < largoArrayLinks; contadorLinks++) {
                JSONObject registroLink = (JSONObject) arrayResultLinks.get(contadorLinks);
                String uri = (String) registroLink.get("uri");
                String rel = (String) registroLink.get("rel");
                System.out.println("==>Record Row " + iterator + "." + contadorLinks
                        + " =>  URI:" + uri + " REL:" + rel);

            }
            /*/
            System.out.println("########################");
        }
    }


}

