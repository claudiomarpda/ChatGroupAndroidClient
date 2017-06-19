package com.mz.chat.control;


import android.os.AsyncTask;

import com.mz.chat.util.AsyncResponse;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by mz on 15/06/17.
 * <p>
 * Receives message from the server and update UI while connection is on.
 */
class MessageReceiver extends AsyncTask<Void, String, String> implements Connection {

    private static final String SERVER_DOWN = "Server DOWN";

    private Socket socket;
    private DataInputStream inputStream;
    private AsyncResponse asyncResponse; // for UI update

    MessageReceiver(String ipAddress, int port, AsyncResponse asyncResponse) throws IOException {
        socket = new Socket(ipAddress, port);
        inputStream = new DataInputStream(socket.getInputStream());
        this.asyncResponse = asyncResponse;
    }

    @Override
    protected String doInBackground(Void... values) {
        String message = "";
        while (socket != null && inputStream != null) {
            try {
                // waits for message from server (sent by others clients)
                message = inputStream.readUTF();
                // runs the UI thread
                publishProgress(message);
            } catch(EOFException e) {
                publishProgress(SERVER_DOWN);
                disconnect();
            } catch (IOException e) {
                disconnect();
                e.printStackTrace();
            }
        }
        return message;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if(values[0].equals(SERVER_DOWN)) {
            // tells the user the connection has finished from the server
            onPostExecute(SERVER_DOWN);
        }
        else {
            // updates the received message on UI
            asyncResponse.setMessageReceived(values[0]);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        // updates UI after server down
        asyncResponse.alertServerDown(s);
    }

    /**
     * Closes Socket and Stream connections.
     */
    @Override
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

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

}
