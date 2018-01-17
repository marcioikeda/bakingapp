package br.com.marcioikeda.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;


import java.util.List;

import javax.sql.DataSource;

import br.com.marcioikeda.bakingapp.R;
import br.com.marcioikeda.bakingapp.data.RecipeDataSource;
import br.com.marcioikeda.bakingapp.data.RecipeRepository;
import br.com.marcioikeda.bakingapp.main.RecipeViewModel;
import br.com.marcioikeda.bakingapp.model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link IngredientWidgetConfigureActivity IngredientWidgetConfigureActivity}
 */
public class IngredientWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        int recipePref = IngredientWidgetConfigureActivity.loadRecipePref(context, appWidgetId);

        RecipeRepository repos = RecipeRepository.getInstance(context);
        repos.getRecipe(recipePref, new RecipeDataSource.GetRecipeCallBack() {
            @Override
            public void onRecipeLoaded(Recipe recipe) {
                //RemoteViews Service needed to provide adapter for ListView
                Intent svcIntent = new Intent(context, IngredientWidgetService.class);
                //passing app widget id to that RemoteViews Service
                svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                //setting a unique Uri to the intent
                //don't know its purpose to me right now
                svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
                //setting adapter to listview of the widget
                // Construct the RemoteViews object
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);
                views.setRemoteAdapter(R.id.listView, svcIntent);
                views.setTextViewText(R.id.tv_title, recipe.getName());
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            IngredientWidgetConfigureActivity.deleteRecipePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

