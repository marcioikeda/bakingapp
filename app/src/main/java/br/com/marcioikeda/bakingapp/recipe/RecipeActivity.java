package br.com.marcioikeda.bakingapp.recipe;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import br.com.marcioikeda.bakingapp.R;
import br.com.marcioikeda.bakingapp.model.Recipe;

import java.util.List;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeActivity extends AppCompatActivity implements IngredientStepAdapter.ListItemClickListener {

    public static final String KEY_EXTRA_RECIPE = "key_extra_recipe";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private IngredientStepAdapter adapter;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;

    private final static String  KEY_SELECTED_STEP_ID = "key_selected_step_id";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.item_list);
        //toolbar.setTitle(getTitle());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int recipeId = extras.getInt(KEY_EXTRA_RECIPE);
            RecipeDetailViewModel viewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
            viewModel.getRecipe(recipeId).observe(this, new Observer<Recipe>() {
                @Override
                public void onChanged(@Nullable Recipe recipe) {
                    mToolbar.setTitle(recipe.getName());
                    setSupportActionBar(mToolbar);
                    setupRecyclerView(mRecyclerView, recipe);

                    //UI states for twopane:
                    if (mTwoPane) {
                        if (savedInstanceState == null) {
                            setupDefaultFragment(recipe);
                        } else {
                            int selectedStepid = savedInstanceState.getInt(KEY_SELECTED_STEP_ID);
                            adapter.setSelectedStepId(selectedStepid);
                            changeStepFragment(recipe.getId(), selectedStepid);
                        }

                    }
                }
            });
        }


        setSupportActionBar(mToolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SELECTED_STEP_ID, adapter.getSelectedStepId());
        super.onSaveInstanceState(outState);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, Recipe recipe) {
        adapter = new IngredientStepAdapter(this, this, mTwoPane);
        if (recipe != null) {
            adapter.setRecipe(recipe);
        }
        recyclerView.setAdapter(adapter);
    }

    private void setupDefaultFragment(Recipe recipe) {
        changeStepFragment(recipe.getId(), recipe.getSteps().get(0).getId());
    }

    private void changeStepFragment(int recipeId, int stepId) {
        Bundle arguments = new Bundle();
        arguments.putInt(StepFragment.RECIPE_ID, recipeId);
        arguments.putInt(StepFragment.STEP_ID, stepId);
        StepFragment fragment = new StepFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .commit();
    }

    @Override
    public void onStepItemClick(int recipeId, int stepId) {
        if (mTwoPane) {
            changeStepFragment(recipeId, stepId);
        } else {
            Intent intent = new Intent(this, StepActivity.class);
            intent.putExtra(StepFragment.RECIPE_ID, recipeId);
            intent.putExtra(StepFragment.STEP_ID, stepId);
            startActivity(intent);
        }
    }
}
