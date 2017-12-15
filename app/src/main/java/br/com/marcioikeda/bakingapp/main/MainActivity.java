package br.com.marcioikeda.bakingapp.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;
import java.util.ListIterator;

import br.com.marcioikeda.bakingapp.R;
import br.com.marcioikeda.bakingapp.model.Recipe;
import br.com.marcioikeda.bakingapp.recipe.RecipeActivity;

import static br.com.marcioikeda.bakingapp.recipe.RecipeActivity.KEY_EXTRA_RECIPE;

public class MainActivity extends AppCompatActivity implements RecipeListAdapter.ListItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RecipeListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListAdapter = new RecipeListAdapter(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mRecyclerView.setAdapter(mListAdapter);

        /*
        if (Util.isSW600(this)) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }
        */

        RecipeViewModel viewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        viewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                ListIterator<Recipe> it = recipes.listIterator();
                while(it.hasNext()) {
                    Log.d(TAG, it.next().getName());
                }
                mListAdapter.setRecipes(recipes);
            }
        });

    }

    @Override
    public void onListItemClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelable(KEY_EXTRA_RECIPE, recipe);
        intent.putExtras(extras);
        startActivity(intent);
    }

}
