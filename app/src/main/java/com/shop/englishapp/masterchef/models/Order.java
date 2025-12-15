package com.shop.englishapp.masterchef.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer order in Master Chef
 * Example: "Add two eggs", "Put three apples in the bowl"
 */
public class Order {
    private String orderId;
    private String customerName;
    private int customerAvatarResId;
    private List<OrderItem> items;
    private int difficultyLevel; // 1-3 (easy, medium, hard)
    private int goldReward;
    private int energyCost;
    private long timeLimit; // milliseconds (0 = no limit for kids)

    public Order(String orderId, String customerName, int customerAvatarResId, int difficultyLevel) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerAvatarResId = customerAvatarResId;
        this.difficultyLevel = difficultyLevel;
        this.items = new ArrayList<>();
        
        // Set rewards based on difficulty
        switch (difficultyLevel) {
            case 1: // Easy
                this.goldReward = 50;
                this.energyCost = 1;
                break;
            case 2: // Medium
                this.goldReward = 100;
                this.energyCost = 2;
                break;
            case 3: // Hard
                this.goldReward = 200;
                this.energyCost = 3;
                break;
            default:
                this.goldReward = 50;
                this.energyCost = 1;
        }
        
        this.timeLimit = 0; // No time pressure for kids
    }

    public void addItem(String foodId, String foodName, int quantity) {
        items.add(new OrderItem(foodId, foodName, quantity));
    }

    /**
     * Generates the spoken sentence for this order
     * Example: "I want two eggs and one bread"
     */
    public String getSpokenSentence() {
        if (items.isEmpty()) {
            return "I want something delicious!";
        }

        StringBuilder sentence = new StringBuilder("I want ");
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            String quantityWord = convertNumberToWord(item.getQuantity());
            
            sentence.append(quantityWord)
                    .append(" ")
                    .append(item.getQuantity() > 1 ? pluralize(item.getFoodName()) : item.getFoodName());
            
            if (i < items.size() - 2) {
                sentence.append(", ");
            } else if (i == items.size() - 2) {
                sentence.append(" and ");
            }
        }
        
        return sentence.toString();
    }

    private String convertNumberToWord(int number) {
        String[] words = {"zero", "one", "two", "three", "four", "five", 
                         "six", "seven", "eight", "nine", "ten",
                         "eleven", "twelve", "thirteen", "fourteen", "fifteen",
                         "sixteen", "seventeen", "eighteen", "nineteen", "twenty"};
        
        if (number >= 0 && number < words.length) {
            return words[number];
        }
        return String.valueOf(number);
    }

    private String pluralize(String word) {
        // Simple pluralization (not perfect but works for our vocabulary)
        if (word.endsWith("s") || word.endsWith("sh") || word.endsWith("ch")) {
            return word + "es";
        } else if (word.endsWith("y")) {
            return word.substring(0, word.length() - 1) + "ies";
        } else {
            return word + "s";
        }
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public int getCustomerAvatarResId() { return customerAvatarResId; }
    public List<OrderItem> getItems() { return items; }
    public int getDifficultyLevel() { return difficultyLevel; }
    public int getGoldReward() { return goldReward; }
    public int getEnergyCost() { return energyCost; }
    public long getTimeLimit() { return timeLimit; }

    /**
     * Inner class representing a single item in an order
     */
    public static class OrderItem {
        private String foodId;
        private String foodName;
        private int quantity;
        private boolean isCompleted;

        public OrderItem(String foodId, String foodName, int quantity) {
            this.foodId = foodId;
            this.foodName = foodName;
            this.quantity = quantity;
            this.isCompleted = false;
        }

        public String getFoodId() { return foodId; }
        public String getFoodName() { return foodName; }
        public int getQuantity() { return quantity; }
        public boolean isCompleted() { return isCompleted; }
        public void setCompleted(boolean completed) { isCompleted = completed; }
    }
}
