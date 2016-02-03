package org.openmrs.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import org.openmrs.mobile.R;

public class Graph extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Button input = (Button) findViewById(R.id.graph_input);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Graph.this, DashboardActivity.class);
                startActivity(i);
            }
        });

        Button graph = (Button) findViewById(R.id.graph_graph);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Graph.this, Graph.class);
                startActivity(i);
            }
        });

        Button chat = (Button) findViewById(R.id.graph_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Graph.this, Chat.class);
                startActivity(i);
            }
        });
    }

}
