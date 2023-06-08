package com.example.thesis_1;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class VideoStream extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_stream);

        findViewById(R.id.exitButton).setOnClickListener(v -> {
            // Go back to the main activity
            startActivity(new Intent(VideoStream.this , MainActivity.class));
        });
        //Get the address value from the Intent
        String address = getIntent().getStringExtra("ADDRESS");
        
    }
}
