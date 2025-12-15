package com.shop.englishapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.englishapp.AcademyLearningActivity;
import com.example.englishapp.MasterChefActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.shop.englishapp.R;

public class HomeActivity extends AppCompatActivity {

    // Header Views
    private TextView greetingText;
    private TextView subtitleText;
    private MaterialCardView avatarCard;
    private ImageView avatarImage;

    // Banner Views
    private MaterialCardView heroBanner;
    private TextView bannerTitle;
    private TextView bannerSubtitle;
    private MaterialButton continueLearningBtn;

    // Progress Views
    private ProgressBar learningProgressBar;
    private TextView progressPercent;

    // Bottom Navigation
    private BottomNavigationView bottomNavigation;
    
    // Master Chef Feature Cards
    private CardView cardAcademy;
    private CardView cardMasterChef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_home);

            // Initialize views
            initializeViews();

            // Setup listeners
            setupListeners();

            // Load user data (mock data for now)
            loadUserData();
            
            // NOTE: ProgressManager temporarily disabled to prevent crashes
            // Will re-enable after fixing the root cause
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading home: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Don't crash, just show error
        }
    }

    private void initializeViews() {
        try {
            // Header
            greetingText = findViewById(R.id.greetingText);
            subtitleText = findViewById(R.id.subtitleText);
            avatarCard = findViewById(R.id.avatarCard);
            avatarImage = findViewById(R.id.avatarImage);

            // Banner
            heroBanner = findViewById(R.id.heroBanner);
            bannerTitle = findViewById(R.id.bannerTitle);
            bannerSubtitle = findViewById(R.id.bannerSubtitle);
            continueLearningBtn = findViewById(R.id.continueLearningBtn);

            // Progress
            learningProgressBar = findViewById(R.id.learningProgressBar);
            progressPercent = findViewById(R.id.progressPercent);

            // Bottom Navigation
            bottomNavigation = findViewById(R.id.bottomNavigation);
            
            // Try finding Master Chef cards multiple ways
            cardAcademy = findViewById(R.id.cardAcademy);
            cardMasterChef = findViewById(R.id.cardMasterChef);
            
            android.util.Log.d("HomeActivity", "cardMasterChef after findViewById: " + cardMasterChef);
            
            // If null, try finding by traversing view tree
            if (cardMasterChef == null) {
                android.util.Log.e("HomeActivity", "cardMasterChef is NULL! Searching view tree...");
                cardMasterChef = findViewInTree(getWindow().getDecorView(), "cardMasterChef");
                android.util.Log.d("HomeActivity", "After tree search: " + cardMasterChef);
            }
            
            // Setup listeners immediately
            if (cardMasterChef != null) {
                android.util.Log.d("HomeActivity", "Setting up Master Chef listener NOW");
                setupMasterChefListener();
                Toast.makeText(this, "✅ Master Chef ready to click!", Toast.LENGTH_SHORT).show();
            } else {
                android.util.Log.e("HomeActivity", "FATAL: cardMasterChef is NULL even after tree search!");
                Toast.makeText(this, "❌ Master Chef card NOT FOUND in layout!", Toast.LENGTH_LONG).show();
            }
            
            if (cardAcademy != null) {
                setupAcademyListener();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing views: " + e.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
        }
    }
    
    // Helper method to find view by ID name in entire view tree
    private <T extends View> T findViewInTree(View root, String idName) {
        try {
            int id = getResources().getIdentifier(idName, "id", getPackageName());
            if (id != 0) {
                return root.findViewById(id);
            }
        } catch (Exception e) {
            android.util.Log.e("HomeActivity", "Error finding view: " + idName, e);
        }
        return null;
    }
    
    // XML onClick handler for Master Chef card
    public void onMasterChefClick(View view) {
        android.util.Log.d("HomeActivity", "🎯 onMasterChefClick called from XML!");
        Toast.makeText(this, "🎯 Master Chef clicked via XML!", Toast.LENGTH_SHORT).show();
        
        try {
            Intent intent = new Intent(HomeActivity.this, com.example.englishapp.MasterChefActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            android.util.Log.d("HomeActivity", "Starting MasterChefActivity from XML click...");
            startActivity(intent);
            android.util.Log.d("HomeActivity", "✅ MasterChefActivity started!");
        } catch (ActivityNotFoundException e) {
            android.util.Log.e("HomeActivity", "❌ Activity not found", e);
            Toast.makeText(this, "❌ Master Chef not found: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            android.util.Log.e("HomeActivity", "❌ Error starting activity", e);
            Toast.makeText(this, "❌ Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupListeners() {
        try {
            // Avatar click - open profile
            if (avatarCard != null) {
                avatarCard.setOnClickListener(v -> {
                    Toast.makeText(this, "Profile clicked!", Toast.LENGTH_SHORT).show();
                    // TODO: Navigate to Profile Activity
                });
            }

            // Continue Learning button
            if (continueLearningBtn != null) {
                continueLearningBtn.setOnClickListener(v -> {
                    // TEST MODE: Try both activities to see which works
                    android.util.Log.d("HomeActivity", "Continue button clicked - testing navigation");
                    
                    // First try: Simple test activity (should always work)
                    Toast.makeText(this, "Testing navigation with TestMasterChefActivity...", Toast.LENGTH_SHORT).show();
                    try {
                        Intent testIntent = new Intent(HomeActivity.this, com.example.englishapp.TestMasterChefActivity.class);
                        startActivity(testIntent);
                        android.util.Log.d("HomeActivity", "TestMasterChefActivity started");
                    } catch (Exception e) {
                        android.util.Log.e("HomeActivity", "TestMasterChefActivity failed", e);
                        
                        // Second try: Real MasterChefActivity
                        Toast.makeText(this, "Test failed, trying MasterChefActivity...", Toast.LENGTH_SHORT).show();
                        try {
                            Intent intent = new Intent(HomeActivity.this, MasterChefActivity.class);
                            startActivity(intent);
                            android.util.Log.d("HomeActivity", "MasterChefActivity started");
                        } catch (Exception e2) {
                            android.util.Log.e("HomeActivity", "MasterChefActivity also failed", e2);
                            Toast.makeText(this, "❌ Both failed: " + e2.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            // Bottom Navigation
            if (bottomNavigation != null) {
                bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int itemId = item.getItemId();
                        
                        if (itemId == R.id.nav_home) {
                            // Already on home
                            return true;
                        } else if (itemId == R.id.nav_lessons) {
                            Toast.makeText(HomeActivity.this, "Lessons", Toast.LENGTH_SHORT).show();
                            // TODO: Navigate to Lessons Activity
                            return true;
                        } else if (itemId == R.id.nav_games) {
                            Toast.makeText(HomeActivity.this, "Games", Toast.LENGTH_SHORT).show();
                            // TODO: Navigate to Games Activity
                            return true;
                        } else if (itemId == R.id.nav_profile) {
                            Toast.makeText(HomeActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                            // TODO: Navigate to Profile Activity
                            return true;
                        }
                        return false;
                    }
                });

                // Set home as selected by default
                bottomNavigation.setSelectedItemId(R.id.nav_home);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting up listeners: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void initializeMasterChefProgress() {
        // Temporarily disabled to prevent crashes
        // Will re-enable after fixing the root cause
    }
    private void loadUserData() {
        try {
            // Mock data - in real app, load from database or API
            String userName = "little explorer";
            int progress = 65;

            // Update greeting
            if (greetingText != null) {
                greetingText.setText("Hello, " + userName + "!");
            }

            // Update progress
            if (learningProgressBar != null) {
                learningProgressBar.setProgress(progress);
            }
            if (progressPercent != null) {
                progressPercent.setText(progress + "%");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Don't crash if loading user data fails
        }
    }
    
    private void setupAcademyListener() {
        if (cardAcademy != null) {
            cardAcademy.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(HomeActivity.this, AcademyLearningActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(HomeActivity.this, "Cannot open Academy", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });
        }
    }
    
    private void setupMasterChefListener() {
        android.util.Log.d("HomeActivity", "setupMasterChefListener called");
        
        if (cardMasterChef != null) {
            android.util.Log.d("HomeActivity", "Setting click listener on cardMasterChef");
            
            cardMasterChef.setOnClickListener(v -> {
                android.util.Log.d("HomeActivity", "Master Chef card CLICKED!");
                Toast.makeText(HomeActivity.this, "🍳 Opening Master Chef... Please wait!", Toast.LENGTH_SHORT).show();
                
                try {
                    Intent intent = new Intent(HomeActivity.this, MasterChefActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    android.util.Log.d("HomeActivity", "Master Chef activity started successfully");
                } catch (ActivityNotFoundException anfe) {
                    Toast.makeText(HomeActivity.this, "❌ Master Chef activity not found!", Toast.LENGTH_LONG).show();
                    anfe.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(HomeActivity.this, "❌ Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            
            android.util.Log.d("HomeActivity", "Click listener set successfully");
        } else {
            android.util.Log.e("HomeActivity", "Cannot setup listener - cardMasterChef is NULL!");
            Toast.makeText(this, "⚠️ Cannot setup listener - card is NULL!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        // Exit app confirmation or go to previous activity
        super.onBackPressed();
    }
}
