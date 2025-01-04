package com.example.maturitaapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private WebSocketClient mWebSocketClient;
    private String espIpAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Show IP input dialog
        promptForIpAddress();

        // Set up buttons
        Button btnUp = findViewById(R.id.btnUp);
        Button btnDown = findViewById(R.id.btnDown);
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);

        btnUp.setOnClickListener(v -> sendCommandToESP("forward"));
        btnDown.setOnClickListener(v -> sendCommandToESP("backward"));
        btnLeft.setOnClickListener(v -> sendCommandToESP("left"));
        btnRight.setOnClickListener(v -> sendCommandToESP("right"));
    }

    private void promptForIpAddress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter IP Address");

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.ip_input, null);
        builder.setView(dialogView);

        EditText ipInput = dialogView.findViewById(R.id.editTextIpAddress);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String ipAddress = ipInput.getText().toString().trim();
            if (!ipAddress.isEmpty()) {
                espIpAddress = ipAddress;
                String wsUri = "http://" + espIpAddress + ":81/stream";
                connectToWebSocket(wsUri);
            } else {
                dialog.dismiss();
                promptForIpAddress();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> finish());
        builder.setCancelable(false);
        builder.show();
    }

    private void connectToWebSocket(String wsUri) {
        URI uri = URI.create(wsUri);
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakeData) {
                Toast.makeText(MainActivity.this, "Connected to ESP32", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessage(String message) {
                // Handle incoming message if needed
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Toast.makeText(MainActivity.this, "Disconnected from ESP32", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };
        mWebSocketClient.connect();
    }

    private void sendCommandToESP(String command) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            mWebSocketClient.send(command);
            Toast.makeText(this, "Sent: " + command, Toast.LENGTH_SHORT).show();
        }
    }
}
