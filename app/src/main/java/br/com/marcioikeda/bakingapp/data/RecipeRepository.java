package br.com.marcioikeda.bakingapp.data;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.marcioikeda.bakingapp.model.Recipe;
import br.com.marcioikeda.bakingapp.util.AppExecutors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by marcio.ikeda on 27/11/2017.
 */

public class RecipeRepository implements RecipeDataSource{

    private static volatile RecipeRepository sInstance;

    private final RecipeLocalDataSource mRecipeLocalDataSource;

    private final RecipeRemoteDataSource mRecipeRemoteDataSource;

    private Map<Integer, Recipe> mCachedRecipes;

    private boolean mCacheIsDirty = true;

    public static RecipeRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (RecipeRepository.class) {
                if (sInstance == null) {
                    sInstance = new RecipeRepository(context);
                }
            }
        }
        return sInstance;
    }

    private RecipeRepository(Context context) {
        RecipeDatabase database = RecipeDatabase.getInstance(context);
        mRecipeLocalDataSource = RecipeLocalDataSource.getInstance(new AppExecutors(), database.getRecipeDao());
        mRecipeRemoteDataSource = RecipeRemoteDataSource.getInstance();
    }

    @Override
    public void getRecipes(@NonNull RecipeDataSource.LoadRecipesCallBack callBack) {
        // Respond immediately with cache if available and not dirty
        if (mCachedRecipes != null && !mCacheIsDirty) {
            callBack.onRecipesLoaded(new ArrayList<>(mCachedRecipes.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getRecipesFromRemote(callBack);
        } else {
            // ---- NOT ---Query the local storage if available. If not, query the network.

            // Query first the network for new values, if not available, query local.
            // If on cache, this code is not reached, so when the app launches it reaches here.
            mRecipeRemoteDataSource.getRecipes(new LoadRecipesCallBack() {
                @Override
                public void onRecipesLoaded(List<Recipe> recipes) {
                    refreshCache(recipes);
                    refreshLocalDataSource(recipes);
                    callBack.onRecipesLoaded(recipes);
                }

                @Override
                public void onDataNotAvailable() {
                    mRecipeLocalDataSource.getRecipes(new LoadRecipesCallBack() {
                        @Override
                        public void onRecipesLoaded(List<Recipe> recipes) {
                            refreshCache(recipes);
                            callBack.onRecipesLoaded(recipes);
                        }

                        @Override
                        public void onDataNotAvailable() {
                            callBack.onDataNotAvailable();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void getRecipe(int id, @NonNull GetRecipeCallBack callback) {
        // Respond immediately with cache if available
        if (mCachedRecipes != null && !mCachedRecipes.isEmpty()) {
            Recipe recipe = mCachedRecipes.get(id);
            if (recipe != null) {
                callback.onRecipeLoaded(recipe);
                return;
            }
        }

        // Is the task in the local data source? If not, query the network.
        mRecipeLocalDataSource.getRecipe(id, new GetRecipeCallBack() {
            @Override
            public void onRecipeLoaded(Recipe recipe) {
                callback.onRecipeLoaded(recipe);
            }

            @Override
            public void onDataNotAvailable() {
                mRecipeRemoteDataSource.getRecipes(new LoadRecipesCallBack() {
                    @Override
                    public void onRecipesLoaded(List<Recipe> recipes) {
                        refreshCache(recipes);
                        refreshLocalDataSource(recipes);
                        if (mCachedRecipes != null) {
                            Recipe recipe = mCachedRecipes.get(id);
                            if (recipe != null) {
                                callback.onRecipeLoaded(recipe);
                            } else {
                                callback.onDataNotAvailable();
                            }
                        }
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void saveRecipes(@NonNull List<Recipe> recipes) {
        mRecipeLocalDataSource.saveRecipes(recipes);
    }

    @Override
    public void deleteAllRecipes() {
        mRecipeLocalDataSource.deleteAllRecipes();
        if (mCachedRecipes == null) {
            mCachedRecipes = new LinkedHashMap<>();
        }
        mCachedRecipes.clear();
    }

    public void refreshRecipes() {
        mCacheIsDirty = true;
    }

    private void getRecipesFromRemote(@NonNull LoadRecipesCallBack callback) {
        mRecipeRemoteDataSource.getRecipes(new LoadRecipesCallBack() {
            @Override
            public void onRecipesLoaded(List<Recipe> recipes) {
                refreshCache(recipes);
                refreshLocalDataSource(recipes);
                callback.onRecipesLoaded(recipes);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Recipe> recipes) {
        if (mCachedRecipes == null) {
            mCachedRecipes = new LinkedHashMap<>();
        }
        mCachedRecipes.clear();
        for (Recipe recipe: recipes) {
            mCachedRecipes.put(recipe.getId(), recipe);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Recipe> recipes) {
        mRecipeLocalDataSource.deleteAllRecipes();
        mRecipeLocalDataSource.saveRecipes(recipes);
    }
}
