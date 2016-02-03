package org.openmrs.mobile.activities;

/**
 * Created by Diana on 03/02/2016.
 */

public class ChatMessage {
    public boolean left;
    public String message;

    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }
}