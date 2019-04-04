package com.example.lenovo.bakingapp.DataUtil;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by LENOVO on 7/19/2018.
 */

public class RecipeContentProvider extends ContentProvider {

    // define integer constant to perform a particular task
    public static final int RECIPE = 100;

    // create RecipeDbHelper object
    private RecipeDbHelper mRecipeDbHelper;

    // call UriMatcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // build the UriMatcher function to perform Uri matching operation
    public static UriMatcher buildUriMatcher( ){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // define only Uri to perform actions
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.RECIPE_PATH, RECIPE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mRecipeDbHelper = new RecipeDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = mRecipeDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)){
            case RECIPE:
                long id = db.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, contentValues);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(RecipeContract.RecipeEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mRecipeDbHelper.getWritableDatabase();
        int recipeDeleted;

        switch (sUriMatcher.match(uri)){
            case RECIPE:
                recipeDeleted = db.delete(RecipeContract.RecipeEntry.TABLE_NAME, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(recipeDeleted != 0){
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return recipeDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mRecipeDbHelper.getReadableDatabase();
        Cursor returnCursor;

        switch (sUriMatcher.match(uri)){
            case RECIPE:
                returnCursor = db.query(RecipeContract.RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return returnCursor;
    }

    @Override
    public String getType(@NonNull Uri uri){
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
