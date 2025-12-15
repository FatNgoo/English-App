package com.shop.englishapp.masterchef.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.shop.englishapp.masterchef.models.UserProgress;

/**
 * Room Database for Master Chef feature
 * Singleton pattern to ensure only one instance
 */
@Database(entities = {UserProgress.class}, version = 1, exportSchema = false)
public abstract class MasterChefDatabase extends RoomDatabase {
    
    private static MasterChefDatabase instance;
    
    public abstract UserProgressDao userProgressDao();
    
    public static synchronized MasterChefDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    MasterChefDatabase.class,
                    "master_chef_database"
            )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()  // Allow queries on main thread for now
            .build();
        }
        return instance;
    }
}
