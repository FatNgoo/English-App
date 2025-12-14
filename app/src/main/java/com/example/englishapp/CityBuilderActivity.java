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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

public class CityBuilderActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack;
    private TextView txtGoldCount, txtLevel, txtHappinessLevel, txtNewLevel;
    private ImageView imgHappiness, imgLevelStars, ghostBuilding;
    private ImageView check1, check2, check3;
    private TextView txtQuest1, txtQuest2, txtQuest3;
    private View xpBarFill;
    private GridLayout cityGrid;
    private FrameLayout levelUpOverlay, cityMapContainer;
    private LinearLayout btnGetCoins, btnContinue;
    private LinearLayout tabHouses, tabTrees, tabVehicles, tabZoo, tabRoads;
    private LinearLayout btnPlaceHouse, btnPlaceTree, btnPlaceShop, btnPlaceRoad, btnPlacePlayground, btnUnlockLand;
    private FrameLayout btnZoomIn, btnZoomOut;
    
    // Game State
    private int goldCoins = 245;
    private int currentLevel = 5;
    private int currentXP = 120;
    private int xpToNextLevel = 200;
    private float cityScale = 1.0f;
    
    // City Grid
    private static final int GRID_COLS = 4;
    private static final int GRID_ROWS = 5;
    private CityTile[][] tiles = new CityTile[GRID_ROWS][GRID_COLS];
    private int unlockedTiles = 12; // Start with 12 tiles unlocked
    
    // Building System
    private Building selectedBuilding = null;
    private boolean isPlacingMode = false;
    
    // Quests
    private int housesBuilt = 0;
    private int treesPlanted = 0;
    private boolean roadAdded = false;
    
    // Happiness
    private int happinessScore = 75; // 0-100
    
    private Handler handler = new Handler();
    private ScaleGestureDetector scaleDetector;
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_builder);

        initializeViews();
        initializeCityGrid();
        setupGestureDetectors();
        setupListeners();
        updateUI();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        txtGoldCount = findViewById(R.id.txtGoldCount);
        txtLevel = findViewById(R.id.txtLevel);
        txtHappinessLevel = findViewById(R.id.txtHappinessLevel);
        txtNewLevel = findViewById(R.id.txtNewLevel);
        
        imgHappiness = findViewById(R.id.imgHappiness);
        imgLevelStars = findViewById(R.id.imgLevelStars);
        ghostBuilding = findViewById(R.id.ghostBuilding);
        
        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        check3 = findViewById(R.id.check3);
        
        txtQuest1 = findViewById(R.id.txtQuest1);
        txtQuest2 = findViewById(R.id.txtQuest2);
        txtQuest3 = findViewById(R.id.txtQuest3);
        
        xpBarFill = findViewById(R.id.xpBarFill);
        cityGrid = findViewById(R.id.cityGrid);
        levelUpOverlay = findViewById(R.id.levelUpOverlay);
        cityMapContainer = findViewById(R.id.cityMapContainer);
        
        btnGetCoins = findViewById(R.id.btnGetCoins);
        btnContinue = findViewById(R.id.btnContinue);
        
        tabHouses = findViewById(R.id.tabHouses);
        tabTrees = findViewById(R.id.tabTrees);
        tabVehicles = findViewById(R.id.tabVehicles);
        tabZoo = findViewById(R.id.tabZoo);
        tabRoads = findViewById(R.id.tabRoads);
        
        btnPlaceHouse = findViewById(R.id.btnPlaceHouse);
        btnPlaceTree = findViewById(R.id.btnPlaceTree);
        btnPlaceShop = findViewById(R.id.btnPlaceShop);
        btnPlaceRoad = findViewById(R.id.btnPlaceRoad);
        btnPlacePlayground = findViewById(R.id.btnPlacePlayground);
        btnUnlockLand = findViewById(R.id.btnUnlockLand);
        
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);
    }

    private void initializeCityGrid() {
        int tileSize = (cityMapContainer.getWidth() > 0 ? cityMapContainer.getWidth() : 1000) / GRID_COLS - 16;
        
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                final int finalRow = row;
                final int finalCol = col;
                
                FrameLayout tileView = new FrameLayout(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = tileSize;
                params.height = tileSize;
                params.setMargins(4, 4, 4, 4);
                tileView.setLayoutParams(params);
                
                // Determine if tile is unlocked
                int tileIndex = row * GRID_COLS + col;
                boolean isUnlocked = tileIndex < unlockedTiles;
                
                if (isUnlocked) {
                    tileView.setBackgroundResource(R.drawable.bg_land_tile_empty);
                } else {
                    tileView.setBackgroundResource(R.drawable.bg_land_tile_locked);
                    
                    // Add lock icon
                    ImageView lockIcon = new ImageView(this);
                    lockIcon.setImageResource(R.drawable.ic_lock);
                    FrameLayout.LayoutParams lockParams = new FrameLayout.LayoutParams(48, 48);
                    lockParams.gravity = android.view.Gravity.CENTER;
                    lockIcon.setLayoutParams(lockParams);
                    tileView.addView(lockIcon);
                }
                
                // Tap listener for placing buildings
                tileView.setOnClickListener(v -> handleTileTap(finalRow, finalCol));
                
                cityGrid.addView(tileView);
                tiles[finalRow][finalCol] = new CityTile(tileView, isUnlocked, null);
            }
        }
    }

    private void setupGestureDetectors() {
        scaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                cityScale *= detector.getScaleFactor();
                cityScale = Math.max(0.5f, Math.min(cityScale, 2.0f));
                
                cityMapContainer.setScaleX(cityScale);
                cityMapContainer.setScaleY(cityScale);
                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            animateButtonPress(v);
            finish();
        });
        
        btnGetCoins.setOnClickListener(v -> {
            animateButtonPress(v);
            // In real app, show rewarded ad or mini-game
            addCoins(50);
        });
        
        btnContinue.setOnClickListener(v -> {
            animateButtonPress(v);
            levelUpOverlay.setVisibility(View.GONE);
        });
        
        // Zoom controls
        btnZoomIn.setOnClickListener(v -> {
            animateButtonPress(v);
            zoomIn();
        });
        
        btnZoomOut.setOnClickListener(v -> {
            animateButtonPress(v);
            zoomOut();
        });
        
        // Tab listeners
        tabHouses.setOnClickListener(v -> {
            animateButtonPress(v);
            selectTab(tabHouses);
        });
        
        tabTrees.setOnClickListener(v -> {
            animateButtonPress(v);
            selectTab(tabTrees);
        });
        
        tabVehicles.setOnClickListener(v -> {
            animateButtonPress(v);
            selectTab(tabVehicles);
        });
        
        tabZoo.setOnClickListener(v -> {
            animateButtonPress(v);
            selectTab(tabZoo);
        });
        
        tabRoads.setOnClickListener(v -> {
            animateButtonPress(v);
            selectTab(tabRoads);
        });
        
        // Building placement buttons
        btnPlaceHouse.setOnClickListener(v -> {
            animateButtonPress(v);
            startPlacementMode(new Building("house", R.drawable.ic_house, 50));
        });
        
        btnPlaceTree.setOnClickListener(v -> {
            animateButtonPress(v);
            startPlacementMode(new Building("tree", R.drawable.ic_tree, 20));
        });
        
        btnPlaceShop.setOnClickListener(v -> {
            animateButtonPress(v);
            startPlacementMode(new Building("shop", R.drawable.ic_shop, 80));
        });
        
        btnPlaceRoad.setOnClickListener(v -> {
            animateButtonPress(v);
            startPlacementMode(new Building("road", R.drawable.ic_road, 15));
        });
        
        btnPlacePlayground.setOnClickListener(v -> {
            animateButtonPress(v);
            startPlacementMode(new Building("playground", R.drawable.ic_playground, 100));
        });
        
        btnUnlockLand.setOnClickListener(v -> {
            animateButtonPress(v);
            unlockNewLand();
        });
        
        // Touch listener for pinch-to-zoom
        cityMapContainer.setOnTouchListener((v, event) -> {
            scaleDetector.onTouchEvent(event);
            return true;
        });
    }

    private void selectTab(LinearLayout selectedTab) {
        // Reset all tabs
        tabHouses.setBackgroundResource(0);
        tabTrees.setBackgroundResource(0);
        tabVehicles.setBackgroundResource(0);
        tabZoo.setBackgroundResource(0);
        tabRoads.setBackgroundResource(0);
        
        // Highlight selected tab
        selectedTab.setBackgroundResource(R.drawable.bg_button_rounded);
        animatePulse(selectedTab);
    }

    private void startPlacementMode(Building building) {
        if (goldCoins < building.cost) {
            showToast("Not enough coins!");
            return;
        }
        
        selectedBuilding = building;
        isPlacingMode = true;
        
        // Show ghost building
        ghostBuilding.setImageResource(building.iconRes);
        ghostBuilding.setVisibility(View.VISIBLE);
        animatePulse(ghostBuilding);
    }

    private void handleTileTap(int row, int col) {
        CityTile tile = tiles[row][col];
        
        if (!tile.isUnlocked) {
            showToast("Unlock this area first!");
            return;
        }
        
        if (isPlacingMode && selectedBuilding != null) {
            if (tile.building != null) {
                showToast("Tile already occupied!");
                highlightTileInvalid(tile.view);
                return;
            }
            
            // Place building
            placeBuilding(row, col);
        } else {
            // Tap existing building to show info
            if (tile.building != null) {
                animateBounce(tile.view);
            }
        }
    }

    private void placeBuilding(int row, int col) {
        CityTile tile = tiles[row][col];
        
        // Deduct coins
        goldCoins -= selectedBuilding.cost;
        updateUI();
        
        // Create building view
        ImageView buildingView = new ImageView(this);
        buildingView.setImageResource(selectedBuilding.iconRes);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        );
        params.setMargins(8, 8, 8, 8);
        buildingView.setLayoutParams(params);
        
        tile.view.removeAllViews();
        tile.view.addView(buildingView);
        tile.building = selectedBuilding;
        
        // Animate placement
        animateBuildingPlacement(buildingView);
        
        // Update quests
        updateQuests(selectedBuilding.type);
        
        // Update happiness
        updateHappiness(selectedBuilding.type);
        
        // Add XP
        gainXP(10);
        
        // Reset placement mode
        isPlacingMode = false;
        selectedBuilding = null;
        ghostBuilding.setVisibility(View.GONE);
        
        // Highlight tile as valid
        highlightTileValid(tile.view);
    }

    private void unlockNewLand() {
        if (goldCoins < 200) {
            showToast("Not enough coins!");
            return;
        }
        
        // Find next locked tile
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                if (!tiles[row][col].isUnlocked) {
                    // Unlock this tile
                    goldCoins -= 200;
                    unlockedTiles++;
                    
                    CityTile tile = tiles[row][col];
                    tile.isUnlocked = true;
                    tile.view.removeAllViews();
                    tile.view.setBackgroundResource(R.drawable.bg_land_tile_empty);
                    
                    // Animate unlock
                    animateUnlock(tile.view);
                    
                    updateUI();
                    gainXP(50);
                    return;
                }
            }
        }
    }

    private void updateQuests(String buildingType) {
        boolean questCompleted = false;
        
        switch (buildingType) {
            case "house":
                housesBuilt++;
                if (housesBuilt >= 2 && check1.getTag() == null) {
                    check1.setImageResource(R.drawable.ic_quest_check);
                    check1.setTag("completed");
                    animatePulse(check1);
                    questCompleted = true;
                }
                break;
                
            case "tree":
                treesPlanted++;
                if (treesPlanted >= 3 && check2.getTag() == null) {
                    check2.setImageResource(R.drawable.ic_quest_check);
                    check2.setTag("completed");
                    animatePulse(check2);
                    questCompleted = true;
                }
                break;
                
            case "road":
                if (!roadAdded && check3.getTag() == null) {
                    roadAdded = true;
                    check3.setImageResource(R.drawable.ic_quest_check);
                    check3.setTag("completed");
                    animatePulse(check3);
                    questCompleted = true;
                }
                break;
        }
        
        if (questCompleted) {
            handler.postDelayed(() -> checkAllQuestsComplete(), 500);
        }
    }

    private void checkAllQuestsComplete() {
        if (check1.getTag() != null && check2.getTag() != null && check3.getTag() != null) {
            // All quests complete - give reward
            addCoins(50);
            showToast("All quests complete! +50 coins! 🎉");
            
            // Reset quests
            handler.postDelayed(() -> {
                check1.setImageResource(R.drawable.ic_circle);
                check2.setImageResource(R.drawable.ic_circle);
                check3.setImageResource(R.drawable.ic_circle);
                check1.setTag(null);
                check2.setTag(null);
                check3.setTag(null);
                housesBuilt = 0;
                treesPlanted = 0;
                roadAdded = false;
            }, 3000);
        }
    }

    private void updateHappiness(String buildingType) {
        // Increase happiness for parks, trees, playgrounds
        if (buildingType.equals("tree") || buildingType.equals("playground")) {
            happinessScore = Math.min(100, happinessScore + 5);
        } else {
            happinessScore = Math.min(100, happinessScore + 2);
        }
        
        updateHappinessDisplay();
    }

    private void updateHappinessDisplay() {
        if (happinessScore >= 70) {
            imgHappiness.setImageResource(R.drawable.ic_happy_face);
            txtHappinessLevel.setText("Happy");
            txtHappinessLevel.setTextColor(0xFF4CAF50);
        } else if (happinessScore >= 40) {
            imgHappiness.setImageResource(R.drawable.ic_neutral_face);
            txtHappinessLevel.setText("Neutral");
            txtHappinessLevel.setTextColor(0xFFFF9800);
        } else {
            imgHappiness.setImageResource(R.drawable.ic_sad_face);
            txtHappinessLevel.setText("Unhappy");
            txtHappinessLevel.setTextColor(0xFFF44336);
        }
    }

    private void gainXP(int amount) {
        currentXP += amount;
        
        if (currentXP >= xpToNextLevel) {
            levelUp();
        }
        
        updateXPBar();
    }

    private void levelUp() {
        currentLevel++;
        currentXP = 0;
        xpToNextLevel = currentLevel * 50;
        
        // Show level up popup
        txtNewLevel.setText("You reached Level " + currentLevel + "!");
        levelUpOverlay.setVisibility(View.VISIBLE);
        
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(levelUpOverlay, "alpha", 0f, 1.0f);
        fadeIn.setDuration(400);
        fadeIn.start();
        
        // Animate stars
        animateLevelStars();
        
        // Give reward
        addCoins(100);
        
        updateUI();
    }

    private void addCoins(int amount) {
        goldCoins += amount;
        updateUI();
        
        // Animate coin counter
        animatePulse(txtGoldCount);
        showCoinSparkles();
    }

    private void zoomIn() {
        cityScale = Math.min(2.0f, cityScale + 0.2f);
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(cityMapContainer, "scaleX", cityMapContainer.getScaleX(), cityScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(cityMapContainer, "scaleY", cityMapContainer.getScaleY(), cityScale);
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        scaleX.start();
        scaleY.start();
    }

    private void zoomOut() {
        cityScale = Math.max(0.5f, cityScale - 0.2f);
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(cityMapContainer, "scaleX", cityMapContainer.getScaleX(), cityScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(cityMapContainer, "scaleY", cityMapContainer.getScaleY(), cityScale);
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        scaleX.start();
        scaleY.start();
    }

    private void updateUI() {
        txtGoldCount.setText(String.valueOf(goldCoins));
        txtLevel.setText("Level " + currentLevel);
        updateXPBar();
        updateHappinessDisplay();
    }

    private void updateXPBar() {
        float progress = (float) currentXP / xpToNextLevel;
        View xpBarBg = findViewById(R.id.xpBarBg);
        int maxWidth = xpBarBg.getWidth() > 0 ? xpBarBg.getWidth() : 200;
        
        ViewGroup.LayoutParams params = xpBarFill.getLayoutParams();
        params.width = (int) (maxWidth * progress);
        xpBarFill.setLayoutParams(params);
    }

    // Animation Methods
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
        scaleX.setDuration(400);
        scaleY.setDuration(400);
        scaleX.start();
        scaleY.start();
    }

    private void animateBounce(View view) {
        ObjectAnimator bounce = ObjectAnimator.ofFloat(view, "translationY", 0, -20, 0);
        bounce.setDuration(500);
        bounce.start();
    }

    private void animateBuildingPlacement(View view) {
        view.setScaleX(0f);
        view.setScaleY(0f);
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1.2f, 1.0f);
        scaleX.setDuration(600);
        scaleY.setDuration(600);
        scaleX.start();
        scaleY.start();
    }

    private void animateUnlock(View view) {
        // Bright light effect
        view.setAlpha(0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1.0f);
        fadeIn.setDuration(800);
        fadeIn.start();
        
        // Scale bounce
        view.setScaleX(0.5f);
        view.setScaleY(0.5f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1.2f, 1.0f);
        scaleX.setDuration(600);
        scaleY.setDuration(600);
        scaleX.start();
        scaleY.start();
    }

    private void animateLevelStars() {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(imgLevelStars, "rotation", 0, 360);
        rotate.setDuration(2000);
        rotate.setRepeatCount(ObjectAnimator.INFINITE);
        rotate.start();
    }

    private void highlightTileValid(View tile) {
        tile.setBackgroundResource(R.drawable.bg_tile_valid);
        
        handler.postDelayed(() -> {
            tile.setBackgroundResource(R.drawable.bg_land_tile_empty);
        }, 1000);
    }

    private void highlightTileInvalid(View tile) {
        tile.setBackgroundResource(R.drawable.bg_tile_invalid);
        
        ObjectAnimator shake = ObjectAnimator.ofFloat(tile, "translationX", 0, -10, 10, -10, 10, 0);
        shake.setDuration(500);
        shake.start();
        
        handler.postDelayed(() -> {
            if (tiles != null) {
                for (int row = 0; row < GRID_ROWS; row++) {
                    for (int col = 0; col < GRID_COLS; col++) {
                        if (tiles[row][col].view == tile) {
                            if (tiles[row][col].building == null) {
                                tile.setBackgroundResource(R.drawable.bg_land_tile_empty);
                            }
                            return;
                        }
                    }
                }
            }
        }, 1000);
    }

    private void showCoinSparkles() {
        // In real app, show particle effect
        animatePulse(findViewById(R.id.goldContainer));
    }

    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    // Data Classes
    private static class CityTile {
        View view;
        boolean isUnlocked;
        Building building;
        
        CityTile(View view, boolean isUnlocked, Building building) {
            this.view = view;
            this.isUnlocked = isUnlocked;
            this.building = building;
        }
    }

    private static class Building {
        String type;
        int iconRes;
        int cost;
        
        Building(String type, int iconRes, int cost) {
            this.type = type;
            this.iconRes = iconRes;
            this.cost = cost;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
