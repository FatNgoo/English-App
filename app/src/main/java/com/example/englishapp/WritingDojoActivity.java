package com.example.englishapp;


import com.shop.englishapp.R;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class WritingDojoActivity extends AppCompatActivity {

    private TracingCanvasView tracingCanvas;
    private TextView tvPreviewLetter, tvLetterOutline, tvExampleWord;
    private ImageButton btnBack, btnPlayPronunciation, btnPlayExample;
    private ImageButton btnPrevLetter, btnNextLetterIcon;
    private Button btnNextLetter;
    private LinearLayout btnReplayStroke, btnShowStrokeOrder, btnErase;
    private LinearLayout layoutSuccess;
    private ImageView imgSparkles;
    private View dot1, dot2, dot3, dot4;
    
    private Handler handler = new Handler();
    private String[] letters = {"A", "B", "C", "D"};
    private String[] exampleWords = {"A - Apple", "B - Banana", "C - Cat", "D - Dog"};
    private int currentLetterIndex = 0;
    private boolean isTracing = false;
    private boolean letterCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writing_dojo);

        initializeViews();
        setupButtons();
        loadCurrentLetter();
    }

    private void initializeViews() {
        tracingCanvas = findViewById(R.id.tracingCanvas);
        tvPreviewLetter = findViewById(R.id.tvPreviewLetter);
        tvLetterOutline = findViewById(R.id.tvLetterOutline);
        tvExampleWord = findViewById(R.id.tvExampleWord);
        
        btnBack = findViewById(R.id.btnBack);
        btnPlayPronunciation = findViewById(R.id.btnPlayPronunciation);
        btnPlayExample = findViewById(R.id.btnPlayExample);
        btnPrevLetter = findViewById(R.id.btnPrevLetter);
        btnNextLetter = findViewById(R.id.btnNextLetter);
        btnNextLetterIcon = findViewById(R.id.btnNextLetterIcon);
        
        btnReplayStroke = findViewById(R.id.btnReplayStroke);
        btnShowStrokeOrder = findViewById(R.id.btnShowStrokeOrder);
        btnErase = findViewById(R.id.btnErase);
        
        layoutSuccess = findViewById(R.id.layoutSuccess);
        imgSparkles = findViewById(R.id.imgSparkles);
        
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);
        dot4 = findViewById(R.id.dot4);
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());
        
        btnPlayPronunciation.setOnClickListener(v -> {
            animateButton(btnPlayPronunciation);
            playPronunciation();
        });
        
        btnPlayExample.setOnClickListener(v -> {
            animateButton(btnPlayExample);
            playPronunciation();
        });
        
        btnReplayStroke.setOnClickListener(v -> {
            animateButton(btnReplayStroke);
            replayStroke();
        });
        
        btnShowStrokeOrder.setOnClickListener(v -> {
            animateButton(btnShowStrokeOrder);
            showStrokeOrder();
        });
        
        btnErase.setOnClickListener(v -> {
            animateButton(btnErase);
            eraseCanvas();
        });
        
        btnPrevLetter.setOnClickListener(v -> {
            if (currentLetterIndex > 0) {
                currentLetterIndex--;
                loadCurrentLetter();
            }
        });
        
        btnNextLetter.setOnClickListener(v -> goToNextLetter());
        btnNextLetterIcon.setOnClickListener(v -> goToNextLetter());
    }

    private void loadCurrentLetter() {
        String letter = letters[currentLetterIndex];
        tvPreviewLetter.setText(letter);
        tvLetterOutline.setText(letter);
        tvExampleWord.setText(exampleWords[currentLetterIndex]);
        
        eraseCanvas();
        layoutSuccess.setVisibility(View.GONE);
        letterCompleted = false;
        
        updateProgressDots();
        
        // Animate letter appearance
        tvPreviewLetter.setAlpha(0f);
        tvPreviewLetter.setScaleX(0.5f);
        tvPreviewLetter.setScaleY(0.5f);
        tvPreviewLetter.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .start();
    }

    private void updateProgressDots() {
        View[] dots = {dot1, dot2, dot3, dot4};
        for (int i = 0; i < dots.length; i++) {
            if (i == currentLetterIndex) {
                dots[i].setBackgroundResource(R.drawable.bg_progress_dot_active);
                animateBounce(dots[i]);
            } else {
                dots[i].setBackgroundResource(R.drawable.bg_progress_dot_inactive);
            }
        }
    }

    private void goToNextLetter() {
        if (currentLetterIndex < letters.length - 1) {
            currentLetterIndex++;
            loadCurrentLetter();
        }
    }

    private void playPronunciation() {
        // Simulate pronunciation animation
        animateBounce(tvPreviewLetter);
        
        // Flash the letter
        tvPreviewLetter.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(200)
            .withEndAction(() -> {
                tvPreviewLetter.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start();
            })
            .start();
    }

    private void replayStroke() {
        // Animate stroke order demonstration
        tracingCanvas.clear();
        tracingCanvas.showStrokeDemo();
    }

    private void showStrokeOrder() {
        // Show stroke order arrows
        tracingCanvas.toggleStrokeOrderGuide();
    }

    private void eraseCanvas() {
        tracingCanvas.clear();
        letterCompleted = false;
        layoutSuccess.setVisibility(View.GONE);
    }

    private void onLetterCompleted() {
        if (!letterCompleted) {
            letterCompleted = true;
            showSuccessAnimation();
        }
    }

    private void showSuccessAnimation() {
        // Show sparkles
        imgSparkles.setVisibility(View.VISIBLE);
        imgSparkles.setAlpha(0f);
        imgSparkles.setScaleX(0.5f);
        imgSparkles.setScaleY(0.5f);
        
        imgSparkles.animate()
            .alpha(1f)
            .scaleX(1.5f)
            .scaleY(1.5f)
            .setDuration(800)
            .withEndAction(() -> {
                imgSparkles.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> imgSparkles.setVisibility(View.GONE))
                    .start();
            })
            .start();
        
        // Show success message
        handler.postDelayed(() -> {
            layoutSuccess.setVisibility(View.VISIBLE);
            layoutSuccess.setAlpha(0f);
            layoutSuccess.setScaleX(0.8f);
            layoutSuccess.setScaleY(0.8f);
            
            layoutSuccess.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .start();
        }, 500);
        
        // Animate letter outline with color
        animateLetterSuccess();
    }

    private void animateLetterSuccess() {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(tvLetterOutline, "rotation", 0f, 360f);
        rotate.setDuration(1000);
        rotate.start();
        
        handler.postDelayed(() -> {
            tvLetterOutline.setRotation(0f);
        }, 1000);
    }

    private void animateButton(View button) {
        button.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction(() -> {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start();
            })
            .start();
    }

    private void animateBounce(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f);
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        scaleX.start();
        scaleY.start();
    }

    // Custom Canvas View for Tracing
    private class TracingCanvasView extends View {
        private Paint paint;
        private Paint glowPaint;
        private Path path;
        private List<Path> paths;
        private boolean showStrokeGuide = false;
        private float lastX, lastY;

        public TracingCanvasView(Context context) {
            super(context);
            init();
        }

        private void init() {
            paint = new Paint();
            paint.setColor(Color.parseColor("#4CAF50"));
            paint.setStrokeWidth(20f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setAntiAlias(true);
            
            glowPaint = new Paint();
            glowPaint.setColor(Color.parseColor("#80A8E6CF"));
            glowPaint.setStrokeWidth(30f);
            glowPaint.setStyle(Paint.Style.STROKE);
            glowPaint.setStrokeJoin(Paint.Join.ROUND);
            glowPaint.setStrokeCap(Paint.Cap.ROUND);
            glowPaint.setAntiAlias(true);
            
            path = new Path();
            paths = new ArrayList<>();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            
            // Draw all completed paths
            for (Path p : paths) {
                canvas.drawPath(p, glowPaint);
                canvas.drawPath(p, paint);
            }
            
            // Draw current path with glow
            canvas.drawPath(path, glowPaint);
            canvas.drawPath(path, paint);
            
            // Draw stroke order guide if enabled
            if (showStrokeGuide) {
                drawStrokeOrderGuide(canvas);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x, y);
                    lastX = x;
                    lastY = y;
                    isTracing = true;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    path.lineTo(x, y);
                    invalidate();
                    
                    // Check if stroke is approximately correct
                    if (isTracing && isStrokeValid(x, y)) {
                        // Good stroke - green glow
                        paint.setColor(Color.parseColor("#4CAF50"));
                    }
                    
                    lastX = x;
                    lastY = y;
                    return true;

                case MotionEvent.ACTION_UP:
                    paths.add(new Path(path));
                    path.reset();
                    isTracing = false;
                    
                    // Check if letter is complete
                    if (paths.size() >= 2) {
                        handler.postDelayed(() -> onLetterCompleted(), 300);
                    }
                    return true;
            }
            return super.onTouchEvent(event);
        }

        private boolean isStrokeValid(float x, float y) {
            // Simple validation - check if stroke is within reasonable bounds
            // In a real app, this would use stroke recognition algorithms
            return true;
        }

        private void drawStrokeOrderGuide(Canvas canvas) {
            Paint arrowPaint = new Paint();
            arrowPaint.setColor(Color.parseColor("#FF9ECD"));
            arrowPaint.setStrokeWidth(8f);
            arrowPaint.setStyle(Paint.Style.STROKE);
            arrowPaint.setAntiAlias(true);
            
            // Draw simple arrow guide (example for letter A)
            Path arrowPath = new Path();
            arrowPath.moveTo(getWidth() / 2 - 100, getHeight() / 2 + 100);
            arrowPath.lineTo(getWidth() / 2, getHeight() / 2 - 100);
            canvas.drawPath(arrowPath, arrowPaint);
        }

        public void clear() {
            path.reset();
            paths.clear();
            invalidate();
        }

        public void toggleStrokeOrderGuide() {
            showStrokeGuide = !showStrokeGuide;
            invalidate();
        }

        public void showStrokeDemo() {
            // Animate stroke demonstration
            handler.postDelayed(() -> {
                Path demoPath = new Path();
                demoPath.moveTo(getWidth() / 2 - 100, getHeight() / 2 + 100);
                demoPath.lineTo(getWidth() / 2, getHeight() / 2 - 100);
                paths.add(demoPath);
                invalidate();
                
                handler.postDelayed(() -> {
                    Path demoPath2 = new Path();
                    demoPath2.moveTo(getWidth() / 2, getHeight() / 2 - 100);
                    demoPath2.lineTo(getWidth() / 2 + 100, getHeight() / 2 + 100);
                    paths.add(demoPath2);
                    invalidate();
                    
                    handler.postDelayed(() -> clear(), 2000);
                }, 500);
            }, 300);
        }
    }
}
