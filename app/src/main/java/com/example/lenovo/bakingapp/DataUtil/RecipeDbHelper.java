package com.example.lenovo.bakingapp.DataUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LENOVO on 7/19/2018.
 */

public class RecipeDbHelper extends SQLiteOpenHelper {

    // define the database name
    private static final String DATABASE_NAME = "recipeDb.db";

    // define the database version
    private static final int VERSION = 1;

    public RecipeDbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create table and follow the SQL syntax
        final String CREATE_TABLE = "CREATE TABLE " + RecipeContract.RecipeEntry.TABLE_NAME + " (" +
                RecipeContract.RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                RecipeContract.RecipeEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                RecipeContract.RecipeEntry.COLUMN_INGREDIENT + " TEXT NOT NULL, " +
                RecipeContract.RecipeEntry.COLUMN_QUANTITY + " TEXT NOT NULL, " +
                RecipeContract.RecipeEntry.COLUMN_MEASURE + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + RecipeContract.RecipeEntry.TABLE_NAME);
        onCreate(db);
    }
}
