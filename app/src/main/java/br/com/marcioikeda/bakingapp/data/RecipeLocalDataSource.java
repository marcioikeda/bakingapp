package br.com.marcioikeda.bakingapp.data;

import android.support.annotation.NonNull;

import java.util.List;

import br.com.marcioikeda.bakingapp.model.Recipe;
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
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteAllRecipes() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mRecipesDao.deleteAllRecipes();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
