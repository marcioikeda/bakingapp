package br.com.marcioikeda.bakingapp.data;

import android.support.annotation.NonNull;

import java.util.List;

import br.com.marcioikeda.bakingapp.model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by marcio.ikeda on 11/01/2018.
 */

public class RecipeRemoteDataSource {
    static final String BASE_URL = "http://go.udacity.com";

    private static RecipeRemoteDataSource INSTANCE;

    private RecipeAPI service;

    public static RecipeRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RecipeRemoteDataSource();
        }
        return INSTANCE;
    }

    private RecipeRemoteDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(RecipeAPI.class);
    }

    public void getRecipes(@NonNull final RecipeDataSource.LoadRecipesCallBack callback) {
        Call<List<Recipe>> call = service.getRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    callback.onRecipesLoaded(response.body());
                } else {
                    callback.onDataNotAvailable();
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                callback.onDataNotAvailable();
            }
        });
    }


}
