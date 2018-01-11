package br.com.marcioikeda.bakingapp.data;

import android.support.annotation.NonNull;

import java.util.List;

import br.com.marcioikeda.bakingapp.model.Recipe;

/**
 * Created by marcio.ikeda on 11/01/2018.
 */

public interface RecipeDataSource {

    interface LoadRecipesCallBack {
        void onRecipesLoaded(List<Recipe> recipes);
        void onDataNotAvailable();
    }

    interface GetRecipeCallBack {
        void onRecipeLoaded(Recipe recipe);
        void onDataNotAvailable();
    }

    void getRecipes(@NonNull LoadRecipesCallBack callback);

    void getRecipe(@NonNull GetRecipeCallBack callback);

    void saveRecipes(@NonNull List<Recipe> recipes);

    void deleteAllRecipes();
}
