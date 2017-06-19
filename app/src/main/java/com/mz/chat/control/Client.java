package com.mz.chat.control;

import android.os.AsyncTask;

import com.mz.chat.util.AsyncResponse;

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
public class Client implements Connection {

    private AsyncResponse asyncResponse;
    private Semaphore sendSemaphore;
    private Semaphore connectionSemaphore;
    private ChatConnection chatConnection;
    private static Client singleton;

    private Client(String ipAddress, String port, AsyncResponse asyncResponse, Semaphore connectionSemaphore) throws IOException {
        // semaphore for message sending synchronizing
        sendSemaphore = new Semaphore(0);
        // semaphore used by main thread to check if chatConnection is ok
        this.connectionSemaphore = connectionSemaphore;
        // connects to the external main server system for sending and receiving
        chatConnection = new ChatConnection();
        chatConnection.execute(ipAddress, port);
        // reference for UI updating from the caller class
        this.asyncResponse = asyncResponse;
    }

    public static Client connect(String ipAddress, String port, AsyncResponse asyncResponse, Semaphore connectionSemaphore) throws IOException {
        if (singleton == null) {
            singleton = new Client(ipAddress, port, asyncResponse, connectionSemaphore);
        }
        return singleton;
    }

    @Override
    public boolean isConnected() {
        return chatConnection.isConnected();
    }

    @Override
    public void disconnect() {
        chatConnection.disconnect();
        singleton = null;
    }

    /**
     * Sends message to the server trough ChatConnection and MessageSender
     */
    public void sendMessage(String message) {
        chatConnection.sendMessage(message);
    }

    /**
     * ChatConnection thread initiates the MessageSender, MessageReceiver and its connections and streams.
     */
    private class ChatConnection extends AsyncTask<String, Void, String> implements Connection {

        private static final String SUCCESS = "Connection successful";
        private static final String FAIL = "Connection fail";

        private MessageSender messageSender;
        private MessageReceiver messageReceiver;
        private String connectionResult;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String ipAddress = strings[0];
                int port = Integer.valueOf(strings[1]);

                // will connect to the external main server
                messageSender = new MessageSender(ipAddress, port, sendSemaphore);
                messageReceiver = new MessageReceiver(ipAddress, port + 1, asyncResponse);

                // run the output and input threads
                this.publishProgress();
                connectionResult = SUCCESS;
            } catch (IOException e) {
                e.printStackTrace();
                singleton = null;
                connectionResult = FAIL;
            } finally {
                // releases the UI thread whether chatConnection was successful or not
                connectionSemaphore.release();
            }
            return null;
        }

        /**
         * This method initiates the output and input thread in MessageSender and MessageReceiver
         * Executor demands that threads are executed from UI thread.
         */
        @Override
        protected void onProgressUpdate(Void... values) {
            // Executor is necessary to run more than one thread
            Executor executor = Executors.newFixedThreadPool(2);
            // runs sender thread
            messageSender.executeOnExecutor(executor);
            // runs receiver thread
            messageReceiver.executeOnExecutor(executor);
        }

        @Override
        protected void onPostExecute(String string) {
            asyncResponse.showConnectionResult(connectionResult);
        }

        /**
         * Closes chatConnection of sender and receiver connections.
         */
        @Override
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

        @Override
        public boolean isConnected() {
            if (messageSender != null && messageReceiver != null) {
                return messageSender.isConnected() && messageReceiver.isConnected();
            }
            return false;
        }

        void sendMessage(String message) {
            messageSender.setMessage(message);
            sendSemaphore.release();
        }
    }
}
