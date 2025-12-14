package com.example.englishapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class RobotFactoryActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack;
    private TextView txtLevelTitle;
    private TextView txtCurrentLevel;
    private GridLayout mazeGrid;
    private ImageView imgRobot;
    private ImageView imgGoalFlag;
    private ImageView imgSparkles;
    private ImageView imgWrong;
    private View ghostPath1, ghostPath2, ghostPath3;
    private LinearLayout sequenceContainer;
    private FrameLayout btnRun;
    private FrameLayout btnReset;
    private FrameLayout btnStep;
    private FrameLayout btnHint;
    private Button btnVocabForward, btnVocabLeft, btnVocabRight;
    private FrameLayout toolForward, toolTurnLeft, toolTurnRight, toolLoop;
    private LinearLayout challengeInfo;
    private TextView txtTimer;
    private TextView txtBonusCoins;
    private FrameLayout completionOverlay;
    private ImageView imgCelebrationRobot;
    private LinearLayout perfectBadgeContainer;
    private Button btnNextLevel;
    private BottomNavigationView bottomNavigation;

    // Command Slots
    private FrameLayout commandSlot1, commandSlot2, commandSlot3, commandSlot4, commandSlot5, commandSlot6;
    private List<FrameLayout> commandSlots;

    // Game State
    private int[][] maze;
    private int gridSize = 6;
    private int robotRow = 0;
    private int robotCol = 0;
    private int goalRow = 5;
    private int goalCol = 5;
    private int robotDirection = 0; // 0=North, 1=East, 2=South, 3=West
    private List<String> commandSequence;
    private boolean isRunning = false;
    private int currentLevel = 3;
    private int commandCount = 0;
    private int optimalCommands = 8;
    private Handler handler = new Handler();

    // Maze tile types
    private static final int EMPTY = 0;
    private static final int WALL = 1;
    private static final int START = 2;
    private static final int GOAL = 3;

    // Command types
    private static final String CMD_FORWARD = "forward";
    private static final String CMD_LEFT = "left";
    private static final String CMD_RIGHT = "right";
    private static final String CMD_LOOP = "loop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.robot_factory);

        initializeViews();
        initializeMaze();
        setupDragListeners();
        setupClickListeners();
        drawMaze();
        positionRobot();
    }

    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        txtLevelTitle = findViewById(R.id.txtLevelTitle);
        txtCurrentLevel = findViewById(R.id.txtCurrentLevel);

        // Maze
        mazeGrid = findViewById(R.id.mazeGrid);
        imgRobot = findViewById(R.id.imgRobot);
        imgGoalFlag = findViewById(R.id.imgGoalFlag);
        imgSparkles = findViewById(R.id.imgSparkles);
        imgWrong = findViewById(R.id.imgWrong);
        ghostPath1 = findViewById(R.id.ghostPath1);
        ghostPath2 = findViewById(R.id.ghostPath2);
        ghostPath3 = findViewById(R.id.ghostPath3);

        // Sequence Panel
        sequenceContainer = findViewById(R.id.sequenceContainer);
        commandSlot1 = findViewById(R.id.commandSlot1);
        commandSlot2 = findViewById(R.id.commandSlot2);
        commandSlot3 = findViewById(R.id.commandSlot3);
        commandSlot4 = findViewById(R.id.commandSlot4);
        commandSlot5 = findViewById(R.id.commandSlot5);
        commandSlot6 = findViewById(R.id.commandSlot6);

        commandSlots = new ArrayList<>();
        commandSlots.add(commandSlot1);
        commandSlots.add(commandSlot2);
        commandSlots.add(commandSlot3);
        commandSlots.add(commandSlot4);
        commandSlots.add(commandSlot5);
        commandSlots.add(commandSlot6);

        // Control Buttons
        btnRun = findViewById(R.id.btnRun);
        btnReset = findViewById(R.id.btnReset);
        btnStep = findViewById(R.id.btnStep);
        btnHint = findViewById(R.id.btnHint);

        // Vocabulary Buttons
        btnVocabForward = findViewById(R.id.btnVocabForward);
        btnVocabLeft = findViewById(R.id.btnVocabLeft);
        btnVocabRight = findViewById(R.id.btnVocabRight);

        // Toolbox
        toolForward = findViewById(R.id.toolForward);
        toolTurnLeft = findViewById(R.id.toolTurnLeft);
        toolTurnRight = findViewById(R.id.toolTurnRight);
        toolLoop = findViewById(R.id.toolLoop);

        // Challenge Info
        challengeInfo = findViewById(R.id.challengeInfo);
        txtTimer = findViewById(R.id.txtTimer);
        txtBonusCoins = findViewById(R.id.txtBonusCoins);

        // Completion Overlay
        completionOverlay = findViewById(R.id.completionOverlay);
        imgCelebrationRobot = findViewById(R.id.imgCelebrationRobot);
        perfectBadgeContainer = findViewById(R.id.perfectBadgeContainer);
        btnNextLevel = findViewById(R.id.btnNextLevel);

        // Bottom Navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);

        commandSequence = new ArrayList<>();
    }

    private void initializeMaze() {
        // Simple maze for level 3
        maze = new int[][]{
                {START, EMPTY, WALL, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, WALL, EMPTY, WALL, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY},
                {WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, GOAL}
        };

        robotRow = 0;
        robotCol = 0;
        goalRow = 5;
        goalCol = 5;
        robotDirection = 0; // Facing North
        optimalCommands = 8;
    }

    private void drawMaze() {
        mazeGrid.removeAllViews();

        int tileSize = 350 / gridSize; // Approximate tile size

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                View tile = new View(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = tileSize;
                params.height = tileSize;
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                params.setMargins(2, 2, 2, 2);

                switch (maze[row][col]) {
                    case EMPTY:
                        tile.setBackgroundResource(R.drawable.bg_maze_tile);
                        break;
                    case WALL:
                        tile.setBackgroundResource(R.drawable.bg_maze_wall);
                        break;
                    case START:
                        tile.setBackgroundResource(R.drawable.bg_maze_start);
                        break;
                    case GOAL:
                        tile.setBackgroundResource(R.drawable.bg_maze_goal);
                        break;
                }

                mazeGrid.addView(tile, params);
            }
        }
    }

    private void positionRobot() {
        int tileSize = 350 / gridSize;
        int marginX = 12 + (robotCol * (tileSize + 4));
        int marginY = 12 + (robotRow * (tileSize + 4));

        FrameLayout.LayoutParams robotParams = (FrameLayout.LayoutParams) imgRobot.getLayoutParams();
        robotParams.leftMargin = marginX;
        robotParams.topMargin = marginY;
        imgRobot.setLayoutParams(robotParams);

        // Position goal flag
        int goalMarginX = 12 + (goalCol * (tileSize + 4));
        int goalMarginY = 12 + (goalRow * (tileSize + 4));

        FrameLayout.LayoutParams goalParams = (FrameLayout.LayoutParams) imgGoalFlag.getLayoutParams();
        goalParams.leftMargin = goalMarginX;
        goalParams.topMargin = goalMarginY;
        imgGoalFlag.setLayoutParams(goalParams);

        // Update robot rotation based on direction
        imgRobot.setRotation(robotDirection * 90);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnRun.setOnClickListener(v -> runProgram());

        btnReset.setOnClickListener(v -> resetRobot());

        btnStep.setOnClickListener(v -> stepProgram());

        btnHint.setOnClickListener(v -> showHint());

        btnVocabForward.setOnClickListener(v -> playVocabAudio("forward"));

        btnVocabLeft.setOnClickListener(v -> playVocabAudio("left"));

        btnVocabRight.setOnClickListener(v -> playVocabAudio("right"));

        btnNextLevel.setOnClickListener(v -> {
            hideCompletionOverlay();
            loadNextLevel();
        });

        bottomNavigation.setOnItemSelectedListener(item -> true);
    }

    private void setupDragListeners() {
        // Setup drag on toolbox items
        List<FrameLayout> tools = new ArrayList<>();
        tools.add(toolForward);
        tools.add(toolTurnLeft);
        tools.add(toolTurnRight);
        tools.add(toolLoop);

        for (FrameLayout tool : tools) {
            tool.setOnLongClickListener(v -> {
                String commandType = getCommandType(v.getId());
                ClipData data = ClipData.newPlainText("command", commandType);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                animateToolLift(v, true);
                return true;
            });
        }

        // Setup drop on command slots
        for (int i = 0; i < commandSlots.size(); i++) {
            final int slotIndex = i;
            FrameLayout slot = commandSlots.get(i);

            slot.setOnDragListener((v, event) -> {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.setBackgroundResource(R.drawable.bg_command_highlight);
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setBackgroundResource(R.drawable.bg_command_slot);
                        return true;

                    case DragEvent.ACTION_DROP:
                        ClipData clipData = event.getClipData();
                        String commandType = clipData.getItemAt(0).getText().toString();
                        addCommandToSlot(slotIndex, commandType);
                        v.setBackgroundResource(R.drawable.bg_command_slot);
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        View draggedView = (View) event.getLocalState();
                        animateToolLift(draggedView, false);
                        return true;
                }
                return false;
            });

            // Long press on slot to delete
            slot.setOnLongClickListener(v -> {
                removeCommandFromSlot(slotIndex);
                return true;
            });
        }
    }

    private String getCommandType(int viewId) {
        if (viewId == R.id.toolForward) return CMD_FORWARD;
        if (viewId == R.id.toolTurnLeft) return CMD_LEFT;
        if (viewId == R.id.toolTurnRight) return CMD_RIGHT;
        if (viewId == R.id.toolLoop) return CMD_LOOP;
        return "";
    }

    private void addCommandToSlot(int slotIndex, String commandType) {
        if (slotIndex >= commandSequence.size()) {
            commandSequence.add(commandType);
        } else {
            commandSequence.set(slotIndex, commandType);
        }

        updateSlotUI(slotIndex, commandType);
        animateBounce(commandSlots.get(slotIndex));
    }

    private void removeCommandFromSlot(int slotIndex) {
        if (slotIndex < commandSequence.size()) {
            commandSequence.remove(slotIndex);
            refreshAllSlots();
        }
    }

    private void updateSlotUI(int slotIndex, String commandType) {
        FrameLayout slot = commandSlots.get(slotIndex);
        slot.removeAllViews();

        ImageView icon = new ImageView(this);
        icon.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setPadding(20, 20, 20, 20);

        switch (commandType) {
            case CMD_FORWARD:
                slot.setBackgroundResource(R.drawable.bg_command_forward);
                icon.setImageResource(R.drawable.ic_arrow_forward);
                break;
            case CMD_LEFT:
                slot.setBackgroundResource(R.drawable.bg_command_left);
                icon.setImageResource(R.drawable.ic_turn_left);
                break;
            case CMD_RIGHT:
                slot.setBackgroundResource(R.drawable.bg_command_right);
                icon.setImageResource(R.drawable.ic_turn_right);
                break;
            case CMD_LOOP:
                slot.setBackgroundResource(R.drawable.bg_command_loop);
                icon.setImageResource(R.drawable.ic_loop);
                break;
        }

        slot.addView(icon);
    }

    private void refreshAllSlots() {
        for (int i = 0; i < commandSlots.size(); i++) {
            FrameLayout slot = commandSlots.get(i);
            slot.removeAllViews();
            slot.setBackgroundResource(R.drawable.bg_command_slot);

            TextView number = new TextView(this);
            number.setText(String.valueOf(i + 1));
            number.setTextSize(24);
            number.setTextColor(Color.parseColor("#BDBDBD"));
            number.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.Gravity.CENTER));
            slot.addView(number);

            if (i < commandSequence.size()) {
                updateSlotUI(i, commandSequence.get(i));
            }
        }
    }

    private void runProgram() {
        if (isRunning || commandSequence.isEmpty()) {
            return;
        }

        isRunning = true;
        commandCount = commandSequence.size();
        executeCommands(0);
    }

    private void executeCommands(int index) {
        if (index >= commandSequence.size()) {
            isRunning = false;
            checkGoalReached();
            return;
        }

        String command = commandSequence.get(index);
        highlightCurrentCommand(index);

        handler.postDelayed(() -> {
            boolean success = executeCommand(command);

            if (!success) {
                isRunning = false;
                showWrongAnimation();
                return;
            }

            if (checkGoalReached()) {
                isRunning = false;
                return;
            }

            executeCommands(index + 1);
        }, 600);
    }

    private boolean executeCommand(String command) {
        switch (command) {
            case CMD_FORWARD:
                return moveForward();
            case CMD_LEFT:
                turnLeft();
                return true;
            case CMD_RIGHT:
                turnRight();
                return true;
            case CMD_LOOP:
                // Simple loop implementation (repeat last command)
                return true;
        }
        return false;
    }

    private boolean moveForward() {
        int newRow = robotRow;
        int newCol = robotCol;

        switch (robotDirection) {
            case 0: // North
                newRow--;
                break;
            case 1: // East
                newCol++;
                break;
            case 2: // South
                newRow++;
                break;
            case 3: // West
                newCol--;
                break;
        }

        // Check boundaries
        if (newRow < 0 || newRow >= gridSize || newCol < 0 || newCol >= gridSize) {
            return false;
        }

        // Check walls
        if (maze[newRow][newCol] == WALL) {
            return false;
        }

        // Move robot
        robotRow = newRow;
        robotCol = newCol;
        animateRobotMove();
        showSparkles();

        return true;
    }

    private void turnLeft() {
        robotDirection = (robotDirection - 1 + 4) % 4;
        animateRobotRotate();
    }

    private void turnRight() {
        robotDirection = (robotDirection + 1) % 4;
        animateRobotRotate();
    }

    private boolean checkGoalReached() {
        if (robotRow == goalRow && robotCol == goalCol) {
            handler.postDelayed(this::showCompletionOverlay, 500);
            return true;
        }
        return false;
    }

    private void stepProgram() {
        // Execute one command at a time
        if (commandSequence.isEmpty()) {
            return;
        }

        if (!isRunning) {
            isRunning = true;
            executeCommands(0);
        }
    }

    private void resetRobot() {
        robotRow = 0;
        robotCol = 0;
        robotDirection = 0;
        isRunning = false;
        positionRobot();
        animateBounce(imgRobot);
    }

    private void showHint() {
        // Show ghost path for optimal solution
        animatePulse(btnHint);

        // Highlight suggested next command
        if (commandSequence.isEmpty()) {
            highlightTool(toolForward);
        } else {
            highlightTool(toolTurnRight);
        }

        // Show ghost path tiles
        showGhostPath();
    }

    private void showGhostPath() {
        ghostPath1.setVisibility(View.VISIBLE);
        ghostPath2.setVisibility(View.VISIBLE);
        ghostPath3.setVisibility(View.VISIBLE);

        int tileSize = 350 / gridSize;

        // Position ghost paths (example: next 3 steps)
        FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) ghostPath1.getLayoutParams();
        params1.leftMargin = 12 + (1 * (tileSize + 4));
        params1.topMargin = 12 + (0 * (tileSize + 4));
        ghostPath1.setLayoutParams(params1);

        FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) ghostPath2.getLayoutParams();
        params2.leftMargin = 12 + (1 * (tileSize + 4));
        params2.topMargin = 12 + (1 * (tileSize + 4));
        ghostPath2.setLayoutParams(params2);

        FrameLayout.LayoutParams params3 = (FrameLayout.LayoutParams) ghostPath3.getLayoutParams();
        params3.leftMargin = 12 + (1 * (tileSize + 4));
        params3.topMargin = 12 + (2 * (tileSize + 4));
        ghostPath3.setLayoutParams(params3);

        animatePulse(ghostPath1);
        animatePulse(ghostPath2);
        animatePulse(ghostPath3);

        handler.postDelayed(() -> {
            ghostPath1.setVisibility(View.INVISIBLE);
            ghostPath2.setVisibility(View.INVISIBLE);
            ghostPath3.setVisibility(View.INVISIBLE);
        }, 3000);
    }

    private void highlightTool(FrameLayout tool) {
        ObjectAnimator glow = ObjectAnimator.ofFloat(tool, "alpha", 1f, 0.5f, 1f);
        glow.setDuration(600);
        glow.setRepeatCount(2);
        glow.start();
    }

    private void highlightCurrentCommand(int index) {
        if (index < commandSlots.size()) {
            FrameLayout slot = commandSlots.get(index);
            animatePulse(slot);
        }
    }

    private void showCompletionOverlay() {
        completionOverlay.setVisibility(View.VISIBLE);
        completionOverlay.setAlpha(0f);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(completionOverlay, "alpha", 0f, 1f);
        fadeIn.setDuration(400);
        fadeIn.start();

        // Check if perfect route
        if (commandCount <= optimalCommands) {
            perfectBadgeContainer.setVisibility(View.VISIBLE);
            animateBounce(perfectBadgeContainer);
        }

        // Animate celebration robot
        animateRobotCelebration();
    }

    private void hideCompletionOverlay() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(completionOverlay, "alpha", 1f, 0f);
        fadeOut.setDuration(300);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                completionOverlay.setVisibility(View.INVISIBLE);
                perfectBadgeContainer.setVisibility(View.GONE);
            }
        });
        fadeOut.start();
    }

    private void loadNextLevel() {
        currentLevel++;
        commandSequence.clear();
        refreshAllSlots();
        initializeMaze();
        drawMaze();
        resetRobot();
    }

    private void playVocabAudio(String word) {
        // Simulate audio playback with animation
        Button btn = null;
        switch (word) {
            case "forward":
                btn = btnVocabForward;
                break;
            case "left":
                btn = btnVocabLeft;
                break;
            case "right":
                btn = btnVocabRight;
                break;
        }

        if (btn != null) {
            animatePulse(btn);
        }
    }

    // Animation Methods
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

    private void animateBounce(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(400);
        set.start();
    }

    private void animatePulse(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.15f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.15f, 1f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(600);
        set.setRepeatCount(2);
        set.start();
    }

    private void animateRobotMove() {
        positionRobot();
        ObjectAnimator moveAnim = ObjectAnimator.ofFloat(imgRobot, "alpha", 0.7f, 1f);
        moveAnim.setDuration(400);
        moveAnim.start();
    }

    private void animateRobotRotate() {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(imgRobot, "rotation", 
                imgRobot.getRotation(), robotDirection * 90);
        rotation.setDuration(400);
        rotation.start();
    }

    private void showSparkles() {
        imgSparkles.setX(imgRobot.getX());
        imgSparkles.setY(imgRobot.getY());
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
        sparkleSet.setDuration(600);
        sparkleSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imgSparkles.setVisibility(View.INVISIBLE);
            }
        });
        sparkleSet.start();
    }

    private void showWrongAnimation() {
        imgWrong.setX(imgRobot.getX() + 5);
        imgWrong.setY(imgRobot.getY() - 20);
        imgWrong.setVisibility(View.VISIBLE);
        imgWrong.setAlpha(1f);

        ObjectAnimator shake1 = ObjectAnimator.ofFloat(imgRobot, "translationX", 0f, -10f);
        ObjectAnimator shake2 = ObjectAnimator.ofFloat(imgRobot, "translationX", -10f, 10f);
        ObjectAnimator shake3 = ObjectAnimator.ofFloat(imgRobot, "translationX", 10f, 0f);

        AnimatorSet shakeSet = new AnimatorSet();
        shakeSet.playSequentially(shake1, shake2, shake3);
        shakeSet.setDuration(300);
        shakeSet.start();

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(imgWrong, "alpha", 1f, 0f);
        fadeOut.setDuration(800);
        fadeOut.setStartDelay(400);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imgWrong.setVisibility(View.INVISIBLE);
            }
        });
        fadeOut.start();
    }

    private void animateRobotCelebration() {
        ObjectAnimator spin = ObjectAnimator.ofFloat(imgCelebrationRobot, "rotation", 0f, 360f);
        spin.setDuration(1500);
        spin.setRepeatCount(ObjectAnimator.INFINITE);
        spin.start();

        ObjectAnimator bounce1 = ObjectAnimator.ofFloat(imgCelebrationRobot, "scaleX", 1f, 1.2f);
        ObjectAnimator bounce2 = ObjectAnimator.ofFloat(imgCelebrationRobot, "scaleY", 1f, 1.2f);

        AnimatorSet bounceSet = new AnimatorSet();
        bounceSet.playTogether(bounce1, bounce2);
        bounceSet.setDuration(600);
        bounceSet.setRepeatCount(ObjectAnimator.INFINITE);
        bounceSet.setRepeatMode(ObjectAnimator.REVERSE);
        bounceSet.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
