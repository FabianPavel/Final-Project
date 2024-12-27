package com.example.maturitaapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

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

        // Load the ESP32-CAM video stream URL
        webView.loadUrl("http://192.168.80.72:81/stream");

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

    // Function to send commands to ESP32
    private void sendCommandToESP(String command) {
        new Thread(() -> {
            try {
                // Replace with your ESP32's endpoint for motor control
                String espUrl = "http://192.168.80.72/control?command=" + command;
                URL url = new URL(espUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);

                int responseCode = connection.getResponseCode();
                /*if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Command sent successfully
                } else {
                    // Handle error
                }*/

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
