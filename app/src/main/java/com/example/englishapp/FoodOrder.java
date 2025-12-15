package com.example.englishapp;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a food order from a customer in Master Chef game
 * Includes the food items, quantities, and audio resources for pronunciation practice
 */
public class FoodOrder {
    private Map<String, Integer> items; // Food item -> quantity
    private int audioResourceId;
    private String orderText; // e.g., "Two eggs and three apples"
    private int difficultyLevel; // 1-5
    
    public FoodOrder() {
        items = new HashMap<>();
        difficultyLevel = 1;
    }
    
    public FoodOrder(Map<String, Integer> items, int audioResourceId, String orderText, int difficultyLevel) {
        this.items = items != null ? items : new HashMap<>();
        this.audioResourceId = audioResourceId;
        this.orderText = orderText;
        this.difficultyLevel = difficultyLevel;
    }
    
    // Add item to order
    public void addItem(String foodItem, int quantity) {
        if (foodItem != null && quantity > 0) {
            items.put(foodItem, quantity);
        }
    }
    
    // Get required quantity for a food item
    public int getRequiredQuantity(String foodItem) {
        return items.getOrDefault(foodItem, 0);
    }
    
    // Check if the order is complete
    public boolean isComplete(Map<String, Integer> providedItems) {
        if (providedItems == null || items.size() != providedItems.size()) {
            return false;
        }
        
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String foodItem = entry.getKey();
            int requiredQty = entry.getValue();
            int providedQty = providedItems.getOrDefault(foodItem, 0);
            
            if (providedQty != requiredQty) {
                return false;
            }
        }
        
        return true;
    }
    
    // Calculate how many mistakes were made
    public int calculateMistakes(Map<String, Integer> providedItems) {
        int mistakes = 0;
        
        // Check for wrong quantities
        for (String foodItem : items.keySet()) {
            int required = items.get(foodItem);
            int provided = providedItems.getOrDefault(foodItem, 0);
            mistakes += Math.abs(required - provided);
        }
        
        // Check for extra items
        for (String foodItem : providedItems.keySet()) {
            if (!items.containsKey(foodItem)) {
                mistakes += providedItems.get(foodItem);
            }
        }
        
        return mistakes;
    }
    
    // Calculate star rating based on mistakes
    // 3 stars = perfect (0 mistakes)
    // 2 stars = 1-2 mistakes
    // 1 star = 3+ mistakes
    public int calculateStars(Map<String, Integer> providedItems) {
        int mistakes = calculateMistakes(providedItems);
        
        if (mistakes == 0) {
            return 3;
        } else if (mistakes <= 2) {
            return 2;
        } else {
            return 1;
        }
    }
    
    // Getters and Setters
    public Map<String, Integer> getItems() {
        return items;
    }
    
    public void setItems(Map<String, Integer> items) {
        this.items = items != null ? items : new HashMap<>();
    }
    
    public int getAudioResourceId() {
        return audioResourceId;
    }
    
    public void setAudioResourceId(int audioResourceId) {
        this.audioResourceId = audioResourceId;
    }
    
    public String getOrderText() {
        return orderText;
    }
    
    public void setOrderText(String orderText) {
        this.orderText = orderText;
    }
    
    public int getDifficultyLevel() {
        return difficultyLevel;
    }
    
    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
    
    public int getTotalItems() {
        int total = 0;
        for (int qty : items.values()) {
            total += qty;
        }
        return total;
    }
    
    @Override
    public String toString() {
        return "FoodOrder{" +
                "items=" + items +
                ", orderText='" + orderText + '\'' +
                ", difficultyLevel=" + difficultyLevel +
                '}';
    }
}
