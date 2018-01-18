package br.com.marcioikeda.bakingapp.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import br.com.marcioikeda.bakingapp.model.Ingredient;
import br.com.marcioikeda.bakingapp.model.Recipe;
import br.com.marcioikeda.bakingapp.model.Step;

/**
 * Created by marcio.ikeda on 11/12/2017.
 */

@Database(entities={Recipe.class, Ingredient.class, Step.class}, version=2)
public abstract class RecipeDatabase extends RoomDatabase {
    private static RecipeDatabase INSTANCE;

    public abstract RecipeDAO getRecipeDao();

    private static final Object sLock = new Object();

    public static RecipeDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        RecipeDatabase.class, "recipes.db")
                        .fallbackToDestructiveMigration() // TODO remove before release
                        .build();
            }
            return INSTANCE;
        }
    }
}
