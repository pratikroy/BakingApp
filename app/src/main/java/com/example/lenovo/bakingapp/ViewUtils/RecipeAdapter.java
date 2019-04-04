package com.example.lenovo.bakingapp.ViewUtils;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.bakingapp.DataUtil.RecipeContract;
import com.example.lenovo.bakingapp.R;
import com.example.lenovo.bakingapp.RecipeDetails;

import java.util.ArrayList;

/**
 * Created by LENOVO on 7/12/2018.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> {

    private static final String TAG = RecipeAdapter.class.getSimpleName();

    private ArrayList<RecipeDetails> recipeDetailsArrayList = new ArrayList<>();
    private Context mContext;
    private Cursor mCursor = null;
    private String savedRecipeName = null;
    private final String performEntry = "add";
    private final String performDelete = "delete";
    final private RecipeAdapterClickHandler mClickHandler;

    public interface RecipeAdapterClickHandler{
        void onClickRecipe(int position, RecipeDetails recipe);
        void onClickFavouriteButton(int position, RecipeDetails recipe, String action);
    }

    public RecipeAdapter(RecipeAdapterClickHandler clickHandler, Context context){
        mClickHandler = clickHandler;
        mContext = context;
        queryDB();
    }

    public void updateDetails(ArrayList<RecipeDetails> details){
        recipeDetailsArrayList = details;
        notifyDataSetChanged();
    }

    private void queryDB(){

        String[] projection = {RecipeContract.RecipeEntry.COLUMN_NAME};

        try{
            mCursor = mContext.getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    RecipeContract.RecipeEntry._ID + " ASC");
        } catch (Exception e){
            e.printStackTrace();
            Log.v(TAG, "Unable to query DB.");
        }

        if(mCursor != null && mCursor.getCount() != 0){
            mCursor.moveToFirst();
            savedRecipeName =
                    mCursor.getString(mCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NAME));
        }
    }

    @Override
    public RecipeAdapter.RecipeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForTextItem = R.layout.recipe_name;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForTextItem, parent, false);

        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.RecipeAdapterViewHolder holder, int position) {

        holder.mRecipeName.setText(recipeDetailsArrayList.get(position).getRecipeName());
        String servings = "For " + recipeDetailsArrayList.get(position).getServings() + " peoples";
        holder.mServings.setText(servings);
        if(recipeDetailsArrayList.get(position).getRecipeName().equals(savedRecipeName)){
            holder.mButton.setText("DELETE FROM LIST");
        } else {
            holder.mButton.setText("ADD TO LIST");
        }

    }

    @Override
    public int getItemCount() {
        if(recipeDetailsArrayList.size() == 0)
            return 0;
        else
            return recipeDetailsArrayList.size();
    }

    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mRecipeName;
        public TextView mServings;
        public Button mButton;

        public RecipeAdapterViewHolder(View view){
            super(view);
            mRecipeName = (TextView) view.findViewById(R.id.tv_recipe_name);
            mServings = (TextView) view.findViewById(R.id.tv_servings);
            mButton = (Button) view.findViewById(R.id.bv_favourite_btn);
            view.setOnClickListener(this);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // for each click query db
                    queryDB();
                    int position = getAdapterPosition();
                    String action = mButton.getText().toString().toLowerCase();
                    if(action.contains(performDelete)){
                        Log.v(TAG, "Action = " + action);
                        mClickHandler.onClickFavouriteButton(position, recipeDetailsArrayList.get(position), performDelete);
                        mButton.setText("ADD TO LIST");
                    } else if(action.contains(performEntry) && mCursor.getCount() == 0){
                        Log.v(TAG, "Action = " + action);
                        mClickHandler.onClickFavouriteButton(position, recipeDetailsArrayList.get(position), performEntry);
                        mButton.setText("DELETE FROM LIST");
                    } else if(action.contains(performEntry) && mCursor.getCount() != 0){
                        Toast.makeText(mContext, "Delete the previous recipe", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickHandler.onClickRecipe(position, recipeDetailsArrayList.get(position));
        }
    }
}
