package com.example.englishapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.shop.englishapp.R;
import com.shop.englishapp.masterchef.models.UserProgress;
import com.shop.englishapp.masterchef.utils.AudioManager;
import com.shop.englishapp.masterchef.utils.ProgressManager;

/**
 * Academy Screen - Food & Drinks Learning
 * Educational content to unlock Master Chef
 * 
 * Features:
 * - Food vocabulary with audio
 * - Number learning (1-10, 11-20)
 * - XP rewards
 * - Energy rewards
 * - Unlocks Master Chef when complete
 */
public class AcademyLearningActivity extends AppCompatActivity {

    // Managers
    private ProgressManager progressManager;
    private AudioManager audioManager;
    private UserProgress userProgress;

    // Learning State
    private int currentVocabIndex = 0;
    private int totalVocabItems = 9; // 9 food items
    private int experienceGained = 0;

    // UI Components
    private ImageButton btnBack;
    private TextView txtTitle, txtProgress, txtXpCount, txtEnergyCount;
    private ProgressBar progressBar;
    
    // Vocabulary Card
    private CardView vocabCard;
    private ImageView imgFoodItem;
    private TextView txtFoodName, txtFoodNameVietnamese;
    private Button btnPlayAudio, btnNext, btnComplete;
    
    // Reward Display
    private TextView txtRewardXp, txtRewardEnergy;
    private ImageView imgXpIcon, imgEnergyIcon;

    // Vocabulary Data
    private static final String[] FOOD_NAMES = {
        "apple", "banana", "egg", "bread", "milk", "juice", "cheese", "tomato", "fish"
    };
    
    private static final String[] FOOD_NAMES_VI = {
        "táo", "chuối", "trứng", "bánh mì", "sữa", "nước ép", "phô mai", "cà chua", "cá"
    };
    
    // You should replace these with actual drawable resources
    private static final int[] FOOD_IMAGES = {
        R.drawable.ic_food_apple,
        R.drawable.ic_food_banana,
        R.drawable.ic_food_egg,
        R.drawable.ic_food_bread,
        R.drawable.ic_food_milk,
        R.drawable.ic_food_juice,
        R.drawable.ic_food_cheese,
        R.drawable.ic_food_tomato,
        R.drawable.ic_food_fish
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academy_learning);

        progressManager = ProgressManager.getInstance(this);
        audioManager = AudioManager.getInstance();

        initializeViews();
        setupListeners();
        
        // Initialize audio
        audioManager.initialize(this, new AudioManager.OnInitCallback() {
            @Override
            public void onSuccess() {
                // Audio ready
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AcademyLearningActivity.this, "Audio error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        loadUserProgress();
        showVocabularyItem(0);
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        txtTitle = findViewById(R.id.txtTitle);
        txtProgress = findViewById(R.id.txtProgress);
        txtXpCount = findViewById(R.id.txtXpCount);
        txtEnergyCount = findViewById(R.id.txtEnergyCount);
        progressBar = findViewById(R.id.progressBar);
        
        vocabCard = findViewById(R.id.vocabCard);
        imgFoodItem = findViewById(R.id.imgFoodItem);
        txtFoodName = findViewById(R.id.txtFoodName);
        txtFoodNameVietnamese = findViewById(R.id.txtFoodNameVietnamese);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);
        btnNext = findViewById(R.id.btnNext);
        btnComplete = findViewById(R.id.btnComplete);
        
        txtRewardXp = findViewById(R.id.txtRewardXp);
        txtRewardEnergy = findViewById(R.id.txtRewardEnergy);
        imgXpIcon = findViewById(R.id.imgXpIcon);
        imgEnergyIcon = findViewById(R.id.imgEnergyIcon);
        
        // Hide complete button initially
        btnComplete.setVisibility(View.GONE);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        
        btnPlayAudio.setOnClickListener(v -> playCurrentAudio());
        
        btnNext.setOnClickListener(v -> {
            if (currentVocabIndex < totalVocabItems - 1) {
                currentVocabIndex++;
                showVocabularyItem(currentVocabIndex);
                awardXp(10); // 10 XP per vocabulary learned
            } else {
                // Reached end
                btnNext.setVisibility(View.GONE);
                btnComplete.setVisibility(View.VISIBLE);
            }
        });
        
        btnComplete.setOnClickListener(v -> completeLesson());
    }

    private void loadUserProgress() {
        progressManager.getUserProgress(new ProgressManager.OnProgressCallback() {
            @Override
            public void onSuccess(UserProgress progress) {
                userProgress = progress;
                updateCurrencyDisplay();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AcademyLearningActivity.this, "Error loading progress", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCurrencyDisplay() {
        if (userProgress != null) {
            txtXpCount.setText(String.valueOf(userProgress.getExperiencePoints()));
            txtEnergyCount.setText(String.valueOf(userProgress.getEnergy()));
        }
    }

    private void showVocabularyItem(int index) {
        if (index < 0 || index >= FOOD_NAMES.length) return;
        
        // Update progress
        txtProgress.setText((index + 1) + " / " + totalVocabItems);
        progressBar.setMax(totalVocabItems);
        progressBar.setProgress(index + 1);
        
        // Show vocabulary
        imgFoodItem.setImageResource(FOOD_IMAGES[index]);
        txtFoodName.setText(FOOD_NAMES[index].toUpperCase());
        txtFoodNameVietnamese.setText(FOOD_NAMES_VI[index]);
        
        // Animate card entrance
        animateCardEntrance();
        
        // Auto-play audio
        playCurrentAudio();
    }

    private void playCurrentAudio() {
        String word = FOOD_NAMES[currentVocabIndex];
        
        audioManager.speakOrder(word, new AudioManager.OnAudioCallback() {
            @Override
            public void onStart() {
                btnPlayAudio.setEnabled(false);
                animateSpeakerPulse();
            }

            @Override
            public void onComplete() {
                runOnUiThread(() -> {
                    btnPlayAudio.setEnabled(true);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnPlayAudio.setEnabled(true);
                    Toast.makeText(AcademyLearningActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void awardXp(int xp) {
        experienceGained += xp;
        
        // Animate XP reward
        txtRewardXp.setText("+" + xp + " XP");
        txtRewardXp.setVisibility(View.VISIBLE);
        imgXpIcon.setVisibility(View.VISIBLE);
        
        animateRewardFloat(txtRewardXp);
        animateRewardFloat(imgXpIcon);
        
        // Update in database
        progressManager.addExperience(xp, new ProgressManager.OnLevelUpCallback() {
            @Override
            public void onLevelUp(int newLevel) {
                runOnUiThread(() -> {
                    Toast.makeText(AcademyLearningActivity.this, 
                        "🎉 Level Up! Level " + newLevel, 
                        Toast.LENGTH_LONG).show();
                    updateCurrencyDisplay();
                });
            }

            @Override
            public void onXpGained(int xp) {
                runOnUiThread(() -> {
                    updateCurrencyDisplay();
                });
            }
        });
    }

    private void completeLesson() {
        // Award bonus rewards
        int bonusXp = 100; // Big bonus for completing entire lesson
        int bonusEnergy = 3; // Energy reward
        
        awardXp(bonusXp);
        
        // Update energy
        userProgress.setEnergy(userProgress.getEnergy() + bonusEnergy);
        
        // Animate energy reward
        txtRewardEnergy.setText("+" + bonusEnergy + " ⚡");
        txtRewardEnergy.setVisibility(View.VISIBLE);
        imgEnergyIcon.setVisibility(View.VISIBLE);
        
        animateRewardFloat(txtRewardEnergy);
        animateRewardFloat(imgEnergyIcon);
        
        // Mark lesson as completed and unlock Master Chef
        progressManager.completeFoodLesson(new ProgressManager.OnUnlockCallback() {
            @Override
            public void onUnlocked() {
                runOnUiThread(() -> {
                    showUnlockDialog();
                });
            }

            @Override
            public void onAlreadyUnlocked() {
                runOnUiThread(() -> {
                    Toast.makeText(AcademyLearningActivity.this, 
                        "Lesson completed! Great job!", 
                        Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void showUnlockDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("🎉 Master Chef Unlocked!");
        builder.setMessage("Congratulations!\n\n" +
                         "You've completed the Food & Drinks lesson!\n\n" +
                         "Master Chef mode is now unlocked!\n" +
                         "Go cook some delicious food! 👨‍🍳");
        builder.setPositiveButton("Let's Cook!", (dialog, which) -> {
            // Navigate to Master Chef
            Toast.makeText(this, "Opening Master Chef...", Toast.LENGTH_SHORT).show();
            finish();
        });
        builder.setCancelable(false);
        builder.show();
    }

    // Animation Methods
    
    private void animateCardEntrance() {
        vocabCard.setAlpha(0f);
        vocabCard.setScaleX(0.8f);
        vocabCard.setScaleY(0.8f);
        
        AnimatorSet entranceAnim = new AnimatorSet();
        entranceAnim.playTogether(
            ObjectAnimator.ofFloat(vocabCard, "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(vocabCard, "scaleX", 0.8f, 1f),
            ObjectAnimator.ofFloat(vocabCard, "scaleY", 0.8f, 1f)
        );
        entranceAnim.setDuration(400);
        entranceAnim.start();
    }

    private void animateSpeakerPulse() {
        AnimatorSet pulseAnim = new AnimatorSet();
        pulseAnim.playTogether(
            ObjectAnimator.ofFloat(btnPlayAudio, "scaleX", 1f, 1.2f, 1f),
            ObjectAnimator.ofFloat(btnPlayAudio, "scaleY", 1f, 1.2f, 1f)
        );
        pulseAnim.setDuration(600);
        pulseAnim.start();
    }

    private void animateRewardFloat(View view) {
        AnimatorSet floatAnim = new AnimatorSet();
        floatAnim.playTogether(
            ObjectAnimator.ofFloat(view, "alpha", 1f, 0f),
            ObjectAnimator.ofFloat(view, "translationY", 0f, -100f),
            ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f),
            ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f)
        );
        floatAnim.setDuration(1500);
        floatAnim.start();
        
        // Reset after animation
        floatAnim.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                view.setVisibility(View.GONE);
                view.setAlpha(1f);
                view.setTranslationY(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioManager.stop();
    }

    @Override
    public void onBackPressed() {
        if (currentVocabIndex > 0) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Leave Learning?");
            builder.setMessage("Your progress will be saved, but you won't get completion rewards yet.");
            builder.setPositiveButton("Leave", (dialog, which) -> {
                super.onBackPressed();
            });
            builder.setNegativeButton("Stay", null);
            builder.show();
        } else {
            super.onBackPressed();
        }
    }
}
