package com.example.hboxtv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.hboxtv.R;

import hb.xvideoplayer.MxVideoPlayer;
import hb.xvideoplayer.MxVideoPlayerWidget;

public class MxPlayerActivity extends AppCompatActivity {
//    String videoURL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
    private String STREAM_URL;
    private String movieName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mx_player);
        // for full screen activity
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        Intent intent = getIntent();
        if (intent != null) {
            STREAM_URL = intent.getStringExtra("stream_url");
            movieName = intent.getStringExtra("movie_name");
//            initializePlayer();
        }

        MxVideoPlayerWidget videoPlayerWidget = findViewById(R.id.mpw_video_player);
        videoPlayerWidget.autoStartPlay(STREAM_URL, MxVideoPlayer.SCREEN_LAYOUT_NORMAL, movieName);
//        MxVideoPlayerWidget.startFullscreen(MxPlayerActivity.this, MxVideoPlayerWidget.class, videoURL, "BigBuckBunny");

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MxVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (MxVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
}