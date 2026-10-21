package com.kashi402307064.smartpantrymanager.model;

public class RecipeIngredient {
    private String name, normalizedName, unit;
    private double quantity;

    public RecipeIngredient() {}
    public RecipeIngredient(String name, String normalizedName, double quantity, String unit) {
        this.name = name; this.normalizedName = normalizedName;
        this.quantity = quantity; this.unit = unit;
    }
    public String getName() { return name; } public void setName(String n) { this.name = n; }
    public String getNormalizedName() { return normalizedName; } public void setNormalizedName(String n) { this.normalizedName = n; }
    public double getQuantity() { return quantity; } public void setQuantity(double q) { this.quantity = q; }
    public String getUnit() { return unit; } public void setUnit(String u) { this.unit = u; }
}
