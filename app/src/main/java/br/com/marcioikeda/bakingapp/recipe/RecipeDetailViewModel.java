package br.com.marcioikeda.bakingapp.recipe;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import java.util.List;

import br.com.marcioikeda.bakingapp.data.RecipeDataSource;
import br.com.marcioikeda.bakingapp.data.RecipeRepository;
import br.com.marcioikeda.bakingapp.model.Recipe;

/**
 * Created by marcio.ikeda on 12/01/2018.
 */

public class RecipeDetailViewModel extends AndroidViewModel{

    public MutableLiveData<Recipe> mRecipe = new MutableLiveData<>();

    private final RecipeRepository repos;

    public RecipeDetailViewModel(Application application) {
        super(application);
        repos = RecipeRepository.getInstance(application);
    }

    public LiveData<Recipe> getRecipe(int id) {
        repos.getRecipe(id, new RecipeDataSource.GetRecipeCallBack() {
            @Override
            public void onRecipeLoaded(Recipe recipe) {
                mRecipe.setValue(recipe);
            }

            @Override
            public void onDataNotAvailable() {
                mRecipe.setValue(null);
            }
        });

        return mRecipe;
    }

}
