package br.com.marcioikeda.bakingapp.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import br.com.marcioikeda.bakingapp.model.Recipe;

/**
 * Created by marcio.ikeda on 11/12/2017.
 */

@Dao
public interface RecipeDAO {

    @Insert
    public List<Long> insertRecipes(List<Recipe> recipes);

    @Query("SELECT * FROM recipes")
    public List<Recipe> getRecipes();

    @Query("DELETE FROM recipes")
    public void deleteAllRecipes();

    @Query("SELECT * FROM recipes WHERE id IS :arg0")
    public Recipe getRecipeWithid(int arg0);
}
