package com.kashi402307064.smartpantrymanager.model;

public class CalorieEntry {
    private long id = -1;
    private String foodName, date;
    private int calories;

    public CalorieEntry() {}

    public CalorieEntry(String foodName, int calories, String date) {
        this.foodName = foodName;
        this.calories = calories;
        this.date = date;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getFoodName() { return foodName; }
    public void setFoodName(String f) { this.foodName = f; }
    public int getCalories() { return calories; }
    public void setCalories(int c) { this.calories = c; }
    public String getDate() { return date; }
    public void setDate(String d) { this.date = d; }
}
