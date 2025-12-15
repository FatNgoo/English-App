package com.example.englishapp;


import com.shop.englishapp.R;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RhythmStarActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack, btnPlayPause, btnSlowMode, btnRepeatSound, btnAutoHint;
    private LinearLayout btnPlayAgain, btnNextSong, comboContainer, vocabBubble;
    private TextView txtSongTitle, txtDifficulty, txtScore, txtCombo;
    private TextView txtPerfect, txtGood, txtMiss, txtVocabWord;
    private TextView txtFinalScore, txtAccuracy, txtWordsCollected;
    private TextView check1, check2, check3, check4, check5;
    private ImageView imgEqualizer, imgPerfectBurst, imgMissCloud, imgDancingStars;
    private ImageView star1, star2, star3, resultStar1, resultStar2, resultStar3;
    private View starMeterFill, hitZone, track1, track2, track3, track4;
    private FrameLayout notesContainer, resultOverlay;
    
    // Game State
    private int score = 0;
    private int combo = 0;
    private int maxCombo = 0;
    private int perfectHits = 0;
    private int goodHits = 0;
    private int missedNotes = 0;
    private int totalNotes = 0;
    private boolean isPlaying = false;
    private boolean isSlowMode = false;
    private boolean autoHintEnabled = false;
    
    // Note System
    private List<Note> activeNotes = new ArrayList<>();
    private List<String> vocabularyWords = new ArrayList<>();
    private List<Boolean> wordsCollected = new ArrayList<>();
    private int currentNoteIndex = 0;
    
    // Timing
    private static final float NOTE_FALL_DURATION_NORMAL = 3000f; // 3 seconds
    private static final float NOTE_FALL_DURATION_SLOW = 4500f; // 4.5 seconds
    private static final float PERFECT_TIMING_WINDOW = 100f; // ±100ms
    private static final float GOOD_TIMING_WINDOW = 200f; // ±200ms
    
    // Layout measurements
    private int laneWidth;
    private int laneHeight;
    private int hitZoneY;
    
    private Handler handler = new Handler();
    private Handler noteSpawnHandler = new Handler();
    private Random random = new Random();
    
    // Note spawning pattern
    private Runnable noteSpawner;
    private long nextSpawnTime = 0;
    private static final long SPAWN_INTERVAL_BASE = 800; // Spawn every 800ms

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rhythm_star);

        initializeViews();
        setupVocabulary();
        setupListeners();
        setupTouchDetection();
        
        // Start equalizer animation
        animateEqualizer();
    }

    private void initializeViews() {
        // Buttons
        btnBack = findViewById(R.id.btnBack);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnSlowMode = findViewById(R.id.btnSlowMode);
        btnRepeatSound = findViewById(R.id.btnRepeatSound);
        btnAutoHint = findViewById(R.id.btnAutoHint);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnNextSong = findViewById(R.id.btnNextSong);
        
        // TextViews
        txtSongTitle = findViewById(R.id.txtSongTitle);
        txtDifficulty = findViewById(R.id.txtDifficulty);
        txtScore = findViewById(R.id.txtScore);
        txtCombo = findViewById(R.id.txtCombo);
        txtPerfect = findViewById(R.id.txtPerfect);
        txtGood = findViewById(R.id.txtGood);
        txtMiss = findViewById(R.id.txtMiss);
        txtVocabWord = findViewById(R.id.txtVocabWord);
        txtFinalScore = findViewById(R.id.txtFinalScore);
        txtAccuracy = findViewById(R.id.txtAccuracy);
        txtWordsCollected = findViewById(R.id.txtWordsCollected);
        
        // Checkboxes
        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        check3 = findViewById(R.id.check3);
        check4 = findViewById(R.id.check4);
        check5 = findViewById(R.id.check5);
        
        // ImageViews
        imgEqualizer = findViewById(R.id.imgEqualizer);
        imgPerfectBurst = findViewById(R.id.imgPerfectBurst);
        imgMissCloud = findViewById(R.id.imgMissCloud);
        imgDancingStars = findViewById(R.id.imgDancingStars);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        resultStar1 = findViewById(R.id.resultStar1);
        resultStar2 = findViewById(R.id.resultStar2);
        resultStar3 = findViewById(R.id.resultStar3);
        
        // Containers
        comboContainer = findViewById(R.id.comboContainer);
        vocabBubble = findViewById(R.id.vocabBubble);
        notesContainer = findViewById(R.id.notesContainer);
        resultOverlay = findViewById(R.id.resultOverlay);
        
        // Game elements
        starMeterFill = findViewById(R.id.starMeterFill);
        hitZone = findViewById(R.id.hitZone);
        track1 = findViewById(R.id.track1);
        track2 = findViewById(R.id.track2);
        track3 = findViewById(R.id.track3);
        track4 = findViewById(R.id.track4);
        
        // Initialize star meter
        ViewGroup.LayoutParams params = starMeterFill.getLayoutParams();
        params.width = 0;
        starMeterFill.setLayoutParams(params);
    }

    private void setupVocabulary() {
        vocabularyWords.add("apple");
        vocabularyWords.add("sunny");
        vocabularyWords.add("dance");
        vocabularyWords.add("jump");
        vocabularyWords.add("blue");
        
        for (int i = 0; i < vocabularyWords.size(); i++) {
            wordsCollected.add(false);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            animateButtonPress(v);
            finish();
        });
        
        btnPlayPause.setOnClickListener(v -> {
            animateButtonPress(v);
            togglePlayPause();
        });
        
        btnSlowMode.setOnClickListener(v -> {
            animateButtonPress(v);
            toggleSlowMode();
        });
        
        btnRepeatSound.setOnClickListener(v -> {
            animateButtonPress(v);
            repeatLastWord();
        });
        
        btnAutoHint.setOnClickListener(v -> {
            animateButtonPress(v);
            toggleAutoHint();
        });
        
        btnPlayAgain.setOnClickListener(v -> {
            animateButtonPress(v);
            restartGame();
        });
        
        btnNextSong.setOnClickListener(v -> {
            animateButtonPress(v);
            loadNextSong();
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTouchDetection() {
        // Setup touch listeners for each track
        View[] tracks = {track1, track2, track3, track4};
        
        for (int i = 0; i < tracks.length; i++) {
            final int trackIndex = i;
            tracks[i].setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN && isPlaying) {
                    handleTrackTap(trackIndex, event.getY());
                }
                return true;
            });
        }
    }

    private void togglePlayPause() {
        isPlaying = !isPlaying;
        
        if (isPlaying) {
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            startGame();
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play);
            pauseGame();
        }
    }

    private void startGame() {
        // Start spawning notes
        noteSpawner = new Runnable() {
            @Override
            public void run() {
                if (isPlaying && currentNoteIndex < 30) { // Spawn 30 notes total
                    spawnNote();
                    currentNoteIndex++;
                    
                    long spawnDelay = SPAWN_INTERVAL_BASE + random.nextInt(400);
                    noteSpawnHandler.postDelayed(this, spawnDelay);
                } else if (currentNoteIndex >= 30 && activeNotes.isEmpty()) {
                    // Song complete
                    endSong();
                }
            }
        };
        noteSpawnHandler.postDelayed(noteSpawner, 500);
    }

    private void pauseGame() {
        if (noteSpawner != null) {
            noteSpawnHandler.removeCallbacks(noteSpawner);
        }
        
        // Pause all note animations
        for (Note note : activeNotes) {
            if (note.animator != null) {
                note.animator.pause();
            }
        }
    }

    private void spawnNote() {
        // Get lane dimensions after first layout
        if (laneWidth == 0) {
            laneWidth = notesContainer.getWidth();
            laneHeight = notesContainer.getHeight();
            hitZoneY = laneHeight - hitZone.getHeight() / 2;
        }
        
        // Random track (0-3)
        int track = random.nextInt(4);
        
        // Determine if vocab note (30% chance)
        boolean isVocabNote = random.nextInt(100) < 30;
        String word = null;
        
        if (isVocabNote && !vocabularyWords.isEmpty()) {
            // Pick random uncollected word
            List<String> uncollected = new ArrayList<>();
            for (int i = 0; i < vocabularyWords.size(); i++) {
                if (!wordsCollected.get(i)) {
                    uncollected.add(vocabularyWords.get(i));
                }
            }
            
            if (!uncollected.isEmpty()) {
                word = uncollected.get(random.nextInt(uncollected.size()));
            }
        }
        
        // Create note view
        TextView noteView = createNoteView(track, word);
        notesContainer.addView(noteView);
        
        // Create note object
        Note note = new Note(noteView, track, word, System.currentTimeMillis());
        activeNotes.add(note);
        
        // Animate note falling
        animateNoteFall(note);
    }

    private TextView createNoteView(int track, String word) {
        TextView noteView = new TextView(this);
        
        // Size
        int noteSize = 80;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(noteSize, noteSize);
        
        // Position at top of track
        int trackWidth = laneWidth / 4;
        params.leftMargin = track * trackWidth + (trackWidth - noteSize) / 2;
        params.topMargin = -noteSize;
        
        noteView.setLayoutParams(params);
        
        // Style based on color
        int[] backgrounds = {
            R.drawable.bg_note_pink,
            R.drawable.bg_note_cyan,
            R.drawable.bg_note_yellow,
            R.drawable.bg_note_green
        };
        noteView.setBackgroundResource(backgrounds[random.nextInt(backgrounds.length)]);
        
        // Text
        if (word != null) {
            noteView.setText(word);
            noteView.setTextSize(14);
            noteView.setTextColor(0xFF000000);
            noteView.setGravity(android.view.Gravity.CENTER);
        }
        
        noteView.setElevation(10);
        
        return noteView;
    }

    private void animateNoteFall(Note note) {
        float duration = isSlowMode ? NOTE_FALL_DURATION_SLOW : NOTE_FALL_DURATION_NORMAL;
        
        ObjectAnimator animator = ObjectAnimator.ofFloat(note.view, "translationY", 
            0, laneHeight + note.view.getHeight());
        animator.setDuration((long) duration);
        
        animator.addUpdateListener(animation -> {
            // Check if note passed hit zone without being tapped
            float y = note.view.getY() + note.view.getTranslationY();
            if (y > hitZoneY + 100 && !note.isHit) {
                note.isMissed = true;
                handleMiss();
                removeNote(note);
                animator.cancel();
            }
        });
        
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!note.isHit) {
                    removeNote(note);
                }
            }
        });
        
        animator.start();
        note.animator = animator;
    }

    private void handleTrackTap(int track, float tapY) {
        // Find note in this track near hit zone
        Note targetNote = null;
        float bestDistance = Float.MAX_VALUE;
        
        for (Note note : activeNotes) {
            if (note.track == track && !note.isHit && !note.isMissed) {
                float noteY = note.view.getY() + note.view.getTranslationY();
                float distance = Math.abs(noteY - hitZoneY);
                
                if (distance < bestDistance && distance < 200) {
                    bestDistance = distance;
                    targetNote = note;
                }
            }
        }
        
        if (targetNote != null) {
            // Calculate timing accuracy
            if (bestDistance <= PERFECT_TIMING_WINDOW) {
                handlePerfectHit(targetNote);
            } else if (bestDistance <= GOOD_TIMING_WINDOW) {
                handleGoodHit(targetNote);
            } else {
                handleMiss();
            }
        } else {
            // Tapped with no note nearby
            handleMiss();
        }
    }

    private void handlePerfectHit(Note note) {
        note.isHit = true;
        perfectHits++;
        combo++;
        score += 100 * (1 + combo / 10);
        
        // Show perfect feedback
        showFeedback("PERFECT", imgPerfectBurst);
        
        // Handle vocabulary
        if (note.word != null) {
            collectVocabulary(note.word);
        }
        
        // Update UI
        updateScore();
        updateCombo();
        updateStarMeter();
        
        // Remove note
        removeNote(note);
    }

    private void handleGoodHit(Note note) {
        note.isHit = true;
        goodHits++;
        combo++;
        score += 50 * (1 + combo / 10);
        
        // Show good feedback
        showFeedback("GOOD", null);
        
        // Handle vocabulary
        if (note.word != null) {
            collectVocabulary(note.word);
        }
        
        // Update UI
        updateScore();
        updateCombo();
        updateStarMeter();
        
        // Remove note
        removeNote(note);
    }

    private void handleMiss() {
        missedNotes++;
        combo = 0;
        
        // Show miss feedback
        showFeedback("MISS", imgMissCloud);
        
        // Hide combo
        comboContainer.setVisibility(View.INVISIBLE);
    }

    private void showFeedback(String type, ImageView icon) {
        TextView feedbackText = null;
        
        switch (type) {
            case "PERFECT":
                feedbackText = txtPerfect;
                if (icon != null) {
                    icon.setVisibility(View.VISIBLE);
                    animateIcon(icon);
                }
                break;
            case "GOOD":
                feedbackText = txtGood;
                break;
            case "MISS":
                feedbackText = txtMiss;
                if (icon != null) {
                    icon.setVisibility(View.VISIBLE);
                    animateIcon(icon);
                }
                break;
        }
        
        if (feedbackText != null) {
            feedbackText.setVisibility(View.VISIBLE);
            feedbackText.setScaleX(0.5f);
            feedbackText.setScaleY(0.5f);
            feedbackText.setAlpha(1.0f);
            
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(feedbackText, "scaleX", 0.5f, 1.2f, 1.0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(feedbackText, "scaleY", 0.5f, 1.2f, 1.0f);
            scaleX.setDuration(400);
            scaleY.setDuration(400);
            scaleX.start();
            scaleY.start();
            
            final TextView finalText = feedbackText;
            handler.postDelayed(() -> {
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(finalText, "alpha", 1.0f, 0f);
                fadeOut.setDuration(300);
                fadeOut.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        finalText.setVisibility(View.INVISIBLE);
                    }
                });
                fadeOut.start();
            }, 600);
        }
    }

    private void animateIcon(ImageView icon) {
        ObjectAnimator scale = ObjectAnimator.ofFloat(icon, "scaleX", 0.5f, 1.2f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(icon, "scaleY", 0.5f, 1.2f);
        ObjectAnimator fade = ObjectAnimator.ofFloat(icon, "alpha", 1.0f, 0f);
        
        scale.setDuration(600);
        scaleY.setDuration(600);
        fade.setDuration(600);
        fade.setStartDelay(200);
        
        scale.start();
        scaleY.start();
        fade.start();
        
        fade.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                icon.setVisibility(View.INVISIBLE);
                icon.setAlpha(1.0f);
            }
        });
    }

    private void collectVocabulary(String word) {
        int index = vocabularyWords.indexOf(word);
        if (index >= 0 && !wordsCollected.get(index)) {
            wordsCollected.set(index, true);
            
            // Update checkbox
            TextView[] checks = {check1, check2, check3, check4, check5};
            if (index < checks.length) {
                checks[index].setText("✓");
                checks[index].setTextColor(0xFF4CAF50);
                animatePulse(checks[index]);
            }
            
            // Show vocab bubble
            showVocabBubble(word);
            
            // Play pronunciation (simulated)
            playWordPronunciation(word);
        }
    }

    private void showVocabBubble(String word) {
        txtVocabWord.setText(word);
        vocabBubble.setVisibility(View.VISIBLE);
        vocabBubble.setScaleX(0.5f);
        vocabBubble.setScaleY(0.5f);
        vocabBubble.setAlpha(0f);
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(vocabBubble, "scaleX", 0.5f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(vocabBubble, "scaleY", 0.5f, 1.0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(vocabBubble, "alpha", 0f, 1.0f);
        
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        fadeIn.setDuration(300);
        
        scaleX.start();
        scaleY.start();
        fadeIn.start();
        
        handler.postDelayed(() -> {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(vocabBubble, "alpha", 1.0f, 0f);
            fadeOut.setDuration(300);
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vocabBubble.setVisibility(View.INVISIBLE);
                }
            });
            fadeOut.start();
        }, 2000);
    }

    private void playWordPronunciation(String word) {
        // In real app, use TextToSpeech
        // textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void removeNote(Note note) {
        activeNotes.remove(note);
        
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(note.view, "alpha", 1.0f, 0f);
        fadeOut.setDuration(200);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                notesContainer.removeView(note.view);
            }
        });
        fadeOut.start();
    }

    private void updateScore() {
        txtScore.setText(String.valueOf(score));
    }

    private void updateCombo() {
        if (combo > 0) {
            comboContainer.setVisibility(View.VISIBLE);
            txtCombo.setText("Combo x" + combo);
            animatePulse(comboContainer);
            
            if (combo > maxCombo) {
                maxCombo = combo;
            }
        }
    }

    private void updateStarMeter() {
        totalNotes = perfectHits + goodHits + missedNotes;
        if (totalNotes == 0) return;
        
        float accuracy = (float) (perfectHits + goodHits) / totalNotes;
        
        // Update meter width
        View starMeterBg = findViewById(R.id.starMeterBg);
        if (starMeterBg != null && starMeterFill != null) {
            ViewGroup.LayoutParams params = starMeterFill.getLayoutParams();
            params.width = (int) (starMeterBg.getWidth() * accuracy);
            starMeterFill.setLayoutParams(params);
        }
        
        // Update stars
        if (accuracy >= 0.9f) {
            if (star1 != null) star1.setImageResource(R.drawable.ic_star_filled);
            if (star2 != null) star2.setImageResource(R.drawable.ic_star_filled);
            if (star3 != null) star3.setImageResource(R.drawable.ic_star_filled);
        } else if (accuracy >= 0.7f) {
            if (star1 != null) star1.setImageResource(R.drawable.ic_star_filled);
            if (star2 != null) star2.setImageResource(R.drawable.ic_star_filled);
        } else if (accuracy >= 0.5f) {
            if (star1 != null) star1.setImageResource(R.drawable.ic_star_filled);
        }
    }

    private void toggleSlowMode() {
        isSlowMode = !isSlowMode;
        animatePulse(btnSlowMode);
    }

    private void repeatLastWord() {
        // Play last collected word
        for (int i = wordsCollected.size() - 1; i >= 0; i--) {
            if (wordsCollected.get(i)) {
                playWordPronunciation(vocabularyWords.get(i));
                break;
            }
        }
        animatePulse(btnRepeatSound);
    }

    private void toggleAutoHint() {
        autoHintEnabled = !autoHintEnabled;
        animatePulse(btnAutoHint);
    }

    private void endSong() {
        isPlaying = false;
        
        // Calculate results
        int wordsCount = 0;
        for (boolean collected : wordsCollected) {
            if (collected) wordsCount++;
        }
        
        totalNotes = perfectHits + goodHits + missedNotes;
        float accuracy = totalNotes > 0 ? (float) (perfectHits + goodHits) / totalNotes * 100 : 0;
        
        // Update result UI
        txtFinalScore.setText(String.valueOf(score));
        txtAccuracy.setText(String.format("%.0f%%", accuracy));
        txtWordsCollected.setText(wordsCount + "/" + vocabularyWords.size());
        
        // Update result stars
        if (accuracy >= 90) {
            resultStar1.setImageResource(R.drawable.ic_star_filled);
            resultStar2.setImageResource(R.drawable.ic_star_filled);
            resultStar3.setImageResource(R.drawable.ic_star_filled);
        } else if (accuracy >= 70) {
            resultStar1.setImageResource(R.drawable.ic_star_filled);
            resultStar2.setImageResource(R.drawable.ic_star_filled);
        } else if (accuracy >= 50) {
            resultStar1.setImageResource(R.drawable.ic_star_filled);
        }
        
        // Show result popup
        handler.postDelayed(() -> {
            resultOverlay.setVisibility(View.VISIBLE);
            resultOverlay.setAlpha(0f);
            
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(resultOverlay, "alpha", 0f, 1.0f);
            fadeIn.setDuration(400);
            fadeIn.start();
            
            animateDancingStars();
        }, 1000);
    }

    private void animateDancingStars() {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(imgDancingStars, "rotation", 0, 360);
        rotate.setDuration(3000);
        rotate.setRepeatCount(ObjectAnimator.INFINITE);
        rotate.start();
    }

    private void animateEqualizer() {
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imgEqualizer, "scaleY", 1.0f, 1.3f, 1.0f);
        scaleY.setDuration(600);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.start();
    }

    private void restartGame() {
        // Reset game state
        score = 0;
        combo = 0;
        perfectHits = 0;
        goodHits = 0;
        missedNotes = 0;
        currentNoteIndex = 0;
        
        // Clear notes
        activeNotes.clear();
        notesContainer.removeAllViews();
        
        // Reset vocabulary
        for (int i = 0; i < wordsCollected.size(); i++) {
            wordsCollected.set(i, false);
        }
        
        TextView[] checks = {check1, check2, check3, check4, check5};
        for (TextView check : checks) {
            check.setText("☐");
            check.setTextColor(0xFF999999);
        }
        
        // Reset UI
        updateScore();
        comboContainer.setVisibility(View.INVISIBLE);
        star1.setImageResource(R.drawable.ic_star_empty);
        star2.setImageResource(R.drawable.ic_star_empty);
        star3.setImageResource(R.drawable.ic_star_empty);
        
        ViewGroup.LayoutParams params = starMeterFill.getLayoutParams();
        params.width = 0;
        starMeterFill.setLayoutParams(params);
        
        // Hide result overlay
        resultOverlay.setVisibility(View.GONE);
        
        // Start game
        isPlaying = true;
        startGame();
    }

    private void loadNextSong() {
        // In real app, load new song
        restartGame();
        resultOverlay.setVisibility(View.GONE);
    }

    // Animation Helpers
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        noteSpawnHandler.removeCallbacksAndMessages(null);
    }

    // Note class
    private static class Note {
        TextView view;
        int track;
        String word;
        long spawnTime;
        boolean isHit;
        boolean isMissed;
        ObjectAnimator animator;
        
        Note(TextView view, int track, String word, long spawnTime) {
            this.view = view;
            this.track = track;
            this.word = word;
            this.spawnTime = spawnTime;
            this.isHit = false;
            this.isMissed = false;
        }
    }
}
