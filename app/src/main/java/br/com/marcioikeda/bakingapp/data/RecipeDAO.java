package br.com.marcioikeda.bakingapp.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import br.com.marcioikeda.bakingapp.model.Ingredient;
import br.com.marcioikeda.bakingapp.model.Recipe;
import br.com.marcioikeda.bakingapp.model.Step;

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
    public Recipe getRecipe(int arg0);

    @Insert
    public List<Long> insertSteps(List<Step> steps);

    @Query("SELECT * FROM steps WHERE idFk is :arg0")
    public List<Step> getSteps(int arg0);

    @Query("DELETE FROM steps")
    public void deleteAllSteps();

    @Insert
    public List<Long> insertIngredients(List<Ingredient> ingredients);

    @Query("SELECT * FROM ingredients WHERE idFk is :arg0")
    public List<Ingredient> getIngredients(int arg0);

    @Query("DELETE FROM ingredients")
    public void deleteAllIngredients();
}
