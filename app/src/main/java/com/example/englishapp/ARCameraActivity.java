package com.example.englishapp;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shop.englishapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ARCameraActivity extends AppCompatActivity {

    // UI Components
    private TextureView cameraPreview;
    private FrameLayout wordBubblesContainer, objectTagsContainer, btnShutter;
    private CardView wordBubbleTemplate, notFoundBubble;
    private LinearLayout objectTagTemplate;
    private ImageView imgFlash, imgLoadingScan;
    private View scanFrame, scanRay, shutterRing, snapshotOverlay, loadingOverlay, quizOverlay;
    private TextView txtHint, txtQuizQuestion;
    private RecyclerView recyclerHistory;
    private BottomSheetBehavior historyBottomSheet;
    
    // Camera & Detection
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private boolean flashOn = false;
    private boolean isDetecting = false;
    private Handler detectionHandler = new Handler();
    
    // Object tracking
    private HashMap<String, View> activeLabels = new HashMap<>();
    private List<DetectedObject> detectedObjects = new ArrayList<>();
    
    // Audio
    private TextToSpeech tts;
    
    // History
    private SharedPreferences prefs;
    private List<ScanHistoryItem> historyItems = new ArrayList<>();
    
    // Quiz
    private boolean quizMode = false;
    private String quizTargetWord = "";
    
    // Animation
    private ObjectAnimator scanPulseAnim, scanRayAnim, loadingSpinAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_camera);
        
        initializeViews();
        initializeTextToSpeech();
        initializeSharedPreferences();
        checkCameraPermission();
        setupClickListeners();
        startAnimations();
    }

    private void initializeViews() {
        // Camera
        cameraPreview = findViewById(R.id.cameraPreview);
        
        // Containers
        wordBubblesContainer = findViewById(R.id.wordBubblesContainer);
        objectTagsContainer = findViewById(R.id.objectTagsContainer);
        
        // Templates
        wordBubbleTemplate = findViewById(R.id.wordBubbleTemplate);
        objectTagTemplate = findViewById(R.id.objectTagTemplate);
        
        // Scan frame
        scanFrame = findViewById(R.id.scanFrame);
        scanRay = findViewById(R.id.scanRay);
        
        // Buttons
        btnShutter = findViewById(R.id.btnShutter);
        shutterRing = findViewById(R.id.shutterRing);
        
        // Overlays
        snapshotOverlay = findViewById(R.id.snapshotOverlay);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        quizOverlay = findViewById(R.id.quizOverlay);
        notFoundBubble = findViewById(R.id.notFoundBubble);
        
        // Images
        imgFlash = findViewById(R.id.imgFlash);
        imgLoadingScan = findViewById(R.id.imgLoadingScan);
        
        // Text
        txtHint = findViewById(R.id.txtHint);
        txtQuizQuestion = findViewById(R.id.txtQuizQuestion);
        
        // History
        recyclerHistory = findViewById(R.id.recyclerHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initializeTextToSpeech() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });
    }

    private void initializeSharedPreferences() {
        prefs = getSharedPreferences("ARCameraData", MODE_PRIVATE);
        loadScanHistory();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.CAMERA}, 
                CAMERA_PERMISSION_REQUEST);
        } else {
            initCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void initCamera() {
        // Initialize camera preview
        // In real implementation, use CameraX here
        cameraPreview.setKeepScreenOn(true);
        
        // Start mock object detection for demo
        startMockObjectDetection();
    }

    private void setupClickListeners() {
        // Back button
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                animateButtonPress(v);
                finish();
            });
        }
        
        // Flash toggle
        View btnFlash = findViewById(R.id.btnFlash);
        if (btnFlash != null) {
            btnFlash.setOnClickListener(v -> {
                animateButtonPress(v);
                toggleFlash();
            });
        }
        
        // Shutter button
        if (btnShutter != null) {
            btnShutter.setOnClickListener(v -> {
                animateShutterPress();
                captureSnapshot();
            });
        }
        
        // Snapshot actions
        View btnSaveAlbum = findViewById(R.id.btnSaveAlbum);
        if (btnSaveAlbum != null) {
            btnSaveAlbum.setOnClickListener(v -> {
                animateButtonPress(v);
                saveSnapshot();
            });
        }
        
        View btnRetry = findViewById(R.id.btnRetry);
        if (btnRetry != null) {
            btnRetry.setOnClickListener(v -> {
                animateButtonPress(v);
                if (snapshotOverlay != null) {
                    snapshotOverlay.setVisibility(View.GONE);
                }
            });
        }
        
        View btnShare = findViewById(R.id.btnShare);
        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                animateButtonPress(v);
                shareSnapshot();
            });
        }
        
        // AR Quiz button
        View btnArQuiz = findViewById(R.id.btnArQuiz);
        if (btnArQuiz != null) {
            btnArQuiz.setOnClickListener(v -> {
                animateButtonPress(v);
                startARQuiz();
            });
        }
        
        // Navigation
        setupNavigation();
    }

    private void setupNavigation() {
        View navHome = findViewById(R.id.navHome);
        if (navHome != null) {
            navHome.setOnClickListener(v -> navigateTo("HomeActivity"));
        }
        
        View navMap = findViewById(R.id.navMap);
        if (navMap != null) {
            navMap.setOnClickListener(v -> navigateTo("MapActivity"));
        }
        
        View navGames = findViewById(R.id.navGames);
        if (navGames != null) {
            navGames.setOnClickListener(v -> navigateTo("GamesActivity"));
        }
        
        View navProfile = findViewById(R.id.navProfile);
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> navigateTo("ProfileActivity"));
        }
    }

    private void navigateTo(String activity) {
        Toast.makeText(this, "Navigate to " + activity, Toast.LENGTH_SHORT).show();
        // Implement navigation
    }

    private void toggleFlash() {
        flashOn = !flashOn;
        if (flashOn) {
            imgFlash.setColorFilter(getResources().getColor(android.R.color.holo_orange_light));
        } else {
            imgFlash.setColorFilter(getResources().getColor(android.R.color.white));
        }
        // In real implementation: camera.cameraControl.enableTorch(flashOn)
    }

    // ============ OBJECT DETECTION ============

    private void startMockObjectDetection() {
        // Mock detection - In real app, use MLKit ObjectDetector
        showLoading();
        
        detectionHandler.postDelayed(() -> {
            hideLoading();
            
            // Mock detected objects
            DetectedObject chair = new DetectedObject("chair", "/tʃeə(r)/", 0.95f, 500, 300);
            DetectedObject table = new DetectedObject("table", "/ˈteɪbl/", 0.88f, 300, 500);
            
            detectedObjects.clear();
            detectedObjects.add(chair);
            detectedObjects.add(table);
            
            showWordBubbles();
            
            // Continue detection
            detectionHandler.postDelayed(() -> {
                if (isDetecting) {
                    startMockObjectDetection();
                }
            }, 3000);
            
        }, 2000);
    }

    private void showWordBubbles() {
        wordBubblesContainer.removeAllViews();
        objectTagsContainer.removeAllViews();
        
        if (detectedObjects.isEmpty()) {
            showNotFound();
            return;
        }
        
        if (detectedObjects.size() == 1) {
            // Show full 3D bubble for single object
            DetectedObject obj = detectedObjects.get(0);
            showFullWordBubble(obj);
        } else {
            // Show small tags for multiple objects
            for (DetectedObject obj : detectedObjects) {
                showObjectTag(obj);
            }
        }
        
        // Hide not found if visible
        notFoundBubble.setVisibility(View.GONE);
    }

    private void showFullWordBubble(DetectedObject obj) {
        // Create 3D word bubble
        CardView bubble = (CardView) getLayoutInflater().inflate(
            R.layout.ar_camera, wordBubblesContainer, false)
            .findViewById(R.id.wordBubbleTemplate);
        
        // Clone template
        CardView newBubble = new CardView(this);
        newBubble.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT));
        newBubble.setCardElevation(12);
        newBubble.setRadius(20);
        
        // Set position based on object location
        newBubble.setX(obj.screenX - 100);
        newBubble.setY(obj.screenY - 200);
        
        // Add content
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(16, 16, 16, 16);
        
        TextView txtWord = new TextView(this);
        txtWord.setText(obj.word);
        txtWord.setTextSize(32);
        txtWord.setTextColor(getResources().getColor(android.R.color.black));
        txtWord.setTypeface(null, android.graphics.Typeface.BOLD);
        content.addView(txtWord);
        
        TextView txtPron = new TextView(this);
        txtPron.setText(obj.pronunciation);
        txtPron.setTextSize(14);
        txtPron.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        content.addView(txtPron);
        
        FrameLayout btnAudio = new FrameLayout(this);
        btnAudio.setLayoutParams(new LinearLayout.LayoutParams(40, 40));
        btnAudio.setOnClickListener(v -> playPronunciation(obj.word));
        content.addView(btnAudio);
        
        newBubble.addView(content);
        wordBubblesContainer.addView(newBubble);
        
        // Animate entrance
        animateBubbleEntrance(newBubble);
        animateBubbleHover(newBubble);
        
        // Save to history
        saveScanHistory(obj);
    }

    private void showObjectTag(DetectedObject obj) {
        LinearLayout tag = new LinearLayout(this);
        tag.setLayoutParams(new FrameLayout.LayoutParams(120, 40));
        tag.setBackgroundResource(R.drawable.bg_object_tag);
        tag.setPadding(8, 8, 8, 8);
        
        // Set position
        tag.setX(obj.screenX - 60);
        tag.setY(obj.screenY - 20);
        
        TextView txtWord = new TextView(this);
        txtWord.setText(obj.word);
        txtWord.setTextSize(18);
        txtWord.setTextColor(getResources().getColor(android.R.color.white));
        tag.addView(txtWord);
        
        // Tap to enlarge
        tag.setOnClickListener(v -> {
            objectTagsContainer.removeAllViews();
            showFullWordBubble(obj);
        });
        
        objectTagsContainer.addView(tag);
        animateFadeIn(tag);
    }

    private void showNotFound() {
        notFoundBubble.setVisibility(View.VISIBLE);
        animateShake(notFoundBubble);
    }

    // ============ AUDIO ============

    private void playPronunciation(String word) {
        if (tts != null) {
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    // ============ SNAPSHOT ============

    private void captureSnapshot() {
        // Capture current camera frame
        // In real implementation: Bitmap bitmap = cameraPreview.getBitmap()
        
        showLoading();
        
        detectionHandler.postDelayed(() -> {
            hideLoading();
            snapshotOverlay.setVisibility(View.VISIBLE);
            animateFadeIn(snapshotOverlay);
        }, 1000);
    }

    private void saveSnapshot() {
        Toast.makeText(this, "Snapshot saved to album!", Toast.LENGTH_SHORT).show();
        snapshotOverlay.setVisibility(View.GONE);
        
        // In real implementation:
        // MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "AR_Scan", "Detected objects");
    }

    private void shareSnapshot() {
        Toast.makeText(this, "Share snapshot", Toast.LENGTH_SHORT).show();
        
        // In real implementation:
        // Intent shareIntent = new Intent(Intent.ACTION_SEND);
        // shareIntent.setType("image/*");
        // shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        // startActivity(Intent.createChooser(shareIntent, "Share snapshot"));
    }

    // ============ SCAN HISTORY ============

    private void saveScanHistory(DetectedObject obj) {
        try {
            JSONObject item = new JSONObject();
            item.put("word", obj.word);
            item.put("pronunciation", obj.pronunciation);
            item.put("timestamp", System.currentTimeMillis());
            
            JSONArray history = new JSONArray(prefs.getString("history", "[]"));
            
            // Add to beginning
            JSONArray newHistory = new JSONArray();
            newHistory.put(item);
            for (int i = 0; i < Math.min(history.length(), 19); i++) {
                newHistory.put(history.getJSONObject(i));
            }
            
            prefs.edit().putString("history", newHistory.toString()).apply();
            loadScanHistory();
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadScanHistory() {
        historyItems.clear();
        
        try {
            JSONArray history = new JSONArray(prefs.getString("history", "[]"));
            for (int i = 0; i < history.length(); i++) {
                JSONObject item = history.getJSONObject(i);
                historyItems.add(new ScanHistoryItem(
                    item.getString("word"),
                    item.getString("pronunciation"),
                    item.getLong("timestamp")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        // Update RecyclerView
        if (recyclerHistory.getAdapter() == null) {
            // Set adapter
        }
    }

    // ============ AR QUIZ ============

    private void startARQuiz() {
        if (detectedObjects.isEmpty()) {
            Toast.makeText(this, "Scan some objects first!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        quizMode = true;
        quizOverlay.setVisibility(View.VISIBLE);
        animateFadeIn(quizOverlay);
        
        // Select random target
        Random random = new Random();
        DetectedObject target = detectedObjects.get(random.nextInt(detectedObjects.size()));
        quizTargetWord = target.word;
        
        txtQuizQuestion.setText("Which one is the " + quizTargetWord + "?");
        
        // Enable tap on object tags
        for (int i = 0; i < objectTagsContainer.getChildCount(); i++) {
            View tag = objectTagsContainer.getChildAt(i);
            tag.setOnClickListener(v -> checkQuizAnswer((TextView) tag.findViewById(R.id.txtTagWord)));
        }
    }

    private void checkQuizAnswer(TextView tagWord) {
        String selectedWord = tagWord.getText().toString();
        
        if (selectedWord.equals(quizTargetWord)) {
            // Correct!
            Toast.makeText(this, "✨ Correct! +10 XP", Toast.LENGTH_SHORT).show();
            animateCorrectGlow();
            quizOverlay.setVisibility(View.GONE);
            quizMode = false;
        } else {
            // Wrong
            Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show();
            animateShake(quizOverlay);
        }
    }

    // ============ ANIMATIONS ============

    private void startAnimations() {
        // Scan frame pulse
        scanPulseAnim = ObjectAnimator.ofFloat(scanFrame, "scaleX", 1.0f, 1.05f, 1.0f);
        scanPulseAnim.setDuration(2000);
        scanPulseAnim.setRepeatCount(ValueAnimator.INFINITE);
        scanPulseAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        scanPulseAnim.start();
        
        ObjectAnimator scanPulseAnimY = ObjectAnimator.ofFloat(scanFrame, "scaleY", 1.0f, 1.05f, 1.0f);
        scanPulseAnimY.setDuration(2000);
        scanPulseAnimY.setRepeatCount(ValueAnimator.INFINITE);
        scanPulseAnimY.setInterpolator(new AccelerateDecelerateInterpolator());
        scanPulseAnimY.start();
        
        // Scanning ray
        scanRay.setVisibility(View.VISIBLE);
        scanRayAnim = ObjectAnimator.ofFloat(scanRay, "translationY", 0f, 280f);
        scanRayAnim.setDuration(3000);
        scanRayAnim.setRepeatCount(ValueAnimator.INFINITE);
        scanRayAnim.setInterpolator(new LinearInterpolator());
        scanRayAnim.start();
        
        // Sparkles
        animateSparkles();
    }

    private void animateSparkles() {
        int[] sparkleIds = {R.id.sparkle1, R.id.sparkle2, R.id.sparkle3, R.id.sparkle4};
        Random random = new Random();
        
        for (int i = 0; i < sparkleIds.length; i++) {
            View sparkle = findViewById(sparkleIds[i]);
            sparkle.setVisibility(View.VISIBLE);
            sparkle.setX(random.nextInt(280));
            sparkle.setY(random.nextInt(280));
            
            ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(sparkle, "alpha", 0f, 1f, 0f);
            fadeAnim.setDuration(2000);
            fadeAnim.setStartDelay(i * 500);
            fadeAnim.setRepeatCount(ValueAnimator.INFINITE);
            fadeAnim.start();
        }
    }

    private void animateButtonPress(View button) {
        button.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction(() -> {
                button.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start();
            })
            .start();
    }

    private void animateShutterPress() {
        shutterRing.setVisibility(View.VISIBLE);
        shutterRing.setAlpha(1.0f);
        shutterRing.setScaleX(1.0f);
        shutterRing.setScaleY(1.0f);
        
        shutterRing.animate()
            .scaleX(1.3f)
            .scaleY(1.3f)
            .alpha(0f)
            .setDuration(500)
            .withEndAction(() -> shutterRing.setVisibility(View.GONE))
            .start();
    }

    private void animateBubbleEntrance(View bubble) {
        bubble.setScaleX(0f);
        bubble.setScaleY(0f);
        bubble.setAlpha(0f);
        
        bubble.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .alpha(1.0f)
            .setDuration(300)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();
    }

    private void animateBubbleHover(View bubble) {
        // Y-axis sine wave hover
        ObjectAnimator hoverAnim = ObjectAnimator.ofFloat(bubble, "translationY", 
            bubble.getY(), bubble.getY() - 8, bubble.getY());
        hoverAnim.setDuration(3000);
        hoverAnim.setRepeatCount(ValueAnimator.INFINITE);
        hoverAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        hoverAnim.start();
        
        // Slight rotation
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(bubble, "rotation", -3f, 3f, -3f);
        rotateAnim.setDuration(4000);
        rotateAnim.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        rotateAnim.start();
    }

    private void animateFadeIn(View view) {
        view.setAlpha(0f);
        view.animate()
            .alpha(1.0f)
            .setDuration(300)
            .start();
    }

    private void animateShake(View view) {
        ObjectAnimator shakeAnim = ObjectAnimator.ofFloat(view, "translationX", 
            0f, -10f, 10f, -10f, 10f, 0f);
        shakeAnim.setDuration(300);
        shakeAnim.start();
    }

    private void animateCorrectGlow() {
        // Green glow overlay
        View glowView = new View(this);
        glowView.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
        glowView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        glowView.setAlpha(0f);
        
        wordBubblesContainer.addView(glowView);
        
        glowView.animate()
            .alpha(0.5f)
            .setDuration(200)
            .withEndAction(() -> {
                glowView.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(() -> wordBubblesContainer.removeView(glowView))
                    .start();
            })
            .start();
    }

    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
        isDetecting = true;
        
        // Spin loading icon
        loadingSpinAnim = ObjectAnimator.ofFloat(imgLoadingScan, "rotation", 0f, 360f);
        loadingSpinAnim.setDuration(2000);
        loadingSpinAnim.setRepeatCount(ValueAnimator.INFINITE);
        loadingSpinAnim.setInterpolator(new LinearInterpolator());
        loadingSpinAnim.start();
    }

    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
        if (loadingSpinAnim != null) {
            loadingSpinAnim.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        detectionHandler.removeCallbacksAndMessages(null);
    }

    // ============ DATA CLASSES ============

    private static class DetectedObject {
        String word;
        String pronunciation;
        float confidence;
        int screenX;
        int screenY;

        DetectedObject(String word, String pronunciation, float confidence, int x, int y) {
            this.word = word;
            this.pronunciation = pronunciation;
            this.confidence = confidence;
            this.screenX = x;
            this.screenY = y;
        }
    }

    private static class ScanHistoryItem {
        String word;
        String pronunciation;
        long timestamp;

        ScanHistoryItem(String word, String pronunciation, long timestamp) {
            this.word = word;
            this.pronunciation = pronunciation;
            this.timestamp = timestamp;
        }
    }
}
