package com.shop.englishapp.masterchef.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room Entity for storing user's Master Chef progress
 * This tracks all currency, unlocks, and achievements
 */
@Entity(tableName = "user_progress")
public class UserProgress {
    @PrimaryKey(autoGenerate = true)
    private int id;

    // Currency
    private int energy;              // Lightning bolts
    private int maxEnergy;           // Maximum energy capacity
    private long lastEnergyUpdate;   // Timestamp for regeneration
    private int gold;                // Coins for upgrades
    private int totalStarsEarned;    // Total stars collected

    // Lesson Progress
    private boolean foodLessonCompleted;    // "Food & Drinks" lesson
    private boolean numberLessonCompleted;  // "Numbers 1-10" lesson
    private int experiencePoints;           // XP for progression
    private int currentLevel;               // Player level

    // Master Chef Unlock
    private boolean masterChefUnlocked;     // Context lock status

    // Kitchen Upgrades
    private int stoveLevel;           // 0 = basic, 1-5 = upgraded
    private int ovenLevel;
    private int blenderLevel;
    private int kitchenDecorLevel;

    // Statistics
    private int totalOrdersCompleted;
    private int perfectOrdersCount;    // 3-star orders
    private int hintsUsed;
    private int audioReplaysUsed;

    public UserProgress() {
        // Default values for new user
        this.energy = 5;
        this.maxEnergy = 10;
        this.lastEnergyUpdate = System.currentTimeMillis();
        this.gold = 0;
        this.totalStarsEarned = 0;
        
        this.foodLessonCompleted = false;
        this.numberLessonCompleted = false;
        this.experiencePoints = 0;
        this.currentLevel = 1;
        
        this.masterChefUnlocked = false;
        
        this.stoveLevel = 0;
        this.ovenLevel = 0;
        this.blenderLevel = 0;
        this.kitchenDecorLevel = 0;
        
        this.totalOrdersCompleted = 0;
        this.perfectOrdersCount = 0;
        this.hintsUsed = 0;
        this.audioReplaysUsed = 0;
    }

    /**
     * Checks if Master Chef should be unlocked
     * Requirement: Food & Drinks lesson completed
     */
    public boolean canUnlockMasterChef() {
        return foodLessonCompleted;
    }

    /**
     * Attempts to unlock Master Chef mode
     */
    public boolean tryUnlock() {
        if (canUnlockMasterChef()) {
            this.masterChefUnlocked = true;
            return true;
        }
        return false;
    }

    /**
     * Regenerates energy based on time passed
     * Rate: 1 energy per 10 minutes (kid-friendly)
     */
    public void regenerateEnergy() {
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastEnergyUpdate;
        
        // 1 energy per 10 minutes = 600,000 ms
        int energyToAdd = (int) (timePassed / 600000);
        
        if (energyToAdd > 0 && energy < maxEnergy) {
            energy = Math.min(energy + energyToAdd, maxEnergy);
            lastEnergyUpdate = currentTime;
        }
    }

    /**
     * Attempts to consume energy for playing
     */
    public boolean consumeEnergy(int amount) {
        if (energy >= amount) {
            energy -= amount;
            return true;
        }
        return false;
    }

    /**
     * Adds gold and stars after completing an order
     */
    public void rewardOrder(int goldAmount, int stars) {
        this.gold += goldAmount;
        this.totalStarsEarned += stars;
        this.totalOrdersCompleted++;
        
        if (stars == 3) {
            this.perfectOrdersCount++;
        }
    }

    /**
     * Adds XP and checks for level up
     */
    public boolean addExperience(int xp) {
        this.experiencePoints += xp;
        
        // Simple level formula: 100 XP per level
        int requiredXp = currentLevel * 100;
        if (experiencePoints >= requiredXp) {
            currentLevel++;
            experiencePoints -= requiredXp;
            return true; // Level up!
        }
        return false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }

    public int getMaxEnergy() { return maxEnergy; }
    public void setMaxEnergy(int maxEnergy) { this.maxEnergy = maxEnergy; }

    public long getLastEnergyUpdate() { return lastEnergyUpdate; }
    public void setLastEnergyUpdate(long lastEnergyUpdate) { this.lastEnergyUpdate = lastEnergyUpdate; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }

    public int getTotalStarsEarned() { return totalStarsEarned; }
    public void setTotalStarsEarned(int totalStarsEarned) { this.totalStarsEarned = totalStarsEarned; }

    public boolean isFoodLessonCompleted() { return foodLessonCompleted; }
    public void setFoodLessonCompleted(boolean foodLessonCompleted) { 
        this.foodLessonCompleted = foodLessonCompleted;
        if (foodLessonCompleted) {
            tryUnlock();
        }
    }

    public boolean isNumberLessonCompleted() { return numberLessonCompleted; }
    public void setNumberLessonCompleted(boolean numberLessonCompleted) { this.numberLessonCompleted = numberLessonCompleted; }

    public int getExperiencePoints() { return experiencePoints; }
    public void setExperiencePoints(int experiencePoints) { this.experiencePoints = experiencePoints; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public boolean isMasterChefUnlocked() { return masterChefUnlocked; }
    public void setMasterChefUnlocked(boolean masterChefUnlocked) { this.masterChefUnlocked = masterChefUnlocked; }

    public int getStoveLevel() { return stoveLevel; }
    public void setStoveLevel(int stoveLevel) { this.stoveLevel = stoveLevel; }

    public int getOvenLevel() { return ovenLevel; }
    public void setOvenLevel(int ovenLevel) { this.ovenLevel = ovenLevel; }

    public int getBlenderLevel() { return blenderLevel; }
    public void setBlenderLevel(int blenderLevel) { this.blenderLevel = blenderLevel; }

    public int getKitchenDecorLevel() { return kitchenDecorLevel; }
    public void setKitchenDecorLevel(int kitchenDecorLevel) { this.kitchenDecorLevel = kitchenDecorLevel; }

    public int getTotalOrdersCompleted() { return totalOrdersCompleted; }
    public void setTotalOrdersCompleted(int totalOrdersCompleted) { this.totalOrdersCompleted = totalOrdersCompleted; }

    public int getPerfectOrdersCount() { return perfectOrdersCount; }
    public void setPerfectOrdersCount(int perfectOrdersCount) { this.perfectOrdersCount = perfectOrdersCount; }

    public int getHintsUsed() { return hintsUsed; }
    public void setHintsUsed(int hintsUsed) { this.hintsUsed = hintsUsed; }

    public int getAudioReplaysUsed() { return audioReplaysUsed; }
    public void setAudioReplaysUsed(int audioReplaysUsed) { this.audioReplaysUsed = audioReplaysUsed; }
}
