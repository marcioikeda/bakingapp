package br.com.marcioikeda.bakingapp.data;

import android.support.annotation.NonNull;

import java.util.List;

import br.com.marcioikeda.bakingapp.model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by marcio.ikeda on 27/11/2017.
 */

public class RecipeRepository {

    static final String BASE_URL = "http://go.udacity.com";
    static final String TAG = "RecipeRepository";

    private static volatile RecipeRepository sInstance;
    private RecipeAPI service;

    public static RecipeRepository getInstance() {
        if (sInstance == null) {
            synchronized (RecipeRepository.class) {
                if (sInstance == null) {
                    sInstance = new RecipeRepository();
                }
            }
        }
        return sInstance;
    }

    private RecipeRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(RecipeAPI.class);
    }

    public void getRecipes(Callback<List<Recipe>> callback) {
        Call<List<Recipe>> call = service.getRecipes();
        call.enqueue(callback);
    }


}
