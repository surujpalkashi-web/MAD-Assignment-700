package com.kashi402307064.smartpantrymanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.kashi402307064.smartpantrymanager.model.CalorieEntry;
import com.kashi402307064.smartpantrymanager.model.PantryItem;
import com.kashi402307064.smartpantrymanager.model.Recipe;
import com.kashi402307064.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PantryDataSource {

    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;

    public PantryDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertPantryItem(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_P_NAME, item.getName());
        values.put(DatabaseHelper.COL_P_NORMALIZED, item.getNormalizedName());
        values.put(DatabaseHelper.COL_P_QTY, item.getQuantity());
        values.put(DatabaseHelper.COL_P_UNIT, item.getUnit());
        if (item.getExpiryDate() != null) {
            values.put(DatabaseHelper.COL_P_EXPIRY, item.getExpiryDate());
        } else {
            values.putNull(DatabaseHelper.COL_P_EXPIRY);
        }
        return database.insert(DatabaseHelper.TABLE_PANTRY, null, values);
    }

    public boolean updatePantryItem(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_P_NAME, item.getName());
        values.put(DatabaseHelper.COL_P_NORMALIZED, item.getNormalizedName());
        values.put(DatabaseHelper.COL_P_QTY, item.getQuantity());
        values.put(DatabaseHelper.COL_P_UNIT, item.getUnit());
        if (item.getExpiryDate() != null) {
            values.put(DatabaseHelper.COL_P_EXPIRY, item.getExpiryDate());
        } else {
            values.putNull(DatabaseHelper.COL_P_EXPIRY);
        }
        return database.update(DatabaseHelper.TABLE_PANTRY, values,
                DatabaseHelper.COL_P_ID + "=?", new String[]{String.valueOf(item.getId())}) > 0;
    }

    public boolean deletePantryItem(long id) {
        return database.delete(DatabaseHelper.TABLE_PANTRY,
                DatabaseHelper.COL_P_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_PANTRY, null, null, null, null, null,
                DatabaseHelper.COL_P_NAME + " COLLATE NOCASE");
        while (cursor.moveToNext()) {
            list.add(cursorToPantry(cursor));
        }
        cursor.close();
        return list;
    }

    private PantryItem cursorToPantry(Cursor cursor) {
        PantryItem item = new PantryItem();
        item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_P_ID)));
        item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_P_NAME)));
        item.setNormalizedName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_P_NORMALIZED)));
        item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_P_QTY)));
        item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_P_UNIT)));
        int expIdx = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_P_EXPIRY);
        if (!cursor.isNull(expIdx)) {
            item.setExpiryDate(cursor.getLong(expIdx));
        }
        return item;
    }

    public long insertRecipe(Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_R_NAME, recipe.getName());
        StringBuilder stepsBuilder = new StringBuilder();
        if (recipe.getSteps() != null) {
            for (int i = 0; i < recipe.getSteps().size(); i++) {
                if (i > 0) {
                    stepsBuilder.append("\n");
                }
                stepsBuilder.append(recipe.getSteps().get(i));
            }
        }
        values.put(DatabaseHelper.COL_R_STEPS, stepsBuilder.toString());
        long recipeId = database.insert(DatabaseHelper.TABLE_RECIPES, null, values);

        if (recipeId > 0 && recipe.getIngredients() != null) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                ContentValues ingredientValues = new ContentValues();
                ingredientValues.put(DatabaseHelper.COL_RI_RECIPE_ID, recipeId);
                ingredientValues.put(DatabaseHelper.COL_RI_NAME, ingredient.getName());
                ingredientValues.put(DatabaseHelper.COL_RI_NORMALIZED, ingredient.getNormalizedName());
                ingredientValues.put(DatabaseHelper.COL_RI_QTY, ingredient.getQuantity());
                ingredientValues.put(DatabaseHelper.COL_RI_UNIT, ingredient.getUnit());
                database.insert(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, ingredientValues);
            }
        }
        return recipeId;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_RECIPES, null, null, null, null, null,
                DatabaseHelper.COL_R_NAME + " COLLATE NOCASE");
        while (cursor.moveToNext()) {
            Recipe recipe = cursorToRecipe(cursor);
            recipe.setIngredients(getIngredientsForRecipe(recipe.getId()));
            list.add(recipe);
        }
        cursor.close();
        return list;
    }

    public Recipe getRecipeById(long id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_RECIPES, null,
                DatabaseHelper.COL_R_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        Recipe recipe = null;
        if (cursor.moveToFirst()) {
            recipe = cursorToRecipe(cursor);
            recipe.setIngredients(getIngredientsForRecipe(id));
        }
        cursor.close();
        return recipe;
    }

    private Recipe cursorToRecipe(Cursor cursor) {
        Recipe recipe = new Recipe();
        recipe.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_R_ID)));
        recipe.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_R_NAME)));
        String steps = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_R_STEPS));
        if (steps != null && !steps.isEmpty()) {
            recipe.setSteps(Arrays.asList(steps.split("\n")));
        } else {
            recipe.setSteps(new ArrayList<>());
        }
        return recipe;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null,
                DatabaseHelper.COL_RI_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        while (cursor.moveToNext()) {
            RecipeIngredient ingredient = new RecipeIngredient();
            ingredient.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RI_NAME)));
            ingredient.setNormalizedName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RI_NORMALIZED)));
            ingredient.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RI_QTY)));
            ingredient.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RI_UNIT)));
            list.add(ingredient);
        }
        cursor.close();
        return list;
    }

    public int getRecipeCount() {
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_RECIPES, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public long insertCalorieEntry(CalorieEntry entry) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_C_FOOD, entry.getFoodName());
        values.put(DatabaseHelper.COL_C_CALORIES, entry.getCalories());
        values.put(DatabaseHelper.COL_C_DATE, entry.getDate());
        return database.insert(DatabaseHelper.TABLE_CALORIES, null, values);
    }

    public boolean deleteCalorieEntry(long id) {
        return database.delete(DatabaseHelper.TABLE_CALORIES,
                DatabaseHelper.COL_C_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public List<CalorieEntry> getCalorieEntriesForDate(String date) {
        List<CalorieEntry> list = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_CALORIES, null,
                DatabaseHelper.COL_C_DATE + "=?", new String[]{date},
                null, null, DatabaseHelper.COL_C_ID + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToCalorie(cursor));
        }
        cursor.close();
        return list;
    }

    public int getTotalCaloriesForDate(String date) {
        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + DatabaseHelper.COL_C_CALORIES + ") FROM " +
                        DatabaseHelper.TABLE_CALORIES + " WHERE " +
                        DatabaseHelper.COL_C_DATE + "=?",
                new String[]{date});
        int total = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    private CalorieEntry cursorToCalorie(Cursor cursor) {
        CalorieEntry entry = new CalorieEntry();
        entry.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_C_ID)));
        entry.setFoodName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_C_FOOD)));
        entry.setCalories(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_C_CALORIES)));
        entry.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_C_DATE)));
        return entry;
    }
}
