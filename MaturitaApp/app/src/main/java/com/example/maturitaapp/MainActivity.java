package com.example.maturitaapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private WebSocketClient mWebSocketClient;
    private String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Prompt user for IP address
        showIpInputDialog();
    }

    private void showIpInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter ESP32 IP Address");

        // Input field for IP address
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // "OK" button with lambda expression
        builder.setPositiveButton("OK", (dialog, which) -> {
            ipAddress = input.getText().toString().trim();
            if (validateIpAddress(ipAddress)) {
                setupUi(ipAddress);
            } else {
                Toast.makeText(MainActivity.this, "Invalid IP Address", Toast.LENGTH_SHORT).show();
                showIpInputDialog(); // Retry on invalid input
            }
        });

        // "Cancel" button with lambda expression
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            finish(); // Close the app if the user cancels
        });

        builder.show();
    }

    private boolean validateIpAddress(String ip) {
        String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(ipPattern);
    }

    private void setupUi(String ipAddress) {
        // Initialize WebView for video streaming
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Load MJPEG stream from ESP32
        webView.loadUrl("http://" + ipAddress);

        // Initialize WebSocket for control messages
        connectToWebSocket(ipAddress);

        // Setup control buttons
        setupControlButtons();
    }

    private void connectToWebSocket(String ipAddress) {
        // WebSocket URI with IP Address
        String wsUri = "ws://" + ipAddress + ":81/"; // WebSocket address of ESP32
        URI uri = URI.create(wsUri);

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "WebSocket Connected", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onMessage(String message) {
                // Handle incoming messages from WebSocket
                runOnUiThread(() -> {
                    // If required, handle commands or feedback from the ESP32 here
                    Toast.makeText(MainActivity.this, "Message from ESP32: " + message, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "WebSocket Disconnected", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(Exception ex) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "WebSocket Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show());
            }
        };

        mWebSocketClient.connect();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupControlButtons() {
        // Control buttons initialization
        Button btnUp = findViewById(R.id.btnUp);
        Button btnDown = findViewById(R.id.btnDown);
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);

        // Define control actions for each button
        btnUp.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sendCommand("forward"); // Command when button is pressed down
                    break;
                case MotionEvent.ACTION_UP:
                    sendCommand("stop"); // Stop when button is released
                    break;
            }
            v.performClick(); // Ensure performClick() is called
            return true;
        });

        btnDown.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sendCommand("backward"); // Command when button is pressed down
                    break;
                case MotionEvent.ACTION_UP:
                    sendCommand("stop"); // Stop when button is released
                    break;
            }
            v.performClick(); // Ensure performClick() is called
            return true;
        });

        btnLeft.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sendCommand("left"); // Command when button is pressed down
                    break;
                case MotionEvent.ACTION_UP:
                    sendCommand("stop"); // Stop when button is released
                    break;
            }
            v.performClick(); // Ensure performClick() is called
            return true;
        });

        btnRight.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sendCommand("right"); // Command when button is pressed down
                    break;
                case MotionEvent.ACTION_UP:
                    sendCommand("stop"); // Stop when button is released
                    break;
            }
            v.performClick(); // Ensure performClick() is called
            return true;
        });
    }

    private void sendCommand(String command) {
        // Send WebSocket control command to ESP32
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            mWebSocketClient.send(command);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            mWebSocketClient.close();
        }
    }
}
