package com.example.maturitaapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    // Use your ESP32-CAM stream URL
    private final String streamUrl = "http://192.168.184.72:81/stream"; // Replace with your ESP32-CAM IP address

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display by disabling the system windows fitting
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_main);

        // Handle system bar insets for padding (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the VideoView in the layout
        videoView = findViewById(R.id.videoView);

        // Set the video URI to the ESP32-CAM stream URL
        videoView.setVideoURI(Uri.parse(streamUrl));

        // Set error listeners
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // Handle specific error cases
                if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                    Toast.makeText(MainActivity.this, "Stream not found or cannot be accessed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "An unknown error occurred", Toast.LENGTH_LONG).show();
                }
                return true; // Indicates that the error was handled
            }
        });

        // Start the video stream
        videoView.setOnPreparedListener(mediaPlayer -> {
            // Check if video is ready to play
            Toast.makeText(MainActivity.this, "Streaming started", Toast.LENGTH_SHORT).show();
            videoView.start();
        });

        videoView.setOnCompletionListener(mediaPlayer -> {
            // Handle completion
            Toast.makeText(MainActivity.this, "Stream has ended", Toast.LENGTH_SHORT).show();
        });

        // Start loading the video stream
        videoView.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release resources
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}
