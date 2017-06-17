package com.mz.chat;

/**
 * Created by mz on 15/06/17.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;

/**
 * Sends a message to the server.
 */
class MessageSender extends AsyncTask<Void, Void, Void> {

    private Socket socket;
    private DataOutputStream outputStream;
    private Semaphore semaphore;
    private String message;

    public MessageSender(String ipAddress, int port, Semaphore semaphore) throws IOException {
        socket = new Socket(ipAddress, port);
        outputStream = new DataOutputStream(socket.getOutputStream());
        this.semaphore = semaphore;
        message = "";
        if (socket.isConnected()) {
            Log.d("TAG", "connected " + getClass().getSimpleName());
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (true) {
            try {
                Log.d("TAG", "before acquire" + getClass());
                semaphore.acquire();
                if (outputStream != null) {
                    outputStream.writeUTF(message);
                }
            } catch (IOException | InterruptedException e) {
                disconnect();
                e.printStackTrace();
            }
        }
        // return null;
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
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isConnected() {
        return socket.isConnected();
    }
}
