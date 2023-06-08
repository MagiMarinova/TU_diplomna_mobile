package com.example.thesis_1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

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
        loadFeed(address);
    }

    private void loadFeed(final String address){
        final VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse("http://" + address + ":8081"));
        new Thread(new Runnable() {
            @Override
            public void run() {

                videoView.start();
            }
        }).start();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        // Go back to the main activity
        startActivity(new Intent(this , MainActivity.class));
    }

}
