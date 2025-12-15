package com.example.englishapp;

import com.shop.englishapp.R;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.shop.englishapp.masterchef.utils.ProgressManager;
import com.shop.englishapp.masterchef.models.UserProgress;
import java.util.HashMap;
import java.util.Map;

public class MasterChefActivity extends AppCompatActivity {

    // Progress Manager
    private ProgressManager progressManager;
    private UserProgress userProgress;
    
    // Order System
    private FoodOrder currentOrder;
    private OrderGenerator orderGenerator;
    private Map<String, Integer> userPlate; // What user has prepared
    private MediaPlayer mediaPlayer;
    private int audioReplayCount = 0;
    private int hintsUsed = 0;

    // UI Components
    private ImageButton btnBack, btnHint, btnReplayAudio, btnSlowMode;
    private CardView orderCard, cookingStationCard, lockedOverlay;
    private ImageView imgCustomerAvatar, imgSpeaker, imgSteam, imgSmoke, imgFryingPan;
    private LinearLayout waveformContainer, completionOverlay;
    private TextView txtOrderSubtitle, txtEnergyCount, txtGoldCount, txtLevel, txtLockedMessage;
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
    private Button btnNextOrder, btnGoToAcademy;
    private ImageView imgConfettiCompletion;
    
    // State Variables (Deprecated - now using FoodOrder system)
    private int starsEarned = 0;
    private boolean isPlayingAudio = false;
    private boolean slowModeEnabled = false;
    private int mistakeCount = 0; // Track number of mistakes for progressive hints

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        android.util.Log.d("MasterChef", "onCreate started");
        
        try {
            android.util.Log.d("MasterChef", "Setting content view...");
            setContentView(R.layout.master_chef);
            android.util.Log.d("MasterChef", "Content view set successfully");
            
            Toast.makeText(this, "✅ Master Chef opened successfully! 🍳", Toast.LENGTH_LONG).show();
            
            android.util.Log.d("MasterChef", "Initializing Progress Manager...");
            // Initialize Progress Manager (with null check)
            try {
                progressManager = ProgressManager.getInstance(this);
                android.util.Log.d("MasterChef", "Progress Manager initialized");
            } catch (Exception pmEx) {
                android.util.Log.e("MasterChef", "ProgressManager error: " + pmEx.getMessage());
                pmEx.printStackTrace();
                // Continue without ProgressManager
                progressManager = null;
            }
            
            android.util.Log.d("MasterChef", "Initializing order system...");
            // Initialize order system
            orderGenerator = new OrderGenerator();
            userPlate = new HashMap<>();
            android.util.Log.d("MasterChef", "Order system initialized");
            
            android.util.Log.d("MasterChef", "Loading user progress...");
            // Load user progress
            loadUserProgress();
            
            android.util.Log.d("MasterChef", "Initializing views...");
            // Initialize views
            initializeViews();
            
            android.util.Log.d("MasterChef", "Checking unlock...");
            // Check context lock first
            checkMasterChefUnlock();
            
            android.util.Log.d("MasterChef", "Setting up listeners...");
            // Setup listeners
            setupListeners();
            setupDragAndDrop();
            
            android.util.Log.d("MasterChef", "Starting first order...");
            // Start first order
            startNextOrder();
            
            android.util.Log.d("MasterChef", "onCreate completed successfully");
            
        } catch (Exception e) {
            android.util.Log.e("MasterChef", "FATAL ERROR in onCreate", e);
            e.printStackTrace();
            Toast.makeText(this, "❌ Error loading Master Chef: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Don't crash - stay on screen with error
        }
    }
    
    private void loadUserProgress() {
        if (progressManager != null) {
            progressManager.getUserProgress(new ProgressManager.OnProgressCallback() {
                @Override
                public void onSuccess(UserProgress progress) {
                    userProgress = progress;
                    android.util.Log.d("MasterChef", "Progress loaded - Energy: " + userProgress.getEnergy() + 
                        ", Food lesson: " + userProgress.isFoodLessonCompleted());
                    updateCurrencyDisplay();
                }

                @Override
                public void onFailure(String error) {
                    android.util.Log.e("MasterChef", "Failed to load progress: " + error);
                    // DEMO MODE: Tạo progress mặc định
                    userProgress = new UserProgress();
                    userProgress.setFoodLessonCompleted(true); // DEMO: cho phép chơi
                    userProgress.setMasterChefUnlocked(true);
                    userProgress.setEnergy(5); // Bắt đầu với 5 Energy
                    userProgress.setGold(100);
                    userProgress.setCurrentLevel(1);
                    updateCurrencyDisplay();
                }
            });
        } else {
            // Fallback: DEMO progress
            android.util.Log.d("MasterChef", "No ProgressManager - using DEMO data");
            userProgress = new UserProgress();
            userProgress.setFoodLessonCompleted(true); // DEMO
            userProgress.setMasterChefUnlocked(true);
            userProgress.setEnergy(5);
            userProgress.setGold(100);
            userProgress.setCurrentLevel(1);
            updateCurrencyDisplay();
        }
    }
    
    private void checkMasterChefUnlock() {
        // DEMO MODE: Always unlocked for testing
        // In production, this will check userProgress.isMasterChefUnlocked()
        
        if (userProgress == null) {
            // Create demo progress
            userProgress = new UserProgress();
            userProgress.setFoodLessonCompleted(true);
            userProgress.setMasterChefUnlocked(true);
            userProgress.setEnergy(5);
            userProgress.setGold(100);
        }
        
        // Show welcome message
        Toast.makeText(this, "Welcome to Master Chef! 👨‍🍳", Toast.LENGTH_SHORT).show();
        updateCurrencyDisplay();
        
        // TODO: Add proper unlock check when integrated with Academy
        /*
        if (!userProgress.isMasterChefUnlocked() && !userProgress.canUnlockMasterChef()) {
            showLockedState("Let's learn Food & Drinks first! 📚");
            return;
        }
        
        if (userProgress.getEnergy() <= 0) {
            showLockedState("No energy! ⚡ Complete lessons to recharge!");
            return;
        }
        */
        
        hideLockedState();
    }
    
    private void showLockedState(String message) {
        if (lockedOverlay != null) {
            lockedOverlay.setVisibility(View.VISIBLE);
        }
        if (txtLockedMessage != null) {
            txtLockedMessage.setText(message);
        }
        // Disable all game controls
        if (orderCard != null) orderCard.setVisibility(View.GONE);
        if (cookingStationCard != null) cookingStationCard.setVisibility(View.GONE);
    }
    
    private void hideLockedState() {
        if (lockedOverlay != null) {
            lockedOverlay.setVisibility(View.GONE);
        }
        // Enable game controls
        if (orderCard != null) orderCard.setVisibility(View.VISIBLE);
        if (cookingStationCard != null) cookingStationCard.setVisibility(View.VISIBLE);
    }
    
    private void updateCurrencyDisplay() {
        if (userProgress != null) {
            if (txtEnergyCount != null) {
                txtEnergyCount.setText(String.valueOf(userProgress.getEnergy()));
            }
            if (txtGoldCount != null) {
                txtGoldCount.setText(String.valueOf(userProgress.getGold()));
            }
            if (txtLevel != null) {
                txtLevel.setText("Lvl " + userProgress.getCurrentLevel());
            }
        }
    }

    private void initializeViews() {
        try {
            // Header
            btnBack = findViewById(R.id.btnBack);
            star1 = findViewById(R.id.star1);
            star2 = findViewById(R.id.star2);
            star3 = findViewById(R.id.star3);
            
            // Currency Display - use correct IDs from layout
            txtEnergyCount = findViewById(R.id.txtEnergyCount);
            txtGoldCount = findViewById(R.id.txtGoldCount);
            txtLevel = findViewById(R.id.txtLevel);
            
            // Locked Overlay
            lockedOverlay = findViewById(R.id.lockedOverlay);
            txtLockedMessage = findViewById(R.id.txtLockedMessage);
            btnGoToAcademy = findViewById(R.id.btnGoToAcademy);
            
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
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing views", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        android.util.Log.d("MasterChef", "setupListeners called");
        
        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                android.util.Log.d("MasterChef", "Back button clicked");
                finish();
            });
        } else {
            android.util.Log.e("MasterChef", "btnBack is NULL!");
        }
        
        // Go to Academy button (for locked state)
        if (btnGoToAcademy != null) {
            btnGoToAcademy.setOnClickListener(v -> {
                Intent intent = new Intent(MasterChefActivity.this, AcademyLearningActivity.class);
                startActivity(intent);
                finish();
            });
        }
        
        // Play Order button
        View btnPlayOrder = findViewById(R.id.btnPlayOrder);
        android.util.Log.d("MasterChef", "btnPlayOrder: " + (btnPlayOrder != null ? "FOUND" : "NULL"));
        
        if (btnPlayOrder != null) {
            btnPlayOrder.setOnClickListener(v -> {
                android.util.Log.d("MasterChef", "Play Order button CLICKED!");
                Toast.makeText(this, "🔊 Playing order...", Toast.LENGTH_SHORT).show();
                playOrderAudio();
            });
        } else {
            android.util.Log.e("MasterChef", "btnPlayOrder is NULL - cannot set listener!");
            Toast.makeText(this, "❌ Play Order button not found!", Toast.LENGTH_LONG).show();
        }
        
        // Hint Tools
        if (btnHint != null) {
            btnHint.setOnClickListener(v -> {
                android.util.Log.d("MasterChef", "Hint button clicked");
                showHint();
            });
        }
        if (btnReplayAudio != null) {
            btnReplayAudio.setOnClickListener(v -> {
                android.util.Log.d("MasterChef", "Replay Audio clicked");
                playOrderAudio();
            });
        }
        if (btnSlowMode != null) {
            btnSlowMode.setOnClickListener(v -> {
                android.util.Log.d("MasterChef", "Slow Mode clicked");
                toggleSlowMode();
            });
        }
        
        // Completion
        if (btnNextOrder != null) {
            btnNextOrder.setOnClickListener(v -> {
                android.util.Log.d("MasterChef", "Next Order clicked");
                startNextOrder();
            });
        }
        
        android.util.Log.d("MasterChef", "setupListeners completed");
    }

    private void setupDragAndDrop() {
        android.util.Log.d("MasterChef", "setupDragAndDrop called");
        
        // Make ingredients draggable
        setupDraggableIngredient(ingredientEgg, "egg");
        setupDraggableIngredient(ingredientBread, "bread");
        setupDraggableIngredient(ingredientCheese, "cheese");
        setupDraggableIngredient(ingredientBacon, "bacon");
        setupDraggableIngredient(ingredientFruit, "fruit");
        setupDraggableIngredient(ingredientVegetables, "vegetables");
        setupDraggableIngredient(ingredientJuice, "juice");
        
        android.util.Log.d("MasterChef", "Ingredients made draggable");
        
        // Make cooking areas drop zones
        setupDropZone(stoveArea, "stove");
        setupDropZone(cuttingBoardArea, "cutting_board");
        setupDropZone(toasterArea, "toaster");
        setupDropZone(plateArea, "plate");
        
        android.util.Log.d("MasterChef", "Drop zones set up - setupDragAndDrop completed");
    }

    private void setupDraggableIngredient(FrameLayout ingredient, String ingredientType) {
        if (ingredient == null) {
            android.util.Log.e("MasterChef", "Ingredient is NULL for type: " + ingredientType);
            return;
        }
        
        android.util.Log.d("MasterChef", "Setting up draggable ingredient: " + ingredientType);
        
        ingredient.setOnLongClickListener(v -> {
            android.util.Log.d("MasterChef", "Ingredient LONG CLICKED: " + ingredientType);
            Toast.makeText(this, "Dragging " + ingredientType + "...", Toast.LENGTH_SHORT).show();
            
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(null, shadowBuilder, ingredientType, 0);
            
            // Highlight ingredient
            ingredient.setBackgroundResource(R.drawable.bg_ingredient_selected);
            animateIngredientLift(ingredient);
            
            return true;
        });
        
        // Also add regular click for testing
        ingredient.setOnClickListener(v -> {
            android.util.Log.d("MasterChef", "Ingredient CLICKED (short): " + ingredientType);
            Toast.makeText(this, "Tap & Hold to drag " + ingredientType, Toast.LENGTH_SHORT).show();
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
        if (currentOrder == null) return;
        
        // For now, accept ingredients dropped on the plate area
        // In full implementation, different zones would have different cooking actions
        if (!zone.equals("plate")) {
            // Show cooking animation but don't count yet
            if (zone.equals("stove")) {
                showSteamEffect();
                animateCooking(dropZone);
            } else if (zone.equals("toaster")) {
                animateCooking(dropZone);
            }
            
            Toast.makeText(this, "Great! Now drag to the plate when ready.", Toast.LENGTH_SHORT).show();
            resetIngredientBackgrounds();
            return;
        }
        
        // Add ingredient to user's plate
        int currentCount = userPlate.getOrDefault(ingredient, 0);
        userPlate.put(ingredient, currentCount + 1);
        
        // Visual feedback
        animateSuccessFlash(dropZone);
        animatePop(dropZone);
        
        // Update checklist (simplified for now)
        updateChecklistFromPlate();
        
        // Check if order is complete
        checkOrderCompletion();
        
        // Reset ingredient background
        resetIngredientBackgrounds();
    }
    
    private void updateChecklistFromPlate() {
        // Count total items on plate
        int totalItems = 0;
        for (int qty : userPlate.values()) {
            totalItems += qty;
        }
        
        // Update checkboxes based on progress
        int requiredTotal = currentOrder != null ? currentOrder.getTotalItems() : 3;
        float progress = (float) totalItems / requiredTotal;
        
        if (progress >= 0.33f && checkbox1 != null) {
            checkbox1.setBackgroundResource(R.drawable.bg_checkbox_checked);
            if (txtCheckItem1 != null) txtCheckItem1.setTextColor(0xFF4CAF50);
        }
        if (progress >= 0.66f && checkbox2 != null) {
            checkbox2.setBackgroundResource(R.drawable.bg_checkbox_checked);
            if (txtCheckItem2 != null) txtCheckItem2.setTextColor(0xFF4CAF50);
        }
        if (progress >= 1.0f && checkbox3 != null) {
            checkbox3.setBackgroundResource(R.drawable.bg_checkbox_checked);
            if (txtCheckItem3 != null) txtCheckItem3.setTextColor(0xFF4CAF50);
        }
    }

    private void playOrderAudio() {
        android.util.Log.d("MasterChef", "playOrderAudio called");
        
        if (isPlayingAudio || currentOrder == null) {
            android.util.Log.d("MasterChef", "Skipping - already playing or no order");
            return;
        }
        
        // Giới hạn BẮT BUỘC: Tối đa 2 lần replay = 3 lần nghe total
        if (audioReplayCount >= 3) {
            Toast.makeText(this, "🎧 You've heard this 3 times!\nTry your best!", Toast.LENGTH_LONG).show();
            
            // Gợi ý sau lần nghe thứ 3
            if (mistakeCount == 0) {
                // Lần đầu - gợi ý nhẹ
                Toast.makeText(this, "💡 Hint: Count the items in the list!", Toast.LENGTH_LONG).show();
            }
            return;
        }
        
        audioReplayCount++;
        isPlayingAudio = true;
        
        android.util.Log.d("MasterChef", "Playing audio - attempt " + audioReplayCount + "/3");
        
        if (imgSpeaker != null) imgSpeaker.setVisibility(View.INVISIBLE);
        if (waveformContainer != null) waveformContainer.setVisibility(View.VISIBLE);
        
        // Animate waveform
        animateWaveform();
        
        // Get audio resource ID (if available)
        int audioResId = currentOrder.getAudioResourceId();
        
        if (audioResId != 0) {
            // Play actual audio file
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                mediaPlayer = MediaPlayer.create(this, audioResId);
                
                // Giọng đọc chậm, rõ ràng (0.8x speed cho trẻ em)
                if (slowModeEnabled) {
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(0.7f));
                } else {
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(0.8f));
                }
                
                mediaPlayer.setOnCompletionListener(mp -> {
                    isPlayingAudio = false;
                    if (imgSpeaker != null) imgSpeaker.setVisibility(View.VISIBLE);
                    if (waveformContainer != null) waveformContainer.setVisibility(View.INVISIBLE);
                    
                    // Hiện subtitle sau audio (cho phụ huynh/giáo viên, không ép bé đọc)
                    if (txtOrderSubtitle != null && audioReplayCount >= 2) {
                        txtOrderSubtitle.setVisibility(View.VISIBLE);
                        txtOrderSubtitle.setAlpha(0.6f); // Mờ nhẹ - không nổi bật
                        animateSubtitleAppear();
                    }
                    
                    mp.release();
                });
                
                mediaPlayer.start();
                
                // Toast thông báo thân thiện
                String message = audioReplayCount == 1 ? "🎧 Listen carefully!" : 
                               audioReplayCount == 2 ? "🎧 Listen again!" : 
                               "🎧 Last time!";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
                isPlayingAudio = false;
            }
        } else {
            // Simulate audio playback (no audio file yet)
            waveformContainer.postDelayed(() -> {
                isPlayingAudio = false;
                if (imgSpeaker != null) imgSpeaker.setVisibility(View.VISIBLE);
                if (waveformContainer != null) waveformContainer.setVisibility(View.INVISIBLE);
                
                // Show subtitle after audio
                if (txtOrderSubtitle != null) {
                    txtOrderSubtitle.setVisibility(View.VISIBLE);
                    animateSubtitleAppear();
                }
            }, slowModeEnabled ? 4500 : 3000);
        }
    }

    private void showHint() {
        if (currentOrder == null) return;
        
        hintsUsed++;
        
        // Track hints used in UserProgress
        if (userProgress != null) {
            userProgress.setHintsUsed(userProgress.getHintsUsed() + 1);
        }
        
        // Find the first incomplete item and pulse it
        for (Map.Entry<String, Integer> entry : currentOrder.getItems().entrySet()) {
            String foodItem = entry.getKey();
            int required = entry.getValue();
            int current = userPlate.getOrDefault(foodItem, 0);
            
            if (current < required) {
                // Pulse the ingredient
                View ingredient = getIngredientViewByName(foodItem);
                if (ingredient != null) {
                    animatePulseHint(ingredient);
                    Toast.makeText(this, "Try " + foodItem + "!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        
        // Optionally replay the order audio
        if (audioReplayCount < 3) {
            playOrderAudio();
        }
    }
    
    private View getIngredientViewByName(String foodName) {
        switch (foodName) {
            case OrderGenerator.FOOD_EGG: return ingredientEgg;
            case OrderGenerator.FOOD_BREAD: return ingredientBread;
            case OrderGenerator.FOOD_CHEESE: return ingredientCheese;
            case OrderGenerator.FOOD_BACON: return ingredientBacon;
            case OrderGenerator.FOOD_APPLE:
            case OrderGenerator.FOOD_BANANA: return ingredientFruit;
            case OrderGenerator.FOOD_CARROT: return ingredientVegetables;
            case OrderGenerator.FOOD_JUICE:
            case OrderGenerator.FOOD_MILK: return ingredientJuice;
            default: return null;
        }
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
        if (currentOrder == null) return;
        
        android.util.Log.d("MasterChef", "Checking order completion...");
        android.util.Log.d("MasterChef", "User plate: " + userPlate);
        android.util.Log.d("MasterChef", "Required: " + currentOrder.getItems());
        
        // Check if user's plate matches the order
        if (currentOrder.isComplete(userPlate)) {
            // ✅ PERFECT! - Phản hồi tích cực
            android.util.Log.d("MasterChef", "✅ ORDER COMPLETED!");
            
            // Calculate stars based on mistakes
            starsEarned = currentOrder.calculateStars(userPlate);
            
            // Calculate rewards - LUÔN CÓ THƯỞNG dù sai
            int goldReward = starsEarned * 10; // 10 gold per star (min 10 gold)
            int xpReward = starsEarned * 20;   // 20 XP per star
            
            // Update progress
            if (userProgress != null && progressManager != null) {
                // KHÔNG TRỪ ENERGY Ở ĐÂY - đã trừ khi vào game
                userProgress.setGold(userProgress.getGold() + goldReward);
                userProgress.setTotalStarsEarned(userProgress.getTotalStarsEarned() + starsEarned);
                userProgress.setTotalOrdersCompleted(userProgress.getTotalOrdersCompleted() + 1);
                
                if (starsEarned == 3) {
                    userProgress.setPerfectOrdersCount(userProgress.getPerfectOrdersCount() + 1);
                }
                
                // Add XP
                userProgress.setExperiencePoints(userProgress.getExperiencePoints() + xpReward);
                
                // Save progress
                progressManager.rewardOrder(goldReward, starsEarned, new ProgressManager.OnProgressCallback() {
                    @Override
                    public void onSuccess(UserProgress progress) {
                        userProgress = progress;
                        updateCurrencyDisplay();
                    }
                    
                    @Override
                    public void onFailure(String error) {
                        android.util.Log.e("MasterChef", "Failed to save progress: " + error);
                    }
                });
            }
            
            // Update star display
            updateStarDisplay();
            
            // Show completion popup với thông điệp tích cực
            showCompletionPopup(goldReward, xpReward);
            
        } else {
            // Chưa đủ items - gợi ý tiếp tục
            int totalItemsNeeded = currentOrder.getTotalItems();
            int totalItemsAdded = 0;
            for (int qty : userPlate.values()) {
                totalItemsAdded += qty;
            }
            
            if (totalItemsAdded < totalItemsNeeded) {
                Toast.makeText(this, "Keep cooking! You need " + (totalItemsNeeded - totalItemsAdded) + " more items", Toast.LENGTH_SHORT).show();
            } else {
                // Sai items - gợi ý nhẹ nhàng
                mistakeCount++;
                showProgressiveHints();
            }
        }
    }
    
    // Hệ thống gợi ý theo tầng - KHÔNG PHẠT
    private void showProgressiveHints() {
        if (mistakeCount == 1) {
            // Lần 1 sai: Gợi ý nhẹ
            Toast.makeText(this, "🤔 Try again! Listen carefully!", Toast.LENGTH_LONG).show();
            // Blink checkboxes
            if (checklistItem1 != null) animateCheckboxHint(checklistItem1);
        } else if (mistakeCount == 2) {
            // Lần 2 sai: Suggest replay
            Toast.makeText(this, "💡 Listen one more time? Tap 🔄", Toast.LENGTH_LONG).show();
            // Highlight replay button
            if (btnReplayAudio != null) {
                btnReplayAudio.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).withEndAction(() -> {
                    btnReplayAudio.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                }).start();
            }
        } else if (mistakeCount >= 3) {
            // Lần 3+ sai: Gợi ý visual
            Toast.makeText(this, "👀 Look at the order list below!", Toast.LENGTH_LONG).show();
            highlightChecklistItems();
            
            // Cho phép hoàn thành với ít sao
            starsEarned = 1;
            updateStarDisplay();
        }
    }
    
    private void animateCheckboxHint(View checkbox) {
        if (checkbox != null) {
            checkbox.animate().alpha(0.5f).setDuration(200).withEndAction(() -> {
                checkbox.animate().alpha(1f).setDuration(200).start();
            }).start();
        }
    }
    
    private void highlightChecklistItems() {
        if (txtCheckItem1 != null) txtCheckItem1.setTextColor(0xFFFF9800); // Orange
        if (txtCheckItem2 != null) txtCheckItem2.setTextColor(0xFFFF9800);
        if (txtCheckItem3 != null) txtCheckItem3.setTextColor(0xFFFF9800);
    }
    
    private void showCompletionPopup(int goldReward, int xpReward) {
        if (completionOverlay == null) return;
        
        completionOverlay.setVisibility(View.VISIBLE);
        completionOverlay.setAlpha(0f);
        
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(completionOverlay, "alpha", 0f, 1f);
        fadeIn.setDuration(400);
        fadeIn.start();
        
        // Animate confetti - VUI VẺ với mọi kết quả
        if (imgConfettiCompletion != null) {
            AnimatorSet confettiAnim = new AnimatorSet();
            confettiAnim.playTogether(
                ObjectAnimator.ofFloat(imgConfettiCompletion, "rotation", 0f, 360f),
                ObjectAnimator.ofFloat(imgConfettiCompletion, "scaleX", 0.5f, 1.2f, 1f),
                ObjectAnimator.ofFloat(imgConfettiCompletion, "scaleY", 0.5f, 1.2f, 1f)
            );
            confettiAnim.setDuration(1000);
            confettiAnim.start();
        }
        
        // Thông điệp LUÔN TÍCH CỰC - không chê, không phạt
        String encouragement;
        if (starsEarned == 3) {
            encouragement = "🌟🌟🌟 Perfect! You're amazing, Chef!";
        } else if (starsEarned == 2) {
            encouragement = "⭐⭐ Great job! Keep practicing!";
        } else {
            encouragement = "⭐ Good try! You can do it better!";
        }
        
        String rewardMessage = "\n+" + goldReward + " gold 🪙 | +" + xpReward + " XP 🧠";
        Toast.makeText(this, encouragement + rewardMessage, Toast.LENGTH_LONG).show();
        
        android.util.Log.d("MasterChef", "Completion shown - Stars: " + starsEarned + ", Rewards: " + goldReward + " gold, " + xpReward + " XP");
    }
    
    private void startNextOrder() {
        android.util.Log.d("MasterChef", "Starting next order...");
        
        // Check if user has energy
        if (userProgress != null && userProgress.getEnergy() <= 0) {
            android.util.Log.d("MasterChef", "No energy left - showing locked state");
            showLockedState("No energy! ⚡\nComplete lessons to recharge!");
            return;
        }
        
        // Hide completion overlay
        if (completionOverlay != null) {
            completionOverlay.setVisibility(View.INVISIBLE);
        }
        
        // Reset game state
        userPlate.clear();
        audioReplayCount = 0;
        hintsUsed = 0;
        mistakeCount = 0; // Reset mistake counter
        slowModeEnabled = false;
        
        // Generate new order based on user level
        int level = userProgress != null ? userProgress.getCurrentLevel() : 1;
        currentOrder = orderGenerator.generateOrder(level);
        
        android.util.Log.d("MasterChef", "New order generated: " + currentOrder.getOrderText());
        
        // Update UI with order text (MỜ NHẸ - không ép đọc)
        if (txtOrderSubtitle != null) {
            txtOrderSubtitle.setText(currentOrder.getOrderText());
            txtOrderSubtitle.setVisibility(View.INVISIBLE); // Ẩn ban đầu - chỉ hiện sau khi nghe
            txtOrderSubtitle.setAlpha(0.5f); // Mờ nhẹ
        }
        
        // Reset checklist
        if (checkbox1 != null) checkbox1.setBackgroundResource(R.drawable.bg_checkbox_empty);
        if (checkbox2 != null) checkbox2.setBackgroundResource(R.drawable.bg_checkbox_empty);
        if (checkbox3 != null) checkbox3.setBackgroundResource(R.drawable.bg_checkbox_empty);
        
        if (txtCheckItem1 != null) txtCheckItem1.setTextColor(0xFF666666);
        if (txtCheckItem2 != null) txtCheckItem2.setTextColor(0xFF666666);
        if (txtCheckItem3 != null) txtCheckItem3.setTextColor(0xFF666666);
        
        // Reset star display to 3 stars (luôn bắt đầu với tinh thần tích cực)
        starsEarned = 3;
        updateStarDisplay();
        
        // Trừ 1 Energy (đã trừ khi checkUnlock)
        // Just update display - actual saving will happen on order completion
        updateCurrencyDisplay();
        
        // Play audio order - TỰ ĐỘNG PHÁT lần đầu
        playOrderAudio();
        
        Toast.makeText(this, "🎧 New customer! Listen carefully!", Toast.LENGTH_SHORT).show();
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
        // AnimatorSet doesn't have setRepeatCount
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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Release MediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
