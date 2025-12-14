package com.shop.englishapp;

import android.content.Intent;
import android.os.Bundle;

/**
 * Example code showing how to integrate SagaMapActivity into your app
 * 
 * USAGE EXAMPLES:
 */
public class SagaMapIntegrationExample {

    /**
     * Example 1: Launch Saga Map from Home Activity
     * Add this code to HomeActivity.java
     */
    public void launchSagaMapFromHome() {
        // Inside HomeActivity onCreate or button click listener:
        // 
        // Button btnViewSagaMap = findViewById(R.id.btnViewSagaMap);
        // btnViewSagaMap.setOnClickListener(v -> {
        //     Intent intent = new Intent(HomeActivity.this, SagaMapActivity.class);
        //     startActivity(intent);
        // });
    }

    /**
     * Example 2: Launch Saga Map with specific progress
     * Pass data via Intent extras
     */
    public void launchSagaMapWithProgress() {
        // Intent intent = new Intent(this, SagaMapActivity.class);
        // intent.putExtra("user_progress", 75);
        // intent.putExtra("unlocked_nodes", new int[]{1, 2, 3});
        // startActivity(intent);
        //
        // Then in SagaMapActivity.onCreate():
        // int progress = getIntent().getIntExtra("user_progress", 0);
        // updateProgress(progress);
    }

    /**
     * Example 3: Return to Home after completing a lesson
     * Use in lesson activities to update saga map
     */
    public void returnToSagaMapAfterLesson() {
        // After lesson completion in LessonActivity:
        //
        // Intent resultIntent = new Intent();
        // resultIntent.putExtra("lesson_completed", true);
        // resultIntent.putExtra("new_progress", 80);
        // setResult(RESULT_OK, resultIntent);
        // finish();
        //
        // Then in SagaMapActivity, start lessons with:
        // startActivityForResult(intent, REQUEST_CODE_LESSON);
        //
        // And handle result in onActivityResult():
        // @Override
        // protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //     super.onActivityResult(requestCode, resultCode, data);
        //     if (requestCode == REQUEST_CODE_LESSON && resultCode == RESULT_OK) {
        //         boolean completed = data.getBooleanExtra("lesson_completed", false);
        //         if (completed) {
        //             int newProgress = data.getIntExtra("new_progress", currentProgress);
        //             updateProgress(newProgress);
        //             // Show celebration animation
        //         }
        //     }
        // }
    }

    /**
     * Example 4: Add a "View Learning Map" button to HomeActivity layout
     * Add this to activity_home.xml:
     */
    public void addButtonToHomeLayout() {
        /*
        <Button
            android:id="@+id/btnViewSagaMap"
            android:layout_width="match_parent"
            android:layout_height="60dp"
            android:layout_margin="16dp"
            android:background="@drawable/bg_button_primary"
            android:text="View Learning Map"
            android:textColor="@color/white"
            android:textStyle="bold"
            android:textSize="16sp"
            android:drawableStart="@drawable/ic_nav_lessons"
            android:drawablePadding="8dp" />
        */
    }

    /**
     * Example 5: Save and load user progress with SharedPreferences
     * Add this to SagaMapActivity.java
     */
    public void saveAndLoadProgress() {
        /*
        // Save progress
        private void saveProgress(int progress) {
            SharedPreferences prefs = getSharedPreferences("UserProgress", MODE_PRIVATE);
            prefs.edit().putInt("overall_progress", progress).apply();
            prefs.edit().putLong("last_updated", System.currentTimeMillis()).apply();
        }
        
        // Load progress
        private int loadSavedProgress() {
            SharedPreferences prefs = getSharedPreferences("UserProgress", MODE_PRIVATE);
            return prefs.getInt("overall_progress", 0);
        }
        
        // Then call in onCreate:
        currentProgress = loadSavedProgress();
        */
    }

    /**
     * Example 6: Animate node unlock
     * Add this to SagaMapActivity.java
     */
    public void animateNodeUnlock() {
        /*
        import android.animation.ObjectAnimator;
        import android.view.animation.BounceInterpolator;
        
        public void unlockNodeWithAnimation(View nodeView) {
            // Scale animation
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(nodeView, "scaleX", 0.8f, 1.2f, 1.0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(nodeView, "scaleY", 0.8f, 1.2f, 1.0f);
            scaleX.setDuration(800);
            scaleY.setDuration(800);
            scaleX.setInterpolator(new BounceInterpolator());
            scaleY.setInterpolator(new BounceInterpolator());
            
            // Start animations
            scaleX.start();
            scaleY.start();
            
            // Play unlock sound
            // MediaPlayer.create(this, R.raw.unlock_sound).start();
        }
        */
    }

    /**
     * Example 7: Track analytics events
     * Add Firebase Analytics or similar
     */
    public void trackAnalytics() {
        /*
        import com.google.firebase.analytics.FirebaseAnalytics;
        
        private FirebaseAnalytics mFirebaseAnalytics;
        
        // In onCreate:
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        
        // Track screen view
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Saga Map");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "SagaMapActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
        
        // Track button clicks
        private void trackButtonClick(String buttonName) {
            Bundle bundle = new Bundle();
            bundle.putString("button_name", buttonName);
            bundle.putString("screen", "saga_map");
            mFirebaseAnalytics.logEvent("button_click", bundle);
        }
        */
    }

    /**
     * Example 8: Add bottom navigation to saga map
     * Uncomment the BottomNavigationView in saga_map.xml and add this:
     */
    public void setupBottomNavigation() {
        /*
        import com.google.android.material.bottomnavigation.BottomNavigationView;
        
        // In onCreate:
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setVisibility(View.VISIBLE);
        bottomNav.setSelectedItemId(R.id.nav_lessons);
        
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_lessons) {
                // Already on lessons screen
                return true;
            } else if (itemId == R.id.nav_games) {
                // Navigate to games
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Navigate to profile
                return true;
            }
            return false;
        });
        */
    }

    /**
     * Example 9: Add pull-to-refresh
     * Wrap content in SwipeRefreshLayout
     */
    public void addPullToRefresh() {
        /*
        // In saga_map.xml, wrap NestedScrollView with:
        <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
            android:id="@+id/swipeRefresh"
            android:layout_width="match_parent"
            android:layout_height="match_parent">
            
            <!-- Your NestedScrollView here -->
            
        </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
        
        // In SagaMapActivity:
        SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            loadUserProgress();
            swipeRefresh.setRefreshing(false);
        });
        */
    }

    /**
     * Example 10: Dynamic node creation with RecyclerView
     * For unlimited nodes
     */
    public void useDynamicNodes() {
        /*
        // Create a model class:
        public class SagaNode {
            public int id;
            public String title;
            public int iconResId;
            public boolean isUnlocked;
            public boolean isCurrent;
        }
        
        // Create adapter:
        public class SagaNodeAdapter extends RecyclerView.Adapter<SagaNodeAdapter.ViewHolder> {
            private List<SagaNode> nodes;
            
            // Implement adapter methods...
        }
        
        // Replace LinearLayout with RecyclerView in saga_map.xml:
        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rvSagaNodes"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />
        */
    }
}
