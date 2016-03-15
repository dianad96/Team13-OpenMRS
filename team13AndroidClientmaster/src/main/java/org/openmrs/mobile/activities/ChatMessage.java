package org.openmrs.mobile.activities;

/**
 * Created by Diana on 03/02/2016.
 */

public class ChatMessage {
    public boolean right;
    public String message;

    public ChatMessage(boolean left, String message) {
        super();
        this.right = left;
        this.message = message;
    }
}