package br.com.marcioikeda.bakingapp.recipe;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.marcioikeda.bakingapp.R;
import br.com.marcioikeda.bakingapp.model.Ingredient;
import br.com.marcioikeda.bakingapp.model.Recipe;
import br.com.marcioikeda.bakingapp.model.Step;


/**
 * Created by marcio.ikeda on 08/12/2017.
 */

public class IngredientStepAdapter extends RecyclerView.Adapter {

    private Recipe mRecipe;
    private List<Object> items;
    private Context mContext;
    private int selectedStepPosition = 0;
    private int selectedStepId;
    private boolean isTwoPane;

    private final int SERVINGS = 0;
    private final int TITLE = 1;
    private final int INGREDIENT = 2;
    private final int STEP = 3;

    private ListItemClickListener mListener;

    public interface ListItemClickListener {
        void onStepItemClick(int recipeId, int stepId);
    }

    public IngredientStepAdapter(Context context, ListItemClickListener listener, boolean twoPane) {
        mContext = context;
        mListener = listener;
        isTwoPane = twoPane;
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
        syncItems();
    }

    private void syncItems() {
        items = new ArrayList<>();
        items.add(mContext.getString(R.string.servings));
        items.add(mContext.getString(R.string.ingredients_title));
        items.addAll(mRecipe.getIngredients());
        items.add(mContext.getString(R.string.steps_title));
        if (isTwoPane)
            selectedStepPosition = items.size();
        items.addAll(mRecipe.getSteps());
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return SERVINGS;
        } else if (items.get(position) instanceof String) {
            String string = (String) items.get(position);
            if (string.equals(R.string.ingredients_title)) {
                return STEP;
            }
            return TITLE;
        } else if (items.get(position) instanceof Ingredient) {
            return INGREDIENT;
        } else if (items.get(position) instanceof Step) {
            return STEP;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case SERVINGS:
                View v1 = inflater.inflate(R.layout.content_servings, parent, false);
                viewHolder = new TitleViewHolder(v1);
                break;
            case TITLE:
                View v2 = inflater.inflate(R.layout.content_subtitle, parent, false);
                viewHolder = new TitleViewHolder(v2);
                break;
            case INGREDIENT:
                View v3 = inflater.inflate(R.layout.content_ingredient, parent, false);
                viewHolder = new IngredientViewHolder(v3);
                break;
            case STEP:
                View v4 = inflater.inflate(R.layout.content_step, parent, false);
                viewHolder = new StepViewHolder(v4);
                break;
            default:
                View v5 = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new RecyclerView.ViewHolder(v5) {};
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder == null) {
            return;
        }

        switch (holder.getItemViewType()) {
            case SERVINGS:
                TitleViewHolder viewHolder0 = (TitleViewHolder) holder;
                viewHolder0.textView.setText(mContext.getString(R.string.servings, mRecipe.getServings()));
                break;
            case TITLE:
                TitleViewHolder viewHolder1 = (TitleViewHolder) holder;
                String title = (String) items.get(position);
                viewHolder1.textView.setText(title);
                break;
            case INGREDIENT:
                IngredientViewHolder viewHolder2 = (IngredientViewHolder) holder;
                Ingredient ingredient = (Ingredient) items.get(position);
                viewHolder2.tvIngredient.setText(String.format("%s %s %s", ingredient.getIngredient(), ingredient.getQuantity(), ingredient.getMeasure()));
                break;
            case STEP:
                StepViewHolder viewHolder3 = (StepViewHolder) holder;
                Step step = (Step) items.get(position);
                viewHolder3.textView.setText(step.getShortDescription());
                viewHolder3.stepContainer.setTag(step.getId());
                viewHolder3.stepContainer.setSelected(selectedStepPosition == position);
                if (selectedStepPosition == position) {
                    this.selectedStepId = step.getId();
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public int getSelectedStepId() {
        return selectedStepId;
    }

    public void setSelectedStepId(int selectedStepId) {
        //find step position and mark as selected
        for (Object item: items) {
            if (item instanceof Step) {
                if (((Step) item).getId() == selectedStepId) {
                    // Redraw the old selection and the new
                    notifyItemChanged(selectedStepPosition);
                    selectedStepPosition = items.indexOf(item);
                    notifyItemChanged(selectedStepPosition);
                }
            }
        }
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public TitleViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_list_subtitle);
        }
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredient;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            tvIngredient = itemView.findViewById(R.id.tv_ingredient);
        }
    }

    class StepViewHolder extends RecyclerView.ViewHolder {
        View stepContainer;
        TextView textView;

        public StepViewHolder(View itemView) {
            super(itemView);
            stepContainer = itemView.findViewById(R.id.step_container);
            textView = itemView.findViewById(R.id.tv_step_title);
            stepContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isTwoPane) {
                        // Redraw the old selection and the new
                        notifyItemChanged(selectedStepPosition);
                        selectedStepPosition = getLayoutPosition();
                        notifyItemChanged(selectedStepPosition);
                    }
                    int id = (int) stepContainer.getTag();
                    mListener.onStepItemClick(mRecipe.getId(), id);
                }
            });
        }
    }
}
