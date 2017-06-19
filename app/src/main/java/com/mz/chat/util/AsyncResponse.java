package com.mz.chat.util;

/**
 * Created by mz on 13/06/17.
 * <p>
 * Allows communication with the implementing class when dealing with threads.
 */
public interface AsyncResponse {

    /**
     * Updates UI with received message from server
     */
    void setMessageReceived(String message);

    /**
     * Shows a message with the result of a connection attempting
     */
    void showConnectionResult(String result);

    /**
     * Alerts the user with a message when main external server is closed,
     * which finishes all clients connections.
     */
    void alertServerDown(String message);
}
