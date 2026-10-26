package com.kashi402307064.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kashi402307064.smartpantrymanager.R;
import com.kashi402307064.smartpantrymanager.model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of recipes on the Suggested Recipes screen.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final OnRecipeClickListener listener;
    private boolean showMissingHint;

    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public void setRecipes(List<Recipe> items) {
        recipes.clear();
        if (items != null) {
            recipes.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void setShowMissingHint(boolean showMissingHint) {
        this.showMissingHint = showMissingHint;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvSubtitle;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRecipeName);
            tvSubtitle = itemView.findViewById(R.id.tvRecipeSubtitle);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRecipeClick(recipes.get(pos));
                }
            });
        }

        void bind(Recipe recipe) {
            tvName.setText(recipe.getName());

            int count = recipe.getIngredients() != null ? recipe.getIngredients().size() : 0;
            if (showMissingHint && recipe.getMissingSummary() != null
                    && !recipe.getMissingSummary().isEmpty()) {
                tvSubtitle.setText("Missing: " + recipe.getMissingSummary());
            } else {
                tvSubtitle.setText(count + " ingredients - you have everything");
            }
        }
    }
}
