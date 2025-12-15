package com.example.englishapp;


import com.shop.englishapp.R;
import android.animation.ObjectAnimator;
import android.content.ClipData;
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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordWorkshopActivity extends AppCompatActivity {

    private FrameLayout slot1, slot2, slot3;
    private TextView tvSlot1, tvSlot2, tvSlot3;
    private TextView letterC, letterA, letterT, letterS, letterO, letterN;
    private LinearLayout layoutSuccess;
    private Button btnNextWord;
    private ImageButton btnBack;
    private LinearLayout btnHintTool, btnShuffleTool, btnSoundTool;
    private ImageView star1, star2, star3;
    
    private Handler handler = new Handler();
    private String currentWord = "CAT";
    private int currentSlotIndex = 0;
    private List<TextView> letterBlocks = new ArrayList<>();
    private List<TextView> slotTexts = new ArrayList<>();
    private List<FrameLayout> slots = new ArrayList<>();
    private Map<Integer, String> slotValues = new HashMap<>();
    private int starsEarned = 0;
    private int hintsUsed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_workshop);

        initializeViews();
        setupDragAndDrop();
        setupButtons();
        shuffleLetters();
    }

    private void initializeViews() {
        // Slots
        slot1 = findViewById(R.id.slot1);
        slot2 = findViewById(R.id.slot2);
        slot3 = findViewById(R.id.slot3);
        tvSlot1 = findViewById(R.id.tvSlot1);
        tvSlot2 = findViewById(R.id.tvSlot2);
        tvSlot3 = findViewById(R.id.tvSlot3);
        
        slots.add(slot1);
        slots.add(slot2);
        slots.add(slot3);
        
        slotTexts.add(tvSlot1);
        slotTexts.add(tvSlot2);
        slotTexts.add(tvSlot3);
        
        // Letter blocks
        letterC = findViewById(R.id.letterC);
        letterA = findViewById(R.id.letterA);
        letterT = findViewById(R.id.letterT);
        letterS = findViewById(R.id.letterS);
        letterO = findViewById(R.id.letterO);
        letterN = findViewById(R.id.letterN);
        
        letterBlocks.add(letterC);
        letterBlocks.add(letterA);
        letterBlocks.add(letterT);
        letterBlocks.add(letterS);
        letterBlocks.add(letterO);
        letterBlocks.add(letterN);
        
        // Other views
        layoutSuccess = findViewById(R.id.layoutSuccess);
        btnNextWord = findViewById(R.id.btnNextWord);
        btnBack = findViewById(R.id.btnBack);
        btnHintTool = findViewById(R.id.btnHintTool);
        btnShuffleTool = findViewById(R.id.btnShuffleTool);
        btnSoundTool = findViewById(R.id.btnSoundTool);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
    }

    private void setupDragAndDrop() {
        // Set up drag listeners for letter blocks
        for (TextView letter : letterBlocks) {
            letter.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("letter", ((TextView) v).getText().toString());
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDragAndDrop(data, shadowBuilder, v, 0);
                    v.setAlpha(0.5f);
                    return true;
                }
                return false;
            });
        }
        
        // Set up drop listeners for slots
        for (int i = 0; i < slots.size(); i++) {
            final int slotIndex = i;
            FrameLayout slot = slots.get(i);
            TextView slotText = slotTexts.get(i);
            
            slot.setOnDragListener((v, event) -> {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                        
                    case DragEvent.ACTION_DRAG_ENTERED:
                        slot.setBackgroundResource(R.drawable.bg_letter_slot_active);
                        animateGlow(slot);
                        return true;
                        
                    case DragEvent.ACTION_DRAG_EXITED:
                        slot.setBackgroundResource(R.drawable.bg_letter_slot);
                        return true;
                        
                    case DragEvent.ACTION_DROP:
                        ClipData clipData = event.getClipData();
                        String letter = clipData.getItemAt(0).getText().toString();
                        View draggedView = (View) event.getLocalState();
                        
                        // Place letter in slot
                        slotText.setText(letter);
                        slotValues.put(slotIndex, letter);
                        draggedView.setVisibility(View.INVISIBLE);
                        
                        // Check if letter is correct
                        char correctLetter = currentWord.charAt(slotIndex);
                        if (letter.equals(String.valueOf(correctLetter))) {
                            // Correct letter
                            animateBounce(slot);
                            slot.setBackgroundResource(R.drawable.bg_letter_slot_active);
                            checkWordCompletion();
                        } else {
                            // Wrong letter
                            animateShake(slot);
                            handler.postDelayed(() -> {
                                slotText.setText("");
                                slotValues.remove(slotIndex);
                                draggedView.setVisibility(View.VISIBLE);
                                draggedView.setAlpha(1f);
                                slot.setBackgroundResource(R.drawable.bg_letter_slot);
                            }, 500);
                        }
                        return true;
                        
                    case DragEvent.ACTION_DRAG_ENDED:
                        View view = (View) event.getLocalState();
                        if (event.getResult()) {
                            // Drop was successful
                        } else {
                            view.setAlpha(1f);
                        }
                        slot.setBackgroundResource(R.drawable.bg_letter_slot);
                        return true;
                        
                    default:
                        return false;
                }
            });
        }
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());
        
        btnHintTool.setOnClickListener(v -> {
            animateToolButton(btnHintTool);
            showHint();
        });
        
        btnShuffleTool.setOnClickListener(v -> {
            animateToolButton(btnShuffleTool);
            shuffleLetters();
        });
        
        btnSoundTool.setOnClickListener(v -> {
            animateToolButton(btnSoundTool);
            playPronunciation();
        });
        
        btnNextWord.setOnClickListener(v -> {
            loadNextWord();
        });
    }

    private void shuffleLetters() {
        Collections.shuffle(letterBlocks);
        // Animate shuffle
        for (TextView letter : letterBlocks) {
            ObjectAnimator rotate = ObjectAnimator.ofFloat(letter, "rotation", 0f, 360f);
            rotate.setDuration(500);
            rotate.start();
        }
    }

    private void showHint() {
        hintsUsed++;
        
        // Find first empty slot
        for (int i = 0; i < slots.size(); i++) {
            if (!slotValues.containsKey(i)) {
                char hintLetter = currentWord.charAt(i);
                
                // Flash the correct letter block
                for (TextView letter : letterBlocks) {
                    if (letter.getVisibility() == View.VISIBLE && 
                        letter.getText().toString().equals(String.valueOf(hintLetter))) {
                        animatePulse(letter);
                        break;
                    }
                }
                break;
            }
        }
        
        // Deduct a star if too many hints used
        if (hintsUsed > 1 && starsEarned > 0) {
            starsEarned--;
            updateStars();
        }
    }

    private void playPronunciation() {
        // Simulate pronunciation
        tvSlot1.setText("");
        tvSlot2.setText("");
        tvSlot3.setText("");
        
        // Show letters one by one
        handler.postDelayed(() -> {
            tvSlot1.setText(String.valueOf(currentWord.charAt(0)));
            animateBounce(slot1);
        }, 300);
        
        handler.postDelayed(() -> {
            tvSlot2.setText(String.valueOf(currentWord.charAt(1)));
            animateBounce(slot2);
        }, 600);
        
        handler.postDelayed(() -> {
            tvSlot3.setText(String.valueOf(currentWord.charAt(2)));
            animateBounce(slot3);
        }, 900);
        
        handler.postDelayed(() -> {
            tvSlot1.setText("");
            tvSlot2.setText("");
            tvSlot3.setText("");
        }, 2000);
    }

    private void checkWordCompletion() {
        if (slotValues.size() == currentWord.length()) {
            StringBuilder word = new StringBuilder();
            for (int i = 0; i < currentWord.length(); i++) {
                word.append(slotValues.get(i));
            }
            
            if (word.toString().equals(currentWord)) {
                // Word is correct!
                handler.postDelayed(() -> {
                    showSuccessAnimation();
                }, 500);
            }
        }
    }

    private void showSuccessAnimation() {
        // Calculate stars
        starsEarned = 3;
        if (hintsUsed > 0) starsEarned = 2;
        if (hintsUsed > 2) starsEarned = 1;
        
        updateStars();
        
        // Show success message
        layoutSuccess.setVisibility(View.VISIBLE);
        layoutSuccess.setAlpha(0f);
        layoutSuccess.setScaleX(0.5f);
        layoutSuccess.setScaleY(0.5f);
        
        layoutSuccess.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .start();
        
        // Animate confetti
        animateConfetti();
    }

    private void updateStars() {
        star1.setImageResource(starsEarned >= 1 ? R.drawable.ic_star : R.drawable.ic_star_empty);
        star2.setImageResource(starsEarned >= 2 ? R.drawable.ic_star : R.drawable.ic_star_empty);
        star3.setImageResource(starsEarned >= 3 ? R.drawable.ic_star : R.drawable.ic_star_empty);
        
        if (starsEarned >= 1) animateBounce(star1);
        if (starsEarned >= 2) {
            handler.postDelayed(() -> animateBounce(star2), 200);
        }
        if (starsEarned >= 3) {
            handler.postDelayed(() -> animateBounce(star3), 400);
        }
    }

    private void loadNextWord() {
        // Reset for next word
        layoutSuccess.setVisibility(View.GONE);
        slotValues.clear();
        hintsUsed = 0;
        starsEarned = 0;
        
        // Clear slots
        for (TextView slot : slotTexts) {
            slot.setText("");
        }
        
        for (FrameLayout slot : slots) {
            slot.setBackgroundResource(R.drawable.bg_letter_slot);
        }
        
        // Reset letter blocks
        for (TextView letter : letterBlocks) {
            letter.setVisibility(View.VISIBLE);
            letter.setAlpha(1f);
        }
        
        // Update stars display
        updateStars();
        
        // Shuffle letters for next round
        shuffleLetters();
        
        // Could load a different word here
        // currentWord = "DOG"; // Example
    }

    private void animateBounce(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f);
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        scaleX.start();
        scaleY.start();
    }

    private void animateShake(View view) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(view, "translationX", 0f, -25f, 25f, -25f, 25f, 0f);
        shake.setDuration(400);
        shake.start();
    }

    private void animateGlow(View view) {
        ObjectAnimator pulse = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f, 1f);
        ObjectAnimator pulseY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.05f, 1f);
        pulse.setDuration(500);
        pulseY.setDuration(500);
        pulse.start();
        pulseY.start();
    }

    private void animatePulse(View view) {
        ObjectAnimator pulse = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.3f, 1f);
        ObjectAnimator pulseY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.3f, 1f);
        pulse.setDuration(600);
        pulseY.setDuration(600);
        pulse.setRepeatCount(2);
        pulseY.setRepeatCount(2);
        pulse.start();
        pulseY.start();
    }

    private void animateToolButton(View button) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.9f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.9f, 1f);
        scaleX.setDuration(200);
        scaleY.setDuration(200);
        scaleX.start();
        scaleY.start();
    }

    private void animateConfetti() {
        // Simulate confetti explosion
        for (FrameLayout slot : slots) {
            ObjectAnimator rotate = ObjectAnimator.ofFloat(slot, "rotation", 0f, 360f);
            rotate.setDuration(800);
            rotate.start();
        }
        
        handler.postDelayed(() -> {
            for (FrameLayout slot : slots) {
                slot.setRotation(0f);
            }
        }, 800);
    }
}
