package com.shop.englishapp.masterchef.models;

/**
 * Represents a food item in the Master Chef game
 * Target vocabulary: apple, banana, egg, bread, milk, juice, cheese, tomato, fish
 */
public class FoodItem {
    private String id;
    private String name;
    private String nameInVietnamese;
    private int imageResId;
    private FoodCategory category;
    private boolean isUnlocked;

    public enum FoodCategory {
        FRUIT,      // apple, banana
        DAIRY,      // milk, cheese, egg
        BREAD,      // bread
        BEVERAGE,   // juice, milk
        PROTEIN,    // egg, fish
        VEGETABLE   // tomato
    }

    public FoodItem(String id, String name, String nameInVietnamese, int imageResId, FoodCategory category) {
        this.id = id;
        this.name = name;
        this.nameInVietnamese = nameInVietnamese;
        this.imageResId = imageResId;
        this.category = category;
        this.isUnlocked = false; // Locked by default
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameInVietnamese() { return nameInVietnamese; }
    public void setNameInVietnamese(String nameInVietnamese) { this.nameInVietnamese = nameInVietnamese; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public FoodCategory getCategory() { return category; }
    public void setCategory(FoodCategory category) { this.category = category; }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
}
