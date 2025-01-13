package com.example.maturitaapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

        // Setup Settings button
        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> openSettingsScreen());
    }

    private void showIpInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter ESP32 IP Address");

        // Input field for IP address
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            ipAddress = input.getText().toString().trim();
            if (validateIpAddress(ipAddress)) {
                setupUi(ipAddress);
            } else {
                Toast.makeText(MainActivity.this, "Invalid IP Address", Toast.LENGTH_SHORT).show();
                showIpInputDialog(); // Retry on invalid input
            }
        });

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
        try {
            // Initialize WebView for video streaming
            webView = findViewById(R.id.webView);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            // Load MJPEG stream from ESP32
            webView.loadUrl("http://" + ipAddress);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load video stream: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Initialize WebSocket for control messages
        connectToWebSocket(ipAddress);

        // Setup control buttons
        setupControlButtons();
    }

    private void connectToWebSocket(String ipAddress) {
        try {
            String wsUri = "ws://" + ipAddress + ":81/";
            URI uri = URI.create(wsUri);

            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "WebSocket Connected", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onMessage(String message) {
                    runOnUiThread(() -> {
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
        } catch (Exception e) {
            Toast.makeText(this, "Failed to connect WebSocket: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupControlButtons() {
        Button btnUp = findViewById(R.id.btnUp);
        Button btnDown = findViewById(R.id.btnDown);
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);
        Button btnLed = findViewById(R.id.btnLed);

        btnLed.setOnClickListener(v -> sendCommand("led"));

        btnUp.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sendCommand("forward");
                    break;
                case MotionEvent.ACTION_UP:
                    sendCommand("stop");
                    break;
            }
            v.performClick();
            return true;
        });

        btnDown.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sendCommand("backward");
                    break;
                case MotionEvent.ACTION_UP:
                    sendCommand("stop");
                    break;
            }
            v.performClick();
            return true;
        });

        btnLeft.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sendCommand("left");
                    break;
                case MotionEvent.ACTION_UP:
                    sendCommand("stop");
                    break;
            }
            v.performClick();
            return true;
        });

        btnRight.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sendCommand("right");
                    break;
                case MotionEvent.ACTION_UP:
                    sendCommand("stop");
                    break;
            }
            v.performClick();
            return true;
        });
    }

    private void sendCommand(String command) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            mWebSocketClient.send(command);
        } else {
            Toast.makeText(this, "WebSocket not connected. Command failed: " + command, Toast.LENGTH_SHORT).show();
        }
    }

    private void openSettingsScreen() {
        setContentView(R.layout.settings_layout);

        TextView tvCurrentIp = findViewById(R.id.tvCurrentIp);
        EditText etNewIp = findViewById(R.id.etNewIp);
        Button btnSaveIp = findViewById(R.id.btnSaveIp);
        Button btnBack = findViewById(R.id.btnBack);

        // Show current IP address
        tvCurrentIp.setText("Current IP: " + ipAddress);

        btnSaveIp.setOnClickListener(v -> {
            String newIp = etNewIp.getText().toString().trim();
            if (validateIpAddress(newIp)) {
                ipAddress = newIp;
                setupUi(ipAddress); // Reload UI with new IP
                Toast.makeText(this, "IP Address Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Invalid IP Address", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            setContentView(R.layout.activity_main); // Return to main screen
            setupUi(ipAddress); // Ensure UI is set up properly
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            mWebSocketClient.close();
        }
    }
}
