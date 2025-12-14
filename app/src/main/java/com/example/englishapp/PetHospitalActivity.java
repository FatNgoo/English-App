package com.example.englishapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PetHospitalActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack;
    private FrameLayout btnPlayInstruction;
    private TextView txtInstruction;
    private ImageView imgPet;
    private TextView txtCondition;
    private ProgressBar moodProgressBar;
    private ImageView imgMoodIcon;
    private ImageView imgSparkles;
    private FrameLayout completionOverlay;
    private ImageView imgHappyPet;
    private Button btnNextPatient;
    private BottomNavigationView bottomNavigation;

    // Body Part Highlight Zones
    private View highlightHead;
    private View highlightPaw;
    private View highlightEar;
    private View highlightTummy;

    // Tool Buttons
    private FrameLayout toolThermometer;
    private FrameLayout toolBandage;
    private FrameLayout toolMedicine;
    private FrameLayout toolEyeDrops;
    private FrameLayout toolCotton;
    private FrameLayout toolIcePack;
    private FrameLayout toolStethoscope;
    private FrameLayout toolSyringe;

    // Checklist Items
    private LinearLayout checklistItem1, checklistItem2, checklistItem3, checklistItem4;
    private View healthCheckbox1, healthCheckbox2, healthCheckbox3, healthCheckbox4;
    private TextView txtHealthCheck1, txtHealthCheck2, txtHealthCheck3, txtHealthCheck4;

    // Game State
    private int currentPetType = 0; // 0=dog, 1=cat, 2=rabbit
    private int currentTreatmentStep = 0;
    private int petMoodLevel = 30;
    private boolean isInstructionPlaying = false;
    private List<TreatmentStep> treatmentSteps;
    private Random random = new Random();
    private Handler handler = new Handler();

    // Treatment Step Class
    private static class TreatmentStep {
        String instruction;
        String toolType;
        String targetBodyPart;
        int checklistIndex;

        TreatmentStep(String instruction, String toolType, String targetBodyPart, int checklistIndex) {
            this.instruction = instruction;
            this.toolType = toolType;
            this.targetBodyPart = targetBodyPart;
            this.checklistIndex = checklistIndex;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_hospital);

        initializeViews();
        setupDragListeners();
        setupClickListeners();
        loadNewPatient();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Instruction Panel
        btnPlayInstruction = findViewById(R.id.btnPlayInstruction);
        txtInstruction = findViewById(R.id.txtInstruction);

        // Pet Display
        imgPet = findViewById(R.id.imgPet);
        txtCondition = findViewById(R.id.txtCondition);
        imgSparkles = findViewById(R.id.imgSparkles);

        // Body Part Highlights
        highlightHead = findViewById(R.id.highlightHead);
        highlightPaw = findViewById(R.id.highlightPaw);
        highlightEar = findViewById(R.id.highlightEar);
        highlightTummy = findViewById(R.id.highlightTummy);

        // Mood Meter
        moodProgressBar = findViewById(R.id.moodProgressBar);
        imgMoodIcon = findViewById(R.id.imgMoodIcon);

        // Tools
        toolThermometer = findViewById(R.id.toolThermometer);
        toolBandage = findViewById(R.id.toolBandage);
        toolMedicine = findViewById(R.id.toolMedicine);
        toolEyeDrops = findViewById(R.id.toolEyeDrops);
        toolCotton = findViewById(R.id.toolCotton);
        toolIcePack = findViewById(R.id.toolIcePack);
        toolStethoscope = findViewById(R.id.toolStethoscope);
        toolSyringe = findViewById(R.id.toolSyringe);

        // Checklist
        checklistItem1 = findViewById(R.id.checklistItem1);
        checklistItem2 = findViewById(R.id.checklistItem2);
        checklistItem3 = findViewById(R.id.checklistItem3);
        checklistItem4 = findViewById(R.id.checklistItem4);
        healthCheckbox1 = findViewById(R.id.healthCheckbox1);
        healthCheckbox2 = findViewById(R.id.healthCheckbox2);
        healthCheckbox3 = findViewById(R.id.healthCheckbox3);
        healthCheckbox4 = findViewById(R.id.healthCheckbox4);
        txtHealthCheck1 = findViewById(R.id.txtHealthCheck1);
        txtHealthCheck2 = findViewById(R.id.txtHealthCheck2);
        txtHealthCheck3 = findViewById(R.id.txtHealthCheck3);
        txtHealthCheck4 = findViewById(R.id.txtHealthCheck4);

        // Completion Overlay
        completionOverlay = findViewById(R.id.completionOverlay);
        imgHappyPet = findViewById(R.id.imgHappyPet);
        btnNextPatient = findViewById(R.id.btnNextPatient);

        // Bottom Navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPlayInstruction.setOnClickListener(v -> playInstruction());

        imgPet.setOnClickListener(v -> playPetSound());

        btnNextPatient.setOnClickListener(v -> {
            hideCompletionOverlay();
            loadNewPatient();
        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            // Handle navigation
            return true;
        });
    }

    private void setupDragListeners() {
        List<FrameLayout> tools = Arrays.asList(
                toolThermometer, toolBandage, toolMedicine, toolEyeDrops,
                toolCotton, toolIcePack, toolStethoscope, toolSyringe
        );

        for (FrameLayout tool : tools) {
            tool.setOnLongClickListener(v -> {
                String toolType = getToolType(v.getId());
                ClipData data = ClipData.newPlainText("toolType", toolType);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                
                // Animate tool lift
                animateToolLift(v, true);
                
                return true;
            });

            tool.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    animateToolPress(v);
                }
                return false;
            });
        }

        // Setup drop zones on pet container
        View petContainer = findViewById(R.id.petContainer);
        petContainer.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    highlightCorrectBodyPart();
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    hideAllHighlights();
                    return true;

                case DragEvent.ACTION_DROP:
                    ClipData clipData = event.getClipData();
                    String droppedToolType = clipData.getItemAt(0).getText().toString();
                    handleToolDrop(droppedToolType, event.getX(), event.getY());
                    hideAllHighlights();
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    View draggedView = (View) event.getLocalState();
                    animateToolLift(draggedView, false);
                    return true;
            }
            return false;
        });
    }

    private String getToolType(int viewId) {
        if (viewId == R.id.toolThermometer) return "thermometer";
        if (viewId == R.id.toolBandage) return "bandage";
        if (viewId == R.id.toolMedicine) return "medicine";
        if (viewId == R.id.toolEyeDrops) return "eye_drops";
        if (viewId == R.id.toolCotton) return "cotton";
        if (viewId == R.id.toolIcePack) return "ice_pack";
        if (viewId == R.id.toolStethoscope) return "stethoscope";
        if (viewId == R.id.toolSyringe) return "syringe";
        return "";
    }

    private void loadNewPatient() {
        // Reset state
        currentTreatmentStep = 0;
        petMoodLevel = 30;
        moodProgressBar.setProgress(petMoodLevel);
        
        // Reset checklist
        resetChecklist();

        // Random pet type
        currentPetType = random.nextInt(3);
        
        switch (currentPetType) {
            case 0: // Dog
                imgPet.setImageResource(R.drawable.ic_dog_sick);
                txtCondition.setText(R.string.dog_fever);
                setupDogTreatment();
                break;
            case 1: // Cat
                imgPet.setImageResource(R.drawable.ic_cat_sick);
                txtCondition.setText(R.string.cat_eye_problem);
                setupCatTreatment();
                break;
            case 2: // Rabbit
                imgPet.setImageResource(R.drawable.ic_rabbit_sick);
                txtCondition.setText(R.string.rabbit_ear_infection);
                setupRabbitTreatment();
                break;
        }

        // Animate pet entrance
        animatePetEntrance();
        
        // Start blinking teary eyes
        startBlinkingAnimation();
        
        // Show first instruction
        txtInstruction.setText(R.string.tap_play_to_start);
    }

    private void setupDogTreatment() {
        treatmentSteps = new ArrayList<>();
        treatmentSteps.add(new TreatmentStep(
                getString(R.string.instruction_take_temperature),
                "thermometer", "head", 0));
        treatmentSteps.add(new TreatmentStep(
                getString(R.string.instruction_clean_paw),
                "cotton", "paw", 1));
        treatmentSteps.add(new TreatmentStep(
                getString(R.string.instruction_give_medicine),
                "medicine", "tummy", 2));
        treatmentSteps.add(new TreatmentStep(
                getString(R.string.instruction_bandage_paw),
                "bandage", "paw", 3));
    }

    private void setupCatTreatment() {
        treatmentSteps = new ArrayList<>();
        treatmentSteps.add(new TreatmentStep(
                getString(R.string.instruction_check_eyes),
                "eye_drops", "head", 0));
        treatmentSteps.add(new TreatmentStep(
                getString(R.string.instruction_listen_heart),
                "stethoscope", "tummy", 1));
        treatmentSteps.add(new TreatmentStep(
                getString(R.string.instruction_give_vitamins),
                "syringe", "tummy", 2));
        treatmentSteps.add(new TreatmentStep(
                getString(R.string.instruction_apply_ice),
                "ice_pack", "head", 3));
    }

    private void setupRabbitTreatment() {
        treatmentSteps = new ArrayList<>();
        treatmentSteps.add(new TreatmentStep(
                getString(R.string.instruction_clean_ear),
                "cotton", "ear", 0));
        treatmentSteps.add(new TreatmentStep(
                getString(R.string.instruction_take_temperature),
                "thermometer", "head", 1));
        treatmentSteps.add(new TreatmentStep(
                getString(R.string.instruction_give_medicine),
                "medicine", "tummy", 2));
        treatmentSteps.add(new TreatmentStep(
                getString(R.string.instruction_bandage_ear),
                "bandage", "ear", 3));
    }

    private void playInstruction() {
        if (isInstructionPlaying || currentTreatmentStep >= treatmentSteps.size()) {
            return;
        }

        isInstructionPlaying = true;
        TreatmentStep step = treatmentSteps.get(currentTreatmentStep);
        
        // Animate play button
        animatePlayButton();
        
        // Show instruction text
        txtInstruction.setText(step.instruction);
        
        // Simulate audio playback (3 seconds)
        handler.postDelayed(() -> {
            isInstructionPlaying = false;
        }, 3000);
    }

    private void handleToolDrop(String toolType, float x, float y) {
        if (currentTreatmentStep >= treatmentSteps.size()) {
            return;
        }

        TreatmentStep currentStep = treatmentSteps.get(currentTreatmentStep);
        String targetBodyPart = getBodyPartFromPosition(x, y);

        // Check if correct tool and correct body part
        if (toolType.equals(currentStep.toolType) && targetBodyPart.equals(currentStep.targetBodyPart)) {
            handleCorrectTreatment(currentStep);
        } else {
            handleWrongTreatment(targetBodyPart);
        }
    }

    private String getBodyPartFromPosition(float x, float y) {
        View petContainer = findViewById(R.id.petContainer);
        float containerWidth = petContainer.getWidth();
        float containerHeight = petContainer.getHeight();
        
        float relativeX = x / containerWidth;
        float relativeY = y / containerHeight;

        // Head area (top center)
        if (relativeY < 0.4f && relativeX > 0.3f && relativeX < 0.7f) {
            return "head";
        }
        // Ear area (top left)
        if (relativeY < 0.3f && relativeX < 0.3f) {
            return "ear";
        }
        // Tummy area (center)
        if (relativeY > 0.4f && relativeY < 0.7f) {
            return "tummy";
        }
        // Paw area (bottom left)
        if (relativeY > 0.7f && relativeX < 0.4f) {
            return "paw";
        }

        return "unknown";
    }

    private void handleCorrectTreatment(TreatmentStep step) {
        // Update checklist
        updateChecklistItem(step.checklistIndex, true);
        
        // Increase mood
        petMoodLevel += 25;
        if (petMoodLevel > 100) petMoodLevel = 100;
        animateMoodIncrease();
        
        // Show sparkles
        showSparkles();
        
        // Animate pet happy reaction
        animatePetHappy();
        
        // Move to next step
        currentTreatmentStep++;
        
        // Check if treatment complete
        if (currentTreatmentStep >= treatmentSteps.size()) {
            handler.postDelayed(this::showCompletionOverlay, 1500);
        } else {
            txtInstruction.setText(R.string.tap_play_for_next);
        }
    }

    private void handleWrongTreatment(String bodyPart) {
        // Show red highlight on wrong area
        showWrongHighlight(bodyPart);
        
        // Shake pet
        animatePetShake();
        
        // Decrease mood slightly
        petMoodLevel -= 5;
        if (petMoodLevel < 0) petMoodLevel = 0;
        moodProgressBar.setProgress(petMoodLevel);
    }

    private void highlightCorrectBodyPart() {
        if (currentTreatmentStep >= treatmentSteps.size()) {
            return;
        }

        hideAllHighlights();
        TreatmentStep step = treatmentSteps.get(currentTreatmentStep);
        
        switch (step.targetBodyPart) {
            case "head":
                highlightHead.setVisibility(View.VISIBLE);
                animateGlow(highlightHead);
                break;
            case "paw":
                highlightPaw.setVisibility(View.VISIBLE);
                animateGlow(highlightPaw);
                break;
            case "ear":
                highlightEar.setVisibility(View.VISIBLE);
                animateGlow(highlightEar);
                break;
            case "tummy":
                highlightTummy.setVisibility(View.VISIBLE);
                animateGlow(highlightTummy);
                break;
        }
    }

    private void hideAllHighlights() {
        highlightHead.setVisibility(View.INVISIBLE);
        highlightPaw.setVisibility(View.INVISIBLE);
        highlightEar.setVisibility(View.INVISIBLE);
        highlightTummy.setVisibility(View.INVISIBLE);
    }

    private void showWrongHighlight(String bodyPart) {
        View targetView = null;
        
        switch (bodyPart) {
            case "head":
                targetView = highlightHead;
                break;
            case "paw":
                targetView = highlightPaw;
                break;
            case "ear":
                targetView = highlightEar;
                break;
            case "tummy":
                targetView = highlightTummy;
                break;
        }

        if (targetView != null) {
            targetView.setBackgroundResource(R.drawable.bg_body_part_wrong);
            targetView.setVisibility(View.VISIBLE);
            
            handler.postDelayed(() -> {
                targetView.setVisibility(View.INVISIBLE);
                targetView.setBackgroundResource(R.drawable.bg_body_part_highlight);
            }, 500);
        }
    }

    private void updateChecklistItem(int index, boolean completed) {
        View checkbox;
        TextView textView;
        
        switch (index) {
            case 0:
                checkbox = healthCheckbox1;
                textView = txtHealthCheck1;
                break;
            case 1:
                checkbox = healthCheckbox2;
                textView = txtHealthCheck2;
                break;
            case 2:
                checkbox = healthCheckbox3;
                textView = txtHealthCheck3;
                break;
            case 3:
                checkbox = healthCheckbox4;
                textView = txtHealthCheck4;
                break;
            default:
                return;
        }

        if (completed) {
            checkbox.setBackgroundResource(R.drawable.ic_check_green);
            textView.setTextColor(ContextCompat.getColor(this, R.color.green));
            animateBounce(checkbox);
        }
    }

    private void resetChecklist() {
        healthCheckbox1.setBackgroundResource(R.drawable.bg_checkbox_empty);
        healthCheckbox2.setBackgroundResource(R.drawable.bg_checkbox_empty);
        healthCheckbox3.setBackgroundResource(R.drawable.bg_checkbox_empty);
        healthCheckbox4.setBackgroundResource(R.drawable.bg_checkbox_empty);
        
        txtHealthCheck1.setTextColor(ContextCompat.getColor(this, R.color.gray));
        txtHealthCheck2.setTextColor(ContextCompat.getColor(this, R.color.gray));
        txtHealthCheck3.setTextColor(ContextCompat.getColor(this, R.color.gray));
        txtHealthCheck4.setTextColor(ContextCompat.getColor(this, R.color.gray));
    }

    private void showSparkles() {
        imgSparkles.setVisibility(View.VISIBLE);
        imgSparkles.setAlpha(1f);
        imgSparkles.setScaleX(0.5f);
        imgSparkles.setScaleY(0.5f);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imgSparkles, "scaleX", 0.5f, 1.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imgSparkles, "scaleY", 0.5f, 1.5f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imgSparkles, "alpha", 1f, 0f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(imgSparkles, "rotation", 0f, 360f);

        AnimatorSet sparkleSet = new AnimatorSet();
        sparkleSet.playTogether(scaleX, scaleY, alpha, rotation);
        sparkleSet.setDuration(800);
        sparkleSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imgSparkles.setVisibility(View.INVISIBLE);
            }
        });
        sparkleSet.start();
    }

    private void showCompletionOverlay() {
        // Change pet to happy
        switch (currentPetType) {
            case 0:
                imgHappyPet.setImageResource(R.drawable.ic_dog_sick);
                break;
            case 1:
                imgHappyPet.setImageResource(R.drawable.ic_cat_sick);
                break;
            case 2:
                imgHappyPet.setImageResource(R.drawable.ic_rabbit_sick);
                break;
        }

        completionOverlay.setVisibility(View.VISIBLE);
        completionOverlay.setAlpha(0f);
        
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(completionOverlay, "alpha", 0f, 1f);
        fadeIn.setDuration(400);
        fadeIn.start();

        // Animate confetti rotation
        animateConfetti();
    }

    private void hideCompletionOverlay() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(completionOverlay, "alpha", 1f, 0f);
        fadeOut.setDuration(300);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                completionOverlay.setVisibility(View.INVISIBLE);
            }
        });
        fadeOut.start();
    }

    // Animation Methods
    private void animateToolPress(View tool) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tool, "scaleX", 1f, 0.95f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tool, "scaleY", 1f, 0.95f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(150);
        set.start();
    }

    private void animateToolLift(View tool, boolean lift) {
        float targetElevation = lift ? 16f : 0f;
        float targetScale = lift ? 1.1f : 1f;
        
        ObjectAnimator elevation = ObjectAnimator.ofFloat(tool, "elevation", tool.getElevation(), targetElevation);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tool, "scaleX", tool.getScaleX(), targetScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tool, "scaleY", tool.getScaleY(), targetScale);
        
        AnimatorSet set = new AnimatorSet();
        set.playTogether(elevation, scaleX, scaleY);
        set.setDuration(200);
        set.start();
    }

    private void animatePlayButton() {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(btnPlayInstruction, "rotation", 0f, 360f);
        rotation.setDuration(600);
        rotation.start();
    }

    private void animateGlow(View view) {
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(view, "alpha", 0.3f, 0.8f);
        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(view, "alpha", 0.8f, 0.3f);
        
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(alpha1, alpha2);
        set.setDuration(800);
        set.start();
    }

    private void animateBounce(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.3f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.3f, 1f);
        
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(400);
        set.start();
    }

    private void animatePetEntrance() {
        imgPet.setAlpha(0f);
        imgPet.setScaleX(0.5f);
        imgPet.setScaleY(0.5f);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(imgPet, "alpha", 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imgPet, "scaleX", 0.5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imgPet, "scaleY", 0.5f, 1f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(alpha, scaleX, scaleY);
        set.setDuration(600);
        set.start();
    }

    private void animatePetHappy() {
        ObjectAnimator rotation1 = ObjectAnimator.ofFloat(imgPet, "rotation", 0f, -10f);
        ObjectAnimator rotation2 = ObjectAnimator.ofFloat(imgPet, "rotation", -10f, 10f);
        ObjectAnimator rotation3 = ObjectAnimator.ofFloat(imgPet, "rotation", 10f, 0f);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(rotation1, rotation2, rotation3);
        set.setDuration(600);
        set.start();
    }

    private void animatePetShake() {
        ObjectAnimator shake1 = ObjectAnimator.ofFloat(imgPet, "translationX", 0f, -15f);
        ObjectAnimator shake2 = ObjectAnimator.ofFloat(imgPet, "translationX", -15f, 15f);
        ObjectAnimator shake3 = ObjectAnimator.ofFloat(imgPet, "translationX", 15f, -15f);
        ObjectAnimator shake4 = ObjectAnimator.ofFloat(imgPet, "translationX", -15f, 0f);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(shake1, shake2, shake3, shake4);
        set.setDuration(400);
        set.start();
    }

    private void startBlinkingAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (petMoodLevel < 100) {
                    ObjectAnimator blink = ObjectAnimator.ofFloat(imgPet, "alpha", 1f, 0.5f, 1f);
                    blink.setDuration(300);
                    blink.start();
                    handler.postDelayed(this, 3000);
                }
            }
        }, 2000);
    }

    private void animateMoodIncrease() {
        ObjectAnimator progress = ObjectAnimator.ofInt(moodProgressBar, "progress", 
                moodProgressBar.getProgress(), petMoodLevel);
        progress.setDuration(600);
        progress.start();

        // Pulse mood icon
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imgMoodIcon, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imgMoodIcon, "scaleY", 1f, 1.2f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(400);
        set.start();
    }

    private void animateConfetti() {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(imgHappyPet, "rotation", 0f, 360f);
        rotation.setDuration(2000);
        rotation.setRepeatCount(ObjectAnimator.INFINITE);
        rotation.start();
    }

    private void playPetSound() {
        // Simulate pet whimper sound with animation
        animatePetShake();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
