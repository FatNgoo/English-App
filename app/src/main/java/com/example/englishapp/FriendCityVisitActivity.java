package com.example.englishapp;


import com.shop.englishapp.R;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class FriendCityVisitActivity extends AppCompatActivity {

    // Views
    private FrameLayout btnBack;
    private ImageView imgFriendAvatar;
    private TextView txtFriendCityName;
    private TextView txtVisitCount;
    private ImageView building1, building2, building3, building4;
    private ImageView bird, car, npcCharacter;
    private FrameLayout btnSendGift, btnLeave;
    private FrameLayout giftPopupOverlay;
    private FrameLayout giftOption1, giftOption2, giftOption3;
    private FrameLayout btnCancelGift;

    // Friend Data
    private String friendName;
    private int friendLevel;
    private String friendTier;
    private int visitCount;
    
    // Animation Handlers
    private Handler animationHandler;
    private Runnable birdAnimationRunnable;
    private Runnable npcWaveRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_city_visit);

        // Get friend data from intent
        Intent intent = getIntent();
        friendName = intent.getStringExtra("friend_name");
        friendLevel = intent.getIntExtra("friend_level", 1);
        friendTier = intent.getStringExtra("friend_tier");

        // Initialize views
        initializeViews();

        // Load visit data
        loadVisitData();

        // Setup click listeners
        setupClickListeners();

        // Start decorative animations
        startDecorativeAnimations();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        imgFriendAvatar = findViewById(R.id.imgFriendAvatar);
        txtFriendCityName = findViewById(R.id.txtFriendCityName);
        txtVisitCount = findViewById(R.id.txtVisitCount);
        
        building1 = findViewById(R.id.building1);
        building2 = findViewById(R.id.building2);
        building3 = findViewById(R.id.building3);
        building4 = findViewById(R.id.building4);
        
        bird = findViewById(R.id.bird);
        car = findViewById(R.id.car);
        npcCharacter = findViewById(R.id.npcCharacter);
        
        btnSendGift = findViewById(R.id.btnSendGift);
        btnLeave = findViewById(R.id.btnLeave);
        
        giftPopupOverlay = findViewById(R.id.giftPopupOverlay);
        giftOption1 = findViewById(R.id.giftOption1);
        giftOption2 = findViewById(R.id.giftOption2);
        giftOption3 = findViewById(R.id.giftOption3);
        btnCancelGift = findViewById(R.id.btnCancelGift);
        
        animationHandler = new Handler();
    }

    private void loadVisitData() {
        // Load visit count from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("EnglishAppPrefs", MODE_PRIVATE);
        visitCount = prefs.getInt("visit_count_" + friendName, 1);
        
        // Update UI
        txtFriendCityName.setText(friendName + "'s City — Level " + friendLevel);
        txtVisitCount.setText("Visit #" + visitCount);
        
        // Increment visit count
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("visit_count_" + friendName, visitCount + 1);
        editor.apply();
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Building interactions
        setupBuildingInteraction(building1);
        setupBuildingInteraction(building2);
        setupBuildingInteraction(building3);
        setupBuildingInteraction(building4);

        // Send gift button
        btnSendGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGiftPopup();
            }
        });

        // Leave button
        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Gift options
        giftOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGift("50 Coins");
            }
        });

        giftOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGift("XP Boost");
            }
        });

        giftOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGift("Rare Building");
            }
        });

        // Cancel gift button
        btnCancelGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideGiftPopup();
            }
        });

        // Click overlay to close
        giftPopupOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideGiftPopup();
            }
        });
    }

    private void setupBuildingInteraction(final ImageView building) {
        building.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateBuildingGlow(building);
            }
        });
    }

    private void animateBuildingGlow(final ImageView building) {
        // Scale animation
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(building, "scaleX", 1.0f, 1.1f, 1.0f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(building, "scaleY", 1.0f, 1.1f, 1.0f);
        scaleXAnimator.setDuration(300);
        scaleYAnimator.setDuration(300);
        scaleXAnimator.start();
        scaleYAnimator.start();

        // Brightness filter (glow effect)
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setScale(1.3f, 1.3f, 1.3f, 1.0f);
        ColorFilter brightnessFilter = new ColorMatrixColorFilter(colorMatrix);
        building.setColorFilter(brightnessFilter);

        // Remove filter after delay
        animationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                building.clearColorFilter();
            }
        }, 300);
    }

    private void startDecorativeAnimations() {
        // Bird flying animation
        birdAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                ObjectAnimator birdAnimator = ObjectAnimator.ofFloat(bird, "translationX", 0f, 1080f);
                birdAnimator.setDuration(5000);
                birdAnimator.setInterpolator(new LinearInterpolator());
                birdAnimator.start();
                
                // Repeat every 10 seconds
                animationHandler.postDelayed(this, 10000);
            }
        };
        animationHandler.post(birdAnimationRunnable);

        // Car moving animation
        ObjectAnimator carAnimator = ObjectAnimator.ofFloat(car, "translationX", 0f, 800f);
        carAnimator.setDuration(8000);
        carAnimator.setRepeatCount(ValueAnimator.INFINITE);
        carAnimator.setInterpolator(new LinearInterpolator());
        carAnimator.start();

        // NPC waving animation
        npcWaveRunnable = new Runnable() {
            @Override
            public void run() {
                ObjectAnimator waveAnimator = ObjectAnimator.ofFloat(npcCharacter, "rotation", -10f, 10f, -10f);
                waveAnimator.setDuration(2000);
                waveAnimator.start();
                
                // Repeat every 3 seconds
                animationHandler.postDelayed(this, 3000);
            }
        };
        animationHandler.post(npcWaveRunnable);
    }

    private void showGiftPopup() {
        giftPopupOverlay.setVisibility(View.VISIBLE);
        giftPopupOverlay.setAlpha(0f);
        giftPopupOverlay.animate()
                .alpha(1f)
                .setDuration(200)
                .start();
    }

    private void hideGiftPopup() {
        giftPopupOverlay.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        giftPopupOverlay.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    private void sendGift(String giftType) {
        // Save gift to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("EnglishAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_gift_to_" + friendName, giftType);
        editor.apply();

        // Show success message
        android.widget.Toast.makeText(this, "Sent " + giftType + " to " + friendName + "!", android.widget.Toast.LENGTH_SHORT).show();

        // Hide popup
        hideGiftPopup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up animation handlers
        if (animationHandler != null) {
            animationHandler.removeCallbacks(birdAnimationRunnable);
            animationHandler.removeCallbacks(npcWaveRunnable);
        }
    }
}
