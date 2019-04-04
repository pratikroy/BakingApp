package com.example.lenovo.bakingapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LENOVO on 7/12/2018.
 */

public class Ingredients implements Parcelable {

    private String quantity;
    private String measure;
    private String ingredientName;

    public Ingredients(String q, String m, String name){
        quantity = q;
        measure = m;
        ingredientName = name;
    }

    public String getQuantity(){
        return quantity;
    }

    public String getMeasure(){
        return measure;
    }

    public String getIngredientName(){
        return ingredientName;
    }

    protected Ingredients(Parcel in){
        quantity = in.readString();
        measure = in.readString();
        ingredientName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(quantity);
        dest.writeString(measure);
        dest.writeString(ingredientName);

    }

    public static final Parcelable.Creator<Ingredients> CREATOR =
            new Parcelable.Creator<Ingredients>(){
                @Override
                public Ingredients createFromParcel(Parcel in) {
                    return new Ingredients(in);
                }

                @Override
                public Ingredients[] newArray(int size) {
                    return new Ingredients[size];
                }
            };
}
