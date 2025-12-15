package com.example.englishapp;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.shop.englishapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvatarStudioActivity extends AppCompatActivity {

    // UI Components
    private TextView tvCoins, tvLevel, tvFeedbackMessage;
    private View xpProgress, feedbackOverlay;
    private LinearLayout btnGetCoins, btnSaveLook, btnShare, btnMyItems;
    private LinearLayout btnNavHome, btnNavMap, btnNavGames, btnNavProfile;
    private FrameLayout avatarContainer;
    
    // Avatar Layers
    private ImageView imgAvatarBase, imgHairstyle, imgClothes, imgShoes;
    private ImageView imgAccessory, imgHat, imgBag, imgSparkle;
    
    // Rating Stars
    private ImageView imgStar1, imgStar2, imgStar3;
    
    // Expression Buttons
    private View btnExpressionSmile, btnExpressionLaugh, btnExpressionSurprised, btnExpressionWink;
    
    // Rotate Buttons
    private View btnRotateLeft, btnRotateRight;
    
    // Category Tabs
    private LinearLayout tabClothes, tabShoes, tabHats, tabAccessories, tabHairstyles, tabBags;
    
    // Item Cards
    private LinearLayout itemTshirt, itemDress, itemSneakers, itemCap, itemGlasses, itemBackpack;
    private TextView btnBuyTshirt, btnBuyDress, btnBuySneakers, btnBuyCap, btnBuyGlasses, btnBuyBackpack;
    private ImageView lockTshirt, lockDress, lockSneakers, lockCap, lockGlasses, lockBackpack;
    
    // Game Data
    private int coins = 320;
    private int currentLevel = 4;
    private int currentXP = 80;
    private int xpToNextLevel = 150;
    private int fashionRating = 0; // 0-3 stars
    private String currentExpression = "smile";
    private float avatarRotation = 0f;
    
    // Fashion Item Data
    private Map<String, FashionItem> equippedItems;
    private List<FashionItem> allItems;
    private List<FashionItem> ownedItems;
    private String currentCategory = "clothes";
    
    // Fashion Item Class
    private static class FashionItem {
        String id;
        String name;
        String category;
        int iconRes;
        int price;
        boolean owned;
        boolean equipped;
        int[] colorVariants;
        
        FashionItem(String id, String name, String category, int iconRes, int price, boolean owned) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.iconRes = iconRes;
            this.price = price;
            this.owned = owned;
            this.equipped = false;
            this.colorVariants = new int[]{Color.parseColor("#F8BBD0"), 
                                          Color.parseColor("#64B5F6"), 
                                          Color.parseColor("#FFD93D"), 
                                          Color.parseColor("#A5D6A7")};
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avatar_studio);

        initializeViews();
        initializeFashionData();
        setupAvatarLayers();
        setupListeners();
        updateUI();
    }

    private void initializeViews() {
        // Coins & Level
        tvCoins = findViewById(R.id.tvCoins);
        tvLevel = findViewById(R.id.tvLevel);
        xpProgress = findViewById(R.id.xpProgress);
        btnGetCoins = findViewById(R.id.btnGetCoins);
        
        // Avatar Layers
        imgAvatarBase = findViewById(R.id.imgAvatarBase);
        imgHairstyle = findViewById(R.id.imgHairstyle);
        imgClothes = findViewById(R.id.imgClothes);
        imgShoes = findViewById(R.id.imgShoes);
        imgAccessory = findViewById(R.id.imgAccessory);
        imgHat = findViewById(R.id.imgHat);
        imgBag = findViewById(R.id.imgBag);
        imgSparkle = findViewById(R.id.imgSparkle);
        avatarContainer = findViewById(R.id.avatarContainer);
        
        // Rating Stars
        imgStar1 = findViewById(R.id.imgStar1);
        imgStar2 = findViewById(R.id.imgStar2);
        imgStar3 = findViewById(R.id.imgStar3);
        
        // Expression Buttons
        btnExpressionSmile = findViewById(R.id.btnExpressionSmile);
        btnExpressionLaugh = findViewById(R.id.btnExpressionLaugh);
        btnExpressionSurprised = findViewById(R.id.btnExpressionSurprised);
        btnExpressionWink = findViewById(R.id.btnExpressionWink);
        
        // Rotate Buttons
        btnRotateLeft = findViewById(R.id.btnRotateLeft);
        btnRotateRight = findViewById(R.id.btnRotateRight);
        
        // Save & Share
        btnSaveLook = findViewById(R.id.btnSaveLook);
        btnShare = findViewById(R.id.btnShare);
        btnMyItems = findViewById(R.id.btnMyItems);
        
        // Category Tabs
        tabClothes = findViewById(R.id.tabClothes);
        tabShoes = findViewById(R.id.tabShoes);
        tabHats = findViewById(R.id.tabHats);
        tabAccessories = findViewById(R.id.tabAccessories);
        tabHairstyles = findViewById(R.id.tabHairstyles);
        tabBags = findViewById(R.id.tabBags);
        
        // Item Cards
        itemTshirt = findViewById(R.id.itemTshirt);
        itemDress = findViewById(R.id.itemDress);
        itemSneakers = findViewById(R.id.itemSneakers);
        itemCap = findViewById(R.id.itemCap);
        itemGlasses = findViewById(R.id.itemGlasses);
        itemBackpack = findViewById(R.id.itemBackpack);
        
        btnBuyTshirt = findViewById(R.id.btnBuyTshirt);
        btnBuyDress = findViewById(R.id.btnBuyDress);
        btnBuySneakers = findViewById(R.id.btnBuySneakers);
        btnBuyCap = findViewById(R.id.btnBuyCap);
        btnBuyGlasses = findViewById(R.id.btnBuyGlasses);
        btnBuyBackpack = findViewById(R.id.btnBuyBackpack);
        
        lockTshirt = findViewById(R.id.lockTshirt);
        lockDress = findViewById(R.id.lockDress);
        lockSneakers = findViewById(R.id.lockSneakers);
        lockCap = findViewById(R.id.lockCap);
        lockGlasses = findViewById(R.id.lockGlasses);
        lockBackpack = findViewById(R.id.lockBackpack);
        
        // Feedback
        feedbackOverlay = findViewById(R.id.feedbackOverlay);
        tvFeedbackMessage = findViewById(R.id.tvFeedbackMessage);
        
        // Navigation
        btnNavHome = findViewById(R.id.btnNavHome);
        btnNavMap = findViewById(R.id.btnNavMap);
        btnNavGames = findViewById(R.id.btnNavGames);
        btnNavProfile = findViewById(R.id.btnNavProfile);
    }

    private void initializeFashionData() {
        equippedItems = new HashMap<>();
        allItems = new ArrayList<>();
        ownedItems = new ArrayList<>();
        
        // Create fashion items
        allItems.add(new FashionItem("tshirt", "T-Shirt", "clothes", R.drawable.ic_tshirt, 30, true));
        allItems.add(new FashionItem("dress", "Dress", "clothes", R.drawable.ic_dress, 50, false));
        allItems.add(new FashionItem("sneakers", "Sneakers", "shoes", R.drawable.ic_sneakers, 40, true));
        allItems.add(new FashionItem("cap", "Cap", "hats", R.drawable.ic_cap, 25, false));
        allItems.add(new FashionItem("glasses", "Glasses", "accessories", R.drawable.ic_glasses, 35, false));
        allItems.add(new FashionItem("backpack", "Backpack", "bags", R.drawable.ic_backpack, 45, false));
        allItems.add(new FashionItem("hair", "Hairstyle", "hairstyles", R.drawable.ic_hair, 20, true));
        
        // Add owned items
        for (FashionItem item : allItems) {
            if (item.owned) {
                ownedItems.add(item);
            }
        }
        
        // Equip default items
        equipItem(findItemById("tshirt"));
        equipItem(findItemById("sneakers"));
        equipItem(findItemById("hair"));
    }

    private void setupAvatarLayers() {
        // Set initial avatar base (body)
        imgAvatarBase.setImageResource(R.drawable.ic_expression_smile);
        
        // Apply equipped items to layers
        updateAllAvatarLayers();
    }

    private void setupListeners() {
        // Back button
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                animateButtonPress(v);
                finish();
            });
        }
        
        // Get Coins button
        if (btnGetCoins != null) {
            btnGetCoins.setOnClickListener(v -> {
                animateButtonPress(v);
                coins += 50;
                updateUI();
                showFeedback("You got 50 coins! 🪙", R.drawable.ic_coin);
            });
        }        
        // Expression buttons
        btnExpressionSmile.setOnClickListener(v -> changeExpression("smile", R.drawable.ic_expression_smile));
        btnExpressionLaugh.setOnClickListener(v -> changeExpression("laugh", R.drawable.ic_expression_laugh));
        btnExpressionSurprised.setOnClickListener(v -> changeExpression("surprised", R.drawable.ic_expression_surprised));
        btnExpressionWink.setOnClickListener(v -> changeExpression("wink", R.drawable.ic_expression_wink));
        
        // Rotate buttons
        btnRotateLeft.setOnClickListener(v -> {
            animateButtonPress(v);
            rotateAvatar(-15f);
        });
        
        btnRotateRight.setOnClickListener(v -> {
            animateButtonPress(v);
            rotateAvatar(15f);
        });
        
        // Save & Share
        btnSaveLook.setOnClickListener(v -> {
            animateButtonPress(v);
            saveLook();
        });
        
        btnShare.setOnClickListener(v -> {
            animateButtonPress(v);
            shareLook();
        });
        
        btnMyItems.setOnClickListener(v -> {
            animateButtonPress(v);
            showWardrobe();
        });
        
        // Category Tabs
        tabClothes.setOnClickListener(v -> switchCategory("clothes"));
        tabShoes.setOnClickListener(v -> switchCategory("shoes"));
        tabHats.setOnClickListener(v -> switchCategory("hats"));
        tabAccessories.setOnClickListener(v -> switchCategory("accessories"));
        tabHairstyles.setOnClickListener(v -> switchCategory("hairstyles"));
        tabBags.setOnClickListener(v -> switchCategory("bags"));
        
        // Item Cards - Preview on tap
        itemTshirt.setOnClickListener(v -> previewItem(findItemById("tshirt")));
        itemDress.setOnClickListener(v -> previewItem(findItemById("dress")));
        itemSneakers.setOnClickListener(v -> previewItem(findItemById("sneakers")));
        itemCap.setOnClickListener(v -> previewItem(findItemById("cap")));
        itemGlasses.setOnClickListener(v -> previewItem(findItemById("glasses")));
        itemBackpack.setOnClickListener(v -> previewItem(findItemById("backpack")));
        
        // Purchase Buttons
        btnBuyTshirt.setOnClickListener(v -> handlePurchase(findItemById("tshirt"), lockTshirt, btnBuyTshirt));
        btnBuyDress.setOnClickListener(v -> handlePurchase(findItemById("dress"), lockDress, btnBuyDress));
        btnBuySneakers.setOnClickListener(v -> handlePurchase(findItemById("sneakers"), lockSneakers, btnBuySneakers));
        btnBuyCap.setOnClickListener(v -> handlePurchase(findItemById("cap"), lockCap, btnBuyCap));
        btnBuyGlasses.setOnClickListener(v -> handlePurchase(findItemById("glasses"), lockGlasses, btnBuyGlasses));
        btnBuyBackpack.setOnClickListener(v -> handlePurchase(findItemById("backpack"), lockBackpack, btnBuyBackpack));
        
        // Feedback overlay close
        View btnCloseFeedback = findViewById(R.id.btnCloseFeedback);
        if (btnCloseFeedback != null) {
            btnCloseFeedback.setOnClickListener(v -> hideFeedback());
        }
        if (feedbackOverlay != null) {
            feedbackOverlay.setOnClickListener(v -> hideFeedback());
        }
        
        // Navigation
        btnNavHome.setOnClickListener(v -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show());
        btnNavMap.setOnClickListener(v -> Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show());
        btnNavGames.setOnClickListener(v -> Toast.makeText(this, "Games", Toast.LENGTH_SHORT).show());
        btnNavProfile.setOnClickListener(v -> Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show());
    }

    private void updateUI() {
        tvCoins.setText(String.valueOf(coins));
        tvLevel.setText("Level " + currentLevel);
        
        // Update XP bar
        float xpPercentage = (float) currentXP / xpToNextLevel;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int barWidth = (int) (screenWidth * 0.35 * xpPercentage);
        xpProgress.getLayoutParams().width = barWidth;
        xpProgress.requestLayout();
        
        // Update fashion rating stars
        updateFashionRating();
    }

    private void updateFashionRating() {
        fashionRating = calculateFashionRating();
        
        imgStar1.setImageResource(fashionRating >= 1 ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
        imgStar2.setImageResource(fashionRating >= 2 ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
        imgStar3.setImageResource(fashionRating >= 3 ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
        
        // Animate stars
        animateStars();
    }

    private int calculateFashionRating() {
        int equippedCount = equippedItems.size();
        
        if (equippedCount >= 6) {
            return 3; // Perfect style
        } else if (equippedCount >= 4) {
            return 2; // Good style
        } else if (equippedCount >= 2) {
            return 1; // Basic style
        }
        return 0;
    }

    private void previewItem(FashionItem item) {
        if (item == null) return;
        
        // Update the appropriate avatar layer instantly
        updateAvatarLayer(item.category, item.iconRes);
        
        // Highlight animation
        animatePulse(avatarContainer);
    }

    private void handlePurchase(FashionItem item, ImageView lockIcon, TextView buyButton) {
        if (item == null) return;
        
        animateButtonPress(buyButton);
        
        if (item.owned) {
            // Already owned, equip it
            equipItem(item);
            updateAllAvatarLayers();
            updateFashionRating();
            playSparkleEffect();
            showFeedback("Item equipped! ✨", R.drawable.ic_sparkle);
        } else {
            // Try to purchase
            if (coins >= item.price) {
                coins -= item.price;
                item.owned = true;
                ownedItems.add(item);
                
                // Update UI
                lockIcon.setVisibility(View.GONE);
                buyButton.setText("Equip");
                buyButton.setBackgroundResource(R.drawable.bg_equip_button);
                updateUI();
                
                // Equip immediately
                equipItem(item);
                updateAllAvatarLayers();
                updateFashionRating();
                
                // Celebrate
                playSparkleEffect();
                showFeedback("Item purchased! 🎉", R.drawable.ic_fashion_stars);
                gainXP(20);
            } else {
                // Not enough coins
                animateShake(buyButton);
                showFeedback("Not enough coins! 😢", R.drawable.ic_coin);
            }
        }
    }

    private void equipItem(FashionItem item) {
        if (item == null) return;
        
        // Unequip previous item in same category
        FashionItem previousItem = equippedItems.get(item.category);
        if (previousItem != null) {
            previousItem.equipped = false;
        }
        
        // Equip new item
        item.equipped = true;
        equippedItems.put(item.category, item);
    }

    private void updateAvatarLayer(String category, int iconRes) {
        switch (category) {
            case "hairstyles":
                imgHairstyle.setImageResource(iconRes);
                imgHairstyle.setVisibility(View.VISIBLE);
                break;
            case "clothes":
                imgClothes.setImageResource(iconRes);
                imgClothes.setVisibility(View.VISIBLE);
                break;
            case "shoes":
                imgShoes.setImageResource(iconRes);
                imgShoes.setVisibility(View.VISIBLE);
                break;
            case "accessories":
                imgAccessory.setImageResource(iconRes);
                imgAccessory.setVisibility(View.VISIBLE);
                break;
            case "hats":
                imgHat.setImageResource(iconRes);
                imgHat.setVisibility(View.VISIBLE);
                break;
            case "bags":
                imgBag.setImageResource(iconRes);
                imgBag.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateAllAvatarLayers() {
        // Clear all layers
        imgHairstyle.setVisibility(View.GONE);
        imgClothes.setVisibility(View.GONE);
        imgShoes.setVisibility(View.GONE);
        imgAccessory.setVisibility(View.GONE);
        imgHat.setVisibility(View.GONE);
        imgBag.setVisibility(View.GONE);
        
        // Apply equipped items
        for (Map.Entry<String, FashionItem> entry : equippedItems.entrySet()) {
            FashionItem item = entry.getValue();
            updateAvatarLayer(item.category, item.iconRes);
        }
    }

    private void changeExpression(String expression, int expressionRes) {
        currentExpression = expression;
        imgAvatarBase.setImageResource(expressionRes);
        
        // Fade animation
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.3f);
        fadeOut.setDuration(150);
        AlphaAnimation fadeIn = new AlphaAnimation(0.3f, 1.0f);
        fadeIn.setDuration(150);
        fadeIn.setStartOffset(150);
        
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(fadeOut);
        animationSet.addAnimation(fadeIn);
        imgAvatarBase.startAnimation(animationSet);
    }

    private void rotateAvatar(float degrees) {
        avatarRotation += degrees;
        ObjectAnimator rotation = ObjectAnimator.ofFloat(avatarContainer, "rotation", 
                                                         avatarContainer.getRotation(), avatarRotation);
        rotation.setDuration(300);
        rotation.start();
    }

    private void saveLook() {
        try {
            // Capture avatar as bitmap
            Bitmap bitmap = Bitmap.createBitmap(avatarContainer.getWidth(), 
                                                avatarContainer.getHeight(), 
                                                Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            avatarContainer.draw(canvas);
            
            showFeedback("Look saved! 💾", R.drawable.ic_save);
            gainXP(15);
        } catch (Exception e) {
            Toast.makeText(this, "Error saving look", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareLook() {
        // Placeholder for share functionality
        showFeedback("Look shared! 🌟", R.drawable.ic_share);
        gainXP(10);
    }

    private void showWardrobe() {
        // Placeholder - would open wardrobe panel showing owned items
        Toast.makeText(this, "Wardrobe: " + ownedItems.size() + " items owned", Toast.LENGTH_SHORT).show();
    }

    private void switchCategory(String category) {
        currentCategory = category;
        // In full implementation, would filter items by category
        Toast.makeText(this, "Viewing: " + category, Toast.LENGTH_SHORT).show();
    }

    private void playSparkleEffect() {
        imgSparkle.setVisibility(View.VISIBLE);
        imgSparkle.setAlpha(0f);
        
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imgSparkle, "alpha", 0f, 1f);
        fadeIn.setDuration(300);
        
        ObjectAnimator rotate = ObjectAnimator.ofFloat(imgSparkle, "rotation", 0f, 360f);
        rotate.setDuration(600);
        
        ObjectAnimator scale = ObjectAnimator.ofFloat(imgSparkle, "scaleX", 0.5f, 1.5f);
        scale.setRepeatMode(ObjectAnimator.REVERSE);
        scale.setRepeatCount(1);
        scale.setDuration(300);
        
        fadeIn.start();
        rotate.start();
        scale.start();
        
        new Handler().postDelayed(() -> {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(imgSparkle, "alpha", 1f, 0f);
            fadeOut.setDuration(300);
            fadeOut.start();
            new Handler().postDelayed(() -> imgSparkle.setVisibility(View.GONE), 300);
        }, 600);
    }

    private void gainXP(int amount) {
        currentXP += amount;
        if (currentXP >= xpToNextLevel) {
            levelUp();
        }
        updateUI();
    }

    private void levelUp() {
        currentLevel++;
        currentXP = 0;
        coins += 100;
        showFeedback("Level Up! 🎉\nYou're now Level " + currentLevel, R.drawable.ic_fashion_stars);
    }

    private void showFeedback(String message, int iconRes) {
        tvFeedbackMessage.setText(message);
        ImageView feedbackIcon = findViewById(R.id.imgFeedback);
        feedbackIcon.setImageResource(iconRes);
        
        feedbackOverlay.setVisibility(View.VISIBLE);
        feedbackOverlay.setAlpha(0f);
        ObjectAnimator.ofFloat(feedbackOverlay, "alpha", 0f, 1f).setDuration(200).start();
    }

    private void hideFeedback() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(feedbackOverlay, "alpha", 1f, 0f);
        fadeOut.setDuration(200);
        fadeOut.start();
        new Handler().postDelayed(() -> feedbackOverlay.setVisibility(View.GONE), 200);
    }

    private FashionItem findItemById(String id) {
        for (FashionItem item : allItems) {
            if (item.id.equals(id)) {
                return item;
            }
        }
        return null;
    }

    // Animation Methods
    private void animateButtonPress(View view) {
        ScaleAnimation scaleDown = new ScaleAnimation(1.0f, 0.95f, 1.0f, 0.95f, 
                                                      Animation.RELATIVE_TO_SELF, 0.5f, 
                                                      Animation.RELATIVE_TO_SELF, 0.5f);
        scaleDown.setDuration(100);
        scaleDown.setRepeatCount(1);
        scaleDown.setRepeatMode(Animation.REVERSE);
        view.startAnimation(scaleDown);
    }

    private void animatePulse(View view) {
        ScaleAnimation pulse = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, 
                                                  Animation.RELATIVE_TO_SELF, 0.5f, 
                                                  Animation.RELATIVE_TO_SELF, 0.5f);
        pulse.setDuration(200);
        pulse.setRepeatCount(1);
        pulse.setRepeatMode(Animation.REVERSE);
        view.startAnimation(pulse);
    }

    private void animateShake(View view) {
        TranslateAnimation shake = new TranslateAnimation(-10, 10, 0, 0);
        shake.setDuration(50);
        shake.setRepeatCount(5);
        shake.setRepeatMode(Animation.REVERSE);
        view.startAnimation(shake);
    }

    private void animateStars() {
        animateFloatUp(imgStar1, 0);
        animateFloatUp(imgStar2, 100);
        animateFloatUp(imgStar3, 200);
    }

    private void animateFloatUp(View view, long delay) {
        TranslateAnimation float_up = new TranslateAnimation(0, 0, 0, -20);
        float_up.setDuration(400);
        float_up.setRepeatCount(1);
        float_up.setRepeatMode(Animation.REVERSE);
        float_up.setStartOffset(delay);
        
        AlphaAnimation fade = new AlphaAnimation(0.7f, 1.0f);
        fade.setDuration(400);
        fade.setRepeatCount(1);
        fade.setRepeatMode(Animation.REVERSE);
        fade.setStartOffset(delay);
        
        AnimationSet animSet = new AnimationSet(false);
        animSet.addAnimation(float_up);
        animSet.addAnimation(fade);
        view.startAnimation(animSet);
    }
}
