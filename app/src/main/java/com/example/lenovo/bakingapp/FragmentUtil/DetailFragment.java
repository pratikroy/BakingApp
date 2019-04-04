package com.example.lenovo.bakingapp.FragmentUtil;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lenovo.bakingapp.R;
import com.example.lenovo.bakingapp.RecipeDetails;
import com.example.lenovo.bakingapp.ViewUtils.DetailAdapter;

import timber.log.Timber;

/**
 * Created by LENOVO on 7/24/2018.
 */

public class DetailFragment extends Fragment {

    private static final String TAG = DetailFragment.class.getSimpleName();

    // define class variables from here
    private RecipeDetails recipeDetails;
    private static final String RECIPE_LIST = "recipe_list";
    // define StringBuilder to complete the ingredients list
    private StringBuilder completeIngredients = new StringBuilder();
    private DetailAdapter mDetailsAdapter;
    private RecyclerView mRecyclerView;
    LinearLayoutManager layoutManagerForSteps =
            new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    private OnClickListItem mCallbacks;


    // mandatory empty constructor for fragment
    public DetailFragment(){
    }

    // define an interface to perform click on Recyclerview
    public interface OnClickListItem{
        public void onItemSelected(int position);
    }

    public void setRecipeDetails(RecipeDetails recipe){
        recipeDetails = recipe;
        Timber.v("setRecipeDetails gets called");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallbacks = (OnClickListItem) context;
        } catch (Exception e){
            throw new ClassCastException(context.toString() +
            " must implement OnClickListItem");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(savedInstanceState != null){
            recipeDetails = savedInstanceState.getParcelable(RECIPE_LIST);
        }

        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);

        // First complete the textview for ingredients list
        TextView mTextView = (TextView) rootView.findViewById(R.id.tv_ingredient_names);
        for(int i = 0; i < recipeDetails.getIngredientNames().size(); i++){
            String temp = i+1 + ". " +
                    recipeDetails.getIngredientNames().get(i).getIngredientName() + "(" +
                    recipeDetails.getIngredientNames().get(i).getQuantity() + " " +
                    recipeDetails.getIngredientNames().get(i).getMeasure() + ") \n";
            completeIngredients.append(temp);
        }
        mTextView.setText(completeIngredients.toString());

        // Now set the RecyclerView to the fragment
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_detail_layout);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManagerForSteps);
        mDetailsAdapter = new DetailAdapter(recipeDetails, mCallbacks);
        mRecyclerView.setAdapter(mDetailsAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECIPE_LIST, recipeDetails);
    }
}
