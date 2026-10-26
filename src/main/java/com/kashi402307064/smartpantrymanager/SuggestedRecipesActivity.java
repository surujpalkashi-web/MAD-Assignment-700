package com.kashi402307064.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.kashi402307064.smartpantrymanager.adapter.RecipeAdapter;
import com.kashi402307064.smartpantrymanager.db.PantryDataSource;
import com.kashi402307064.smartpantrymanager.model.PantryItem;
import com.kashi402307064.smartpantrymanager.model.Recipe;
import com.kashi402307064.smartpantrymanager.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows recipes the user can make from the current pantry (strict matches),
 * plus an optional Almost There tab for recipes missing one ingredient.
 */
public class SuggestedRecipesActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    private PantryDataSource dataSource;
    private RecipeAdapter adapter;
    private TextView tvMatchSummary;
    private TextView tvEmpty;
    private RecyclerView recyclerRecipes;

    private List<Recipe> readyList = new ArrayList<>();
    private List<Recipe> almostList = new ArrayList<>();
    private boolean showingReady = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dataSource = new PantryDataSource(this);
        dataSource.open();

        // Ensure recipes exist the first time this screen is opened
        new RecipeSeeder(dataSource).seedIfNeeded();

        tvMatchSummary = findViewById(R.id.tvMatchSummary);
        tvEmpty = findViewById(R.id.tvEmpty);
        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggleGroup);
        MaterialButton btnReady = findViewById(R.id.btnReady);
        MaterialButton btnAlmost = findViewById(R.id.btnAlmost);

        adapter = new RecipeAdapter(recipe -> {
            Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
            intent.putExtra(EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });

        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));
        recyclerRecipes.setAdapter(adapter);

        btnReady.setChecked(true);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            showingReady = checkedId == R.id.btnReady;
            refreshList();
        });

        loadMatches();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMatches();
    }

    private void loadMatches() {
        List<PantryItem> pantry = dataSource.getAllPantryItems();
        List<Recipe> allRecipes = dataSource.getAllRecipes();

        RecipeMatcher matcher = new RecipeMatcher();
        readyList = matcher.findStrictMatches(allRecipes, pantry);
        almostList = matcher.findAlmostThere(allRecipes, pantry);

        tvMatchSummary.setText(
                readyList.size() + " ready to cook | " + almostList.size() + " almost there");

        refreshList();
    }

    private void refreshList() {
        List<Recipe> toShow = showingReady ? readyList : almostList;
        adapter.setShowMissingHint(!showingReady);
        adapter.setRecipes(toShow);

        boolean empty = toShow == null || toShow.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerRecipes.setVisibility(empty ? View.GONE : View.VISIBLE);

        if (empty) {
            if (showingReady) {
                tvEmpty.setText("No recipes match your pantry yet - add more ingredients");
            } else {
                tvEmpty.setText("No almost-there recipes right now");
            }
        }
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }
}
