package com.example.lenovo.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.lenovo.bakingapp.DataUtil.RecipeContract;

import timber.log.Timber;

import static com.example.lenovo.bakingapp.DataUtil.RecipeContract.BASE_CONTENT_URI;
import static com.example.lenovo.bakingapp.DataUtil.RecipeContract.RECIPE_PATH;

/**
 * Created by LENOVO on 7/21/2018.
 */

public class RecipeWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    // define TAG for logging
    private static final String TAG = ListRemoteViewsFactory.class.getSimpleName();

    private Context context;
    private Cursor cursor;

    // define constructor
    public ListRemoteViewsFactory(Context applicationContext){
        context = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        // Define URI and query the database
        Uri RECIPE_URI = BASE_CONTENT_URI.buildUpon().appendPath(RECIPE_PATH).build();

        if(cursor != null)
            cursor.close();

        try{
            cursor = context.getContentResolver().query(RECIPE_URI, null, null, null,
                    RecipeContract.RecipeEntry._ID + " ASC");
        } catch (Exception e){
            e.printStackTrace();
            Timber.v("Unable to query DB during widget update");
        }
    }

    @Override
    public void onDestroy() {
        cursor.close();
    }

    @Override
    public int getCount() {
        if(cursor.getCount() == 0){
            return 0;
        } else {
            return cursor.getCount();
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if(cursor == null || cursor.getCount() == 0){
            return null;
        }
        // move cursor to the proper position
        cursor.moveToPosition(position);

        int recipeNameIndex = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NAME);
        int ingredientIndex = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_INGREDIENT);
        int quantityIndex = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_QUANTITY);
        int measureIndex = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_MEASURE);

        String recipeName = cursor.getString(recipeNameIndex);
        String ingredientName = cursor.getString(ingredientIndex);
        String ingredientQuantity = cursor.getString(quantityIndex);
        String measureIngredient = cursor.getString(measureIndex);
        // Now define the complete text which will set on the widget
        String fullString = recipeName + ": " + ingredientName + "," + ingredientQuantity + "," +
                measureIngredient;

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget_item);
        remoteViews.setTextViewText(R.id.tv_ingredient_name, fullString);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
