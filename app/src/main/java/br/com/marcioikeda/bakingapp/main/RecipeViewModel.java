package br.com.marcioikeda.bakingapp.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.widget.Toast;

import java.util.List;

import br.com.marcioikeda.bakingapp.data.RecipeDataSource;
import br.com.marcioikeda.bakingapp.data.RecipeRepository;
import br.com.marcioikeda.bakingapp.model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by marcio.ikeda on 01/12/2017.
 */

public class RecipeViewModel extends AndroidViewModel {

    private MutableLiveData<List<Recipe>> mRecipes;
    private RecipeRepository repos;

    public RecipeViewModel(Application application) {
        super(application);
        repos = RecipeRepository.getInstance(application);
    }

    public LiveData<List<Recipe>> getRecipes() {
        if (mRecipes == null) {
            mRecipes = new MutableLiveData<>();
            loadRecipes(false);
        }
        return mRecipes;
    }

    private void loadRecipes(boolean forceUpdate) {

        if (forceUpdate) {
            repos.refreshRecipes();
        }

        repos.getRecipes(new RecipeDataSource.LoadRecipesCallBack() {
            @Override
            public void onRecipesLoaded(List<Recipe> recipes) {
                mRecipes.setValue(recipes);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

}
