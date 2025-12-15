package com.example.englishapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Generates random food orders for Master Chef game
 * Based on the 9 food items from the spec
 */
public class OrderGenerator {
    private Random random;
    private List<String> foodItems;
    
    // Food items from spec
    public static final String FOOD_EGG = "egg";
    public static final String FOOD_BREAD = "bread";
    public static final String FOOD_CHEESE = "cheese";
    public static final String FOOD_BACON = "bacon";
    public static final String FOOD_APPLE = "apple";
    public static final String FOOD_BANANA = "banana";
    public static final String FOOD_CARROT = "carrot";
    public static final String FOOD_JUICE = "juice";
    public static final String FOOD_MILK = "milk";
    
    public OrderGenerator() {
        random = new Random();
        foodItems = new ArrayList<>();
        foodItems.add(FOOD_EGG);
        foodItems.add(FOOD_BREAD);
        foodItems.add(FOOD_CHEESE);
        foodItems.add(FOOD_BACON);
        foodItems.add(FOOD_APPLE);
        foodItems.add(FOOD_BANANA);
        foodItems.add(FOOD_CARROT);
        foodItems.add(FOOD_JUICE);
        foodItems.add(FOOD_MILK);
    }
    
    /**
     * Generate a random order based on difficulty level
     * Level 1: 1 item, quantity 1-2
     * Level 2-3: 1-2 items, quantity 1-3
     * Level 4-5: 2-3 items, quantity 1-5
     */
    public FoodOrder generateOrder(int difficultyLevel) {
        Map<String, Integer> items = new HashMap<>();
        int numItems;
        int maxQuantity;
        
        if (difficultyLevel <= 1) {
            numItems = 1;
            maxQuantity = 2;
        } else if (difficultyLevel <= 3) {
            numItems = random.nextInt(2) + 1; // 1-2 items
            maxQuantity = 3;
        } else {
            numItems = random.nextInt(2) + 2; // 2-3 items
            maxQuantity = 5;
        }
        
        // Select random food items
        List<String> selectedFoods = new ArrayList<>();
        while (selectedFoods.size() < numItems) {
            String food = foodItems.get(random.nextInt(foodItems.size()));
            if (!selectedFoods.contains(food)) {
                selectedFoods.add(food);
                int quantity = random.nextInt(maxQuantity) + 1;
                items.put(food, quantity);
            }
        }
        
        // Generate order text
        String orderText = buildOrderText(items);
        
        // TODO: Add audio resource ID when audio files are ready
        int audioResourceId = 0;
        
        return new FoodOrder(items, audioResourceId, orderText, difficultyLevel);
    }
    
    /**
     * Build readable order text from items
     * e.g., "Two eggs and three apples"
     */
    private String buildOrderText(Map<String, Integer> items) {
        if (items.isEmpty()) {
            return "No order";
        }
        
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String food = entry.getKey();
            int quantity = entry.getValue();
            
            String quantityText = getNumberWord(quantity);
            String foodText = getFoodNamePlural(food, quantity);
            
            parts.add(quantityText + " " + foodText);
        }
        
        // Join with "and"
        if (parts.size() == 1) {
            return parts.get(0);
        } else if (parts.size() == 2) {
            return parts.get(0) + " and " + parts.get(1);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.size() - 1; i++) {
                sb.append(parts.get(i)).append(", ");
            }
            sb.append("and ").append(parts.get(parts.size() - 1));
            return sb.toString();
        }
    }
    
    private String getNumberWord(int number) {
        switch (number) {
            case 1: return "One";
            case 2: return "Two";
            case 3: return "Three";
            case 4: return "Four";
            case 5: return "Five";
            case 6: return "Six";
            case 7: return "Seven";
            case 8: return "Eight";
            case 9: return "Nine";
            case 10: return "Ten";
            case 11: return "Eleven";
            case 12: return "Twelve";
            case 13: return "Thirteen";
            case 14: return "Fourteen";
            case 15: return "Fifteen";
            case 16: return "Sixteen";
            case 17: return "Seventeen";
            case 18: return "Eighteen";
            case 19: return "Nineteen";
            case 20: return "Twenty";
            default: return String.valueOf(number);
        }
    }
    
    private String getFoodNamePlural(String food, int quantity) {
        String name;
        switch (food) {
            case FOOD_EGG: name = quantity > 1 ? "eggs" : "egg"; break;
            case FOOD_BREAD: name = quantity > 1 ? "breads" : "bread"; break;
            case FOOD_CHEESE: name = "cheese"; break; // uncountable
            case FOOD_BACON: name = "bacon"; break; // uncountable
            case FOOD_APPLE: name = quantity > 1 ? "apples" : "apple"; break;
            case FOOD_BANANA: name = quantity > 1 ? "bananas" : "banana"; break;
            case FOOD_CARROT: name = quantity > 1 ? "carrots" : "carrot"; break;
            case FOOD_JUICE: name = quantity > 1 ? "glasses of juice" : "glass of juice"; break;
            case FOOD_MILK: name = quantity > 1 ? "glasses of milk" : "glass of milk"; break;
            default: name = food;
        }
        return name;
    }
    
    /**
     * Generate a specific order for testing
     */
    public FoodOrder generateTestOrder() {
        Map<String, Integer> items = new HashMap<>();
        items.put(FOOD_EGG, 2);
        items.put(FOOD_BREAD, 1);
        
        return new FoodOrder(items, 0, "Two eggs and one bread", 1);
    }
}
