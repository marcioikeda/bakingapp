package br.com.marcioikeda.bakingapp.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import br.com.marcioikeda.bakingapp.R;
import br.com.marcioikeda.bakingapp.model.Recipe;
import br.com.marcioikeda.bakingapp.recipe.RecipeActivity;

import static br.com.marcioikeda.bakingapp.recipe.RecipeActivity.KEY_EXTRA_RECIPE;

public class MainActivity extends AppCompatActivity implements RecipeListAdapter.ListItemClickListener {

    private RecipeListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListAdapter = new RecipeListAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.rv_main);
        recyclerView.setAdapter(mListAdapter);
        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        RecipeViewModel viewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        viewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                progressBar.setVisibility(View.GONE);
                mListAdapter.setRecipes(recipes);
            }
        });

    }

    @Override
    public void onListItemClick(int recipeId) {
        Intent intent = new Intent(this, RecipeActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(KEY_EXTRA_RECIPE, recipeId);
        intent.putExtras(extras);
        startActivity(intent);
    }

}
