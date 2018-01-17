package br.com.marcioikeda.bakingapp.data;

import android.support.annotation.NonNull;

import java.util.List;

import br.com.marcioikeda.bakingapp.model.Ingredient;
import br.com.marcioikeda.bakingapp.model.Recipe;
import br.com.marcioikeda.bakingapp.model.Step;
import br.com.marcioikeda.bakingapp.util.AppExecutors;

/**
 * Created by marcio.ikeda on 11/01/2018.
 */

public class RecipeLocalDataSource implements RecipeDataSource{

    private static volatile RecipeLocalDataSource INSTANCE;

    private RecipeDAO mRecipesDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private RecipeLocalDataSource(@NonNull AppExecutors appExecutors,
                                 @NonNull RecipeDAO recipesDAO) {
        mAppExecutors = appExecutors;
        mRecipesDao = recipesDAO;
    }

    public static RecipeLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                   @NonNull  RecipeDAO recipesDAO) {
        if (INSTANCE == null) {
            synchronized (RecipeLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RecipeLocalDataSource(appExecutors, recipesDAO);
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public void getRecipes(@NonNull LoadRecipesCallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Recipe> recipes = mRecipesDao.getRecipes();
                for (Recipe recipe: recipes) {
                    List<Step> steps = mRecipesDao.getSteps(recipe.getId());
                    List<Ingredient> ingredients = mRecipesDao.getIngredients(recipe.getId());
                    recipe.setSteps(steps);
                    recipe.setIngredients(ingredients);
                }
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (recipes != null) {
                            callback.onRecipesLoaded(recipes);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getRecipe(int id, @NonNull GetRecipeCallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Recipe recipe = mRecipesDao.getRecipe(id);
                List<Step> steps = mRecipesDao.getSteps(id);
                List<Ingredient> ingredients = mRecipesDao.getIngredients(id);
                recipe.setSteps(steps);
                recipe.setIngredients(ingredients);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (recipe != null) {
                            callback.onRecipeLoaded(recipe);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRecipes(@NonNull List<Recipe> recipes) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mRecipesDao.insertRecipes(recipes);

                for (Recipe recipe: recipes) {
                    for (Step step : recipe.getSteps()) {
                        step.setIdFk(recipe.getId());
                    }
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        ingredient.setIdFk(recipe.getId());
                    }
                    mRecipesDao.insertSteps(recipe.getSteps());
                    mRecipesDao.insertIngredients(recipe.getIngredients());
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteAllRecipes() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mRecipesDao.deleteAllSteps();
                mRecipesDao.deleteAllIngredients();
                mRecipesDao.deleteAllRecipes();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
