package com.example.lenovo.bakingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.lenovo.bakingapp.FragmentUtil.DetailFragment;
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

public class DetailActivity extends AppCompatActivity implements DetailFragment.OnClickListItem{

    // start defining class variables from here
    private ArrayList<Steps> stepsArrayList = new ArrayList<>();
    private RecipeDetails recipeDetails;
    private int mPosition;
    private boolean mTwoPane;
    private TextView mDescription;
    private SimpleExoPlayerView mExoPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0L;
    private CardView mCardView;
    private boolean mInitializePlayer = false;
    // define two strings to save recipedetails and steps arraylists
    private static final String RECIPE_ARR = "recipe_array";
    private static final String STEP_ARR  = "step_array";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // set Up action button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(findViewById(R.id.two_pane_view) != null){
            mTwoPane = true;
        } else{
            mTwoPane = false;
        }

        Intent intentFromMain = getIntent();
        if(intentFromMain != null){
            int position = intentFromMain.getExtras().getInt("position");
            recipeDetails = intentFromMain.getExtras().getParcelable("recipeObject");
            Timber.v("Position = " + position);
            Timber.v("Name = " + recipeDetails.getRecipeName());
            // Initialize the Step array  and find Exo player view if it is in two pane mode
            if(mTwoPane){
                stepsArrayList = recipeDetails.getPerformSteps();
                mExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exo_player_view);
                mDescription = (TextView) findViewById(R.id.tv_description);
                mCardView = (CardView) findViewById(R.id.cv_empty_video);
            }
        }

        if(findViewById(R.id.detail_fragment) != null){
            if(savedInstanceState != null){
                recipeDetails = savedInstanceState.getParcelable(RECIPE_ARR);
                stepsArrayList = savedInstanceState.getParcelableArrayList(STEP_ARR);
                return;
            }
        }

        DetailFragment detailFragment = new DetailFragment();
        // set the data source in Fragment class
        detailFragment.setRecipeDetails(recipeDetails);
        // begin fragment inside of this activity
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.detail_fragment, detailFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECIPE_ARR, recipeDetails);
        outState.putParcelableArrayList(STEP_ARR, stepsArrayList);
    }

    @Override
    public void onItemSelected(int position) {
        // save the position of the video list which we want to play
        mPosition = position;
        if(!mTwoPane){
            Intent videoIntent = new Intent(this, ViewStep.class);
            videoIntent.putExtra("videoPosition", position);
            videoIntent.putExtra("recipeObject", recipeDetails);
            startActivity(videoIntent);
        } else{
            mDescription.setText(stepsArrayList.get(mPosition).getDescription());
            Timber.v("URL for video = " + stepsArrayList.get(mPosition).getVideoUrl());
            if(stepsArrayList.get(mPosition).getVideoUrl().length() != 0){
                mCardView.setVisibility(View.GONE);
                mExoPlayerView.setVisibility(View.VISIBLE);
                mInitializePlayer = true;
                initializePlayer();
            } else {
                mExoPlayerView.setVisibility(View.GONE);
                mCardView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mInitializePlayer){
            if (Util.SDK_INT > 23) {
                initializePlayer();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mInitializePlayer){
            if ((Util.SDK_INT <= 23 || mExoPlayer == null)) {
                initializePlayer();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer(){
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this),
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
