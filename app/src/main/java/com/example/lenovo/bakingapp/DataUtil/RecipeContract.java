package com.example.lenovo.bakingapp.DataUtil;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by LENOVO on 7/19/2018.
 */

public class RecipeContract {

    // The authority using which your code will know which Content Provider to access
    public static final String AUTHORITY = "com.example.lenovo.bakingapp";

    // Define the base content URI: "content://" <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths to access data in this contract
    public static final String RECIPE_PATH = "recipe";

    public static final class RecipeEntry implements BaseColumns{

        // build the final Uri to access the database
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(RECIPE_PATH).build();

        // Recipe table names and column names
        public static final String TABLE_NAME = "recipe";

        // Column names details
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_INGREDIENT = "ingredient";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_MEASURE = "measure";
    }
}
