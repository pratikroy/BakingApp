package com.example.lenovo.bakingapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;

/**
 * Created by LENOVO on 7/12/2018.
 */

public class Steps implements Parcelable {

    private String description;
    private String videoUrl;

    public Steps(String des, String url){
        description = des;
        videoUrl = url;
    }

    public String getDescription(){
        return description;
    }

    public String getVideoUrl(){
        return videoUrl;
    }

    protected Steps(Parcel in){
        description = in.readString();
        videoUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(description);
        dest.writeString(videoUrl);
    }

    public static final Parcelable.Creator<Steps> CREATOR =
            new Parcelable.Creator<Steps>(){

                @Override
                public Steps createFromParcel(Parcel in) {
                    return new Steps(in);
                }

                @Override
                public Steps[] newArray(int size) {
                    return new Steps[size];
                }
            };
}
