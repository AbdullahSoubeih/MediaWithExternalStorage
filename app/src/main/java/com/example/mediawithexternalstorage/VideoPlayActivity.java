package com.example.mediawithexternalstorage;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        videoView = findViewById(R.id.videoView);

        Uri videoUri = Uri.parse(getIntent().getExtras().getString("videoUri_KEY"));   //using parse to convert from String to Uri

        videoView.setVideoURI(videoUri);
        videoView.start();

    }
}
