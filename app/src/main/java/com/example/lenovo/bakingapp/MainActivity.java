package com.example.lenovo.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lenovo.bakingapp.DataUtil.RecipeContract;
import com.example.lenovo.bakingapp.JSONUtil.ParseJson;
import com.example.lenovo.bakingapp.ViewUtils.RecipeAdapter;

import org.json.JSONException;
import java.util.ArrayList;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeAdapterClickHandler{

    private static final String TAG = MainActivity.class.getSimpleName();

    // Start defining class variables from here
    private RecipeAdapter mRecipeAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mPregressIndicator;
    private TextView mTextView;
    private Button mRefreshButton;
    LinearLayoutManager layoutManagerForRecipeNames =
            new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main_layout);
        mPregressIndicator = (ProgressBar) findViewById(R.id.pb_show_progress);
        mTextView = (TextView) findViewById(R.id.tv_error_message);
        mRefreshButton = (Button) findViewById(R.id.bv_refresh_button);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManagerForRecipeNames);
        mRecipeAdapter = new RecipeAdapter(MainActivity.this, MainActivity.this);
        mRecyclerView.setAdapter(mRecipeAdapter);

        // start parsing JSON and create view
        startParsing();

        // implement click listener on refresh button
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startParsing();
            }
        });

    }

    private void startParsing(){
        if(ParseJson.isConnected(this)){
            new FetchRecipe().execute();
        } else{
            mPregressIndicator.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            mTextView.setVisibility(View.VISIBLE);
            mRefreshButton.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onClickRecipe(int position, RecipeDetails recipe) {
        Timber.v("Clicked position = " + position);
        Timber.v("Recipe name = " + recipe.getRecipeName());
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("recipeObject", recipe);
        startActivity(intent);
    }

    @Override
    public void onClickFavouriteButton(int position, RecipeDetails recipe, String action) {
        Timber.v("Clicked button position = " + position);
        Timber.v("Recipe name = " + recipe.getRecipeName());
        Timber.v("Main action = " + action);
        if(action.equals("add")){
            ContentValues contentValues = new ContentValues();
            for(int i = 0; i < recipe.getIngredientNames().size(); i++){
                contentValues.put(RecipeContract.RecipeEntry.COLUMN_NAME, recipe.getRecipeName());
                contentValues.put(RecipeContract.RecipeEntry.COLUMN_INGREDIENT,
                        recipe.getIngredientNames().get(i).getIngredientName());
                contentValues.put(RecipeContract.RecipeEntry.COLUMN_QUANTITY,
                        recipe.getIngredientNames().get(i).getQuantity());
                contentValues.put(RecipeContract.RecipeEntry.COLUMN_MEASURE,
                        recipe.getIngredientNames().get(i).getMeasure());

                Uri uri = getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI, contentValues);
                if(uri != null){
                    Timber.v("Insert uri = " + uri);
                }
            }
        } else if(action.equals("delete")){
            getContentResolver().delete(RecipeContract.RecipeEntry.CONTENT_URI, null, null);
        }

        // notify the app widget about a data set change in view
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_favourite_menu);
        //RecipeWidget.updateRecipeAppWidgets(this, appWidgetManager, appWidgetIds);
    }

    public class FetchRecipe extends AsyncTask<Void, Void, ArrayList<RecipeDetails>>{

        ArrayList<RecipeDetails> recipeDetails = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRecyclerView.setVisibility(View.VISIBLE);
            mPregressIndicator.setVisibility(View.VISIBLE);
            mRefreshButton.setVisibility(View.INVISIBLE);
            mTextView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList<RecipeDetails> doInBackground(Void... voids) {
            try{
                recipeDetails = ParseJson.extractInfoFromJson();
            } catch (JSONException e){
                e.printStackTrace();
                return null;
            }

            return recipeDetails;
        }

        @Override
        protected void onPostExecute(ArrayList<RecipeDetails> recipeDetails) {
            mPregressIndicator.setVisibility(View.INVISIBLE);
            Timber.v("RecipeDetails size = " + recipeDetails.size());
            for(int i = 0; i < recipeDetails.size(); i++){
                Timber.v("Recipe name = " + recipeDetails.get(i).getRecipeName());
                Timber.v("servings = " + recipeDetails.get(i).getServings());
                Timber.v("Number of ingredients = " + recipeDetails.get(i).getIngredientNames().size());
                Timber.v("Number of steps = " + recipeDetails.get(i).getPerformSteps().size());
            }
            mRecipeAdapter.updateDetails(recipeDetails);
        }
    }
}
