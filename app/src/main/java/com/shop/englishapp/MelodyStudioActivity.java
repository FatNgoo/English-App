package com.shop.englishapp;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

/**
 * MelodyStudioActivity - Karaoke Room for learning English through songs
 * Features:
 * - Album art display with animated music notes
 * - Synchronized karaoke lyrics
 * - Fill-in-the-blank word challenges
 * - Music playback controls
 * - Progress timeline with checkpoints
 * - Reward animations
 */
public class MelodyStudioActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack;
    private TextView tvSongTitle;
    private TextView tvLyricLine1, tvLyricLine2, tvLyricLine3;
    private LinearLayout answerOptionsContainer;
    private Button btnAnswer1, btnAnswer2, btnAnswer3;
    private FrameLayout btnPlayPause;
    private ImageView ivPlayPause;
    private CardView rewardCard;
    private TextView tvReward;
    private Button btnContinueSong;
    private View timelineProgress;
    private TextView tvCurrentTime, tvTotalTime;

    // State variables
    private boolean isPlaying = false;
    private boolean isAnswerPhase = false;
    private int correctAnswerIndex = 1; // "bright" is correct
    private Handler handler = new Handler();
    private int currentTime = 30; // seconds
    private int totalTime = 135; // 2:15 in seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.melody_studio);

        // Initialize UI components
        initializeViews();

        // Set up click listeners
        setupClickListeners();

        // Load song
        loadSong();
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvSongTitle = findViewById(R.id.tvSongTitle);
        tvLyricLine1 = findViewById(R.id.tvLyricLine1);
        tvLyricLine2 = findViewById(R.id.tvLyricLine2);
        tvLyricLine3 = findViewById(R.id.tvLyricLine3);
        answerOptionsContainer = findViewById(R.id.answerOptionsContainer);
        btnAnswer1 = findViewById(R.id.btnAnswer1);
        btnAnswer2 = findViewById(R.id.btnAnswer2);
        btnAnswer3 = findViewById(R.id.btnAnswer3);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        ivPlayPause = findViewById(R.id.ivPlayPause);
        rewardCard = findViewById(R.id.rewardCard);
        tvReward = findViewById(R.id.tvReward);
        btnContinueSong = findViewById(R.id.btnContinueSong);
        timelineProgress = findViewById(R.id.timelineProgress);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
    }

    /**
     * Set up click listeners for interactive elements
     */
    private void setupClickListeners() {
        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Play/Pause button
        if (btnPlayPause != null) {
            btnPlayPause.setOnClickListener(v -> togglePlayPause());
        }

        // Replay button
        View btnReplay = findViewById(R.id.btnReplay);
        if (btnReplay != null) {
            btnReplay.setOnClickListener(v -> {
                replaySnippet();
                Toast.makeText(this, "🔄 Replaying snippet", Toast.LENGTH_SHORT).show();
            });
        }

        // Mic button (Sing Along Mode)
        View btnMic = findViewById(R.id.btnMic);
        if (btnMic != null) {
            btnMic.setOnClickListener(v -> {
                Toast.makeText(this, "🎤 Sing Along Mode activated!", Toast.LENGTH_SHORT).show();
                // In a real app, this would enable microphone and record user's singing
            });
        }

        // Answer buttons
        if (btnAnswer1 != null) {
            btnAnswer1.setOnClickListener(v -> checkAnswer(0));
        }
        if (btnAnswer2 != null) {
            btnAnswer2.setOnClickListener(v -> checkAnswer(1));
        }
        if (btnAnswer3 != null) {
            btnAnswer3.setOnClickListener(v -> checkAnswer(2));
        }

        // Continue button in reward card
        if (btnContinueSong != null) {
            btnContinueSong.setOnClickListener(v -> {
                hideRewardCard();
                continueSong();
            });
        }
    }

    /**
     * Load and prepare song
     */
    private void loadSong() {
        tvSongTitle.setText("Happy Morning Song");
        tvCurrentTime.setText(formatTime(currentTime));
        tvTotalTime.setText(formatTime(totalTime));
        
        // Set initial lyrics
        tvLyricLine1.setText("Good morning, good morning,");
        tvLyricLine2.setText("Good morning, ___ sunshine!");
        tvLyricLine3.setText("How are you today?");
        
        // Hide answer options initially
        answerOptionsContainer.setVisibility(View.GONE);
    }

    /**
     * Toggle play/pause
     */
    private void togglePlayPause() {
        isPlaying = !isPlaying;
        
        if (isPlaying) {
            ivPlayPause.setImageResource(R.drawable.ic_pause_music);
            startMusicPlayback();
        } else {
            ivPlayPause.setImageResource(R.drawable.ic_play_music);
            pauseMusicPlayback();
        }
    }

    /**
     * Start music playback simulation
     */
    private void startMusicPlayback() {
        // Animate lyrics (karaoke bounce effect)
        animateLyricLine(tvLyricLine1);
        
        // Simulate reaching the blank word after 3 seconds
        handler.postDelayed(() -> {
            if (isPlaying) {
                pauseForQuestion();
            }
        }, 3000);
        
        // Update timeline
        updateTimeline();
    }

    /**
     * Pause music playback
     */
    private void pauseMusicPlayback() {
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * Pause music for question
     */
    private void pauseForQuestion() {
        isPlaying = false;
        isAnswerPhase = true;
        ivPlayPause.setImageResource(R.drawable.ic_play_music);
        
        // Highlight the line with blank
        tvLyricLine2.setTextColor(getResources().getColor(R.color.saga_yellow));
        tvLyricLine2.setTextSize(22);
        
        // Show answer options with animation
        answerOptionsContainer.setVisibility(View.VISIBLE);
        animateSlideUp(answerOptionsContainer);
        
        // Reset answer buttons
        resetAnswerButtons();
    }

    /**
     * Replay current snippet
     */
    private void replaySnippet() {
        if (isAnswerPhase) {
            // Replay the specific line with the blank
            Toast.makeText(this, "🎵 Good morning, ... sunshine!", Toast.LENGTH_LONG).show();
        } else {
            // Replay from beginning
            currentTime = 0;
            updateTimeline();
        }
    }

    /**
     * Reset answer button states
     */
    private void resetAnswerButtons() {
        btnAnswer1.setBackgroundResource(R.drawable.bg_answer_option);
        btnAnswer2.setBackgroundResource(R.drawable.bg_answer_option);
        btnAnswer3.setBackgroundResource(R.drawable.bg_answer_option);
        
        btnAnswer1.setEnabled(true);
        btnAnswer2.setEnabled(true);
        btnAnswer3.setEnabled(true);
    }

    /**
     * Check if selected answer is correct
     */
    private void checkAnswer(int selectedIndex) {
        Button selectedButton = getButtonByIndex(selectedIndex);
        
        if (selectedIndex == correctAnswerIndex) {
            // Correct answer
            selectedButton.setBackgroundResource(R.drawable.bg_answer_correct);
            
            // Fill in the blank
            tvLyricLine2.setText("Good morning, bright sunshine!");
            tvLyricLine2.setTextColor(getResources().getColor(R.color.text_primary));
            
            // Show reward
            showRewardCard(true);
        } else {
            // Wrong answer - shake animation
            selectedButton.setBackgroundResource(R.drawable.bg_answer_wrong);
            shakeView(selectedButton);
            
            // Show correct answer after delay
            selectedButton.postDelayed(() -> {
                Button correctButton = getButtonByIndex(correctAnswerIndex);
                correctButton.setBackgroundResource(R.drawable.bg_answer_correct);
                showRewardCard(false);
            }, 800);
        }
        
        // Disable all buttons
        btnAnswer1.setEnabled(false);
        btnAnswer2.setEnabled(false);
        btnAnswer3.setEnabled(false);
    }

    /**
     * Get button by index
     */
    private Button getButtonByIndex(int index) {
        switch (index) {
            case 0: return btnAnswer1;
            case 1: return btnAnswer2;
            case 2: return btnAnswer3;
            default: return btnAnswer1;
        }
    }

    /**
     * Show reward card with animation
     */
    private void showRewardCard(boolean isCorrect) {
        rewardCard.setVisibility(View.VISIBLE);
        
        if (isCorrect) {
            tvReward.setText("🎉 Great job! The word is 'bright'!");
        } else {
            tvReward.setText("The correct word is 'bright'. Keep practicing!");
        }
        
        // Scale and fade in animation
        rewardCard.setAlpha(0f);
        rewardCard.setScaleX(0.8f);
        rewardCard.setScaleY(0.8f);
        
        rewardCard.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .start();
    }

    /**
     * Hide reward card
     */
    private void hideRewardCard() {
        rewardCard.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(200)
            .withEndAction(() -> rewardCard.setVisibility(View.GONE))
            .start();
    }

    /**
     * Continue song after answering
     */
    private void continueSong() {
        isAnswerPhase = false;
        answerOptionsContainer.setVisibility(View.GONE);
        
        // Reset lyric line style
        tvLyricLine2.setTextSize(18);
        
        // Continue playing
        isPlaying = true;
        ivPlayPause.setImageResource(R.drawable.ic_pause_music);
        
        // Move to next lyric line
        tvLyricLine1.setAlpha(0.5f);
        tvLyricLine2.setAlpha(0.5f);
        tvLyricLine3.setAlpha(1.0f);
        tvLyricLine3.setTextSize(20);
        tvLyricLine3.setTextColor(getResources().getColor(R.color.text_primary));
        
        // Continue playback
        startMusicPlayback();
    }

    /**
     * Animate lyric line (karaoke bounce effect)
     */
    private void animateLyricLine(TextView textView) {
        textView.setAlpha(1.0f);
        textView.setTextColor(getResources().getColor(R.color.text_primary));
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 1.0f, 1.1f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 1.0f, 1.1f, 1.0f);
        scaleX.setDuration(600);
        scaleY.setDuration(600);
        scaleX.start();
        scaleY.start();
    }

    /**
     * Animate slide up
     */
    private void animateSlideUp(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
            view, "translationY",
            view.getHeight(), 0f
        );
        animator.setDuration(300);
        animator.start();
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
     * Update timeline progress
     */
    private void updateTimeline() {
        if (!isPlaying) return;
        
        // Update progress bar width
        float progress = (float) currentTime / totalTime;
        int width = (int) (timelineProgress.getParent() instanceof View ? 
            ((View) timelineProgress.getParent()).getWidth() * progress : 0);
        timelineProgress.getLayoutParams().width = width;
        timelineProgress.requestLayout();
        
        // Update time text
        tvCurrentTime.setText(formatTime(currentTime));
        
        // Continue updating
        handler.postDelayed(() -> {
            currentTime++;
            updateTimeline();
        }, 1000);
    }

    /**
     * Format time in MM:SS format
     */
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPlaying) {
            togglePlayPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
