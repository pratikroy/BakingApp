package com.example.lenovo.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Create an intent to launch MainActivity when clicked
        Intent mainIntent = new Intent(context, MainActivity.class);
        //Create another intent to call RemoteViewService
        Intent listViewIntent = new Intent(context, RecipeWidgetService.class);

        // Create a pending intent
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        views.setRemoteAdapter(R.id.lv_favourite_menu, listViewIntent);
        views.setEmptyView(R.id.lv_favourite_menu, R.id.tv_empty_view);
        // Set click listener on button view
        views.setOnClickPendingIntent(R.id.tv_widget_name, mainPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /*
    public static void updateRecipeAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                              int[] appWidgetIds){
        for(int appWidgetId : appWidgetIds){
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    } */

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

