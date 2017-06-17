package com.mz.chat;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by mz on 12/06/17.
 * <p>
 * This is a client for a online message system.
 * It uses two connections (sockets), one for sending and other for receiving.
 * The main external server waits for both connections.
 * Then we have two streams, one for data input and other for data output.
 */
public class Client {

    private AsyncResponse asyncResponse;
    private Semaphore sendSemaphore;
    private Semaphore connectionSemaphore;
    private MessageSender messageSender;
    private MessageReceiver messageReceiver;

    // Executor is necessary to run more than one thread
    private Executor executor = Executors.newFixedThreadPool(2);

    public Client(String ipAddress, String port, AsyncResponse asyncResponse, Semaphore connectionSemaphore) throws IOException {
        // semaphore for message sending synchronizing
        sendSemaphore = new Semaphore(0);
        // semaphore used by main thread to check if connection is ok
        this.connectionSemaphore = connectionSemaphore;
        // connects to the external main server system for sending and receiving
        new Connection().execute(ipAddress, port);
        // reference for UI updating from the caller class
        this.asyncResponse = asyncResponse;
    }

    /**
     * Executor demands that threads are executed from UI thread.
     * This method initiates the output and input thread in MessageSender and MessageReceiver
     */
    private void executeMessengers() {
        // initiates the output thread
        messageSender.executeOnExecutor(executor);
        // initiates the input thread
        messageReceiver.executeOnExecutor(executor);
    }

    /**
     * Closes connection of sender and receiver connections.
     */
    public void disconnect() {
        if (messageSender != null) {
            messageSender.cancel(true);
            messageSender.disconnect();
            messageSender = null;
        }
        if (messageReceiver != null) {
            messageReceiver.cancel(true);
            messageReceiver.disconnect();
            messageReceiver = null;
        }
    }

    public void sendMessage(String message) {
        messageSender.setMessage(message);
        sendSemaphore.release();
    }

    public boolean isConnected() {
        if (messageSender != null && messageReceiver != null) {
            return messageSender.isConnected() && messageReceiver.isConnected();
        }
        return false;
    }

    /**
     * Chat connection thread instantiates the Socket, DataOutputStream and DataInputStream.
     */
    private class Connection extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String ipAddress = strings[0];
                int port = Integer.valueOf(strings[1]);
                // will connect to the external main server
                messageSender = new MessageSender(ipAddress, port, sendSemaphore);
                messageReceiver = new MessageReceiver(ipAddress, port + 1, asyncResponse);

                // initiates the output and input threads
                executeMessengers();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // releases the UI thread whether connection was successful or not
                connectionSemaphore.release();
                connectionSemaphore = null;
                asyncResponse = null;
            }
            return null;
        }
    }


}
