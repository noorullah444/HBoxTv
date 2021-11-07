package com.example.hboxtv.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hboxtv.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

public class PlayerActivity extends AppCompatActivity {
    private static final String TAG = PlayerActivity.class.getSimpleName();
    private PlayerView playerView;
    private ExoPlayer player;
    private String STREAM_URL;
    private ProgressBar progressBar;

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        // for full screen activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        playerView = findViewById(R.id.video_view);
        progressBar = findViewById(R.id.progress_bar);

        Intent intent = getIntent();
        if (intent != null) {
            STREAM_URL = intent.getStringExtra("stream_url");
            initializePlayer();
        }
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
        /*if (player != null) {
            //to pause a video because now our video player is not in focus
            player.setPlayWhenReady(false);
        }*/

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

            player.addListener(new Player.Listener() {
                @Override
                public void onPlayerError(@NonNull PlaybackException error) {
                    Log.e(TAG, "onPlayerError: message: "+error.getMessage());
                    Log.e(TAG, "onPlayerError: localized message: "+error.getLocalizedMessage());
                    Log.e(TAG, "onPlayerError: error code name: "+error.getErrorCodeName());
                    Log.e(TAG, "onPlayerError: cause: "+error.getCause());
                    errorDialog(error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "playVideo: exception: " + e.getMessage());
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.stop();
            player.release();
        }
        player = null;
    }

    private void errorDialog(String message) {
        AlertDialog.Builder adb = new AlertDialog.Builder(PlayerActivity.this);
        adb.setTitle("Couldn't able to stream video");
        adb.setMessage(message);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish(); // take out user from this activity.
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }
}