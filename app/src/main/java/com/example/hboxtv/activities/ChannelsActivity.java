package com.example.hboxtv.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hboxtv.MyApplication;
import com.example.hboxtv.R;
import com.example.hboxtv.adapters.ChannelAdapter;
import com.example.hboxtv.model.Channel;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hb.xvideoplayer.MxVideoPlayer;

public class ChannelsActivity extends AppCompatActivity implements MediaSourceEventListener, BandwidthMeter.EventListener, ChannelAdapter.OnChannelClickListener {
    private static final String TAG = ChannelsActivity.class.getSimpleName();
    private PlayerView playerView;
    private ExoPlayer simpleExoPlayer;
    private String UID;
    private String GUID;
    private String categoryId;
    private String server;
    private String userName;
    private String password;
    List<Channel> channelList = new ArrayList<>();

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0L;

    MediaSourceEventListener eventListener;
    BandwidthMeter.EventListener bandwidthMeterEventListener;

    // url of video which we are loading.
    String videoURL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
//    String videoURL = "http://canalvip.ddns.net:25443/live/noormobipixelsgmailcom270763/NY4T45RN62/9152";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        //            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        // for full screen activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();
        setClickListeners();

        // get value from shared prefs
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ChannelsActivity.this);
        UID = preferences.getString("uid", "");
        GUID = preferences.getString("guid", "");
        server = preferences.getString("server", "");
        userName = preferences.getString("user_name", "");
        password = preferences.getString("password", "");

        Intent intent = getIntent();
        if (intent != null) {
            categoryId = intent.getStringExtra("category_id");
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            if (UID != null && GUID != null) {
                getChannelList(categoryId);
            }
            handler.post(() -> {
                //UI Thread work here
            });
        });

//        playVideo(videoURL);

        eventListener = this;
        bandwidthMeterEventListener = this;
    }

    private void getChannelList(String categoryId) {
        final String JSON_URL = "http://54.36.204.161/iptvapi/objects/channelsbycategory.php";
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("deviceuid", UID);
            jsonBody.put("customerguid", GUID);
            jsonBody.put("categoryID", categoryId);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, JSON_URL, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        Log.d(TAG, "123onResponse: " + response.toString());
                        try {
                            //getting the whole json object from the response
                            JSONArray jsonArray = response.getJSONArray("response");

                            //now looping through all the elements of the json array
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                Channel channel = new Channel(
                                        jsonObject1.getString("channelid"),
                                        jsonObject1.getString("name"),
                                        jsonObject1.getString("stream_type"),
                                        jsonObject1.getString("stream_icon"),
                                        jsonObject1.getString("added"),
                                        jsonObject1.getString("container_extension"));

                                //adding the list
                                channelList.add(channel);
                            }
                            populateRecyclerView(channelList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.toString());
//                    Toast.makeText(LiveTvActivity.this, "Error: "+ error.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            jsonObject.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });

            //creating a request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //adding the string request to request queue
            requestQueue.add(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void populateRecyclerView(List<Channel> channelList) {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.hasFixedSize();
        ChannelAdapter adapter = new ChannelAdapter(this, channelList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void setClickListeners() {
        findViewById(R.id.btn_back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(ChannelsActivity.this, R.anim.button_click));
                onBackPressed();
            }
        });

        findViewById(R.id.ic_full_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(ChannelsActivity.this, R.anim.button_click));
                LinearLayout channelsLayout = findViewById(R.id.channels_layout);
                LinearLayout headerLayout = findViewById(R.id.header_layout);
                LinearLayout extraLayout = findViewById(R.id.extra_layout);

                if (channelsLayout.getVisibility() == View.VISIBLE && headerLayout.getVisibility() == View.VISIBLE) {
                    channelsLayout.setVisibility(View.GONE);
                    headerLayout.setVisibility(View.GONE);
                    extraLayout.setVisibility(View.GONE);
                } else {
                    channelsLayout.setVisibility(View.VISIBLE);
                    headerLayout.setVisibility(View.VISIBLE);
                    extraLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /*private void playVideo(String videoURL) {
        try {
            simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
            playerView.setPlayer(simpleExoPlayer);
            MediaItem mediaItem = MediaItem.fromUri(videoURL);
            simpleExoPlayer.addMediaItem(mediaItem);
            simpleExoPlayer.seekTo(currentWindow, playbackPosition);
            simpleExoPlayer.setPlayWhenReady(playWhenReady);
            simpleExoPlayer.prepare();
        } catch (Exception e){
            Log.d(TAG, "playVideo: exception: "+ e.getMessage());
        }

        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NotNull PlaybackException error) {
                Log.e(TAG, "onPlayerError: message: "+error.getMessage());
                Log.e(TAG, "onPlayerError: localized message: "+error.getLocalizedMessage());
                Log.e(TAG, "onPlayerError: error code name: "+error.getErrorCodeName());
                Log.e(TAG, "onPlayerError: cause: "+error.getCause());
                errorDialog(error.getMessage());
            }
        });

    }*/

    private void initializePlayer(String m3u8URL) {
        // 1. Create a default TrackSelector
        Handler mainHandler = new Handler();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder()
                .setEventListener(mainHandler, bandwidthMeterEventListener)
                .build();

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        playerView = findViewById(R.id.video_view);
        playerView.hideController();
        playerView.setPlayer(player);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "example-hls-app"), bandwidthMeter);

        // This is the MediaSource representing the media to be played.
        HlsMediaSource videoSource = new HlsMediaSource(Uri.parse(m3u8URL), dataSourceFactory, 5, mainHandler, eventListener);

        // Prepare the player with the source.
        player.prepare(videoSource);
    }

    /*private void playVideo(String channelName, String videoURL) {
        MxVideoPlayerWidget videoPlayerWidget = findViewById(R.id.mpw_video_player);
        videoPlayerWidget.autoStartPlay(videoURL, MxVideoPlayer.SCREEN_LAYOUT_NORMAL, channelName);
    }*/

    private void initViews() {
    }

    @Override
    public void onBackPressed() {
        /*if (MxVideoPlayer.backPress()) {
            return;
        }*/
        super.onBackPressed();
        releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MxVideoPlayer.releaseAllVideos();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    /*private void releasePlayer() {
        if (simpleExoPlayer != null) {
//            updateResumePosition();
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
//            trackSelector = null;
//            trackSelectionHelper = null;
//            eventLogger = null;
        }
    }*/

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
//            playbackPosition = simpleExoPlayer.getCurrentPosition();
//            currentWindow = simpleExoPlayer.getCurrentWindowIndex();
//            playWhenReady = simpleExoPlayer.getPlayWhenReady();
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
        }
        simpleExoPlayer = null;
    }

    private void errorDialog(String message) {
        AlertDialog.Builder adb = new AlertDialog.Builder(ChannelsActivity.this);
        adb.setTitle("Couldn't able to stream channel");
        adb.setMessage(message);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                finish(); // take out user from this activity.
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }

    @Override
    public void OnChannelClick(String channelName, String channelId, String streamType, String extension) {
//        Toast.makeText(this, "Channel Id: "+ channelId+ "type: "+ streamType, Toast.LENGTH_SHORT).show();
        if (MyApplication.isNetworkNotAvailable(ChannelsActivity.this)) {
            MyApplication.networkDialog(ChannelsActivity.this,
                    "No internet available. Please connect to the internet first.");
            return;
        }

        releasePlayer();
        if (userName != null && password != null) {
            String url = server+"/"+streamType+"/"+userName+"/"+password+"/"+channelId+".m3u8"/*extension*/;
            Log.d(TAG, "OnChannelClick: url: "+ url);
//            playVideo(url);
//            playVideo(channelName, url);
            initializePlayer(url);
        }
    }

    @Override
    public void onBandwidthSample(int elapsedMs, long bytesTransferred, long bitrateEstimate) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onMediaPeriodCreated(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {

    }

    @Override
    public void onMediaPeriodReleased(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {

    }

    @Override
    public void onLoadStarted(int windowIndex, @Nullable @org.jetbrains.annotations.Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

    }

    @Override
    public void onLoadCompleted(int windowIndex, @Nullable @org.jetbrains.annotations.Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

    }

    @Override
    public void onLoadCanceled(int windowIndex, @Nullable @org.jetbrains.annotations.Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

    }

    @Override
    public void onLoadError(int windowIndex, @Nullable @org.jetbrains.annotations.Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {

    }

    @Override
    public void onReadingStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {

    }

    @Override
    public void onUpstreamDiscarded(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {

    }

    @Override
    public void onDownstreamFormatChanged(int windowIndex, @Nullable @org.jetbrains.annotations.Nullable MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {

    }
}