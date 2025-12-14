package com.shop.englishapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        initializeViews();

        // Setup listeners
        setupListeners();

        // Load user data (mock data for now)
        loadUserData();
    }

    private void initializeViews() {
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
    }

    private void setupListeners() {
        // Avatar click - open profile
        avatarCard.setOnClickListener(v -> {
            Toast.makeText(this, "Profile clicked!", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Profile Activity
        });

        // Continue Learning button
        continueLearningBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Starting lesson: Animals & Food", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Lesson Activity
        });

        // Bottom Navigation
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

    private void loadUserData() {
        // Mock data - in real app, load from database or API
        String userName = "little explorer";
        int progress = 65;

        // Update greeting
        greetingText.setText("Hello, " + userName + "!");

        // Update progress
        learningProgressBar.setProgress(progress);
        progressPercent.setText(progress + "%");
    }

    @Override
    public void onBackPressed() {
        // Exit app confirmation or go to previous activity
        super.onBackPressed();
    }
}
