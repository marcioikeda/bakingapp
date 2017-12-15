package br.com.marcioikeda.bakingapp.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import br.com.marcioikeda.bakingapp.data.RecipeRepository;
import br.com.marcioikeda.bakingapp.model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by marcio.ikeda on 01/12/2017.
 */

public class RecipeViewModel extends ViewModel {

    private MutableLiveData<List<Recipe>> recipes;
    private RecipeRepository repos;

    public RecipeViewModel() {
        repos = RecipeRepository.getInstance();
    }

    public LiveData<List<Recipe>> getRecipes() {
        if (recipes == null) {
            recipes = new MutableLiveData<List<Recipe>>();
            loadRecipes();
        }
        return recipes;
    }

    private void loadRecipes() {
        // Always load from remote for now.

        repos.getRecipes(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    recipes.setValue(response.body());
                } else {
                    //parse error, log, warn the UI?
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                t.printStackTrace();
                //warn the UI?
            }
        });
    }
}
