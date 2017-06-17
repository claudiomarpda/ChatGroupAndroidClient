package com.mz.chat;

/**
 * Created by mz on 15/06/17.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Receives message and update UI while connection is on.
 */
public class MessageReceiver extends AsyncTask<Void, String, String> {

    private Socket socket;
    private DataInputStream inputStream;
    private AsyncResponse asyncResponse; // for UI update

    public MessageReceiver(String ipAddress, int port, AsyncResponse asyncResponse) throws IOException {
        socket = new Socket(ipAddress, port);
        inputStream = new DataInputStream(socket.getInputStream());
        this.asyncResponse = asyncResponse;
        if (socket.isConnected()) {
            Log.d("TAG", "connected " + getClass().getSimpleName());
        }
    }

    @Override
    protected String doInBackground(Void... values) {
        String message = "";
        while (socket != null && inputStream != null) {
            try {
                message = inputStream.readUTF();
                publishProgress(message);
            } catch (IOException e) {
                disconnect();
                e.printStackTrace();
            }
        }
        return message;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        asyncResponse.messageReceived(values[0]);
    }

    /**
     * Closes Socket and Stream connections.
     */
    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket.isConnected();
    }
}
