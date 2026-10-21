package com.kashi402307064.smartpantrymanager.model;

public class PantryItem {
    private long id = -1;
    private String name, normalizedName, unit;
    private double quantity;
    private Long expiryDate;

    public PantryItem() {}
    public PantryItem(String name, String normalizedName, double quantity, String unit, Long expiryDate) {
        this.name = name; this.normalizedName = normalizedName;
        this.quantity = quantity; this.unit = unit; this.expiryDate = expiryDate;
    }
    public long getId() { return id; } public void setId(long id) { this.id = id; }
    public String getName() { return name; } public void setName(String n) { this.name = n; }
    public String getNormalizedName() { return normalizedName; } public void setNormalizedName(String n) { this.normalizedName = n; }
    public double getQuantity() { return quantity; } public void setQuantity(double q) { this.quantity = q; }
    public String getUnit() { return unit; } public void setUnit(String u) { this.unit = u; }
    public Long getExpiryDate() { return expiryDate; } public void setExpiryDate(Long e) { this.expiryDate = e; }
}
