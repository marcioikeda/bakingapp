package br.com.marcioikeda.bakingapp.data;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

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

public class RecipeRepository {

    private static volatile RecipeRepository sInstance;

    private final RecipeLocalDataSource mRecipeLocalDataSource;

    private final RecipeRemoteDataSource mRecipeRemoteDataSource;

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

    public void getRecipes(@NonNull RecipeDataSource.LoadRecipesCallBack callBack) {
        // always load from remote for now.
        mRecipeRemoteDataSource.getRecipes(callBack);
    }

}
