package com.kashi402307064.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kashi402307064.smartpantrymanager.db.PantryDataSource;
import com.kashi402307064.smartpantrymanager.model.Recipe;
import com.kashi402307064.smartpantrymanager.model.RecipeIngredient;

/**
 * Shows full ingredients and method for a selected recipe.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private PantryDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        dataSource = new PantryDataSource(this);
        dataSource.open();

        long recipeId = getIntent().getLongExtra(SuggestedRecipesActivity.EXTRA_RECIPE_ID, -1);
        if (recipeId < 0) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Recipe recipe = dataSource.getRecipeById(recipeId);
        if (recipe == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvTitle = findViewById(R.id.tvRecipeTitle);
        TextView tvIngredients = findViewById(R.id.tvIngredients);
        TextView tvMethod = findViewById(R.id.tvMethod);
        Button btnBack = findViewById(R.id.btnBack);

        tvTitle.setText(recipe.getName());

        StringBuilder ingredientsText = new StringBuilder();
        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ing : recipe.getIngredients()) {
                ingredientsText.append("\u2022 ");
                if (ing.getQuantity() > 0) {
                    ingredientsText.append(formatQty(ing.getQuantity()));
                    if (ing.getUnit() != null && !ing.getUnit().isEmpty()) {
                        ingredientsText.append(" ").append(ing.getUnit());
                    }
                    ingredientsText.append(" ");
                }
                ingredientsText.append(ing.getName()).append("\n");
            }
        }
        tvIngredients.setText(ingredientsText.toString().trim());

        StringBuilder methodText = new StringBuilder();
        if (recipe.getSteps() != null) {
            int stepNum = 1;
            for (String step : recipe.getSteps()) {
                methodText.append(stepNum).append(". ").append(step).append("\n\n");
                stepNum++;
            }
        }
        tvMethod.setText(methodText.toString().trim());

        btnBack.setOnClickListener(v -> finish());
    }

    private String formatQty(double qty) {
        if (qty == Math.floor(qty)) {
            return String.valueOf((long) qty);
        }
        return String.valueOf(qty);
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }
}
