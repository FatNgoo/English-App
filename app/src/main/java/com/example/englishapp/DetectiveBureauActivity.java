package com.example.englishapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DetectiveBureauActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack, btnHint, btnZoom, btnReplayWord, btnPronunciation;
    private LinearLayout btnReadAloud, btnReplay, btnNextMystery;
    private ImageView imgScene, imgTargetObject, imgCorrectFeedback, imgWrongFeedback;
    private ImageView imgSparkles1, imgSparkles2, imgSparkles3, imgConfetti;
    private TextView txtTargetWord, txtDifficulty, txtGreatJob, txtSingleProgress, txtTimer, txtScore;
    private FrameLayout sceneContainer, glowOverlay, successOverlay;
    private LinearLayout challengeInfoContainer, progressItemsContainer;
    
    // Progress item backgrounds
    private ImageView progressItem1Bg, progressItem2Bg, progressItem3Bg;

    // Game State
    private String currentTargetWord = "apple";
    private List<String> targetObjects = new ArrayList<>();
    private List<Boolean> objectsFound = new ArrayList<>();
    private int objectsFoundCount = 0;
    private int totalObjectsToFind = 3;
    private boolean isSingleObjectMode = true;
    
    // Hidden object coordinates (normalized 0-1, relative to scene size)
    private float targetObjectX = 0.35f; // 35% from left
    private float targetObjectY = 0.45f; // 45% from top
    private float targetObjectRadius = 0.08f; // 8% of scene width for tap detection
    
    // Zoom & Pinch
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private float minScale = 1.0f;
    private float maxScale = 3.0f;
    
    // Challenge Mode
    private boolean isChallengeMode = false;
    private int remainingTime = 90; // 1:30 in seconds
    private int coinsEarned = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    
    // Difficulty Level
    private String difficulty = "Easy"; // Easy, Medium, Hard
    
    private Handler handler = new Handler();
    private Random random = new Random();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detective_bureau);

        initializeViews();
        setupGame();
        setupListeners();
        setupSceneInteraction();
        
        if (isChallengeMode) {
            startTimer();
        }
    }

    private void initializeViews() {
        // Buttons
        btnBack = findViewById(R.id.btnBack);
        btnHint = findViewById(R.id.btnHint);
        btnZoom = findViewById(R.id.btnZoom);
        btnReplayWord = findViewById(R.id.btnReplayWord);
        btnPronunciation = findViewById(R.id.btnPronunciation);
        btnReadAloud = findViewById(R.id.btnReadAloud);
        btnReplay = findViewById(R.id.btnReplay);
        btnNextMystery = findViewById(R.id.btnNextMystery);
        
        // ImageViews
        imgScene = findViewById(R.id.imgScene);
        imgTargetObject = findViewById(R.id.imgTargetObject);
        imgCorrectFeedback = findViewById(R.id.imgCorrectFeedback);
        imgWrongFeedback = findViewById(R.id.imgWrongFeedback);
        imgSparkles1 = findViewById(R.id.imgSparkles1);
        imgSparkles2 = findViewById(R.id.imgSparkles2);
        imgSparkles3 = findViewById(R.id.imgSparkles3);
        imgConfetti = findViewById(R.id.imgConfetti);
        
        // TextViews
        txtTargetWord = findViewById(R.id.txtTargetWord);
        txtDifficulty = findViewById(R.id.txtDifficulty);
        txtGreatJob = findViewById(R.id.txtGreatJob);
        txtSingleProgress = findViewById(R.id.txtSingleProgress);
        txtTimer = findViewById(R.id.txtTimer);
        txtScore = findViewById(R.id.txtScore);
        
        // Containers
        sceneContainer = findViewById(R.id.sceneContainer);
        glowOverlay = findViewById(R.id.glowOverlay);
        successOverlay = findViewById(R.id.successOverlay);
        challengeInfoContainer = findViewById(R.id.challengeInfoContainer);
        progressItemsContainer = findViewById(R.id.progressItemsContainer);
        
        // Progress items
        progressItem1Bg = findViewById(R.id.progressItem1Bg);
        progressItem2Bg = findViewById(R.id.progressItem2Bg);
        progressItem3Bg = findViewById(R.id.progressItem3Bg);
    }

    private void setupGame() {
        // Initialize target objects
        targetObjects.add("apple");
        targetObjects.add("ball");
        targetObjects.add("pencil");
        
        // Initialize found status
        for (int i = 0; i < targetObjects.size(); i++) {
            objectsFound.add(false);
        }
        
        // Set current target
        currentTargetWord = targetObjects.get(0);
        txtTargetWord.setText(currentTargetWord);
        
        // Set difficulty
        setDifficulty(difficulty);
        
        // Configure mode
        if (isSingleObjectMode) {
            totalObjectsToFind = 1;
            progressItemsContainer.setVisibility(View.GONE);
            txtSingleProgress.setVisibility(View.VISIBLE);
            txtSingleProgress.setText("0/1 Found");
        } else {
            totalObjectsToFind = targetObjects.size();
            progressItemsContainer.setVisibility(View.VISIBLE);
            txtSingleProgress.setVisibility(View.GONE);
        }
        
        // Show/hide challenge mode UI
        if (isChallengeMode) {
            challengeInfoContainer.setVisibility(View.VISIBLE);
            updateTimerDisplay();
            txtScore.setText("+" + coinsEarned);
        } else {
            challengeInfoContainer.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            animateButtonPress(v);
            finish();
        });
        
        btnHint.setOnClickListener(v -> {
            animateButtonPress(v);
            showHint();
        });
        
        btnZoom.setOnClickListener(v -> {
            animateButtonPress(v);
            autoZoomToTarget();
        });
        
        btnReplayWord.setOnClickListener(v -> {
            animateButtonPress(v);
            playWordPronunciation(currentTargetWord);
        });
        
        btnPronunciation.setOnClickListener(v -> {
            animateButtonPress(v);
            playWordPronunciation(currentTargetWord);
        });
        
        btnReadAloud.setOnClickListener(v -> {
            animatePulse(v);
            playWordPronunciation(currentTargetWord);
        });
        
        btnReplay.setOnClickListener(v -> {
            animateButtonPress(v);
            replayScene();
        });
        
        btnNextMystery.setOnClickListener(v -> {
            animateButtonPress(v);
            loadNextMystery();
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupSceneInteraction() {
        // Setup pinch-to-zoom
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(minScale, Math.min(scaleFactor, maxScale));
                
                imgScene.setScaleX(scaleFactor);
                imgScene.setScaleY(scaleFactor);
                return true;
            }
        });
        
        // Setup tap detection on scene
        imgScene.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                handleSceneTap(event.getX(), event.getY());
            }
            
            return true;
        });
    }

    private void handleSceneTap(float tapX, float tapY) {
        // Get scene dimensions
        int sceneWidth = imgScene.getWidth();
        int sceneHeight = imgScene.getHeight();
        
        // Normalize tap coordinates
        float normalizedTapX = tapX / sceneWidth;
        float normalizedTapY = tapY / sceneHeight;
        
        // Calculate distance from target object center
        float deltaX = normalizedTapX - targetObjectX;
        float deltaY = normalizedTapY - targetObjectY;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        // Check if tap is within target radius
        if (distance <= targetObjectRadius) {
            handleCorrectTap(tapX, tapY);
        } else {
            handleWrongTap(tapX, tapY);
        }
    }

    private void handleCorrectTap(float tapX, float tapY) {
        // Mark object as found
        if (isSingleObjectMode) {
            objectsFoundCount = 1;
        } else {
            int currentIndex = targetObjects.indexOf(currentTargetWord);
            if (currentIndex >= 0 && !objectsFound.get(currentIndex)) {
                objectsFound.set(currentIndex, true);
                objectsFoundCount++;
                updateProgressDisplay(currentIndex);
            }
        }
        
        // Show correct feedback
        showCorrectFeedback(tapX, tapY);
        
        // Show sparkles animation
        showSparkles(tapX, tapY);
        
        // Show "Great job!" text
        showGreatJobText();
        
        // Update progress
        updateProgressText();
        
        // Check if all objects found
        handler.postDelayed(() -> {
            if (objectsFoundCount >= totalObjectsToFind) {
                showSuccessPopup();
            } else {
                // Load next target object in multi-object mode
                loadNextTargetObject();
            }
        }, 2000);
    }

    private void handleWrongTap(float tapX, float tapY) {
        // Position wrong feedback at tap location
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imgWrongFeedback.getLayoutParams();
        params.leftMargin = (int) tapX - imgWrongFeedback.getWidth() / 2;
        params.topMargin = (int) tapY - imgWrongFeedback.getHeight() / 2;
        imgWrongFeedback.setLayoutParams(params);
        
        // Show wrong feedback with shake animation
        imgWrongFeedback.setVisibility(View.VISIBLE);
        imgWrongFeedback.setAlpha(1.0f);
        
        // Shake scene
        animateShake(sceneContainer);
        
        // Fade out wrong icon
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(imgWrongFeedback, "alpha", 1.0f, 0f);
        fadeOut.setDuration(800);
        fadeOut.setStartDelay(400);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imgWrongFeedback.setVisibility(View.INVISIBLE);
            }
        });
        fadeOut.start();
    }

    private void showCorrectFeedback(float tapX, float tapY) {
        // Position correct feedback at tap location
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imgCorrectFeedback.getLayoutParams();
        params.leftMargin = (int) tapX - imgCorrectFeedback.getWidth() / 2;
        params.topMargin = (int) tapY - imgCorrectFeedback.getHeight() / 2;
        imgCorrectFeedback.setLayoutParams(params);
        
        // Show with scale animation
        imgCorrectFeedback.setVisibility(View.VISIBLE);
        imgCorrectFeedback.setScaleX(0.5f);
        imgCorrectFeedback.setScaleY(0.5f);
        imgCorrectFeedback.setAlpha(1.0f);
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imgCorrectFeedback, "scaleX", 0.5f, 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imgCorrectFeedback, "scaleY", 0.5f, 1.2f, 1.0f);
        scaleX.setDuration(600);
        scaleY.setDuration(600);
        scaleX.start();
        scaleY.start();
        
        // Fade out after delay
        handler.postDelayed(() -> {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(imgCorrectFeedback, "alpha", 1.0f, 0f);
            fadeOut.setDuration(400);
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    imgCorrectFeedback.setVisibility(View.INVISIBLE);
                }
            });
            fadeOut.start();
        }, 1000);
    }

    private void showSparkles(float centerX, float centerY) {
        // Position sparkles around tap location
        showSingleSparkle(imgSparkles1, centerX - 30, centerY - 40);
        showSingleSparkle(imgSparkles2, centerX + 35, centerY - 20);
        showSingleSparkle(imgSparkles3, centerX + 10, centerY + 40);
    }

    private void showSingleSparkle(ImageView sparkle, float x, float y) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sparkle.getLayoutParams();
        params.leftMargin = (int) x;
        params.topMargin = (int) y;
        sparkle.setLayoutParams(params);
        
        sparkle.setVisibility(View.VISIBLE);
        sparkle.setScaleX(0.3f);
        sparkle.setScaleY(0.3f);
        sparkle.setAlpha(1.0f);
        sparkle.setRotation(0);
        
        // Scale up and rotate
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(sparkle, "scaleX", 0.3f, 1.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(sparkle, "scaleY", 0.3f, 1.5f);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(sparkle, "rotation", 0, 360);
        ObjectAnimator fade = ObjectAnimator.ofFloat(sparkle, "alpha", 1.0f, 0f);
        
        scaleX.setDuration(800);
        scaleY.setDuration(800);
        rotate.setDuration(800);
        fade.setDuration(800);
        fade.setStartDelay(400);
        
        scaleX.start();
        scaleY.start();
        rotate.start();
        fade.start();
        
        fade.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                sparkle.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showGreatJobText() {
        txtGreatJob.setVisibility(View.VISIBLE);
        txtGreatJob.setScaleX(0.5f);
        txtGreatJob.setScaleY(0.5f);
        txtGreatJob.setAlpha(0f);
        
        // Scale and fade in
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(txtGreatJob, "scaleX", 0.5f, 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(txtGreatJob, "scaleY", 0.5f, 1.2f, 1.0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(txtGreatJob, "alpha", 0f, 1.0f);
        
        scaleX.setDuration(600);
        scaleY.setDuration(600);
        fadeIn.setDuration(400);
        
        scaleX.start();
        scaleY.start();
        fadeIn.start();
        
        // Fade out after delay
        handler.postDelayed(() -> {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(txtGreatJob, "alpha", 1.0f, 0f);
            fadeOut.setDuration(400);
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    txtGreatJob.setVisibility(View.INVISIBLE);
                }
            });
            fadeOut.start();
        }, 1500);
    }

    private void showHint() {
        // Position glow overlay at target object location
        int sceneWidth = imgScene.getWidth();
        int sceneHeight = imgScene.getHeight();
        
        int glowX = (int) (targetObjectX * sceneWidth) - glowOverlay.getWidth() / 2;
        int glowY = (int) (targetObjectY * sceneHeight) - glowOverlay.getHeight() / 2;
        
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) glowOverlay.getLayoutParams();
        params.leftMargin = glowX;
        params.topMargin = glowY;
        glowOverlay.setLayoutParams(params);
        
        // Animate glow (pulse effect)
        glowOverlay.setVisibility(View.VISIBLE);
        glowOverlay.setAlpha(0f);
        
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(glowOverlay, "alpha", 0f, 0.8f);
        fadeIn.setDuration(800);
        fadeIn.setRepeatCount(2);
        fadeIn.setRepeatMode(ObjectAnimator.REVERSE);
        fadeIn.start();
        
        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                handler.postDelayed(() -> {
                    ObjectAnimator fadeOut = ObjectAnimator.ofFloat(glowOverlay, "alpha", 0.8f, 0f);
                    fadeOut.setDuration(600);
                    fadeOut.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            glowOverlay.setVisibility(View.INVISIBLE);
                        }
                    });
                    fadeOut.start();
                }, 500);
            }
        });
        
        // Pulse hint button
        animatePulse(btnHint);
    }

    private void autoZoomToTarget() {
        // Zoom in to target object area
        scaleFactor = 2.0f;
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imgScene, "scaleX", imgScene.getScaleX(), scaleFactor);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imgScene, "scaleY", imgScene.getScaleY(), scaleFactor);
        
        scaleX.setDuration(800);
        scaleY.setDuration(800);
        
        scaleX.start();
        scaleY.start();
        
        // Pan to target (would need to implement translation)
        // For simplicity, just zoom here
    }

    private void playWordPronunciation(String word) {
        // Simulate audio playback with button pulse
        animatePulse(btnPronunciation);
        
        // In real app, would use TextToSpeech or audio files:
        // textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void updateProgressDisplay(int index) {
        // Update progress item background to "found" state
        switch (index) {
            case 0:
                progressItem1Bg.setBackgroundResource(R.drawable.bg_progress_item_found);
                break;
            case 1:
                progressItem2Bg.setBackgroundResource(R.drawable.bg_progress_item_found);
                break;
            case 2:
                progressItem3Bg.setBackgroundResource(R.drawable.bg_progress_item_found);
                break;
        }
        
        // Animate the progress item
        ImageView itemBg = null;
        switch (index) {
            case 0: itemBg = progressItem1Bg; break;
            case 1: itemBg = progressItem2Bg; break;
            case 2: itemBg = progressItem3Bg; break;
        }
        
        if (itemBg != null) {
            animatePulse(itemBg);
        }
    }

    private void updateProgressText() {
        if (isSingleObjectMode) {
            txtSingleProgress.setText(objectsFoundCount + "/1 Found");
        }
        // Multi-object mode uses visual progress items
    }

    private void loadNextTargetObject() {
        // Find next unfound object
        for (int i = 0; i < targetObjects.size(); i++) {
            if (!objectsFound.get(i)) {
                currentTargetWord = targetObjects.get(i);
                txtTargetWord.setText(currentTargetWord);
                
                // Update target object image (would load appropriate drawable)
                // For now, just update text
                
                // Update target coordinates for new object (simplified - would be different per object)
                targetObjectX = 0.35f + (i * 0.2f);
                targetObjectY = 0.45f;
                
                break;
            }
        }
    }

    private void showSuccessPopup() {
        // Stop timer if challenge mode
        if (isChallengeMode) {
            stopTimer();
            calculateCoins();
        }
        
        // Show overlay with animation
        successOverlay.setVisibility(View.VISIBLE);
        successOverlay.setAlpha(0f);
        
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(successOverlay, "alpha", 0f, 1.0f);
        fadeIn.setDuration(400);
        fadeIn.start();
        
        // Animate confetti
        animateConfetti();
    }

    private void animateConfetti() {
        imgConfetti.setRotation(0);
        
        ObjectAnimator rotate = ObjectAnimator.ofFloat(imgConfetti, "rotation", 0, 360);
        rotate.setDuration(2000);
        rotate.setRepeatCount(ObjectAnimator.INFINITE);
        rotate.start();
    }

    private void replayScene() {
        // Reset game state
        objectsFoundCount = 0;
        for (int i = 0; i < objectsFound.size(); i++) {
            objectsFound.set(i, false);
        }
        
        // Reset UI
        progressItem1Bg.setBackgroundResource(R.drawable.bg_progress_item);
        progressItem2Bg.setBackgroundResource(R.drawable.bg_progress_item);
        progressItem3Bg.setBackgroundResource(R.drawable.bg_progress_item);
        
        updateProgressText();
        
        // Reset target
        currentTargetWord = targetObjects.get(0);
        txtTargetWord.setText(currentTargetWord);
        targetObjectX = 0.35f;
        targetObjectY = 0.45f;
        
        // Reset zoom
        scaleFactor = 1.0f;
        imgScene.setScaleX(1.0f);
        imgScene.setScaleY(1.0f);
        
        // Hide popup
        successOverlay.setVisibility(View.GONE);
        
        // Restart timer if challenge mode
        if (isChallengeMode) {
            remainingTime = 90;
            startTimer();
        }
    }

    private void loadNextMystery() {
        // In real app, would load new scene with different objects
        // For now, just replay
        replayScene();
        successOverlay.setVisibility(View.GONE);
    }

    private void setDifficulty(String level) {
        difficulty = level;
        txtDifficulty.setText(level);
        
        switch (level) {
            case "Easy":
                txtDifficulty.setBackgroundResource(R.drawable.bg_difficulty_easy);
                targetObjectRadius = 0.1f; // Larger hit area
                break;
            case "Medium":
                txtDifficulty.setBackgroundResource(R.drawable.bg_difficulty_medium);
                targetObjectRadius = 0.08f;
                break;
            case "Hard":
                txtDifficulty.setBackgroundResource(R.drawable.bg_difficulty_hard);
                targetObjectRadius = 0.06f; // Smaller hit area
                break;
        }
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (remainingTime > 0) {
                    remainingTime--;
                    updateTimerDisplay();
                    timerHandler.postDelayed(this, 1000);
                } else {
                    // Time's up - show failure or reduce rewards
                    handleTimeUp();
                }
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void stopTimer() {
        if (timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    private void updateTimerDisplay() {
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        txtTimer.setText(String.format(Locale.getDefault(), "%d:%02d", minutes, seconds));
    }

    private void calculateCoins() {
        // Award bonus coins based on remaining time
        int timeBonus = remainingTime / 2;
        coinsEarned += 30 + timeBonus;
        txtScore.setText("+" + coinsEarned);
    }

    private void handleTimeUp() {
        // Show message or reduced rewards
        // For now, just allow continuing without time bonus
    }

    // Animation Helpers
    private void animateButtonPress(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.9f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.9f, 1.0f);
        scaleX.setDuration(200);
        scaleY.setDuration(200);
        scaleX.start();
        scaleY.start();
    }

    private void animatePulse(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.15f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.15f, 1.0f);
        scaleX.setDuration(600);
        scaleY.setDuration(600);
        scaleX.setRepeatCount(1);
        scaleY.setRepeatCount(1);
        scaleX.start();
        scaleY.start();
    }

    private void animateShake(View view) {
        ObjectAnimator shake1 = ObjectAnimator.ofFloat(view, "translationX", 0, -10, 10, -10, 10, 0);
        shake1.setDuration(500);
        shake1.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (timerHandler != null) {
            timerHandler.removeCallbacksAndMessages(null);
        }
    }
}
