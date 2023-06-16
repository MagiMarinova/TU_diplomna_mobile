package com.example.thesis_1;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.longdo.mjpegviewer.MjpegView;

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
        final MjpegView videoView = findViewById(R.id.videoView);
        System.out.println("ADRESS:" + address);
        videoView.setUrl("http://" + address + ":8081");
        videoView.setMode(MjpegView.MODE_FIT_WIDTH);
        videoView.setAdjustHeight(true);
        videoView.setSupportPinchZoomAndPan(true);
        new Thread(new Runnable() {
            @Override
            public void run() {

                videoView.startStream();
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
