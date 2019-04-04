package com.example.lenovo.bakingapp.ViewUtils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lenovo.bakingapp.FragmentUtil.DetailFragment;
import com.example.lenovo.bakingapp.R;
import com.example.lenovo.bakingapp.RecipeDetails;

import java.util.ArrayList;

/**
 * Created by LENOVO on 7/24/2018.
 */

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

    //Define class variables
    private RecipeDetails recipeDetails;
    private DetailFragment.OnClickListItem mClickHandler;

    public DetailAdapter(RecipeDetails recipe, DetailFragment.OnClickListItem item){
        recipeDetails = recipe;
        notifyDataSetChanged();
        mClickHandler = item;
    }

    @Override
    public DetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForTextItem = R.layout.recipe_steps;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForTextItem, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailAdapter.ViewHolder holder, int position) {
        int step = position + 1;
        String temp = "See step " + Integer.toString(step);
        holder.mTextView.setText(temp);
    }

    @Override
    public int getItemCount() {
        if(recipeDetails.getPerformSteps().size() == 0){
            return 0;
        } else {
            return recipeDetails.getPerformSteps().size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_step_number);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    mClickHandler.onItemSelected(position);
                }
            });
        }
    }
}
