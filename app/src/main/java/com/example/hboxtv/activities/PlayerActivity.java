package com.example.hboxtv.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.hboxtv.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

public class PlayerActivity extends AppCompatActivity {
    private static final String TAG = PlayerActivity.class.getSimpleName();
    private PlayerView playerView;
    private ExoPlayer player;
    private String server;
    private String userName;
    private String password;
    private String STREAM_URL;
    private ProgressBar progressBar;

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
            // for full screen activity
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        playerView = findViewById(R.id.video_view);
        progressBar = findViewById(R.id.progress_bar);

        // get value from shared prefs
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PlayerActivity.this);
        server = preferences.getString("server", "");
        userName = preferences.getString("user_name", "");
        password = preferences.getString("password", "");

        Intent intent = getIntent();
        if (intent != null) {
            String streamType = intent.getStringExtra("stream_type");
            String movieId = intent.getStringExtra("movie_id");
            String extension = intent.getStringExtra("extension");

            if (userName != null && password != null) {
                STREAM_URL = server + "/" + streamType + "/" + userName + "/" + password + "/" + movieId + "." + extension;
                Log.d(TAG, "OnChannelClick: url: " + STREAM_URL);
//                initializePlayer();
            }
        }
    }

    public void errorDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(PlayerActivity.this);
        adb.setTitle("Could not able to stream video");
        adb.setMessage("It seems that something is going wrong.\nPlease try again.");
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish(); // take out user from this activity. you can skip this
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            //to pause a video because now our video player is not in focus
            player.setPlayWhenReady(false);
        }

        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void initializePlayer() {
        try {
//            player = new SimpleExoPlayer.Builder(this).build();
//            playerView.setPlayer(player);
//            MediaItem mediaItem = MediaItem.fromUri(STREAM_URL);

            DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
            trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd());

            player = new SimpleExoPlayer.Builder(this)
                    .setTrackSelector(trackSelector)
                    .build();
            playerView.setPlayer(player);

            /*MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(STREAM_URL)
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .build();*/

            MediaItem mediaItem = MediaItem.fromUri(STREAM_URL);

            player.addMediaItem(mediaItem);
            player.seekTo(currentWindow, playbackPosition);
            player.setPlayWhenReady(playWhenReady);
            player.prepare();
        } catch (Exception e) {
            Log.d(TAG, "playVideo: exception: " + e.getMessage());
        }
    }

    protected void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
        }
        player = null;
    }
}