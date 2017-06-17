package com.mz.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity implements AsyncResponse {

    private Client client;
    private TextView messagesTextView;
    private Button connectButton;
    private EditText editTextIp;
    private EditText editTextPort;
    private EditText editTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messagesTextView = (TextView) findViewById(R.id.text_view);
        connectButton = (Button) findViewById(R.id.connectButton);
        editTextIp = (EditText) findViewById(R.id.editTextIp);
        editTextPort = (EditText) findViewById(R.id.editTextPort);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        final Button disconnectButton = (Button) findViewById(R.id.disconnectButton);
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editTextMessage.getText().toString();
                if (client != null && client.isConnected()) {
                    client.sendMessage(message);
                } else {
                    Snackbar.make(view, "Not connected", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                editTextMessage.setText("");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            if(client != null) {
                client.disconnect();
                client = null;
                connectButton.setVisibility(Button.VISIBLE);
                editTextIp.setVisibility(EditText.VISIBLE);
                editTextPort.setVisibility(EditText.VISIBLE);

                // Check if no view has focus
                View view = this.getCurrentFocus();
                if (view != null) {
                    // hides the keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                Toast.makeText(getBaseContext(), "Connection closed", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getBaseContext(), "There is no connection", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
