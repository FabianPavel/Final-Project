package com.example.maturitaapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private String espIpAddress; // Removed default IP address

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // Ensures links open within the WebView
        webView.setWebViewClient(new WebViewClient());

        // Show the IP prompt dialog
        promptForIpAddress();

        // Bind buttons to control motors
        Button btnUp = findViewById(R.id.btnUp);
        Button btnDown = findViewById(R.id.btnDown);
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);

        // Set listeners for button clicks
        btnUp.setOnClickListener(v -> sendCommandToESP("forward"));
        btnDown.setOnClickListener(v -> sendCommandToESP("backward"));
        btnLeft.setOnClickListener(v -> sendCommandToESP("left"));
        btnRight.setOnClickListener(v -> sendCommandToESP("right"));
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // Function to show the dialog for IP address input
    private void promptForIpAddress() {
        // Create a dialog for IP input
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter IP Address");

        // Inflate the custom layout with an EditText
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.ip_input, null);
        builder.setView(dialogView);

        // Bind the EditText for input
        EditText ipInput = dialogView.findViewById(R.id.editTextIpAddress);

        // Configure buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String ipAddress = ipInput.getText().toString().trim();
            if (!ipAddress.isEmpty()) {
                espIpAddress = ipAddress;  // No default IP, use the user input
                String videoUrl = "http://" + espIpAddress + ":81/stream";
                webView.loadUrl(videoUrl);
                Toast.makeText(this, "Connecting to " + videoUrl, Toast.LENGTH_SHORT).show();
            } else {
                // Show the dialog again if no IP is entered
                dialog.dismiss();
                promptForIpAddress();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            finish(); // Exit the app if the user cancels
        });

        // Make the dialog non-cancelable to ensure the user inputs an IP
        builder.setCancelable(false);
        builder.show();
    }

    // Function to send commands to ESP32
    private void sendCommandToESP(String command) {
        new Thread(() -> {
            try {
                // Build the ESP32 URL with the user-provided IP
                String espUrl = "http://" + espIpAddress + "/control?command=" + command;
                URL url = new URL(espUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);

                // Optional: Read the response
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Command sent successfully: " + command);
                } else {
                    System.out.println("Failed to send command: " + command);
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
