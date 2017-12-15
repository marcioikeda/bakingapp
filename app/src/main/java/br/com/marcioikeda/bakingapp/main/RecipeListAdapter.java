package br.com.marcioikeda.bakingapp.main;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.marcioikeda.bakingapp.R;
import br.com.marcioikeda.bakingapp.model.Recipe;

/**
 * Created by marcio.ikeda on 08/12/2017.
 */

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>{

    private List<Recipe> mRecipes;
    private final ListItemClickListener mListener;

    public interface ListItemClickListener {
        void onListItemClick(Recipe recipe);
    }

    public RecipeListAdapter(ListItemClickListener listener) {
        mListener = listener;
    }

    public void setRecipes(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    public Recipe getRecipe(int position) {
        return mRecipes.get(position);
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.card_recipe;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = mRecipes.get(position);
        holder.tvRecipeName.setText(recipe.getName());
        holder.tvServings.setText(holder.tvServings.getContext().getString(R.string.servings, recipe.getServings()));
        if (!TextUtils.isEmpty(recipe.getImage())) {
            Picasso.with(holder.ivImage.getContext())
                    .load(recipe.getImage())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return mRecipes != null ? mRecipes.size() : 0;
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeName;
        TextView tvServings;
        ImageView ivImage;
        TextView tvAction;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            tvRecipeName = (TextView) itemView.findViewById(R.id.tv_title);
            tvServings = (TextView) itemView.findViewById(R.id.tv_subtitle);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_media);
            tvAction = (TextView) itemView.findViewById(R.id.tv_action1);
            tvAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onListItemClick(mRecipes.get(getAdapterPosition()));
                }
            });
        }
    }
}
