package com.example.englishapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.DragEvent;
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

public class MasterChefActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack, btnHint, btnReplayAudio, btnSlowMode;
    private CardView orderCard, cookingStationCard;
    private ImageView imgCustomerAvatar, imgSpeaker, imgSteam, imgSmoke, imgFryingPan;
    private LinearLayout waveformContainer, completionOverlay;
    private TextView txtOrderSubtitle;
    private ImageView wave1, wave2, wave3;
    private ImageView star1, star2, star3;
    
    // Kitchen Areas
    private FrameLayout stoveArea, cuttingBoardArea, toasterArea, plateArea;
    
    // Ingredients
    private FrameLayout ingredientEgg, ingredientBread, ingredientCheese, ingredientBacon;
    private FrameLayout ingredientFruit, ingredientVegetables, ingredientJuice;
    
    // Checklist
    private View checkbox1, checkbox2, checkbox3;
    private TextView txtCheckItem1, txtCheckItem2, txtCheckItem3;
    private LinearLayout checklistItem1, checklistItem2, checklistItem3;
    
    // Completion
    private Button btnNextOrder;
    private ImageView imgConfettiCompletion;
    
    // State Variables
    private int starsEarned = 2;
    private boolean isPlayingAudio = false;
    private boolean slowModeEnabled = false;
    private int eggsCooked = 0;
    private int sandwichMade = 0;
    private int juicePoured = 0;
    private int requiredEggs = 2;
    private int requiredSandwich = 1;
    private int requiredJuice = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_chef);

        initializeViews();
        setupListeners();
        setupDragAndDrop();
        updateStarDisplay();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        
        // Order Panel
        orderCard = findViewById(R.id.orderCard);
        imgCustomerAvatar = findViewById(R.id.imgCustomerAvatar);
        imgSpeaker = findViewById(R.id.imgSpeaker);
        waveformContainer = findViewById(R.id.waveformContainer);
        wave1 = findViewById(R.id.wave1);
        wave2 = findViewById(R.id.wave2);
        wave3 = findViewById(R.id.wave3);
        txtOrderSubtitle = findViewById(R.id.txtOrderSubtitle);
        
        // Cooking Station
        cookingStationCard = findViewById(R.id.cookingStationCard);
        stoveArea = findViewById(R.id.stoveArea);
        cuttingBoardArea = findViewById(R.id.cuttingBoardArea);
        toasterArea = findViewById(R.id.toasterArea);
        plateArea = findViewById(R.id.plateArea);
        imgFryingPan = findViewById(R.id.imgFryingPan);
        imgSteam = findViewById(R.id.imgSteam);
        imgSmoke = findViewById(R.id.imgSmoke);
        
        // Ingredients
        ingredientEgg = findViewById(R.id.ingredientEgg);
        ingredientBread = findViewById(R.id.ingredientBread);
        ingredientCheese = findViewById(R.id.ingredientCheese);
        ingredientBacon = findViewById(R.id.ingredientBacon);
        ingredientFruit = findViewById(R.id.ingredientFruit);
        ingredientVegetables = findViewById(R.id.ingredientVegetables);
        ingredientJuice = findViewById(R.id.ingredientJuice);
        
        // Checklist
        checkbox1 = findViewById(R.id.checkbox1);
        checkbox2 = findViewById(R.id.checkbox2);
        checkbox3 = findViewById(R.id.checkbox3);
        txtCheckItem1 = findViewById(R.id.txtCheckItem1);
        txtCheckItem2 = findViewById(R.id.txtCheckItem2);
        txtCheckItem3 = findViewById(R.id.txtCheckItem3);
        checklistItem1 = findViewById(R.id.checklistItem1);
        checklistItem2 = findViewById(R.id.checklistItem2);
        checklistItem3 = findViewById(R.id.checklistItem3);
        
        // Hint Tools
        btnHint = findViewById(R.id.btnHint);
        btnReplayAudio = findViewById(R.id.btnReplayAudio);
        btnSlowMode = findViewById(R.id.btnSlowMode);
        
        // Completion
        completionOverlay = findViewById(R.id.completionOverlay);
        btnNextOrder = findViewById(R.id.btnNextOrder);
        imgConfettiCompletion = findViewById(R.id.imgConfettiCompletion);
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());
        
        // Play Order button
        findViewById(R.id.btnPlayOrder).setOnClickListener(v -> playOrderAudio());
        
        // Hint Tools
        btnHint.setOnClickListener(v -> showHint());
        btnReplayAudio.setOnClickListener(v -> playOrderAudio());
        btnSlowMode.setOnClickListener(v -> toggleSlowMode());
        
        // Completion
        btnNextOrder.setOnClickListener(v -> startNextOrder());
    }

    private void setupDragAndDrop() {
        // Make ingredients draggable
        setupDraggableIngredient(ingredientEgg, "egg");
        setupDraggableIngredient(ingredientBread, "bread");
        setupDraggableIngredient(ingredientCheese, "cheese");
        setupDraggableIngredient(ingredientBacon, "bacon");
        setupDraggableIngredient(ingredientFruit, "fruit");
        setupDraggableIngredient(ingredientVegetables, "vegetables");
        setupDraggableIngredient(ingredientJuice, "juice");
        
        // Setup drop zones
        setupDropZone(stoveArea, "stove");
        setupDropZone(cuttingBoardArea, "cutting_board");
        setupDropZone(toasterArea, "toaster");
        setupDropZone(plateArea, "plate");
    }

    private void setupDraggableIngredient(FrameLayout ingredient, String ingredientType) {
        ingredient.setOnLongClickListener(v -> {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(null, shadowBuilder, ingredientType, 0);
            
            // Highlight ingredient
            ingredient.setBackgroundResource(R.drawable.bg_ingredient_selected);
            animateIngredientLift(ingredient);
            
            return true;
        });
    }

    private void setupDropZone(FrameLayout dropZone, String zoneType) {
        dropZone.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                    
                case DragEvent.ACTION_DRAG_ENTERED:
                    // Show glow effect when ingredient enters zone
                    v.setAlpha(0.8f);
                    animateZoneGlow(v);
                    return true;
                    
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setAlpha(1.0f);
                    return true;
                    
                case DragEvent.ACTION_DROP:
                    String ingredientType = (String) event.getLocalState();
                    handleIngredientDrop(ingredientType, zoneType, v);
                    v.setAlpha(1.0f);
                    return true;
                    
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setAlpha(1.0f);
                    return true;
                    
                default:
                    return false;
            }
        });
    }

    private void handleIngredientDrop(String ingredient, String zone, View dropZone) {
        boolean validCombination = false;
        
        // Check if ingredient + zone combination is correct
        if (ingredient.equals("egg") && zone.equals("stove")) {
            if (eggsCooked < requiredEggs) {
                eggsCooked++;
                validCombination = true;
                showSteamEffect();
                animateCooking(dropZone);
                
                if (eggsCooked >= requiredEggs) {
                    updateChecklist(1);
                }
            }
        } else if (ingredient.equals("bread") && zone.equals("toaster")) {
            if (sandwichMade < requiredSandwich) {
                sandwichMade++;
                validCombination = true;
                animateCooking(dropZone);
                
                if (sandwichMade >= requiredSandwich) {
                    updateChecklist(2);
                }
            }
        } else if (ingredient.equals("juice") && zone.equals("plate")) {
            if (juicePoured < requiredJuice) {
                juicePoured++;
                validCombination = true;
                animatePouring(dropZone);
                
                if (juicePoured >= requiredJuice) {
                    updateChecklist(3);
                }
            }
        }
        
        if (validCombination) {
            // Correct action
            animateSuccessFlash(dropZone);
            animatePop(dropZone);
            checkOrderCompletion();
        } else {
            // Wrong action
            animateShake(dropZone);
            loseOneStar();
        }
        
        // Reset ingredient background
        resetIngredientBackgrounds();
    }

    private void playOrderAudio() {
        if (isPlayingAudio) return;
        
        isPlayingAudio = true;
        imgSpeaker.setVisibility(View.INVISIBLE);
        waveformContainer.setVisibility(View.VISIBLE);
        
        // Animate waveform
        animateWaveform();
        
        // Simulate audio playback (3 seconds)
        waveformContainer.postDelayed(() -> {
            isPlayingAudio = false;
            imgSpeaker.setVisibility(View.VISIBLE);
            waveformContainer.setVisibility(View.INVISIBLE);
            
            // Show subtitle after audio
            txtOrderSubtitle.setVisibility(View.VISIBLE);
            animateSubtitleAppear();
        }, slowModeEnabled ? 4500 : 3000);
    }

    private void showHint() {
        // Pulse the next needed ingredient
        if (eggsCooked < requiredEggs) {
            animatePulseHint(ingredientEgg);
        } else if (sandwichMade < requiredSandwich) {
            animatePulseHint(ingredientBread);
        } else if (juicePoured < requiredJuice) {
            animatePulseHint(ingredientJuice);
        }
        
        // Replay the order
        playOrderAudio();
    }

    private void toggleSlowMode() {
        slowModeEnabled = !slowModeEnabled;
        btnSlowMode.setAlpha(slowModeEnabled ? 1.0f : 0.5f);
    }

    private void updateChecklist(int itemNumber) {
        View checkbox;
        LinearLayout checklistItem;
        
        switch (itemNumber) {
            case 1:
                checkbox = checkbox1;
                checklistItem = checklistItem1;
                break;
            case 2:
                checkbox = checkbox2;
                checklistItem = checklistItem2;
                break;
            case 3:
                checkbox = checkbox3;
                checklistItem = checklistItem3;
                break;
            default:
                return;
        }
        
        // Show checkmark
        checkbox.setBackgroundResource(R.drawable.ic_check_green);
        
        // Animate bounce
        animateChecklistBounce(checklistItem);
        
        // Change text color to green
        TextView textView = (TextView) ((LinearLayout) checklistItem).getChildAt(1);
        textView.setTextColor(0xFF4CAF50);
    }

    private void checkOrderCompletion() {
        if (eggsCooked >= requiredEggs && 
            sandwichMade >= requiredSandwich && 
            juicePoured >= requiredJuice) {
            
            // All items completed
            showCompletionPopup();
        }
    }

    private void showCompletionPopup() {
        completionOverlay.setVisibility(View.VISIBLE);
        completionOverlay.setAlpha(0f);
        
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(completionOverlay, "alpha", 0f, 1f);
        fadeIn.setDuration(400);
        fadeIn.start();
        
        // Animate confetti
        AnimatorSet confettiAnim = new AnimatorSet();
        confettiAnim.playTogether(
            ObjectAnimator.ofFloat(imgConfettiCompletion, "rotation", 0f, 360f),
            ObjectAnimator.ofFloat(imgConfettiCompletion, "scaleX", 0.5f, 1.2f, 1f),
            ObjectAnimator.ofFloat(imgConfettiCompletion, "scaleY", 0.5f, 1.2f, 1f)
        );
        confettiAnim.setDuration(1000);
        confettiAnim.start();
    }

    private void startNextOrder() {
        // Reset everything for next order
        completionOverlay.setVisibility(View.INVISIBLE);
        
        eggsCooked = 0;
        sandwichMade = 0;
        juicePoured = 0;
        
        checkbox1.setBackgroundResource(R.drawable.bg_checkbox_empty);
        checkbox2.setBackgroundResource(R.drawable.bg_checkbox_empty);
        checkbox3.setBackgroundResource(R.drawable.bg_checkbox_empty);
        
        txtCheckItem1.setTextColor(0xFF666666);
        txtCheckItem2.setTextColor(0xFF666666);
        txtCheckItem3.setTextColor(0xFF666666);
        
        txtOrderSubtitle.setVisibility(View.INVISIBLE);
        
        starsEarned = 3;
        updateStarDisplay();
    }

    private void loseOneStar() {
        if (starsEarned > 0) {
            starsEarned--;
            updateStarDisplay();
        }
    }

    private void updateStarDisplay() {
        star1.setImageResource(starsEarned >= 1 ? R.drawable.ic_star : R.drawable.ic_star_empty);
        star2.setImageResource(starsEarned >= 2 ? R.drawable.ic_star : R.drawable.ic_star_empty);
        star3.setImageResource(starsEarned >= 3 ? R.drawable.ic_star : R.drawable.ic_star_empty);
    }

    // Animation Methods
    private void animateIngredientLift(View ingredient) {
        AnimatorSet liftAnim = new AnimatorSet();
        liftAnim.playTogether(
            ObjectAnimator.ofFloat(ingredient, "scaleX", 1f, 1.1f),
            ObjectAnimator.ofFloat(ingredient, "scaleY", 1f, 1.1f),
            ObjectAnimator.ofFloat(ingredient, "translationZ", 0f, 8f)
        );
        liftAnim.setDuration(200);
        liftAnim.start();
    }

    private void animateZoneGlow(View zone) {
        AnimatorSet glowAnim = new AnimatorSet();
        glowAnim.playTogether(
            ObjectAnimator.ofFloat(zone, "scaleX", 1f, 1.05f, 1f),
            ObjectAnimator.ofFloat(zone, "scaleY", 1f, 1.05f, 1f)
        );
        glowAnim.setDuration(500);
        glowAnim.setRepeatCount(ObjectAnimator.INFINITE);
        glowAnim.start();
    }

    private void animateCooking(View zone) {
        AnimatorSet cookAnim = new AnimatorSet();
        cookAnim.playTogether(
            ObjectAnimator.ofFloat(zone, "rotation", 0f, 5f, -5f, 0f),
            ObjectAnimator.ofFloat(zone, "scaleX", 1f, 1.1f, 1f),
            ObjectAnimator.ofFloat(zone, "scaleY", 1f, 1.1f, 1f)
        );
        cookAnim.setDuration(600);
        cookAnim.start();
    }

    private void animatePouring(View zone) {
        AnimatorSet pourAnim = new AnimatorSet();
        pourAnim.playTogether(
            ObjectAnimator.ofFloat(zone, "rotation", 0f, 10f, 0f),
            ObjectAnimator.ofFloat(zone, "translationY", 0f, -20f, 0f)
        );
        pourAnim.setDuration(500);
        pourAnim.start();
    }

    private void animateSuccessFlash(View zone) {
        ObjectAnimator flashAnim = ObjectAnimator.ofFloat(zone, "alpha", 1f, 0.5f, 1f);
        flashAnim.setDuration(300);
        flashAnim.start();
    }

    private void animatePop(View zone) {
        AnimatorSet popAnim = new AnimatorSet();
        popAnim.playTogether(
            ObjectAnimator.ofFloat(zone, "scaleX", 1f, 1.2f, 1f),
            ObjectAnimator.ofFloat(zone, "scaleY", 1f, 1.2f, 1f)
        );
        popAnim.setDuration(400);
        popAnim.setInterpolator(new BounceInterpolator());
        popAnim.start();
    }

    private void animateShake(View zone) {
        AnimatorSet shakeAnim = new AnimatorSet();
        shakeAnim.playSequentially(
            ObjectAnimator.ofFloat(zone, "translationX", 0f, -15f, 15f, -15f, 15f, 0f)
        );
        shakeAnim.setDuration(500);
        shakeAnim.start();
    }

    private void animateWaveform() {
        AnimatorSet waveAnim1 = new AnimatorSet();
        waveAnim1.playTogether(
            ObjectAnimator.ofFloat(wave1, "scaleY", 1f, 1.5f, 1f)
        );
        waveAnim1.setDuration(400);
        waveAnim1.setRepeatCount(ObjectAnimator.INFINITE);
        waveAnim1.start();

        AnimatorSet waveAnim2 = new AnimatorSet();
        waveAnim2.playTogether(
            ObjectAnimator.ofFloat(wave2, "scaleY", 1f, 1.8f, 1f)
        );
        waveAnim2.setDuration(500);
        waveAnim2.setRepeatCount(ObjectAnimator.INFINITE);
        waveAnim2.setStartDelay(100);
        waveAnim2.start();

        AnimatorSet waveAnim3 = new AnimatorSet();
        waveAnim3.playTogether(
            ObjectAnimator.ofFloat(wave3, "scaleY", 1f, 1.5f, 1f)
        );
        waveAnim3.setDuration(400);
        waveAnim3.setRepeatCount(ObjectAnimator.INFINITE);
        waveAnim3.setStartDelay(200);
        waveAnim3.start();
    }

    private void animateSubtitleAppear() {
        txtOrderSubtitle.setAlpha(0f);
        txtOrderSubtitle.setTranslationY(-20f);
        
        AnimatorSet appearAnim = new AnimatorSet();
        appearAnim.playTogether(
            ObjectAnimator.ofFloat(txtOrderSubtitle, "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(txtOrderSubtitle, "translationY", -20f, 0f)
        );
        appearAnim.setDuration(500);
        appearAnim.setInterpolator(new DecelerateInterpolator());
        appearAnim.start();
    }

    private void animatePulseHint(View ingredient) {
        AnimatorSet pulseAnim = new AnimatorSet();
        pulseAnim.playTogether(
            ObjectAnimator.ofFloat(ingredient, "scaleX", 1f, 1.15f, 1f),
            ObjectAnimator.ofFloat(ingredient, "scaleY", 1f, 1.15f, 1f),
            ObjectAnimator.ofFloat(ingredient, "alpha", 1f, 0.7f, 1f)
        );
        pulseAnim.setDuration(800);
        pulseAnim.setRepeatCount(2);
        pulseAnim.start();
    }

    private void animateChecklistBounce(View checklistItem) {
        AnimatorSet bounceAnim = new AnimatorSet();
        bounceAnim.playTogether(
            ObjectAnimator.ofFloat(checklistItem, "scaleX", 1f, 1.1f, 1f),
            ObjectAnimator.ofFloat(checklistItem, "scaleY", 1f, 1.1f, 1f)
        );
        bounceAnim.setDuration(500);
        bounceAnim.setInterpolator(new BounceInterpolator());
        bounceAnim.start();
    }

    private void showSteamEffect() {
        imgSteam.setVisibility(View.VISIBLE);
        imgSteam.setAlpha(0f);
        
        AnimatorSet steamAnim = new AnimatorSet();
        steamAnim.playTogether(
            ObjectAnimator.ofFloat(imgSteam, "alpha", 0f, 0.7f),
            ObjectAnimator.ofFloat(imgSteam, "translationY", 0f, -30f),
            ObjectAnimator.ofFloat(imgSteam, "scaleX", 1f, 1.3f),
            ObjectAnimator.ofFloat(imgSteam, "scaleY", 1f, 1.3f)
        );
        steamAnim.setDuration(1000);
        steamAnim.start();
        
        steamAnim.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                imgSteam.setVisibility(View.INVISIBLE);
                imgSteam.setTranslationY(0f);
            }
        });
    }

    private void resetIngredientBackgrounds() {
        ingredientEgg.setBackgroundResource(R.drawable.bg_ingredient_item);
        ingredientBread.setBackgroundResource(R.drawable.bg_ingredient_item);
        ingredientCheese.setBackgroundResource(R.drawable.bg_ingredient_item);
        ingredientBacon.setBackgroundResource(R.drawable.bg_ingredient_item);
        ingredientFruit.setBackgroundResource(R.drawable.bg_ingredient_item);
        ingredientVegetables.setBackgroundResource(R.drawable.bg_ingredient_item);
        ingredientJuice.setBackgroundResource(R.drawable.bg_ingredient_item);
    }
}
