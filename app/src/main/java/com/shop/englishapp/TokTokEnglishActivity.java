package com.shop.englishapp;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

/**
 * TokTokEnglishActivity - Cinema Mode for learning English through short videos
 * Features:
 * - Vertical short video player (TikTok/Reels style)
 * - Interactive questions after videos
 * - Swipe up to next video
 * - Answer validation with animations
 * - Progress tracking with dots
 */
public class TokTokEnglishActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack;
    private ImageView ivVolume;
    private TextView tvSubtitle;
    private LinearLayout swipeIndicator;
    private CardView questionCard;
    private CardView explanationCard;
    private Button btnOption1, btnOption2, btnOption3;
    private Button btnContinue;
    private TextView tvExplanation;

    // State variables
    private boolean isMuted = false;
    private boolean isQuestionShown = false;
    private int currentVideoIndex = 0;
    private int correctAnswerIndex = 1; // Option 1 is correct in this example

    // Gesture detector for swipe
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toktok_english);

        // Initialize UI components
        initializeViews();

        // Set up click listeners
        setupClickListeners();

        // Set up gesture detection for swipe
        setupGestureDetection();

        // Start video playback simulation
        playCurrentVideo();
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        ivVolume = findViewById(R.id.ivVolume);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        swipeIndicator = findViewById(R.id.swipeIndicator);
        questionCard = findViewById(R.id.questionCard);
        explanationCard = findViewById(R.id.explanationCard);
        
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnContinue = findViewById(R.id.btnContinue);
        tvExplanation = findViewById(R.id.tvExplanation);
    }

    /**
     * Set up click listeners for interactive elements
     */
    private void setupClickListeners() {
        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Action buttons
        View btnLike = findViewById(R.id.btnLike);
        if (btnLike != null) {
            btnLike.setOnClickListener(v -> {
                Toast.makeText(this, "❤️ Liked!", Toast.LENGTH_SHORT).show();
            });
        }

        View btnRepeat = findViewById(R.id.btnRepeat);
        if (btnRepeat != null) {
            btnRepeat.setOnClickListener(v -> {
                playCurrentVideo();
                Toast.makeText(this, "🔄 Replaying video", Toast.LENGTH_SHORT).show();
            });
        }

        View btnVolume = findViewById(R.id.btnVolume);
        if (btnVolume != null) {
            btnVolume.setOnClickListener(v -> {
                isMuted = !isMuted;
                if (ivVolume != null) {
                    ivVolume.setImageResource(isMuted ? 
                        R.drawable.ic_volume_off : R.drawable.ic_volume_on);
                }
                Toast.makeText(this, isMuted ? "🔇 Muted" : "🔊 Unmuted", Toast.LENGTH_SHORT).show();
            });
        }

        // Answer option buttons
        if (btnOption1 != null) {
            btnOption1.setOnClickListener(v -> checkAnswer(0));
        }
        if (btnOption2 != null) {
            btnOption2.setOnClickListener(v -> checkAnswer(1));
        }
        if (btnOption3 != null) {
            btnOption3.setOnClickListener(v -> checkAnswer(2));
        }

        // Continue button
        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                hideExplanationCard();
                loadNextVideo();
            });
        }
    }

    /**
     * Set up gesture detection for swipe up/down
     */
    private void setupGestureDetection() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffY = e2.getY() - e1.getY();
                
                // Swipe up (next video)
                if (diffY < -100 && Math.abs(velocityY) > 100) {
                    if (!isQuestionShown) {
                        showQuestionCard();
                    }
                    return true;
                }
                
                // Swipe down (previous video - optional)
                if (diffY > 100 && Math.abs(velocityY) > 100) {
                    if (!isQuestionShown) {
                        Toast.makeText(TokTokEnglishActivity.this, 
                            "First video", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    /**
     * Play current video
     */
    private void playCurrentVideo() {
        // In a real app, this would start video playback
        // For now, simulate video ending after a delay
        tvSubtitle.setText("Hi! Can I get a ticket for the movie tonight?");
        swipeIndicator.setVisibility(View.VISIBLE);
        isQuestionShown = false;
        
        // Simulate video ending after 5 seconds
        tvSubtitle.postDelayed(this::onVideoEnded, 5000);
    }

    /**
     * Called when video ends
     */
    private void onVideoEnded() {
        // Auto-show question card or wait for swipe
        swipeIndicator.setVisibility(View.VISIBLE);
        // Optionally auto-show after a delay
        // showQuestionCard();
    }

    /**
     * Show question card with slide-up animation
     */
    private void showQuestionCard() {
        if (isQuestionShown) return;
        
        isQuestionShown = true;
        swipeIndicator.setVisibility(View.GONE);
        questionCard.setVisibility(View.VISIBLE);
        
        // Slide up animation
        ObjectAnimator animator = ObjectAnimator.ofFloat(
            questionCard, "translationY", 
            questionCard.getHeight(), 0f
        );
        animator.setDuration(300);
        animator.start();
        
        // Reset answer buttons
        resetAnswerButtons();
    }

    /**
     * Reset answer button states
     */
    private void resetAnswerButtons() {
        btnOption1.setBackgroundResource(R.drawable.bg_answer_option);
        btnOption2.setBackgroundResource(R.drawable.bg_answer_option);
        btnOption3.setBackgroundResource(R.drawable.bg_answer_option);
        
        btnOption1.setEnabled(true);
        btnOption2.setEnabled(true);
        btnOption3.setEnabled(true);
    }

    /**
     * Check if selected answer is correct
     */
    private void checkAnswer(int selectedIndex) {
        Button selectedButton = getButtonByIndex(selectedIndex);
        
        if (selectedIndex == correctAnswerIndex) {
            // Correct answer
            selectedButton.setBackgroundResource(R.drawable.bg_answer_correct);
            showExplanationCard(true);
        } else {
            // Wrong answer - shake animation
            selectedButton.setBackgroundResource(R.drawable.bg_answer_wrong);
            shakeView(selectedButton);
            
            // Show correct answer after a delay
            selectedButton.postDelayed(() -> {
                Button correctButton = getButtonByIndex(correctAnswerIndex);
                correctButton.setBackgroundResource(R.drawable.bg_answer_correct);
                showExplanationCard(false);
            }, 500);
        }
        
        // Disable all buttons
        btnOption1.setEnabled(false);
        btnOption2.setEnabled(false);
        btnOption3.setEnabled(false);
    }

    /**
     * Get button by index
     */
    private Button getButtonByIndex(int index) {
        switch (index) {
            case 0: return btnOption1;
            case 1: return btnOption2;
            case 2: return btnOption3;
            default: return btnOption1;
        }
    }

    /**
     * Shake animation for wrong answer
     */
    private void shakeView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
            view, "translationX", 
            0, 25, -25, 25, -25, 15, -15, 6, -6, 0
        );
        animator.setDuration(500);
        animator.start();
    }

    /**
     * Show explanation card with celebration
     */
    private void showExplanationCard(boolean isCorrect) {
        questionCard.setVisibility(View.GONE);
        explanationCard.setVisibility(View.VISIBLE);
        
        if (isCorrect) {
            tvExplanation.setText("🎉 Great job! The boy is asking for a movie ticket.");
        } else {
            tvExplanation.setText("The correct answer is: Buy a movie ticket. The boy wants to watch a movie!");
        }
        
        // Scale animation
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(explanationCard, "scaleX", 0.7f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(explanationCard, "scaleY", 0.7f, 1.0f);
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        scaleX.start();
        scaleY.start();
    }

    /**
     * Hide explanation card
     */
    private void hideExplanationCard() {
        explanationCard.setVisibility(View.GONE);
        questionCard.setVisibility(View.GONE);
    }

    /**
     * Load next video
     */
    private void loadNextVideo() {
        currentVideoIndex++;
        
        // Update progress dots (in a real app)
        updateProgressDots();
        
        // Reset state
        isQuestionShown = false;
        
        // Play next video
        playCurrentVideo();
        
        Toast.makeText(this, "Loading next video...", Toast.LENGTH_SHORT).show();
    }

    /**
     * Update progress dots to show current video
     */
    private void updateProgressDots() {
        // In a real app, dynamically update the progress dots
        // based on currentVideoIndex
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause video playback
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume video playback if needed
    }
}
