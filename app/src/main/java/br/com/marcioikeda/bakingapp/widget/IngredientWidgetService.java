package br.com.marcioikeda.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import br.com.marcioikeda.bakingapp.R;
import br.com.marcioikeda.bakingapp.data.RecipeDataSource;
import br.com.marcioikeda.bakingapp.data.RecipeRepository;
import br.com.marcioikeda.bakingapp.model.Recipe;

/**
 * Created by marcio.ikeda on 17/01/2018.
 */

public class IngredientWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class IngredientRemoteViewsFactory implements RemoteViewsFactory {
        private Context mContext;
        private int mAppWidgetId;
        private int mRecipeId;
        private Recipe mRecipe;

        public IngredientRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            mRecipeId = IngredientWidgetConfigureActivity.loadRecipePref(context, mAppWidgetId);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            RecipeRepository repos = RecipeRepository.getInstance(mContext);
            repos.getRecipe(mRecipeId, new RecipeDataSource.GetRecipeCallBack() {
                @Override
                public void onRecipeLoaded(Recipe recipe) {
                    mRecipe = recipe;
                }

                @Override
                public void onDataNotAvailable() {
                }
            });
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return mRecipe != null ? mRecipe.getIngredients().size(): 0 ;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            // Construct a remote views item based on the app widget item XML file,
            // and set the text based on the position.
            RemoteViews views = new RemoteViews(mContext.getPackageName(),
                    R.layout.ingredient_widget_item_list);
            views.setTextViewText(R.id.tv_ingredient, mRecipe.getIngredients().get(i).getIngredient());
            // Return the remote views object.
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return mRecipe.getIngredients().get(i).getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
