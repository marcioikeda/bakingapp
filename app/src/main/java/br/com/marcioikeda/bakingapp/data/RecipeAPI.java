package br.com.marcioikeda.bakingapp.data;

import java.util.List;

import br.com.marcioikeda.bakingapp.model.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by marcio.ikeda on 27/11/2017.
 */

public interface RecipeAPI {

    @GET("android-baking-app-json")
    Call<List<Recipe>> getRecipes();
}
