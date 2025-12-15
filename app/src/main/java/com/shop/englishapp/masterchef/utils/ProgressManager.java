package com.shop.englishapp.masterchef.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.shop.englishapp.masterchef.database.MasterChefDatabase;
import com.shop.englishapp.masterchef.database.UserProgressDao;
import com.shop.englishapp.masterchef.models.UserProgress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manager class for handling all Master Chef data operations
 * Provides thread-safe database access with callbacks
 */
public class ProgressManager {
    
    private static ProgressManager instance;
    private final UserProgressDao dao;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private UserProgress cachedProgress;
    
    private ProgressManager(Context context) {
        MasterChefDatabase database = MasterChefDatabase.getInstance(context);
        this.dao = database.userProgressDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public static synchronized ProgressManager getInstance(Context context) {
        if (instance == null) {
            instance = new ProgressManager(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Initializes user progress (first time setup)
     */
    public void initializeProgress(OnProgressCallback callback) {
        executorService.execute(() -> {
            UserProgress existing = dao.getUserProgress();
            if (existing == null) {
                // First time user - create new progress
                existing = new UserProgress();
                dao.insertUserProgress(existing);
            }
            
            // Regenerate energy based on time passed
            existing.regenerateEnergy();
            dao.updateUserProgress(existing);
            
            cachedProgress = existing;
            final UserProgress finalExisting = existing;
            
            mainHandler.post(() -> callback.onSuccess(finalExisting));
        });
    }
    
    /**
     * Gets current user progress
     */
    public void getUserProgress(OnProgressCallback callback) {
        if (cachedProgress != null) {
            // Return cached version immediately
            cachedProgress.regenerateEnergy();
            callback.onSuccess(cachedProgress);
            
            // Update in background
            executorService.execute(() -> {
                dao.updateUserProgress(cachedProgress);
            });
        } else {
            // Load from database
            executorService.execute(() -> {
                UserProgress progress = dao.getUserProgress();
                if (progress == null) {
                    progress = new UserProgress();
                    dao.insertUserProgress(progress);
                }
                
                progress.regenerateEnergy();
                dao.updateUserProgress(progress);
                cachedProgress = progress;
                final UserProgress finalProgress = progress;
                
                mainHandler.post(() -> callback.onSuccess(finalProgress));
            });
        }
    }
    
    /**
     * Attempts to consume energy for playing
     */
    public void consumeEnergy(int amount, OnEnergyCallback callback) {
        executorService.execute(() -> {
            UserProgress progress = dao.getUserProgress();
            if (progress != null) {
                progress.regenerateEnergy();
                boolean success = progress.consumeEnergy(amount);
                
                if (success) {
                    dao.updateUserProgress(progress);
                    cachedProgress = progress;
                    mainHandler.post(() -> callback.onSuccess(progress.getEnergy()));
                } else {
                    mainHandler.post(() -> callback.onFailure("Not enough energy!"));
                }
            } else {
                mainHandler.post(() -> callback.onFailure("User progress not found"));
            }
        });
    }
    
    /**
     * Rewards user after completing an order
     */
    public void rewardOrder(int gold, int stars, OnProgressCallback callback) {
        executorService.execute(() -> {
            UserProgress progress = dao.getUserProgress();
            if (progress != null) {
                progress.rewardOrder(gold, stars);
                dao.updateUserProgress(progress);
                cachedProgress = progress;
                
                mainHandler.post(() -> callback.onSuccess(progress));
            }
        });
    }
    
    /**
     * Adds XP and checks for level up
     */
    public void addExperience(int xp, OnLevelUpCallback callback) {
        executorService.execute(() -> {
            UserProgress progress = dao.getUserProgress();
            if (progress != null) {
                boolean leveledUp = progress.addExperience(xp);
                dao.updateUserProgress(progress);
                cachedProgress = progress;
                
                mainHandler.post(() -> {
                    if (leveledUp) {
                        callback.onLevelUp(progress.getCurrentLevel());
                    } else {
                        callback.onXpGained(xp);
                    }
                });
            }
        });
    }
    
    /**
     * Completes the Food & Drinks lesson and unlocks Master Chef
     */
    public void completeFoodLesson(OnUnlockCallback callback) {
        executorService.execute(() -> {
            UserProgress progress = dao.getUserProgress();
            if (progress != null) {
                progress.setFoodLessonCompleted(true);
                boolean unlocked = progress.tryUnlock();
                dao.updateUserProgress(progress);
                cachedProgress = progress;
                
                mainHandler.post(() -> {
                    if (unlocked) {
                        callback.onUnlocked();
                    } else {
                        callback.onAlreadyUnlocked();
                    }
                });
            }
        });
    }
    
    /**
     * Spends gold on upgrades
     */
    public void spendGold(int amount, OnGoldCallback callback) {
        executorService.execute(() -> {
            UserProgress progress = dao.getUserProgress();
            if (progress != null) {
                if (progress.getGold() >= amount) {
                    progress.setGold(progress.getGold() - amount);
                    dao.updateUserProgress(progress);
                    cachedProgress = progress;
                    
                    mainHandler.post(() -> callback.onSuccess(progress.getGold()));
                } else {
                    mainHandler.post(() -> callback.onFailure("Not enough gold!"));
                }
            }
        });
    }
    
    /**
     * Upgrades kitchen equipment
     */
    public void upgradeKitchen(String equipmentType, int cost, OnProgressCallback callback) {
        executorService.execute(() -> {
            UserProgress progress = dao.getUserProgress();
            if (progress != null) {
                if (progress.getGold() >= cost) {
                    progress.setGold(progress.getGold() - cost);
                    
                    // Upgrade specific equipment
                    switch (equipmentType.toLowerCase()) {
                        case "stove":
                            progress.setStoveLevel(progress.getStoveLevel() + 1);
                            break;
                        case "oven":
                            progress.setOvenLevel(progress.getOvenLevel() + 1);
                            break;
                        case "blender":
                            progress.setBlenderLevel(progress.getBlenderLevel() + 1);
                            break;
                        case "decor":
                            progress.setKitchenDecorLevel(progress.getKitchenDecorLevel() + 1);
                            break;
                    }
                    
                    dao.updateUserProgress(progress);
                    cachedProgress = progress;
                    
                    mainHandler.post(() -> callback.onSuccess(progress));
                } else {
                    mainHandler.post(() -> callback.onFailure("Not enough gold!"));
                }
            }
        });
    }
    
    // Callback Interfaces
    public interface OnProgressCallback {
        void onSuccess(UserProgress progress);
        default void onFailure(String error) {}
    }
    
    public interface OnEnergyCallback {
        void onSuccess(int remainingEnergy);
        void onFailure(String error);
    }
    
    public interface OnLevelUpCallback {
        void onLevelUp(int newLevel);
        void onXpGained(int xp);
    }
    
    public interface OnUnlockCallback {
        void onUnlocked();
        void onAlreadyUnlocked();
    }
    
    public interface OnGoldCallback {
        void onSuccess(int remainingGold);
        void onFailure(String error);
    }
}
