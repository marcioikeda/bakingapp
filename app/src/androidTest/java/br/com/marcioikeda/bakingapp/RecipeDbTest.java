package br.com.marcioikeda.bakingapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import br.com.marcioikeda.bakingapp.data.RecipeDAO;
import br.com.marcioikeda.bakingapp.data.RecipeDatabase;
import br.com.marcioikeda.bakingapp.model.Recipe;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RecipeDbTest {

    private RecipeDAO mRecipeDAO;
    private RecipeDatabase mDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, RecipeDatabase.class).build();
        mRecipeDAO = mDb.getRecipeDao();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void loadRecipeFromAssets() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        AssetManager assets = appContext.getAssets();
        InputStream in = assets.open("recipe.json");
        Recipe recipe = null;
        try {
            if (in != null) {
                Gson gson = new Gson();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                recipe = gson.fromJson(reader, Recipe.class);
            }
        } catch (final Exception e) {
            fail("Failed to load gson recipe");
        }

        if (recipe != null) {
            List<Recipe> list = new ArrayList<Recipe>();
            list.add(recipe);
            mRecipeDAO.insertRecipes(list);
        }

        List<Recipe> recipes = mRecipeDAO.getRecipes();
        Recipe recipeFromDb = recipes.get(0);
        assertNotNull(recipeFromDb);
        assertEquals(recipeFromDb.getName(), "Nutella Pie");
    }
}
