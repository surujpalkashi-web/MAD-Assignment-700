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
 * Partial matches are never returned from findStrictMatches.
 * findAlmostThere is a separate optional list for recipes missing exactly one item.
 */
public class RecipeMatcher {

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

    /**
     * Recipes missing exactly one ingredient (bonus / stretch feature).
     * Each returned recipe has missingSummary set for the list subtitle.
     */
    public List<Recipe> findAlmostThere(List<Recipe> recipes, List<PantryItem> pantry) {
        List<Recipe> almost = new ArrayList<>();
        Map<String, Double> available = buildPantryMap(pantry);

        for (Recipe recipe : recipes) {
            List<String> missing = findMissing(recipe, available);
            if (missing.size() == 1) {
                recipe.setMissingSummary(missing.get(0));
                almost.add(recipe);
            }
        }
        return almost;
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

    private List<String> findMissing(Recipe recipe, Map<String, Double> available) {
        List<String> missing = new ArrayList<>();
        if (recipe.getIngredients() == null) {
            return missing;
        }
        for (RecipeIngredient required : recipe.getIngredients()) {
            String key = IngredientNormalizer.normalize(
                    required.getNormalizedName() != null && !required.getNormalizedName().isEmpty()
                            ? required.getNormalizedName()
                            : required.getName());
            Double have = available.get(key);
            if (have == null || have < required.getQuantity()) {
                String label = required.getName();
                if (required.getQuantity() > 0) {
                    label = label + " (" + formatQty(required.getQuantity()) + " "
                            + (required.getUnit() != null ? required.getUnit() : "") + ")";
                }
                missing.add(label.trim());
            }
        }
        return missing;
    }

    private String formatQty(double qty) {
        if (qty == Math.floor(qty)) {
            return String.valueOf((long) qty);
        }
        return String.valueOf(qty);
    }
}
