package com.example.hboxtv.activities;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.example.hboxtv.adapters.EpgAdapter;
import com.example.hboxtv.api.ApiClient;
import com.example.hboxtv.api.ApiInterface;
import com.example.hboxtv.model.Channel;
import com.example.hboxtv.model.Epg.Epg;
import com.example.hboxtv.model.Epg.EpgBody;
import com.example.hboxtv.model.Epg.EpgModel;
import com.example.hboxtv.model.Epg.EpgResponse;
import com.example.hboxtv.model.SignUpModel;
import com.example.hboxtv.model.SignUpResponse;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelsActivity extends AppCompatActivity implements MediaSourceEventListener, BandwidthMeter.EventListener, ChannelAdapter.OnChannelClickListener {
    private static final String TAG = ChannelsActivity.class.getSimpleName();
    private PlayerView playerView;
    private SimpleExoPlayer exoPlayer;
    private String UID;
    private String GUID;
    private String categoryId;
    private String server;
    private String userName;
    private String password;
    private List<Channel> channelList = new ArrayList<>();

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0L;
    String streamUrl;
    boolean isFullScreen = false;

    private LinearLayout headerLayout, mainLayout;
    private RelativeLayout extraLayout;
    private RelativeLayout channelsLayout;
    private ImageView btnMuteUnMute;
    private ProgressBar progressBar;
    private ProgressBar progressBarEpg;
//    private EpgModel epgModel = new EpgModel();

    private MediaSourceEventListener eventListener;
    private BandwidthMeter.EventListener bandwidthMeterEventListener;
    private List<EpgResponse> epgResponseList;
    private ApiInterface apiInterface;
    //    private List<EpgResponse> epgResponseList = new ArrayList<>();

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

        executor.execute(() -> {
            if (UID != null && GUID != null) {
//                getEpg(categoryId);
//                getEpgByRetrofit(categoryId);
            }
        });

//        playVideo(videoURL);

        eventListener = this;
        bandwidthMeterEventListener = this;
    }

    private void initViews() {
        channelsLayout = findViewById(R.id.channels_layout);
        headerLayout = findViewById(R.id.header_layout);
        extraLayout = findViewById(R.id.epg_layout);
        mainLayout = findViewById(R.id.main_layout);
        btnMuteUnMute = findViewById(R.id.mute_unMute);
        progressBar = findViewById(R.id.progress_bar);
        progressBarEpg = findViewById(R.id.progress_bar_epg);
    }

    private void getChannelList(String categoryId) {
        final String JSON_URL = "http://54.36.204.161/iptvapi/objects/channelsbycategory.php";
        try {
            progressBar.setVisibility(View.VISIBLE);
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("deviceuid", UID);
            jsonBody.put("customerguid", GUID);
            jsonBody.put("categoryID", categoryId);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, JSON_URL, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        progressBar.setVisibility(View.GONE);
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
                    progressBar.setVisibility(View.GONE);
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

    private void getEpg(String categoryId) {
        final String JSON_URL = "http://54.36.204.161/iptvapi/objects/channelsbycategoryv1.php";
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarEpg.setVisibility(View.VISIBLE);
                }
            });
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("deviceuid", UID);
            jsonBody.put("customerguid", GUID);
            jsonBody.put("categoryID", categoryId);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, JSON_URL, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        progressBarEpg.setVisibility(View.GONE);
                        try {
                            //getting the whole json object from the response
                            JSONArray jsonArray = response.getJSONArray("response");
                            List<EpgResponse> epgResponseList = new ArrayList<>();
                            EpgResponse epgResponse = new EpgResponse();
                            Log.d(TAG, "onResponse: json array: " + jsonArray.toString());

                            //now looping through all the elements of the json array
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                epgResponse.setChannelid(jsonObject1.getString("channelid"));
                                epgResponse.setName(jsonObject1.getString("name"));

                                JSONArray epgArray = jsonObject1.getJSONArray("epg");
                                List<Epg> epgList = new ArrayList<>();

                                for (int j = 0; j < epgArray.length(); j++) {
                                    Epg epg = new Epg();
                                    epg.setTitle(epgArray.getJSONObject(j).getString("title"));
                                    epg.setStart(epgArray.getJSONObject(j).getString("start"));
                                    epg.setStop(epgArray.getJSONObject(j).getString("stop"));
                                    epgList.add(epg);

                                    epgResponse.setEpg(epgList);
//                                    Log.d(TAG, "onResponse: epg channel: "+ epgResponse.getName());
//                                    Log.d(TAG, "onResponse: epg title: "+ epg.getTitle());
//                                    Log.d(TAG, "onResponse: epg start: "+ epg.getStart());
//                                    Log.d(TAG, "onResponse: epg end: "+ epg.getStop());
                                }
                                epgResponseList.add(epgResponse);
                                Log.d(TAG, "onResponse: epg response list name: " + epgResponseList.get(i).getEpg().size());
                            }
                            Log.d(TAG, "onResponse: epg response list name: " + epgResponseList.toString());
//                            epgModel.setResponse(epgResponseList);
//                            populateEpgRecyclerView(epgList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
//                    Log.d(TAG, "onResponse: epg model list: " + epgModel.getResponse().size());

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBarEpg.setVisibility(View.GONE);
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
    private void getEpgByRetrofit(String categoryId) {
        progressBarEpg.setVisibility(View.VISIBLE);
        apiInterface = ApiClient.getInstance().getMyApi();
        EpgBody epgBody = new EpgBody(UID, GUID, categoryId);
        Log.d(TAG, "getEpgByRetrofit: epgBody: " + epgBody);
        Call<EpgModel> call = apiInterface.getEpgResponse(epgBody);
        call.enqueue(new Callback<EpgModel>() {
            @Override
            public void onResponse(Call<EpgModel> call, Response<EpgModel> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        /*SignUpResponse signUpResponse = new SignUpResponse();
                        signUpResponse.setCode(response.body().getCode());
                        signUpResponse.setMessage(response.body().getMessage());*/
                        Log.d(TAG, "onResponse: getEpgByRetrofit code: " + response.code());
                        Log.d(TAG, "onResponse: getEpgByRetrofit message: " + response.message());
                        progressBarEpg.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<EpgModel> call, Throwable t) {
                progressBarEpg.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void populateEpgRecyclerView(List<Epg> epgList) {
        Log.d(TAG, "populateEpgRecyclerView: epgList: " + epgList.size());
        RecyclerView recyclerView = findViewById(R.id.recyclerview_epg);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.hasFixedSize();
        EpgAdapter adapter = new EpgAdapter(this, epgList);
        recyclerView.setAdapter(adapter);
        progressBarEpg.setVisibility(View.GONE);
    }

    private void populateRecyclerView(List<Channel> channelList) {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.hasFixedSize();
        ChannelAdapter adapter = new ChannelAdapter(this, channelList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        // play fist channel on start
        if (channelList != null && channelList.size() > 0) {
            Channel firstChannel = channelList.get(0);

            if (userName != null && password != null) {
                streamUrl = server + "/" + firstChannel.getStreamType() + "/" + userName + "/" + password + "/" + firstChannel.getChannelId() + ".m3u8";
                initializePlayer();
            }
        }
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
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(ChannelsActivity.this, R.anim.button_click));
//                PlayerView.switchTargetView(exoPlayer,playerView, playerViewFull);
                if (channelsLayout.getVisibility() == View.VISIBLE && headerLayout.getVisibility() == View.VISIBLE) {
                    channelsLayout.setVisibility(View.GONE);
                    headerLayout.setVisibility(View.GONE);
                    extraLayout.setVisibility(View.GONE);
                    mainLayout.setBackground(getResources().getDrawable(R.drawable.black_screen));
                    isFullScreen = true;
                } else {
                    channelsLayout.setVisibility(View.VISIBLE);
                    headerLayout.setVisibility(View.VISIBLE);
                    extraLayout.setVisibility(View.VISIBLE);
                    mainLayout.setBackground(getResources().getDrawable(R.drawable.home_bg));
                    isFullScreen = false;
                }
            }
        });

        btnMuteUnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(ChannelsActivity.this, R.anim.button_click));
                muteUnMute();
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

    private void initializePlayer() {
        // 1. Create a default TrackSelector
        Handler mainHandler = new Handler();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder()
                .setEventListener(mainHandler, bandwidthMeterEventListener)
                .build();

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        playerView = findViewById(R.id.video_view);
        playerView.setPlayer(exoPlayer);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "example-hls-app"), bandwidthMeter);

        // This is the MediaSource representing the media to be played.
        HlsMediaSource videoSource = null;
        if (streamUrl != null) {
            videoSource = new HlsMediaSource(Uri.parse(streamUrl), dataSourceFactory, 5, mainHandler, eventListener);
        }

        // Prepare the player with the source.
        exoPlayer.prepare(videoSource);
        exoPlayer.setPlayWhenReady(playWhenReady);
        setMuteUnMute();
    }

    private void muteUnMute() {
        if (exoPlayer != null) {
            float currentVolume = exoPlayer.getVolume();
            if (currentVolume == 0f) {
                exoPlayer.setVolume(1f);
                btnMuteUnMute.setImageDrawable(getDrawable(R.drawable.ic_mic));
            } else {
                exoPlayer.setVolume(0f);
                btnMuteUnMute.setImageDrawable(getDrawable(R.drawable.ic_mic_off));
            }
        }

    }

    private void setMuteUnMute() {
        if (exoPlayer != null) {
            float currentVolume = exoPlayer.getVolume();
            if (currentVolume == 0f) {
                btnMuteUnMute.setImageDrawable(getDrawable(R.drawable.ic_mic_off));
            } else {
                btnMuteUnMute.setImageDrawable(getDrawable(R.drawable.ic_mic));
            }
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
        if ((Util.SDK_INT < 24 || exoPlayer == null)) {
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "44onPause: called!");
//        MxVideoPlayer.releaseAllVideos();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "44onStop: called!");
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "44onDestroy: called!");
        releasePlayer();
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen) {
            channelsLayout.setVisibility(View.VISIBLE);
            headerLayout.setVisibility(View.VISIBLE);
            extraLayout.setVisibility(View.VISIBLE);
            isFullScreen = false;
            mainLayout.setBackground(getResources().getDrawable(R.drawable.home_bg));
        } else {
            releasePlayer();
            super.onBackPressed();
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            Log.d(TAG, "releasePlayer: released!");
//            playbackPosition = simpleExoPlayer.getCurrentPosition();
//            currentWindow = simpleExoPlayer.getCurrentWindowIndex();
            playWhenReady = exoPlayer.getPlayWhenReady();
            exoPlayer.stop();
            exoPlayer.release();
        }
        exoPlayer = null;
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


        Log.d(TAG, "onResponse: epg response list name: " + epgResponseList.size());

        // loading epg for each channel
        /*if (epgResponseList != null) {
            Log.d(TAG, "OnChannelClick: id epgResponseList size: " + epgResponseList.size());
            Log.d(TAG, "OnChannelClick: id epgResponseList name: " + epgResponseList.get(0).getName());
            Log.d(TAG, "OnChannelClick: id epgResponseList name: " + epgResponseList.get(1).getName());
            Log.d(TAG, "OnChannelClick: id epgResponseList name: " + epgResponseList.get(2).getName());
            Log.d(TAG, "OnChannelClick: id epgResponseList name: " + epgResponseList.get(3).getName());
            Log.d(TAG, "OnChannelClick: id epgResponseList name: " + epgResponseList.get(4).getName());
            Log.d(TAG, "OnChannelClick: id epgResponseList name: " + epgResponseList.get(5).getName());
            Log.d(TAG, "OnChannelClick: id epgResponseList name: " + epgResponseList.get(6).getName());

            *//*progressBarEpg.setVisibility(View.VISIBLE);
            for (EpgResponse epgResponse: epgResponseList) {
                Log.d(TAG, "OnChannelClick: idr: " + epgResponse.getName());

            }*//*
            *//*for (int i = 0; i < epgResponseList.size(); i++) {
                Log.d(TAG, "OnChannelClick: name: " + epgResponseList.get(i).getChannelid());
//                Log.d(TAG, "OnChannelClick: idc: " + channelId);
            }*//*

            *//*if (epgResponse.getChannelid().equals(channelId)) {
                populateEpgRecyclerView(epgResponse.getEpg());
            } else {
                progressBarEpg.setVisibility(View.GONE);
                Toast.makeText(this, "id not matched!", Toast.LENGTH_SHORT).show();
            }*//*
        } else {
            progressBarEpg.setVisibility(View.GONE);
            Toast.makeText(this, "epg not loaded!", Toast.LENGTH_SHORT).show();
        }*/

        releasePlayer();

        if (userName != null && password != null) {
            streamUrl = server + "/" + streamType + "/" + userName + "/" + password + "/" + channelId + ".m3u8"/*extension*/;
            Log.d(TAG, "OnChannelClick: url: " + streamUrl);
            initializePlayer();
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