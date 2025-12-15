package com.example.englishapp;


import com.shop.englishapp.R;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class InteractiveComicsActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack, btnAutoRead, btnSoundToggle, btnHighlight, btnBookmark;
    private CardView comicViewerCard, vocabPopupCard;
    private FrameLayout tappableTree, tappableCat, tappableBall;
    private View glowTree, glowCat, glowBall;
    private ImageView imgTree, imgCat, imgBall, imgVocabIcon, imgComicScene;
    private TextView txtSpeechBubble, txtVocabWord;
    private ImageButton btnVocabSound;
    private LinearLayout branchingCard, successOverlay;
    private Button btnChoice1, btnChoice2, btnChoice3;
    private View progressDot1, progressDot2, progressDot3, progressDot4;

    // State Variables
    private int currentPanel = 1;
    private int totalPanels = 4;
    private boolean soundEnabled = true;
    private boolean autoReadEnabled = false;
    private boolean highlightEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interactive_comics);

        initializeViews();
        setupListeners();
        loadComicPanel(currentPanel);
    }

    private void initializeViews() {
        // Header buttons
        btnBack = findViewById(R.id.btnBack);
        
        // Comic viewer
        comicViewerCard = findViewById(R.id.comicViewerCard);
        imgComicScene = findViewById(R.id.imgComicScene);
        txtSpeechBubble = findViewById(R.id.txtSpeechBubble);

        // Tappable objects
        tappableTree = findViewById(R.id.tappableTree);
        tappableCat = findViewById(R.id.tappableCat);
        tappableBall = findViewById(R.id.tappableBall);

        imgTree = findViewById(R.id.imgTree);
        imgCat = findViewById(R.id.imgCat);
        imgBall = findViewById(R.id.imgBall);

        glowTree = findViewById(R.id.glowTree);
        glowCat = findViewById(R.id.glowCat);
        glowBall = findViewById(R.id.glowBall);

        // Vocabulary popup
        vocabPopupCard = findViewById(R.id.vocabPopupCard);
        imgVocabIcon = findViewById(R.id.imgVocabIcon);
        txtVocabWord = findViewById(R.id.txtVocabWord);
        btnVocabSound = findViewById(R.id.btnVocabSound);

        // Story branching
        branchingCard = findViewById(R.id.branchingCard);
        btnChoice1 = findViewById(R.id.btnChoice1);
        btnChoice2 = findViewById(R.id.btnChoice2);
        btnChoice3 = findViewById(R.id.btnChoice3);

        // Success overlay
        successOverlay = findViewById(R.id.successOverlay);

        // Reading tools
        btnAutoRead = findViewById(R.id.btnAutoRead);
        btnSoundToggle = findViewById(R.id.btnSoundToggle);
        btnHighlight = findViewById(R.id.btnHighlight);
        btnBookmark = findViewById(R.id.btnBookmark);

        // Progress dots
        progressDot1 = findViewById(R.id.progressDot1);
        progressDot2 = findViewById(R.id.progressDot2);
        progressDot3 = findViewById(R.id.progressDot3);
        progressDot4 = findViewById(R.id.progressDot4);
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Tappable objects with glow effect
        setupTappableObject(tappableTree, imgTree, glowTree, R.drawable.ic_tree, "Tree");
        setupTappableObject(tappableCat, imgCat, glowCat, R.drawable.ic_cat, "Cat");
        setupTappableObject(tappableBall, imgBall, glowBall, R.drawable.ic_ball, "Ball");

        // Vocabulary card speaker button
        btnVocabSound.setOnClickListener(v -> {
            if (soundEnabled) {
                playVocabularySound();
                animateSpeakerPulse(btnVocabSound);
            }
        });

        // Close vocabulary card on background tap
        vocabPopupCard.setOnClickListener(v -> hideVocabularyCard());

        // Reading tools
        btnAutoRead.setOnClickListener(v -> toggleAutoRead());
        btnSoundToggle.setOnClickListener(v -> toggleSound());
        btnHighlight.setOnClickListener(v -> toggleHighlight());
        btnBookmark.setOnClickListener(v -> bookmarkProgress());

        // Story branching choices
        btnChoice1.setOnClickListener(v -> selectChoice(1));
        btnChoice2.setOnClickListener(v -> selectChoice(2));
        btnChoice3.setOnClickListener(v -> selectChoice(3));

        // Comic swipe navigation
        comicViewerCard.setOnClickListener(v -> nextPanel());
    }

    private void setupTappableObject(FrameLayout container, ImageView object, View glow, int iconRes, String word) {
        container.setOnClickListener(v -> {
            // Show vocabulary popup
            showVocabularyCard(iconRes, word);
            
            // Bounce animation on object
            animateObjectBounce(object);
            
            // Play sound effect
            if (soundEnabled) {
                playObjectSound(word);
            }
            
            // Hide glow
            glow.setVisibility(View.INVISIBLE);
        });

        // Show glow on proximity (simulated by hover)
        container.setOnHoverListener((v, event) -> {
            glow.setVisibility(View.VISIBLE);
            animateGlowPulse(glow);
            return true;
        });
    }

    private void loadComicPanel(int panelNumber) {
        // Simulate loading different comic panels
        updateProgressDots(panelNumber);
        
        switch (panelNumber) {
            case 1:
                txtSpeechBubble.setText("Hello! Let's explore!");
                imgComicScene.setBackgroundColor(0xFFE3F2FD);
                tappableTree.setVisibility(View.VISIBLE);
                tappableCat.setVisibility(View.VISIBLE);
                tappableBall.setVisibility(View.VISIBLE);
                break;
            case 2:
                txtSpeechBubble.setText("What did you find?");
                imgComicScene.setBackgroundColor(0xFFFFF9C4);
                tappableTree.setVisibility(View.VISIBLE);
                tappableCat.setVisibility(View.INVISIBLE);
                tappableBall.setVisibility(View.VISIBLE);
                break;
            case 3:
                txtSpeechBubble.setText("Let's make a choice!");
                imgComicScene.setBackgroundColor(0xFFF3E5F5);
                showBranchingCard();
                break;
            case 4:
                txtSpeechBubble.setText("The End!");
                imgComicScene.setBackgroundColor(0xFFE8F5E9);
                showSuccessOverlay();
                break;
        }
    }

    private void nextPanel() {
        if (currentPanel < totalPanels) {
            currentPanel++;
            loadComicPanel(currentPanel);
            
            // Slide animation
            animateComicTransition();
        }
    }

    private void showVocabularyCard(int iconRes, String word) {
        imgVocabIcon.setImageResource(iconRes);
        txtVocabWord.setText(word);
        
        vocabPopupCard.setVisibility(View.VISIBLE);
        vocabPopupCard.setScaleX(0.5f);
        vocabPopupCard.setScaleY(0.5f);
        vocabPopupCard.setAlpha(0f);

        AnimatorSet popupAnim = new AnimatorSet();
        popupAnim.playTogether(
            ObjectAnimator.ofFloat(vocabPopupCard, "scaleX", 0.5f, 1f),
            ObjectAnimator.ofFloat(vocabPopupCard, "scaleY", 0.5f, 1f),
            ObjectAnimator.ofFloat(vocabPopupCard, "alpha", 0f, 1f)
        );
        popupAnim.setDuration(300);
        popupAnim.setInterpolator(new BounceInterpolator());
        popupAnim.start();

        // Auto-hide after 3 seconds
        vocabPopupCard.postDelayed(() -> hideVocabularyCard(), 3000);
    }

    private void hideVocabularyCard() {
        AnimatorSet hideAnim = new AnimatorSet();
        hideAnim.playTogether(
            ObjectAnimator.ofFloat(vocabPopupCard, "scaleX", 1f, 0.5f),
            ObjectAnimator.ofFloat(vocabPopupCard, "scaleY", 1f, 0.5f),
            ObjectAnimator.ofFloat(vocabPopupCard, "alpha", 1f, 0f)
        );
        hideAnim.setDuration(200);
        hideAnim.start();
        hideAnim.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                vocabPopupCard.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showBranchingCard() {
        branchingCard.setTranslationY(600f);
        ObjectAnimator slideUp = ObjectAnimator.ofFloat(branchingCard, "translationY", 600f, 0f);
        slideUp.setDuration(500);
        slideUp.setInterpolator(new DecelerateInterpolator());
        slideUp.start();
    }

    private void hideBranchingCard() {
        ObjectAnimator slideDown = ObjectAnimator.ofFloat(branchingCard, "translationY", 0f, 600f);
        slideDown.setDuration(400);
        slideDown.start();
    }

    private void selectChoice(int choiceNumber) {
        Button selectedButton = null;
        
        switch (choiceNumber) {
            case 1:
                selectedButton = btnChoice1;
                break;
            case 2:
                selectedButton = btnChoice2;
                break;
            case 3:
                selectedButton = btnChoice3;
                break;
        }

        if (selectedButton != null) {
            // Highlight selected choice
            selectedButton.setBackgroundResource(R.drawable.bg_choice_selected);
            
            // Animate selection
            animateChoiceSelection(selectedButton);
            
            // Hide other choices
            resetOtherChoices(choiceNumber);
            
            // Continue to final panel after delay
            selectedButton.postDelayed(() -> {
                hideBranchingCard();
                currentPanel = 4;
                loadComicPanel(currentPanel);
            }, 1500);
        }
    }

    private void resetOtherChoices(int selectedChoice) {
        if (selectedChoice != 1) btnChoice1.setAlpha(0.4f);
        if (selectedChoice != 2) btnChoice2.setAlpha(0.4f);
        if (selectedChoice != 3) btnChoice3.setAlpha(0.4f);
    }

    private void showSuccessOverlay() {
        successOverlay.setVisibility(View.VISIBLE);
        successOverlay.setAlpha(0f);
        
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(successOverlay, "alpha", 0f, 1f);
        fadeIn.setDuration(500);
        fadeIn.start();

        // Animate confetti
        ImageView confetti = successOverlay.findViewById(R.id.imgConfetti);
        AnimatorSet confettiAnim = new AnimatorSet();
        confettiAnim.playTogether(
            ObjectAnimator.ofFloat(confetti, "rotation", 0f, 360f),
            ObjectAnimator.ofFloat(confetti, "scaleX", 0.5f, 1.2f, 1f),
            ObjectAnimator.ofFloat(confetti, "scaleY", 0.5f, 1.2f, 1f)
        );
        confettiAnim.setDuration(800);
        confettiAnim.start();
    }

    private void updateProgressDots(int currentPanel) {
        View[] dots = {progressDot1, progressDot2, progressDot3, progressDot4};
        
        for (int i = 0; i < dots.length; i++) {
            if (i < currentPanel) {
                dots[i].setBackgroundResource(R.drawable.bg_progress_dot_active);
            } else {
                dots[i].setBackgroundResource(R.drawable.bg_progress_dot_inactive);
            }
        }
    }

    // Animation Methods
    private void animateObjectBounce(View object) {
        AnimatorSet bounceAnim = new AnimatorSet();
        bounceAnim.playTogether(
            ObjectAnimator.ofFloat(object, "scaleX", 1f, 1.2f, 1f),
            ObjectAnimator.ofFloat(object, "scaleY", 1f, 1.2f, 1f)
        );
        bounceAnim.setDuration(400);
        bounceAnim.setInterpolator(new BounceInterpolator());
        bounceAnim.start();
    }

    private void animateGlowPulse(View glow) {
        AnimatorSet pulseAnim = new AnimatorSet();
        pulseAnim.playTogether(
            ObjectAnimator.ofFloat(glow, "scaleX", 1f, 1.1f, 1f),
            ObjectAnimator.ofFloat(glow, "scaleY", 1f, 1.1f, 1f),
            ObjectAnimator.ofFloat(glow, "alpha", 0.6f, 1f, 0.6f)
        );
        pulseAnim.setDuration(1000);
        // AnimatorSet doesn't have setRepeatCount, would need to set on individual animators
        pulseAnim.start();
    }

    private void animateSpeakerPulse(View speaker) {
        AnimatorSet pulseAnim = new AnimatorSet();
        pulseAnim.playTogether(
            ObjectAnimator.ofFloat(speaker, "scaleX", 1f, 1.15f, 1f),
            ObjectAnimator.ofFloat(speaker, "scaleY", 1f, 1.15f, 1f)
        );
        pulseAnim.setDuration(300);
        pulseAnim.start();
    }

    private void animateChoiceSelection(View choice) {
        AnimatorSet selectAnim = new AnimatorSet();
        selectAnim.playTogether(
            ObjectAnimator.ofFloat(choice, "scaleX", 1f, 1.05f, 1f),
            ObjectAnimator.ofFloat(choice, "scaleY", 1f, 1.05f, 1f)
        );
        selectAnim.setDuration(400);
        selectAnim.start();
    }

    private void animateComicTransition() {
        comicViewerCard.setAlpha(0.7f);
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(comicViewerCard, "alpha", 0.7f, 1f);
        fadeAnim.setDuration(300);
        fadeAnim.start();
    }

    // Reading Tools Methods
    private void toggleAutoRead() {
        autoReadEnabled = !autoReadEnabled;
        btnAutoRead.setAlpha(autoReadEnabled ? 1f : 0.5f);
        
        if (autoReadEnabled) {
            // Simulate auto-read functionality
            animateSpeakerPulse(btnAutoRead);
        }
    }

    private void toggleSound() {
        soundEnabled = !soundEnabled;
        btnSoundToggle.setAlpha(soundEnabled ? 1f : 0.5f);
    }

    private void toggleHighlight() {
        highlightEnabled = !highlightEnabled;
        btnHighlight.setAlpha(highlightEnabled ? 1f : 0.5f);
        
        if (highlightEnabled) {
            txtSpeechBubble.setBackgroundColor(0x40FFD93D);
        } else {
            txtSpeechBubble.setBackgroundColor(0x00000000);
        }
    }

    private void bookmarkProgress() {
        // Simulate bookmarking
        animateSpeakerPulse(btnBookmark);
        // Would save progress to database/preferences in real app
    }

    // Audio Simulation Methods
    private void playObjectSound(String object) {
        // Simulate playing sound for tapped object
        // In real app: MediaPlayer.create(this, R.raw.cat_meow).start();
    }

    private void playVocabularySound() {
        // Simulate playing vocabulary pronunciation
        // In real app: TextToSpeech or audio file playback
    }
}
