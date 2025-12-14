package com.example.englishapp;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SocialArenaActivity extends AppCompatActivity {

    // Views
    private FrameLayout btnBack;
    private FrameLayout btnNotifications;
    private FrameLayout btnRanking;
    private TextView txtNotificationBadge;
    private ImageView imgPlayerAvatar;
    private TextView txtPlayerName;
    private ProgressBar xpProgressBar;
    private TextView txtXPProgress;
    private TextView txtWinRate;
    private TextView txtTierRank;
    private CardView playerCard;
    private CardView cardVisitFriends;
    private CardView cardPKBattle;
    private TextView txtFriendsOnline;

    // Player Data
    private SharedPreferences sharedPreferences;
    private String playerName;
    private int playerLevel;
    private int currentXP;
    private int maxXP;
    private int winRate;
    private String tierRank;
    private int friendsOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_arena);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("EnglishAppPrefs", MODE_PRIVATE);

        // Initialize views
        initializeViews();

        // Load player data
        loadPlayerData();

        // Setup click listeners
        setupClickListeners();

        // Animate entrance
        animateEntrance();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnRanking = findViewById(R.id.btnRanking);
        txtNotificationBadge = findViewById(R.id.txtNotificationBadge);
        
        imgPlayerAvatar = findViewById(R.id.imgPlayerAvatar);
        txtPlayerName = findViewById(R.id.txtPlayerName);
        xpProgressBar = findViewById(R.id.xpProgressBar);
        txtXPProgress = findViewById(R.id.txtXPProgress);
        txtWinRate = findViewById(R.id.txtWinRate);
        txtTierRank = findViewById(R.id.txtTierRank);
        
        playerCard = findViewById(R.id.playerCard);
        cardVisitFriends = findViewById(R.id.cardVisitFriends);
        cardPKBattle = findViewById(R.id.cardPKBattle);
        txtFriendsOnline = findViewById(R.id.txtFriendsOnline);
    }

    private void loadPlayerData() {
        // Load player stats from SharedPreferences
        playerName = sharedPreferences.getString("player_name", "Jenny");
        playerLevel = sharedPreferences.getInt("player_level", 12);
        currentXP = sharedPreferences.getInt("current_xp", 650);
        maxXP = sharedPreferences.getInt("max_xp", 1000);
        winRate = sharedPreferences.getInt("win_rate", 68);
        tierRank = sharedPreferences.getString("tier_rank", "Silver");
        friendsOnline = 12; // Mock data

        // Update UI
        txtPlayerName.setText(playerName + " • Level " + playerLevel);
        txtXPProgress.setText(currentXP + "/" + maxXP);
        txtWinRate.setText(winRate + "%");
        txtTierRank.setText(tierRank);
        txtFriendsOnline.setText(friendsOnline + " friends online");

        // Set XP progress bar
        int xpPercentage = (currentXP * 100) / maxXP;
        xpProgressBar.setProgress(xpPercentage);

        // Set notification badge
        int notificationCount = sharedPreferences.getInt("notification_count", 3);
        txtNotificationBadge.setText(String.valueOf(notificationCount));
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonPress(v);
                onBackPressed();
            }
        });

        // Notification button
        btnNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonPress(v);
                showNotificationsPopup();
            }
        });

        // Ranking button
        btnRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonPress(v);
                openLeaderboard();
            }
        });

        // Visit Friends card
        cardVisitFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateCardPress(cardVisitFriends);
                openFriendCity();
            }
        });

        // PK Battle card
        cardPKBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateCardPress(cardPKBattle);
                startPKBattle();
            }
        });
    }

    private void animateEntrance() {
        // Player card entrance animation
        playerCard.setAlpha(0f);
        playerCard.setScaleX(0.8f);
        playerCard.setScaleY(0.8f);

        playerCard.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(400)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Mode cards entrance animation
        cardVisitFriends.setTranslationX(-300f);
        cardVisitFriends.setAlpha(0f);
        cardVisitFriends.animate()
                .translationX(0f)
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(200)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        cardPKBattle.setTranslationX(300f);
        cardPKBattle.setAlpha(0f);
        cardPKBattle.animate()
                .translationX(0f)
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void animateButtonPress(View view) {
        view.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                    }
                })
                .start();
    }

    private void animateCardPress(CardView card) {
        card.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        card.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                    }
                })
                .start();
    }

    private void showNotificationsPopup() {
        // TODO: Show notifications dialog with battle requests
        // For now, just show a toast
        android.widget.Toast.makeText(this, "You have " + txtNotificationBadge.getText() + " new battle requests!", android.widget.Toast.LENGTH_SHORT).show();
    }

    private void openLeaderboard() {
        // TODO: Open leaderboard activity
        android.widget.Toast.makeText(this, "Leaderboard opening soon!", android.widget.Toast.LENGTH_SHORT).show();
    }

    private void openFriendCity() {
        // Start Friend City Visit Activity
        Intent intent = new Intent(this, FriendCityVisitActivity.class);
        intent.putExtra("friend_name", "Tom");
        intent.putExtra("friend_level", 18);
        intent.putExtra("friend_tier", "Gold");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void startPKBattle() {
        // Start PK Battle Activity
        Intent intent = new Intent(this, PKBattleActivity.class);
        intent.putExtra("player_name", playerName);
        intent.putExtra("player_level", playerLevel);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload player data in case it was updated
        loadPlayerData();
    }
}
