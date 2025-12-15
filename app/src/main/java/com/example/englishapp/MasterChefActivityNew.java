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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.shop.englishapp.R;

import com.shop.englishapp.masterchef.models.Order;
import com.shop.englishapp.masterchef.models.UserProgress;
import com.shop.englishapp.masterchef.utils.AudioManager;
import com.shop.englishapp.masterchef.utils.OrderGenerator;
import com.shop.englishapp.masterchef.utils.ProgressManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Master Chef Activity - Listen & Cook Mode
 * Complete implementation with:
 * - Audio-first learning
 * - Currency system (Energy, Gold, Stars)
 * - Context lock
 * - Core loop integration
 */
public class MasterChefActivityNew extends AppCompatActivity {

    // Managers
    private ProgressManager progressManager;
    private AudioManager audioManager;
    private OrderGenerator orderGenerator;
    
    // Current Game State
    private UserProgress userProgress;
    private Order currentOrder;
    private int currentStars = 3;
    private Map<String, Integer> cookedItems;
    private boolean isOrderInProgress = false;

    // UI Components
    private ImageButton btnBack, btnHint, btnReplayAudio, btnSlowMode;
    private CardView orderCard, cookingStationCard;
    private ImageView imgCustomerAvatar, imgSpeaker, imgSteam, imgSmoke;
    private LinearLayout waveformContainer, completionOverlay;
    private TextView txtOrderSubtitle, txtEnergyCount, txtGoldCount, txtLevel;
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
    private TextView txtRewardGold, txtRewardStars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_chef);

        // Initialize managers
        progressManager = ProgressManager.getInstance(this);
        audioManager = AudioManager.getInstance();
        orderGenerator = new OrderGenerator();
        cookedItems = new HashMap<>();

        initializeViews();
        setupListeners();
        setupDragAndDrop();
        
        // Initialize audio system
        audioManager.initialize(this, new AudioManager.OnInitCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MasterChefActivityNew.this, "Audio ready!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MasterChefActivityNew.this, "Audio error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Load user progress and check unlock status
        loadUserProgress();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        
        // Currency display
        txtEnergyCount = findViewById(R.id.txtEnergyCount);
        txtGoldCount = findViewById(R.id.txtGoldCount);
        txtLevel = findViewById(R.id.txtLevel);
        
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
        txtRewardGold = findViewById(R.id.txtRewardGold);
        txtRewardStars = findViewById(R.id.txtRewardStars);
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
        
        View btnPlayOrder = findViewById(R.id.btnPlayOrder);
        if (btnPlayOrder != null) {
            btnPlayOrder.setOnClickListener(v -> {
                if (!isOrderInProgress) {
                    startNewOrder();
                } else {
                    playOrderAudio();
                }
            });
        }
        
        if (btnHint != null) {
            btnHint.setOnClickListener(v -> showHint());
        }
        if (btnReplayAudio != null) {
            btnReplayAudio.setOnClickListener(v -> replayAudio());
        }
        if (btnSlowMode != null) {
            btnSlowMode.setOnClickListener(v -> toggleSlowMode());
        }
        if (btnNextOrder != null) {
            btnNextOrder.setOnClickListener(v -> prepareNextOrder());
        }
    }

    private void loadUserProgress() {
        progressManager.getUserProgress(new ProgressManager.OnProgressCallback() {
            @Override
            public void onSuccess(UserProgress progress) {
                userProgress = progress;
                updateCurrencyDisplay();
                
                // Check if Master Chef is unlocked
                if (!progress.isMasterChefUnlocked()) {
                    showLockedDialog();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MasterChefActivityNew.this, "Error loading progress", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLockedDialog() {
        new AlertDialog.Builder(this)
            .setTitle("🔒 Master Chef Locked")
            .setMessage("You need to complete the 'Food & Drinks' lesson in the Academy first!\n\n" +
                       "Go to Academy → Learn Food vocabulary → Come back here!")
            .setPositiveButton("Go to Academy", (dialog, which) -> {
                // TODO: Navigate to Academy screen
                Toast.makeText(this, "Opening Academy...", Toast.LENGTH_SHORT).show();
                finish();
            })
            .setNegativeButton("Later", (dialog, which) -> finish())
            .setCancelable(false)
            .show();
    }

    private void updateCurrencyDisplay() {
        if (userProgress != null) {
            txtEnergyCount.setText(String.valueOf(userProgress.getEnergy()));
            txtGoldCount.setText(String.valueOf(userProgress.getGold()));
            txtLevel.setText("Level " + userProgress.getCurrentLevel());
        }
    }

    private void startNewOrder() {
        // Check if we have enough energy
        if (userProgress.getEnergy() < 1) {
            showNoEnergyDialog();
            return;
        }

        // Consume energy
        progressManager.consumeEnergy(1, new ProgressManager.OnEnergyCallback() {
            @Override
            public void onSuccess(int remainingEnergy) {
                userProgress.setEnergy(remainingEnergy);
                updateCurrencyDisplay();
                
                // Generate new order
                int difficulty = Math.min(userProgress.getCurrentLevel() / 3 + 1, 3);
                currentOrder = orderGenerator.generateOrder(difficulty);
                currentStars = 3;
                cookedItems.clear();
                
                setupOrderUI();
                playOrderAudio();
                isOrderInProgress = true;
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MasterChefActivityNew.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNoEnergyDialog() {
        new AlertDialog.Builder(this)
            .setTitle("⚡ No Energy")
            .setMessage("You're out of energy!\n\n" +
                       "Energy recharges over time, or you can:\n" +
                       "• Complete lessons in the Academy for energy\n" +
                       "• Wait for natural regeneration (1 per 10 minutes)")
            .setPositiveButton("Go to Academy", (dialog, which) -> {
                // TODO: Navigate to Academy
                finish();
            })
            .setNegativeButton("OK", null)
            .show();
    }

    private void setupOrderUI() {
        // Update checklist with order items
        if (currentOrder.getItems().size() > 0) {
            Order.OrderItem item0 = currentOrder.getItems().get(0);
            txtCheckItem1.setText(item0.getQuantity() + " " + item0.getFoodName());
            checklistItem1.setVisibility(View.VISIBLE);
            checkbox1.setBackgroundResource(R.drawable.bg_checkbox_empty);
        }
        
        if (currentOrder.getItems().size() > 1) {
            Order.OrderItem item1 = currentOrder.getItems().get(1);
            txtCheckItem2.setText(item1.getQuantity() + " " + item1.getFoodName());
            checklistItem2.setVisibility(View.VISIBLE);
            checkbox2.setBackgroundResource(R.drawable.bg_checkbox_empty);
        }
        
        if (currentOrder.getItems().size() > 2) {
            Order.OrderItem item2 = currentOrder.getItems().get(2);
            txtCheckItem3.setText(item2.getQuantity() + " " + item2.getFoodName());
            checklistItem3.setVisibility(View.VISIBLE);
            checkbox3.setBackgroundResource(R.drawable.bg_checkbox_empty);
        }
        
        updateStarDisplay();
        audioManager.resetReplayCount();
    }

    private void playOrderAudio() {
        String orderText = currentOrder.getSpokenSentence();
        
        imgSpeaker.setVisibility(View.INVISIBLE);
        waveformContainer.setVisibility(View.VISIBLE);
        animateWaveform();
        
        audioManager.speakOrder(orderText, new AudioManager.OnAudioCallback() {
            @Override
            public void onStart() {
                // Audio started
            }

            @Override
            public void onComplete() {
                runOnUiThread(() -> {
                    imgSpeaker.setVisibility(View.VISIBLE);
                    waveformContainer.setVisibility(View.INVISIBLE);
                    
                    // Show subtitle (optional, for support)
                    txtOrderSubtitle.setText(orderText);
                    txtOrderSubtitle.setVisibility(View.VISIBLE);
                    animateSubtitleAppear();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MasterChefActivityNew.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void replayAudio() {
        if (audioManager.canReplay()) {
            playOrderAudio();
            userProgress.setAudioReplaysUsed(userProgress.getAudioReplaysUsed() + 1);
        } else {
            Toast.makeText(this, "No more replays available!", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleSlowMode() {
        // Toggle slow mode - assuming default is false
        audioManager.setSlowMode(true);
        btnSlowMode.setAlpha(1.0f);
        Toast.makeText(this, "Slow mode ON", Toast.LENGTH_SHORT).show();
    }

    private void showHint() {
        // Find next uncompleted item
        for (Order.OrderItem item : currentOrder.getItems()) {
            int cooked = cookedItems.getOrDefault(item.getFoodId(), 0);
            if (cooked < item.getQuantity()) {
                String hint = "Try adding " + item.getFoodName();
                audioManager.playHintSound(hint);
                
                // Pulse the correct ingredient
                FrameLayout ingredientView = getIngredientView(item.getFoodId());
                if (ingredientView != null) {
                    animatePulseHint(ingredientView);
                }
                break;
            }
        }
        
        userProgress.setHintsUsed(userProgress.getHintsUsed() + 1);
        loseOneStar();
    }

    private FrameLayout getIngredientView(String foodId) {
        switch (foodId) {
            case "egg": return ingredientEgg;
            case "bread": return ingredientBread;
            case "cheese": return ingredientCheese;
            case "bacon": return ingredientBacon;
            case "apple":
            case "banana":
            case "fruit": return ingredientFruit;
            case "tomato":
            case "vegetables": return ingredientVegetables;
            case "milk":
            case "juice": return ingredientJuice;
            default: return null;
        }
    }

    private void setupDragAndDrop() {
        setupDraggableIngredient(ingredientEgg, "egg");
        setupDraggableIngredient(ingredientBread, "bread");
        setupDraggableIngredient(ingredientCheese, "cheese");
        setupDraggableIngredient(ingredientBacon, "bacon");
        setupDraggableIngredient(ingredientFruit, "apple");
        setupDraggableIngredient(ingredientVegetables, "tomato");
        setupDraggableIngredient(ingredientJuice, "juice");
        
        setupDropZone(stoveArea, "stove");
        setupDropZone(cuttingBoardArea, "cutting_board");
        setupDropZone(toasterArea, "toaster");
        setupDropZone(plateArea, "plate");
    }

    private void setupDraggableIngredient(FrameLayout ingredient, String ingredientType) {
        ingredient.setOnLongClickListener(v -> {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(null, shadowBuilder, ingredientType, 0);
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
                    v.setAlpha(0.8f);
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
        // Check if this ingredient is in the order
        boolean isCorrect = false;
        Order.OrderItem targetItem = null;
        
        for (Order.OrderItem item : currentOrder.getItems()) {
            if (item.getFoodId().equals(ingredient)) {
                targetItem = item;
                int cooked = cookedItems.getOrDefault(ingredient, 0);
                
                if (cooked < item.getQuantity()) {
                    cookedItems.put(ingredient, cooked + 1);
                    isCorrect = true;
                    
                    animateSuccessFlash(dropZone);
                    animateCooking(dropZone);
                    
                    if (zone.equals("stove")) {
                        showSteamEffect();
                    }
                    
                    // Check if this item is complete
                    if (cookedItems.get(ingredient) >= item.getQuantity()) {
                        updateChecklist(currentOrder.getItems().indexOf(item) + 1);
                    }
                    
                    checkOrderCompletion();
                    break;
                }
            }
        }
        
        if (!isCorrect) {
            animateShake(dropZone);
            loseOneStar();
            Toast.makeText(this, "Wrong ingredient!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateChecklist(int itemNumber) {
        View checkbox = null;
        LinearLayout checklistItem = null;
        TextView textView = null;
        
        switch (itemNumber) {
            case 1:
                checkbox = checkbox1;
                checklistItem = checklistItem1;
                textView = txtCheckItem1;
                break;
            case 2:
                checkbox = checkbox2;
                checklistItem = checklistItem2;
                textView = txtCheckItem2;
                break;
            case 3:
                checkbox = checkbox3;
                checklistItem = checklistItem3;
                textView = txtCheckItem3;
                break;
        }
        
        if (checkbox != null && checklistItem != null && textView != null) {
            checkbox.setBackgroundResource(R.drawable.ic_check_green);
            textView.setTextColor(0xFF4CAF50);
            animateChecklistBounce(checklistItem);
        }
    }

    private void checkOrderCompletion() {
        boolean allCompleted = true;
        
        for (Order.OrderItem item : currentOrder.getItems()) {
            int cooked = cookedItems.getOrDefault(item.getFoodId(), 0);
            if (cooked < item.getQuantity()) {
                allCompleted = false;
                break;
            }
        }
        
        if (allCompleted) {
            completeOrder();
        }
    }

    private void completeOrder() {
        isOrderInProgress = false;
        
        // Calculate rewards
        int baseGold = currentOrder.getGoldReward();
        int goldBonus = currentStars * 20; // Bonus for stars
        int totalGold = baseGold + goldBonus;
        
        int xpReward = 30 * currentStars;
        
        // Award rewards
        progressManager.rewardOrder(totalGold, currentStars, new ProgressManager.OnProgressCallback() {
            @Override
            public void onSuccess(UserProgress progress) {
                userProgress = progress;
                updateCurrencyDisplay();
                
                // Add XP
                progressManager.addExperience(xpReward, new ProgressManager.OnLevelUpCallback() {
                    @Override
                    public void onLevelUp(int newLevel) {
                        showCompletionPopup(totalGold, true);
                    }

                    @Override
                    public void onXpGained(int xp) {
                        showCompletionPopup(totalGold, false);
                    }
                });
            }
        });
        
        audioManager.playCelebrationSound(this);
    }

    private void showCompletionPopup(int goldEarned, boolean leveledUp) {
        txtRewardGold.setText("+" + goldEarned + " Gold");
        txtRewardStars.setText(currentStars + " Stars");
        
        completionOverlay.setVisibility(View.VISIBLE);
        completionOverlay.setAlpha(0f);
        
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(completionOverlay, "alpha", 0f, 1f);
        fadeIn.setDuration(400);
        fadeIn.start();
        
        animateConfetti();
        
        if (leveledUp) {
            Toast.makeText(this, "🎉 Level Up! Level " + userProgress.getCurrentLevel(), Toast.LENGTH_LONG).show();
        }
    }

    private void prepareNextOrder() {
        completionOverlay.setVisibility(View.INVISIBLE);
        cookedItems.clear();
        
        checkbox1.setBackgroundResource(R.drawable.bg_checkbox_empty);
        checkbox2.setBackgroundResource(R.drawable.bg_checkbox_empty);
        checkbox3.setBackgroundResource(R.drawable.bg_checkbox_empty);
        
        txtCheckItem1.setTextColor(0xFF666666);
        txtCheckItem2.setTextColor(0xFF666666);
        txtCheckItem3.setTextColor(0xFF666666);
        
        txtOrderSubtitle.setVisibility(View.INVISIBLE);
        
        startNewOrder();
    }

    private void loseOneStar() {
        if (currentStars > 1) {
            currentStars--;
            updateStarDisplay();
        }
    }

    private void updateStarDisplay() {
        star1.setImageResource(currentStars >= 1 ? R.drawable.ic_star : R.drawable.ic_star_empty);
        star2.setImageResource(currentStars >= 2 ? R.drawable.ic_star : R.drawable.ic_star_empty);
        star3.setImageResource(currentStars >= 3 ? R.drawable.ic_star : R.drawable.ic_star_empty);
    }

    // Animation Methods (keeping existing ones from original code)
    
    private void animateIngredientLift(View ingredient) {
        AnimatorSet liftAnim = new AnimatorSet();
        liftAnim.playTogether(
            ObjectAnimator.ofFloat(ingredient, "scaleX", 1f, 1.1f),
            ObjectAnimator.ofFloat(ingredient, "scaleY", 1f, 1.1f)
        );
        liftAnim.setDuration(200);
        liftAnim.start();
    }

    private void animateCooking(View zone) {
        AnimatorSet cookAnim = new AnimatorSet();
        cookAnim.playTogether(
            ObjectAnimator.ofFloat(zone, "scaleX", 1f, 1.1f, 1f),
            ObjectAnimator.ofFloat(zone, "scaleY", 1f, 1.1f, 1f)
        );
        cookAnim.setDuration(600);
        cookAnim.start();
    }

    private void animateSuccessFlash(View zone) {
        ObjectAnimator flashAnim = ObjectAnimator.ofFloat(zone, "alpha", 1f, 0.5f, 1f);
        flashAnim.setDuration(300);
        flashAnim.start();
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
        // AnimatorSet doesn't have setRepeatCount
        waveAnim1.start();

        AnimatorSet waveAnim2 = new AnimatorSet();
        waveAnim2.playTogether(
            ObjectAnimator.ofFloat(wave2, "scaleY", 1f, 1.8f, 1f)
        );
        waveAnim2.setDuration(500);
        // AnimatorSet doesn't have setRepeatCount
        waveAnim2.setStartDelay(100);
        waveAnim2.start();

        AnimatorSet waveAnim3 = new AnimatorSet();
        waveAnim3.playTogether(
            ObjectAnimator.ofFloat(wave3, "scaleY", 1f, 1.5f, 1f)
        );
        waveAnim3.setDuration(400);
        // AnimatorSet doesn't have setRepeatCount
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
        // AnimatorSet doesn't have setRepeatCount
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
    }

    private void animateConfetti() {
        AnimatorSet confettiAnim = new AnimatorSet();
        confettiAnim.playTogether(
            ObjectAnimator.ofFloat(imgConfettiCompletion, "rotation", 0f, 360f),
            ObjectAnimator.ofFloat(imgConfettiCompletion, "scaleX", 0.5f, 1.2f, 1f),
            ObjectAnimator.ofFloat(imgConfettiCompletion, "scaleY", 0.5f, 1.2f, 1f)
        );
        confettiAnim.setDuration(1000);
        confettiAnim.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioManager.shutdown();
    }

    @Override
    public void onBackPressed() {
        if (isOrderInProgress) {
            new AlertDialog.Builder(this)
                .setTitle("Leave Master Chef?")
                .setMessage("You will lose progress on this order.")
                .setPositiveButton("Leave", (dialog, which) -> {
                    super.onBackPressed();
                })
                .setNegativeButton("Stay", null)
                .show();
        } else {
            super.onBackPressed();
        }
    }
}
