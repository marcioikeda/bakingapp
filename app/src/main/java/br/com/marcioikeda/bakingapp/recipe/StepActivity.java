package br.com.marcioikeda.bakingapp.recipe;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.Iterator;
import java.util.List;

import br.com.marcioikeda.bakingapp.R;
import br.com.marcioikeda.bakingapp.model.Step;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeActivity}.
 */
public class StepActivity extends AppCompatActivity {

    private ViewPager mPager;
    private StepPagerAdapter mPagerAdapter;

    private int recipeId;
    private int stepId;
    private int mCurrentItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        RecipeDetailViewModel mViewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recipeId = getIntent().getIntExtra(StepFragment.RECIPE_ID, -1);
        stepId = getIntent().getIntExtra(StepFragment.STEP_ID, -1);

        mPager = findViewById(R.id.view_pager);
        if (recipeId < 0 || stepId < 0) {
            Snackbar.make(mPager, R.string.message_step_notfound, Snackbar.LENGTH_SHORT);
            Intent intent = new Intent(this, RecipeActivity.class);
            intent.putExtra(RecipeActivity.KEY_EXTRA_RECIPE, getIntent().getIntExtra(StepFragment.RECIPE_ID, -1));
            navigateUpTo(intent);
        }

        mViewModel.getRecipe(recipeId).observe(this, recipe -> {
            mPagerAdapter = new StepPagerAdapter(getSupportFragmentManager(), recipe.getSteps(), recipe.getId());
            mPager.setAdapter(mPagerAdapter);
            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    StepFragment cachedFragmentLeaving = mPagerAdapter.getHoldedItem(mCurrentItem);
                    if (cachedFragmentLeaving != null) {
                        cachedFragmentLeaving.loseVisibility();
                    }
                    mCurrentItem = position;
                    StepFragment cachedFragmentEntering = mPagerAdapter.getHoldedItem(mCurrentItem);
                    if (cachedFragmentEntering != null) {
                        cachedFragmentEntering.gainVisibility();
                    }

                    getSupportActionBar().setTitle(recipe.getSteps().get(position).getShortDescription());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            List<Step> steps = recipe.getSteps();
            int index = 0;
            for (Iterator<Step> it = steps.iterator(); it.hasNext(); index++) {
                Step step = it.next();
                if (step.getId() == stepId) {
                    mPager.setCurrentItem(index);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent intent = new Intent(this, RecipeActivity.class);
            intent.putExtra(RecipeActivity.KEY_EXTRA_RECIPE, getIntent().getIntExtra(StepFragment.RECIPE_ID, -1));
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class StepPagerAdapter extends FragmentStatePagerAdapter {
        private SparseArray<StepFragment> mFragmentsHolded = new SparseArray<>();
        private List<Step> mSteps;


        public StepPagerAdapter(FragmentManager fm, List<Step> steps, int recipeId) {
            super(fm);
            mSteps = steps;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle arguments = new Bundle();
            arguments.putInt(StepFragment.RECIPE_ID, recipeId);
            arguments.putInt(StepFragment.STEP_ID, mSteps.get(position).getId());
            StepFragment fragment = new StepFragment();
            fragment.setArguments(arguments);
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            StepFragment f = (StepFragment) super.instantiateItem(container, position);
            mFragmentsHolded.put(position, f);
            return f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mFragmentsHolded.remove(position);
            super.destroyItem(container, position, object);
        }

        public StepFragment getHoldedItem(int position) {
            return mFragmentsHolded.get(position);
        }

        @Override
        public int getCount() {
            return mSteps == null ? 0: mSteps.size();
        }
    }
}
