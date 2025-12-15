package com.shop.englishapp.masterchef.utils;

import com.shop.englishapp.masterchef.models.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates random orders for Master Chef gameplay
 * Difficulty scales with player level
 */
public class OrderGenerator {
    
    private static final String[] CUSTOMER_NAMES = {
        "Tommy Cat", "Bella Bear", "Charlie Dog", "Daisy Duck", "Eddie Elephant"
    };
    
    private static final String[] FOODS = {
        "apple", "banana", "egg", "bread", "milk", "juice", "cheese", "tomato", "fish"
    };
    
    private Random random;
    
    public OrderGenerator() {
        this.random = new Random();
    }
    
    /**
     * Generates a random order based on difficulty level
     * 
     * Level 1 (Easy): 1-2 items, quantity 1-2
     * Level 2 (Medium): 2-3 items, quantity 1-3
     * Level 3 (Hard): 3-4 items, quantity 2-5
     */
    public Order generateOrder(int difficultyLevel) {
        String orderId = "ORDER_" + System.currentTimeMillis();
        String customerName = CUSTOMER_NAMES[random.nextInt(CUSTOMER_NAMES.length)];
        
        // For now, use a placeholder avatar resource ID
        int avatarResId = android.R.drawable.ic_menu_gallery; // Replace with actual avatar
        
        Order order = new Order(orderId, customerName, avatarResId, difficultyLevel);
        
        // Determine number of items based on difficulty
        int itemCount;
        int maxQuantity;
        
        switch (difficultyLevel) {
            case 1: // Easy
                itemCount = random.nextInt(2) + 1; // 1-2 items
                maxQuantity = 2; // Max quantity of 2
                break;
            case 2: // Medium
                itemCount = random.nextInt(2) + 2; // 2-3 items
                maxQuantity = 3; // Max quantity of 3
                break;
            case 3: // Hard
                itemCount = random.nextInt(2) + 3; // 3-4 items
                maxQuantity = 5; // Max quantity of 5
                break;
            default:
                itemCount = 1;
                maxQuantity = 2;
        }
        
        // Generate unique food items
        List<String> selectedFoods = new ArrayList<>();
        while (selectedFoods.size() < itemCount) {
            String food = FOODS[random.nextInt(FOODS.length)];
            if (!selectedFoods.contains(food)) {
                selectedFoods.add(food);
            }
        }
        
        // Add items to order with random quantities
        for (String food : selectedFoods) {
            int quantity = random.nextInt(maxQuantity) + 1;
            order.addItem(food, food, quantity);
        }
        
        return order;
    }
    
    /**
     * Generates a tutorial order (very simple)
     * Used for first-time players
     */
    public Order generateTutorialOrder() {
        String orderId = "TUTORIAL_ORDER";
        String customerName = "Tommy Cat";
        int avatarResId = android.R.drawable.ic_menu_gallery;
        
        Order order = new Order(orderId, customerName, avatarResId, 1);
        order.addItem("egg", "egg", 2); // "I want two eggs"
        
        return order;
    }
    
    /**
     * Generates a specific order for testing
     */
    public Order generateTestOrder() {
        String orderId = "TEST_ORDER";
        String customerName = "Bella Bear";
        int avatarResId = android.R.drawable.ic_menu_gallery;
        
        Order order = new Order(orderId, customerName, avatarResId, 2);
        order.addItem("egg", "egg", 2);
        order.addItem("bread", "bread", 1);
        order.addItem("juice", "juice", 1);
        
        return order;
    }
}
