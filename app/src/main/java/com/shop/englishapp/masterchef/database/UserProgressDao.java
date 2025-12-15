package com.shop.englishapp.masterchef.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.shop.englishapp.masterchef.models.UserProgress;

/**
 * Data Access Object for UserProgress
 * Handles all database operations for user's Master Chef progress
 */
@Dao
public interface UserProgressDao {
    
    @Insert
    void insertUserProgress(UserProgress progress);
    
    @Update
    void updateUserProgress(UserProgress progress);
    
    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    UserProgress getUserProgress();
    
    @Query("UPDATE user_progress SET energy = :energy, lastEnergyUpdate = :timestamp WHERE id = 1")
    void updateEnergy(int energy, long timestamp);
    
    @Query("UPDATE user_progress SET gold = :gold WHERE id = 1")
    void updateGold(int gold);
    
    @Query("UPDATE user_progress SET totalStarsEarned = :stars WHERE id = 1")
    void updateStars(int stars);
    
    @Query("UPDATE user_progress SET foodLessonCompleted = :completed, masterChefUnlocked = :unlocked WHERE id = 1")
    void updateFoodLesson(boolean completed, boolean unlocked);
    
    @Query("UPDATE user_progress SET experiencePoints = :xp, currentLevel = :level WHERE id = 1")
    void updateProgress(int xp, int level);
    
    @Query("DELETE FROM user_progress")
    void deleteAll();
}
