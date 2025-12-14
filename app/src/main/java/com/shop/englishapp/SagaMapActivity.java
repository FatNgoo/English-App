package com.shop.englishapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

/**
 * SagaMapActivity - Displays the learning journey map with nodes representing different stages
 * Features:
 * - Visual learning path with locked/unlocked nodes
 * - Progress tracking with percentage
 * - Next lesson card with quick actions
 * - Kid-friendly UI with pastel colors and cute illustrations
 */
public class SagaMapActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack;
    private TextView tvTitle;
    private TextView tvProgressPercent;
    private ProgressBar progressOverall;
    private Button btnStartLearning;
    private Button btnPreviewFlashcards;

    // Progress tracking
    private int currentProgress = 65; // Default 65%
    private String nextLessonTitle = "Vocabulary – Animals";
    private String nextLessonDescription = "Learn 10 new words with flashcards";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saga_map);

        // Initialize UI components
        initializeViews();

        // Set up click listeners
        setupClickListeners();

        // Load initial data
        loadUserProgress();
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        progressOverall = findViewById(R.id.progressOverall);
        btnStartLearning = findViewById(R.id.btnStartLearning);
        btnPreviewFlashcards = findViewById(R.id.btnPreviewFlashcards);
    }

    /**
     * Set up click listeners for interactive elements
     */
    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> {
            // Navigate back to previous screen
            finish();
        });

        // Start Learning button
        btnStartLearning.setOnClickListener(v -> {
            // Start the next lesson
            startNextLesson();
        });

        // Preview Flashcards button
        btnPreviewFlashcards.setOnClickListener(v -> {
            // Preview flashcards
            previewFlashcards();
        });

        // Make nodes clickable (for future implementation)
        setupNodeClickListeners();
    }

    /**
     * Load user's learning progress from database or preferences
     */
    private void loadUserProgress() {
        // In a real app, this would fetch data from a database or API
        // For now, we'll use mock data

        // Update progress bar and percentage
        progressOverall.setProgress(currentProgress);
        tvProgressPercent.setText(currentProgress + "%");

        // Update next lesson info
        // This is already set in the layout, but could be updated dynamically
    }

    /**
     * Set up click listeners for saga map nodes
     */
    private void setupNodeClickListeners() {
        // Node 1 - Vocabulary (Current/Unlocked)
        View node1Container = findViewById(R.id.node1Container);
        if (node1Container != null) {
            node1Container.setOnClickListener(v -> {
                showNodeInfo("Vocabulary", "Learn new words with fun illustrations!", true);
            });
        }

        // Node 2 - Flashcards (Unlocked)
        View node2Container = findViewById(R.id.node2Container);
        if (node2Container != null) {
            node2Container.setOnClickListener(v -> {
                showNodeInfo("Flashcards", "Practice vocabulary with interactive cards!", true);
            });
        }

        // Node 3 - Sentence Structure (Locked)
        View node3Container = findViewById(R.id.node3Container);
        if (node3Container != null) {
            node3Container.setOnClickListener(v -> {
                showNodeInfo("Sentence Structure", "Complete previous lessons to unlock!", false);
            });
        }

        // Node 4 - Mini Quiz (Locked)
        View node4Container = findViewById(R.id.node4Container);
        if (node4Container != null) {
            node4Container.setOnClickListener(v -> {
                showNodeInfo("Mini Quiz", "Complete previous lessons to unlock!", false);
            });
        }
    }

    /**
     * Show information about a node
     */
    private void showNodeInfo(String nodeName, String description, boolean isUnlocked) {
        if (isUnlocked) {
            Toast.makeText(this, nodeName + ": " + description, Toast.LENGTH_SHORT).show();
            // In a real app, this would navigate to the specific lesson
        } else {
            Toast.makeText(this, "🔒 " + nodeName + " is locked. " + description, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Start the next lesson
     */
    private void startNextLesson() {
        Toast.makeText(this, "Starting: " + nextLessonTitle, Toast.LENGTH_SHORT).show();
        
        // In a real app, this would:
        // 1. Navigate to the lesson activity
        // 2. Pass lesson data (ID, type, content)
        // 3. Track lesson start time
        
        // Example:
        // Intent intent = new Intent(this, LessonActivity.class);
        // intent.putExtra("lesson_type", "vocabulary");
        // intent.putExtra("lesson_topic", "animals");
        // startActivity(intent);
    }

    /**
     * Preview flashcards for the next lesson
     */
    private void previewFlashcards() {
        Toast.makeText(this, "Previewing flashcards for: " + nextLessonTitle, Toast.LENGTH_SHORT).show();
        
        // In a real app, this would:
        // 1. Navigate to flashcard preview screen
        // 2. Load preview data (first 3-5 cards)
        // 3. Allow user to explore without tracking progress
        
        // Example:
        // Intent intent = new Intent(this, FlashcardPreviewActivity.class);
        // intent.putExtra("lesson_id", currentLessonId);
        // startActivity(intent);
    }

    /**
     * Update progress after completing a lesson
     * This method can be called from other activities via Intent or callback
     */
    public void updateProgress(int newProgress) {
        currentProgress = Math.min(100, Math.max(0, newProgress));
        progressOverall.setProgress(currentProgress);
        tvProgressPercent.setText(currentProgress + "%");
        
        // Save to preferences or database
        // SharedPreferences prefs = getSharedPreferences("UserProgress", MODE_PRIVATE);
        // prefs.edit().putInt("overall_progress", currentProgress).apply();
    }

    /**
     * Unlock a new node when prerequisites are completed
     */
    public void unlockNode(int nodeId) {
        // In a real app, this would:
        // 1. Update node visual state (remove lock, add glow)
        // 2. Enable click interaction
        // 3. Show celebration animation
        // 4. Save unlock state to database
        
        Toast.makeText(this, "🎉 New lesson unlocked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh progress when returning from a lesson
        loadUserProgress();
    }
}
