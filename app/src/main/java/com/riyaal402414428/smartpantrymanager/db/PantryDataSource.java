package com.riyaal402414428.smartpantrymanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.riyaal402414428.smartpantrymanager.model.CalorieEntry;
import com.riyaal402414428.smartpantrymanager.model.PantryItem;
import com.riyaal402414428.smartpantrymanager.model.Recipe;
import com.riyaal402414428.smartpantrymanager.model.RecipeIngredient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PantryDataSource {
    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;

    public PantryDataSource(Context context) { dbHelper = new DatabaseHelper(context); }
    public void open() throws SQLException { database = dbHelper.getWritableDatabase(); }
    public void close() { dbHelper.close(); }

    public long insertPantryItem(PantryItem item) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COL_P_NAME, item.getName());
        v.put(DatabaseHelper.COL_P_NORMALIZED, item.getNormalizedName());
        v.put(DatabaseHelper.COL_P_QTY, item.getQuantity());
        v.put(DatabaseHelper.COL_P_UNIT, item.getUnit());
        if (item.getExpiryDate() != null) v.put(DatabaseHelper.COL_P_EXPIRY, item.getExpiryDate());
        else v.putNull(DatabaseHelper.COL_P_EXPIRY);
        return database.insert(DatabaseHelper.TABLE_PANTRY, null, v);
    }

    public boolean updatePantryItem(PantryItem item) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COL_P_NAME, item.getName());
        v.put(DatabaseHelper.COL_P_NORMALIZED, item.getNormalizedName());
        v.put(DatabaseHelper.COL_P_QTY, item.getQuantity());
        v.put(DatabaseHelper.COL_P_UNIT, item.getUnit());
        if (item.getExpiryDate() != null) v.put(DatabaseHelper.COL_P_EXPIRY, item.getExpiryDate());
        else v.putNull(DatabaseHelper.COL_P_EXPIRY);
        return database.update(DatabaseHelper.TABLE_PANTRY, v, DatabaseHelper.COL_P_ID + "=?", new String[]{String.valueOf(item.getId())}) > 0;
    }

    public boolean deletePantryItem(long id) {
        return database.delete(DatabaseHelper.TABLE_PANTRY, DatabaseHelper.COL_P_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        Cursor c = database.query(DatabaseHelper.TABLE_PANTRY, null, null, null, null, null, DatabaseHelper.COL_P_NAME + " COLLATE NOCASE");
        while (c.moveToNext()) list.add(cursorToPantry(c));
        c.close();
        return list;
    }

    private PantryItem cursorToPantry(Cursor c) {
        PantryItem item = new PantryItem();
        item.setId(c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_P_ID)));
        item.setName(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_P_NAME)));
        item.setNormalizedName(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_P_NORMALIZED)));
        item.setQuantity(c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_P_QTY)));
        item.setUnit(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_P_UNIT)));
        int expIdx = c.getColumnIndexOrThrow(DatabaseHelper.COL_P_EXPIRY);
        if (!c.isNull(expIdx)) item.setExpiryDate(c.getLong(expIdx));
        return item;
    }

    public long insertRecipe(Recipe recipe) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COL_R_NAME, recipe.getName());
        StringBuilder sb = new StringBuilder();
        if (recipe.getSteps() != null) {
            for (int i = 0; i < recipe.getSteps().size(); i++) {
                if (i > 0) sb.append("\n");
                sb.append(recipe.getSteps().get(i));
            }
        }
        v.put(DatabaseHelper.COL_R_STEPS, sb.toString());
        long recipeId = database.insert(DatabaseHelper.TABLE_RECIPES, null, v);
        if (recipeId > 0 && recipe.getIngredients() != null) {
            for (RecipeIngredient ri : recipe.getIngredients()) {
                ContentValues iv = new ContentValues();
                iv.put(DatabaseHelper.COL_RI_RECIPE_ID, recipeId);
                iv.put(DatabaseHelper.COL_RI_NAME, ri.getName());
                iv.put(DatabaseHelper.COL_RI_NORMALIZED, ri.getNormalizedName());
                iv.put(DatabaseHelper.COL_RI_QTY, ri.getQuantity());
                iv.put(DatabaseHelper.COL_RI_UNIT, ri.getUnit());
                database.insert(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, iv);
            }
        }
        return recipeId;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        Cursor c = database.query(DatabaseHelper.TABLE_RECIPES, null, null, null, null, null, DatabaseHelper.COL_R_NAME + " COLLATE NOCASE");
        while (c.moveToNext()) {
            Recipe r = cursorToRecipe(c);
            r.setIngredients(getIngredientsForRecipe(r.getId()));
            list.add(r);
        }
        c.close();
        return list;
    }

    public Recipe getRecipeById(long id) {
        Cursor c = database.query(DatabaseHelper.TABLE_RECIPES, null, DatabaseHelper.COL_R_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Recipe r = null;
        if (c.moveToFirst()) {
            r = cursorToRecipe(c);
            r.setIngredients(getIngredientsForRecipe(id));
        }
        c.close();
        return r;
    }

    private Recipe cursorToRecipe(Cursor c) {
        Recipe r = new Recipe();
        r.setId(c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_R_ID)));
        r.setName(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_R_NAME)));
        String stepsStr = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_R_STEPS));
        if (stepsStr != null && !stepsStr.isEmpty()) r.setSteps(Arrays.asList(stepsStr.split("\n")));
        else r.setSteps(new ArrayList<>());
        return r;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        Cursor c = database.query(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, DatabaseHelper.COL_RI_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)}, null, null, null);
        while (c.moveToNext()) {
            RecipeIngredient ri = new RecipeIngredient();
            ri.setName(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_RI_NAME)));
            ri.setNormalizedName(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_RI_NORMALIZED)));
            ri.setQuantity(c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_RI_QTY)));
            ri.setUnit(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_RI_UNIT)));
            list.add(ri);
        }
        c.close();
        return list;
    }

    public int getRecipeCount() {
        Cursor c = database.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_RECIPES, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public long insertCalorieEntry(CalorieEntry entry) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COL_C_FOOD, entry.getFoodName());
        v.put(DatabaseHelper.COL_C_CALORIES, entry.getCalories());
        v.put(DatabaseHelper.COL_C_DATE, entry.getDate());
        return database.insert(DatabaseHelper.TABLE_CALORIES, null, v);
    }

    public boolean deleteCalorieEntry(long id) {
        return database.delete(DatabaseHelper.TABLE_CALORIES, DatabaseHelper.COL_C_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public List<CalorieEntry> getCalorieEntriesForDate(String date) {
        List<CalorieEntry> list = new ArrayList<>();
        Cursor c = database.query(DatabaseHelper.TABLE_CALORIES, null, DatabaseHelper.COL_C_DATE + "=?", new String[]{date}, null, null, DatabaseHelper.COL_C_ID + " DESC");
        while (c.moveToNext()) list.add(cursorToCalorie(c));
        c.close();
        return list;
    }

    public int getTotalCaloriesForDate(String date) {
        Cursor c = database.rawQuery("SELECT SUM(" + DatabaseHelper.COL_C_CALORIES + ") FROM " + DatabaseHelper.TABLE_CALORIES + " WHERE " + DatabaseHelper.COL_C_DATE + "=?", new String[]{date});
        int total = 0;
        if (c.moveToFirst() && !c.isNull(0)) total = c.getInt(0);
        c.close();
        return total;
    }

    private CalorieEntry cursorToCalorie(Cursor c) {
        CalorieEntry e = new CalorieEntry();
        e.setId(c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_C_ID)));
        e.setFoodName(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_C_FOOD)));
        e.setCalories(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_C_CALORIES)));
        e.setDate(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_C_DATE)));
        return e;
    }
}
