package com.example.lenovo.bakingapp.JSONUtil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.example.lenovo.bakingapp.Ingredients;
import com.example.lenovo.bakingapp.RecipeDetails;
import com.example.lenovo.bakingapp.Steps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import timber.log.Timber;

/**
 * Created by LENOVO on 7/12/2018.
 */


public final class ParseJson {

    private static final String TAG = ParseJson.class.getSimpleName();

    // define required strings for URL
    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net";

    // Check whether network connection is available or not
    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private static URL builtURL(){
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath("topher")
                .appendPath("2017")
                .appendPath("May")
                .appendPath("59121517_baking")
                .appendPath("baking.json").build();

        URL url = null;
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }

        Timber.v("Built URL = " + url);
        return url;
    }

    private static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try{
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            }
            else{
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static ArrayList<RecipeDetails> extractInfoFromJson( )throws JSONException {

        JSONArray jsonArray;
        ArrayList<RecipeDetails> recipeDetailsArrayList = new ArrayList<>(); // Recipe objects
        URL recipeUrl = builtURL();

        try{
            jsonArray = new JSONArray(getResponseFromHttpUrl(recipeUrl));
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }

        for(int i = 0; i < jsonArray.length(); i++){
            String recipeName = null; // recipe name from JSON response
            String servings = null;
            ArrayList<Ingredients> ingredientsArrayList = new ArrayList<>();
            ArrayList<Steps> stepsArrayList = new ArrayList<>();

            // start parsing JSON from this point
            JSONObject recipeDetailsObject = jsonArray.getJSONObject(i);

            //Extract name of the recipe
            if(recipeDetailsObject.has("name") && !recipeDetailsObject.isNull("name"))
                recipeName = recipeDetailsObject.getString("name");
            // Extract information for servings
            if(recipeDetailsObject.has("servings") && !recipeDetailsObject.isNull("servings"))
                servings = recipeDetailsObject.getString("servings");

            // Extract information for Ingredients and Steps
            JSONArray ingredientsArray = recipeDetailsObject.getJSONArray("ingredients");
            JSONArray stepsArray = recipeDetailsObject.getJSONArray("steps");

            for(int j = 0; j < ingredientsArray.length(); j++){
                JSONObject object = ingredientsArray.getJSONObject(j);
                String quantity = object.getString("quantity");
                String measure = object.getString("measure");
                String ingredient = object.getString("ingredient");
                ingredientsArrayList.add(new Ingredients(quantity, measure, ingredient));
            }

            for(int k = 0; k < stepsArray.length(); k++){
                String description = null;
                String videoUrl = null;
                JSONObject object = stepsArray.getJSONObject(k);
                if(object.has("description") && !object.isNull("description")){
                    description = object.getString("description");
                }
                if(object.has("videoURL") && !object.isNull("videoURL")){
                    videoUrl = object.getString("videoURL");
                }
                stepsArrayList.add(new Steps(description, videoUrl));
            }
            // create RecipeDetails object
            recipeDetailsArrayList.add(new RecipeDetails(recipeName, servings, ingredientsArrayList, stepsArrayList));
        }

    return recipeDetailsArrayList;
    }
}