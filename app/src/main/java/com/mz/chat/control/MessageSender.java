package com.mz.chat.control;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;

/**
 * Created by mz on 15/06/17.
 * <p>
 * Sends message to the server.
 */
class MessageSender extends AsyncTask<Void, Void, Void> implements Connection {

    private Socket socket;
    private DataOutputStream outputStream;
    private Semaphore semaphore;
    private String message;

    MessageSender(String ipAddress, int port, Semaphore semaphore) throws IOException {
        socket = new Socket(ipAddress, port);
        outputStream = new DataOutputStream(socket.getOutputStream());
        this.semaphore = semaphore;
        message = "";
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (true) {
            try {
                // waits until user selects send button
                semaphore.acquire();
                // sends the message
                if (outputStream != null) {
                    outputStream.writeUTF(message);
                }
            } catch (IOException | InterruptedException e) {
                disconnect();
                e.printStackTrace();
            }
        }
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
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

    void setMessage(String message) {
        this.message = message;
    }
}
