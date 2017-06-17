package com.mz.chat;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity implements AsyncResponse {

    private Client client;
    private TextView messagesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messagesTextView = (TextView) findViewById(R.id.text_view);
        final EditText editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        final Button connectButton = (Button) findViewById(R.id.connectButton);
        final Button disconnectButton = (Button) findViewById(R.id.disconnectButton);
        final EditText editTextIp = (EditText) findViewById(R.id.editTextIp);
        final EditText editTextPort = (EditText) findViewById(R.id.editTextPort);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        editTextIp.setText("192.168.1.14");
        editTextPort.setText("4444");

        disconnectButton.setEnabled(false);
        disconnectButton.setVisibility(Button.INVISIBLE);

        final AsyncResponse activityResponse = this;
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = editTextIp.getText().toString();
                String port = editTextPort.getText().toString();
                Semaphore connectionSemaphore = new Semaphore(0);
//                client.connect(ip, port, activityResponse);
                try {
                    client = new Client(ip, port, activityResponse, connectionSemaphore);
                    connectionSemaphore.acquire();
                    if (client.isConnected()) {
                        connectButton.setVisibility(Button.INVISIBLE);
                        editTextIp.setVisibility(EditText.INVISIBLE);
                        editTextPort.setVisibility(EditText.INVISIBLE);
                        Toast.makeText(getBaseContext(), "Connection successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Connection fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
//                disconnectButton.setEnabled(true);
            }
        });
/*

        disconnectButton.setEnabled(false);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.disconnect();
                client = null;
                disconnectButton.setEnabled(false);
                connectButton.setEnabled(true);
                fab.setEnabled(false);
            }
        });
*/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editTextMessage.getText().toString();
                if (client != null && client.isConnected()) {
                    client.sendMessage(message);
//                    messagesTextView.setText(messagesTextView.getText().toString() + "\n" + message);
                } else {
                    Snackbar.make(view, "Not connected", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                editTextMessage.setText("");
            }
        });
    }

    @Override
    public void messageReceived(String message) {
        messagesTextView.setText(messagesTextView.getText() + "\n" + message);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (client != null) {
            client.disconnect();
        }
    }
}
