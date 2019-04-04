package com.example.lenovo.bakingapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by LENOVO on 7/12/2018.
 */

public class RecipeDetails implements Parcelable {

    String recipeName;
    String servings;
    ArrayList<Ingredients> ingredientNames = new ArrayList<>();
    ArrayList<Steps> performSteps = new ArrayList<>();

    public RecipeDetails(String name, String serv, ArrayList<Ingredients> ingredients,
                         ArrayList<Steps> requiredSteps){
        recipeName = name;
        servings = serv;
        ingredientNames = ingredients;
        performSteps = requiredSteps;
    }

    public String getRecipeName(){
        return recipeName;
    }

    public String getServings(){
        return servings;
    }

    public ArrayList<Ingredients> getIngredientNames(){
        return ingredientNames;
    }

    public ArrayList<Steps> getPerformSteps(){
        return performSteps;
    }

    private RecipeDetails(Parcel in){
        recipeName = in.readString();
        servings = in.readString();
        in.readTypedList(ingredientNames, Ingredients.CREATOR);
        in.readTypedList(performSteps, Steps.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {

        dest.writeString(recipeName);
        dest.writeString(servings);
        dest.writeTypedList(ingredientNames);
        dest.writeTypedList(performSteps);
    }

    public static final Parcelable.Creator<RecipeDetails> CREATOR =
            new Parcelable.Creator<RecipeDetails>(){

                @Override
                public RecipeDetails createFromParcel(Parcel in) {
                    return new RecipeDetails(in);
                }

                @Override
                public RecipeDetails[] newArray(int size) {
                    return new RecipeDetails[size];
                }
            };
}
