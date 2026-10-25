package com.kashi402307064.smartpantrymanager.util;

import com.kashi402307064.smartpantrymanager.model.PantryItem;
import com.kashi402307064.smartpantrymanager.model.Recipe;
import com.kashi402307064.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Strict matching: a recipe is suggested only when every required ingredient
 * is present in the pantry with at least the needed quantity.
 * Partial matches are never returned.
 */
public class RecipeMatcher {

    /**
     * @param recipes all recipes in the database
     * @param pantry  current pantry items
     * @return only recipes the user can fully make right now
     */
    public List<Recipe> findStrictMatches(List<Recipe> recipes, List<PantryItem> pantry) {
        List<Recipe> matches = new ArrayList<>();
        Map<String, Double> available = buildPantryMap(pantry);

        for (Recipe recipe : recipes) {
            if (canMakeFully(recipe, available)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    private Map<String, Double> buildPantryMap(List<PantryItem> pantry) {
        Map<String, Double> map = new HashMap<>();
        if (pantry == null) {
            return map;
        }
        for (PantryItem item : pantry) {
            String key = IngredientNormalizer.normalize(
                    item.getNormalizedName() != null && !item.getNormalizedName().isEmpty()
                            ? item.getNormalizedName()
                            : item.getName());
            if (key.isEmpty()) {
                continue;
            }
            double current = map.containsKey(key) ? map.get(key) : 0.0;
            map.put(key, current + item.getQuantity());
        }
        return map;
    }

    private boolean canMakeFully(Recipe recipe, Map<String, Double> available) {
        if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return false;
        }

        for (RecipeIngredient required : recipe.getIngredients()) {
            String key = IngredientNormalizer.normalize(
                    required.getNormalizedName() != null && !required.getNormalizedName().isEmpty()
                            ? required.getNormalizedName()
                            : required.getName());

            if (key.isEmpty()) {
                return false;
            }

            Double have = available.get(key);
            if (have == null || have < required.getQuantity()) {
                return false;
            }
        }
        return true;
    }
}
