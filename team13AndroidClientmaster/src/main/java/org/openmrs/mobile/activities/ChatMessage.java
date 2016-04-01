package org.openmrs.mobile.activities;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Diana on 03/02/2016.
 */

public class ChatMessage extends AppCompatActivity {
    public boolean right;
    public String message;

    public ChatMessage(boolean left, String message) {
        super();
        this.right = left;
        this.message = message;
    }
}