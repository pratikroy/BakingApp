package com.example.lenovo.bakingapp.FragmentUtil;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lenovo.bakingapp.R;
import com.example.lenovo.bakingapp.RecipeDetails;
import com.example.lenovo.bakingapp.Steps;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by LENOVO on 7/25/2018.
 */

public class VideoFragment extends Fragment {

    // define TAG to log information
    private static final String TAG = VideoFragment.class.getSimpleName();

    // start defining class variables from here
    private int mPosition;
    private ArrayList<Steps> stepsArrayList = new ArrayList<>();
    private TextView mDescription;
    private SimpleExoPlayerView mExoPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0L;
    private CardView mCardView;
    private boolean mInitializePlayer = false;
    private static final String STEP_LIST = "step_list";

    // mandatory constructor for fragment class
    public VideoFragment(){
    }

    public void setVideoSource(int position, RecipeDetails recipe){
        mPosition = position;
        stepsArrayList = recipe.getPerformSteps();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // if onSavedInstanceState is not null then retrive stepsArrayList from it
        if(savedInstanceState != null){
            stepsArrayList = savedInstanceState.getParcelableArrayList(STEP_LIST);
        }

        // inflate the base layout file
        View rootView = inflater.inflate(R.layout.video_fragment, container, false);

        mExoPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.exo_player_view);
        mDescription = (TextView) rootView.findViewById(R.id.tv_description);
        mCardView = (CardView) rootView.findViewById(R.id.cv_empty_video);

        mDescription.setText(stepsArrayList.get(mPosition).getDescription());
        Timber.v("URL for video = " + stepsArrayList.get(mPosition).getVideoUrl());
        if(stepsArrayList.get(mPosition).getVideoUrl().length() != 0){
            mCardView.setVisibility(View.GONE);
            mExoPlayerView.setVisibility(View.VISIBLE);
            mInitializePlayer = true;
        } else {
            mExoPlayerView.setVisibility(View.GONE);
            mCardView.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STEP_LIST, stepsArrayList);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(stepsArrayList.get(mPosition).getVideoUrl().length() != 0){
            if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
                mDescription.setVisibility(View.GONE);
                LinearLayout.LayoutParams params =
                        (LinearLayout.LayoutParams) mExoPlayerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = params.MATCH_PARENT;
                // hide the action bar in LANDSCAE mode
                if(((AppCompatActivity)getActivity()).getSupportActionBar() != null){
                    ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
                }
            } else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
                mDescription.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params =
                        (LinearLayout.LayoutParams) mExoPlayerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = 0;
                params.weight = 2;
                // show the action bar again in POTRAIT mode
                if(((AppCompatActivity)getActivity()).getSupportActionBar() != null){
                    ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mInitializePlayer){
            if (Util.SDK_INT > 23) {
                initializePlayer();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mInitializePlayer){
            if ((Util.SDK_INT <= 23 || mExoPlayer == null)) {
                initializePlayer();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer(){
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(), new DefaultLoadControl());
        mExoPlayerView.setPlayer(mExoPlayer);
        mExoPlayer.setPlayWhenReady(playWhenReady);
        mExoPlayer.seekTo(currentWindow, playbackPosition);
        Uri uri = Uri.parse(stepsArrayList.get(mPosition).getVideoUrl());
        MediaSource mediaSource = buildMediaSource(uri);
        mExoPlayer.prepare(mediaSource, true, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("my-baking-app")).
                createMediaSource(uri);
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            playbackPosition = mExoPlayer.getCurrentPosition();
            currentWindow = mExoPlayer.getCurrentWindowIndex();
            playWhenReady = mExoPlayer.getPlayWhenReady();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }
}
