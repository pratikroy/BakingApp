package com.example.lenovo.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.lenovo.bakingapp.FragmentUtil.VideoFragment;

import timber.log.Timber;


public class ViewStep extends AppCompatActivity {

    // start defining class variables from here
    private RecipeDetails mRecipeDetails;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_step);

        // set Up action button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mPosition = intent.getExtras().getInt("videoPosition");
        mRecipeDetails = intent.getExtras().getParcelable("recipeObject");
        Timber.v("Position = " + mPosition);
        Timber.v("Recipe name = " + mRecipeDetails.getRecipeName());

        // initialize fragment and set video source
        VideoFragment videoFragment = new VideoFragment();
        videoFragment.setVideoSource(mPosition, mRecipeDetails);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.video_fragment, videoFragment).commit();
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
}
