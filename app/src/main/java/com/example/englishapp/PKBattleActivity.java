package com.example.englishapp;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class PKBattleActivity extends AppCompatActivity {

    // Phase constants
    private static final int PHASE_MATCHMAKING = 0;
    private static final int PHASE_COUNTDOWN = 1;
    private static final int PHASE_QUIZ = 2;
    private static final int PHASE_RESULT = 3;

    // Current phase
    private int currentPhase = PHASE_MATCHMAKING;

    // Matchmaking views
    private RelativeLayout matchmakingLayout;
    private TextView txtMatchmakingStatus;
    private TextView txtLoadingDots;
    private ImageView imgPlayerAvatar, imgOpponentAvatar;
    private TextView txtPlayerName, txtOpponentName;
    private TextView txtPlayerLevel, txtOpponentLevel;
    private TextView txtOpponentPlaceholder;
    private ImageView imgMatchmakingSpinner;
    private FrameLayout btnCancel;
    private FrameLayout countdownOverlay;
    private TextView txtCountdown;

    // Quiz views
    private RelativeLayout quizLayout;
    private TextView txtQuestionCounter;
    private ProgressBar circularTimer;
    private TextView txtTimerSeconds;
    private ImageView imgPlayerScoreAvatar, imgOpponentScoreAvatar;
    private ProgressBar playerScoreBar, opponentScoreBar;
    private TextView txtPlayerScore, txtOpponentScore;
    private TextView txtQuestion;
    private FrameLayout answerChoiceA, answerChoiceB, answerChoiceC, answerChoiceD;
    private TextView txtAnswerA, txtAnswerB, txtAnswerC, txtAnswerD;
    private FrameLayout feedbackOverlay;
    private TextView txtFeedback;

    // Result views
    private View resultLayout;
    private LinearLayout victoryBanner, defeatBanner;
    private TextView txtPlayerFinalScore, txtOpponentFinalScore;
    private TextView txtAccuracy, txtXPGained, txtRankChange;
    private LinearLayout rankChangeContainer;
    private FrameLayout btnPlayAgain, btnReturnArena, btnShareResult;

    // Player data
    private String playerName;
    private int playerLevel;
    private String opponentName;
    private int opponentLevel;

    // Quiz data
    private int currentQuestionIndex = 0;
    private int playerScore = 0;
    private int opponentScore = 0;
    private int timerSeconds = 10;
    private boolean hasAnswered = false;
    private Handler quizHandler;
    private Runnable timerRunnable;
    private Random random;

    // Quiz questions
    private String[][] questions = {
        {"What is the English word for 'chó'?", "Dog", "Cat", "Bird", "Fish"},
        {"Which word means 'màu đỏ'?", "Red", "Blue", "Green", "Yellow"},
        {"What do you call 'quyển sách'?", "Book", "Pen", "Desk", "Bag"},
        {"Translate 'con gà':", "Chicken", "Duck", "Cow", "Pig"},
        {"What is 'nhà'?", "House", "Car", "Tree", "Road"},
        {"The word for 'nước' is:", "Water", "Fire", "Wind", "Earth"},
        {"What means 'ăn'?", "Eat", "Drink", "Sleep", "Run"},
        {"'Bạn' translates to:", "Friend", "Enemy", "Teacher", "Student"},
        {"What is 'lớn'?", "Big", "Small", "Tall", "Short"},
        {"The word for 'đẹp' is:", "Beautiful", "Ugly", "Old", "New"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize handlers
        quizHandler = new Handler();
        random = new Random();

        // Get player data
        Intent intent = getIntent();
        playerName = intent.getStringExtra("player_name");
        playerLevel = intent.getIntExtra("player_level", 12);

        // Start matchmaking phase
        showMatchmakingPhase();
    }

    private void showMatchmakingPhase() {
        setContentView(R.layout.pk_battle_matchmaking);
        currentPhase = PHASE_MATCHMAKING;

        // Initialize matchmaking views
        matchmakingLayout = findViewById(R.id.matchmakingLayout);
        txtMatchmakingStatus = findViewById(R.id.txtMatchmakingStatus);
        txtLoadingDots = findViewById(R.id.txtLoadingDots);
        imgPlayerAvatar = findViewById(R.id.imgPlayerAvatar);
        imgOpponentAvatar = findViewById(R.id.imgOpponentAvatar);
        txtPlayerName = findViewById(R.id.txtPlayerName);
        txtOpponentName = findViewById(R.id.txtOpponentName);
        txtPlayerLevel = findViewById(R.id.txtPlayerLevel);
        txtOpponentLevel = findViewById(R.id.txtOpponentLevel);
        txtOpponentPlaceholder = findViewById(R.id.txtOpponentPlaceholder);
        imgMatchmakingSpinner = findViewById(R.id.imgMatchmakingSpinner);
        btnCancel = findViewById(R.id.btnCancel);
        countdownOverlay = findViewById(R.id.countdownOverlay);
        txtCountdown = findViewById(R.id.txtCountdown);

        // Set player data
        txtPlayerName.setText(playerName);
        txtPlayerLevel.setText("Level " + playerLevel);

        // Animate loading dots
        animateLoadingDots();

        // Rotate matchmaking spinner
        ObjectAnimator spinnerAnimator = ObjectAnimator.ofFloat(imgMatchmakingSpinner, "rotation", 0f, 360f);
        spinnerAnimator.setDuration(2000);
        spinnerAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        spinnerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        spinnerAnimator.start();

        // Cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Find opponent after 3 seconds
        quizHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findOpponent();
            }
        }, 3000);
    }

    private void animateLoadingDots() {
        final String[] dots = {".", "..", "..."};
        final int[] index = {0};
        
        quizHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentPhase == PHASE_MATCHMAKING && txtLoadingDots != null) {
                    txtLoadingDots.setText(dots[index[0]]);
                    index[0] = (index[0] + 1) % dots.length;
                    quizHandler.postDelayed(this, 500);
                }
            }
        }, 500);
    }

    private void findOpponent() {
        // Generate random opponent
        String[] opponentNames = {"Tom", "Alice", "Mike", "Sarah", "David"};
        opponentName = opponentNames[random.nextInt(opponentNames.length)];
        opponentLevel = 10 + random.nextInt(15);

        // Update UI
        txtMatchmakingStatus.setText("Opponent Found!");
        txtOpponentPlaceholder.setVisibility(View.GONE);
        imgOpponentAvatar.setVisibility(View.VISIBLE);
        txtOpponentName.setText(opponentName);
        txtOpponentLevel.setText("Level " + opponentLevel);

        // Start countdown after 1 second
        quizHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startCountdown();
            }
        }, 1000);
    }

    private void startCountdown() {
        currentPhase = PHASE_COUNTDOWN;
        countdownOverlay.setVisibility(View.VISIBLE);

        final int[] countdown = {3};
        
        quizHandler.post(new Runnable() {
            @Override
            public void run() {
                if (countdown[0] > 0) {
                    txtCountdown.setText(String.valueOf(countdown[0]));
                    animateCountdownNumber();
                    countdown[0]--;
                    quizHandler.postDelayed(this, 1000);
                } else {
                    txtCountdown.setText("GO!");
                    txtCountdown.setTextColor(Color.parseColor("#FFD93D"));
                    animateCountdownNumber();
                    quizHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showQuizPhase();
                        }
                    }, 1000);
                }
            }
        });
    }

    private void animateCountdownNumber() {
        txtCountdown.setScaleX(0f);
        txtCountdown.setScaleY(0f);
        txtCountdown.animate()
                .scaleX(1.5f)
                .scaleY(1.5f)
                .setDuration(500)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        txtCountdown.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(500)
                                .start();
                    }
                })
                .start();
    }

    private void showQuizPhase() {
        setContentView(R.layout.pk_battle_quiz);
        currentPhase = PHASE_QUIZ;

        // Initialize quiz views
        initializeQuizViews();

        // Load first question
        loadQuestion();
    }

    private void initializeQuizViews() {
        txtQuestionCounter = findViewById(R.id.txtQuestionCounter);
        circularTimer = findViewById(R.id.circularTimer);
        txtTimerSeconds = findViewById(R.id.txtTimerSeconds);
        imgPlayerScoreAvatar = findViewById(R.id.imgPlayerScoreAvatar);
        imgOpponentScoreAvatar = findViewById(R.id.imgOpponentScoreAvatar);
        playerScoreBar = findViewById(R.id.playerScoreBar);
        opponentScoreBar = findViewById(R.id.opponentScoreBar);
        txtPlayerScore = findViewById(R.id.txtPlayerScore);
        txtOpponentScore = findViewById(R.id.txtOpponentScore);
        txtQuestion = findViewById(R.id.txtQuestion);
        answerChoiceA = findViewById(R.id.answerChoiceA);
        answerChoiceB = findViewById(R.id.answerChoiceB);
        answerChoiceC = findViewById(R.id.answerChoiceC);
        answerChoiceD = findViewById(R.id.answerChoiceD);
        txtAnswerA = findViewById(R.id.txtAnswerA);
        txtAnswerB = findViewById(R.id.txtAnswerB);
        txtAnswerC = findViewById(R.id.txtAnswerC);
        txtAnswerD = findViewById(R.id.txtAnswerD);
        feedbackOverlay = findViewById(R.id.feedbackOverlay);
        txtFeedback = findViewById(R.id.txtFeedback);

        // Setup answer listeners
        setupAnswerListeners();
    }

    private void setupAnswerListeners() {
        answerChoiceA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(0);
            }
        });

        answerChoiceB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(1);
            }
        });

        answerChoiceC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(2);
            }
        });

        answerChoiceD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(3);
            }
        });
    }

    private void loadQuestion() {
        if (currentQuestionIndex >= questions.length) {
            showResultPhase();
            return;
        }

        hasAnswered = false;
        timerSeconds = 10;

        // Update question counter
        txtQuestionCounter.setText("Question " + (currentQuestionIndex + 1) + "/10");

        // Set question and answers
        String[] questionData = questions[currentQuestionIndex];
        txtQuestion.setText(questionData[0]);
        txtAnswerA.setText(questionData[1]);
        txtAnswerB.setText(questionData[2]);
        txtAnswerC.setText(questionData[3]);
        txtAnswerD.setText(questionData[4]);

        // Reset answer backgrounds
        answerChoiceA.setBackgroundResource(R.drawable.bg_answer_choice);
        answerChoiceB.setBackgroundResource(R.drawable.bg_answer_choice);
        answerChoiceC.setBackgroundResource(R.drawable.bg_answer_choice);
        answerChoiceD.setBackgroundResource(R.drawable.bg_answer_choice);

        // Start timer
        startTimer();

        // Simulate opponent answer
        simulateOpponentAnswer();
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (timerSeconds > 0 && !hasAnswered) {
                    txtTimerSeconds.setText(String.valueOf(timerSeconds));
                    circularTimer.setProgress(timerSeconds);
                    timerSeconds--;
                    quizHandler.postDelayed(this, 1000);
                } else if (!hasAnswered) {
                    // Time's up
                    nextQuestion();
                }
            }
        };
        quizHandler.post(timerRunnable);
    }

    private void checkAnswer(int selectedIndex) {
        if (hasAnswered) return;
        hasAnswered = true;

        // Stop timer
        quizHandler.removeCallbacks(timerRunnable);

        boolean isCorrect = (selectedIndex == 0); // First answer is always correct

        if (isCorrect) {
            playerScore++;
            showCorrectFeedback();
        } else {
            showWrongFeedback();
        }

        // Update score bars
        updateScoreBars();

        // Next question after delay
        quizHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentQuestionIndex++;
                loadQuestion();
            }
        }, 2000);
    }

    private void simulateOpponentAnswer() {
        int delay = 2000 + random.nextInt(3000);
        quizHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!hasAnswered && currentPhase == PHASE_QUIZ) {
                    // 70% chance opponent answers correctly
                    boolean opponentCorrect = random.nextInt(100) < 70;
                    if (opponentCorrect) {
                        opponentScore++;
                        updateScoreBars();
                    }
                }
            }
        }, delay);
    }

    private void updateScoreBars() {
        txtPlayerScore.setText(playerScore + "/10");
        txtOpponentScore.setText(opponentScore + "/10");
        playerScoreBar.setProgress(playerScore);
        opponentScoreBar.setProgress(opponentScore);
    }

    private void showCorrectFeedback() {
        feedbackOverlay.setVisibility(View.VISIBLE);
        feedbackOverlay.setBackgroundColor(Color.parseColor("#804CAF50"));
        txtFeedback.setText("Perfect!");
        txtFeedback.setTextColor(Color.WHITE);

        quizHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                feedbackOverlay.setVisibility(View.GONE);
            }
        }, 1000);
    }

    private void showWrongFeedback() {
        feedbackOverlay.setVisibility(View.VISIBLE);
        feedbackOverlay.setBackgroundColor(Color.parseColor("#80FF6B6B"));
        txtFeedback.setText("Try again!");
        txtFeedback.setTextColor(Color.WHITE);

        quizHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                feedbackOverlay.setVisibility(View.GONE);
            }
        }, 1000);
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        loadQuestion();
    }

    private void showResultPhase() {
        setContentView(R.layout.battle_result);
        currentPhase = PHASE_RESULT;

        // Initialize result views
        initializeResultViews();

        // Calculate results
        calculateResults();
    }

    private void initializeResultViews() {
        victoryBanner = findViewById(R.id.victoryBanner);
        defeatBanner = findViewById(R.id.defeatBanner);
        txtPlayerFinalScore = findViewById(R.id.txtPlayerFinalScore);
        txtOpponentFinalScore = findViewById(R.id.txtOpponentFinalScore);
        txtAccuracy = findViewById(R.id.txtAccuracy);
        txtXPGained = findViewById(R.id.txtXPGained);
        txtRankChange = findViewById(R.id.txtRankChange);
        rankChangeContainer = findViewById(R.id.rankChangeContainer);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnReturnArena = findViewById(R.id.btnReturnArena);
        btnShareResult = findViewById(R.id.btnShareResult);

        // Setup button listeners
        btnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartBattle();
            }
        });

        btnReturnArena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnShareResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareResult();
            }
        });
    }

    private void calculateResults() {
        boolean isVictory = playerScore > opponentScore;

        // Show victory or defeat banner
        if (isVictory) {
            victoryBanner.setVisibility(View.VISIBLE);
            defeatBanner.setVisibility(View.GONE);
        } else {
            victoryBanner.setVisibility(View.GONE);
            defeatBanner.setVisibility(View.VISIBLE);
        }

        // Set scores
        txtPlayerFinalScore.setText(String.valueOf(playerScore));
        txtOpponentFinalScore.setText(String.valueOf(opponentScore));

        // Calculate accuracy
        int accuracy = (playerScore * 100) / 10;
        txtAccuracy.setText(accuracy + "%");

        // Calculate XP gained
        int baseXP = 100;
        int questionXP = playerScore * 10;
        int victoryBonus = isVictory ? 50 : 0;
        int totalXP = baseXP + questionXP + victoryBonus;
        txtXPGained.setText("+" + totalXP + " XP");

        // Update player stats
        SharedPreferences prefs = getSharedPreferences("EnglishAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int currentXP = prefs.getInt("current_xp", 650);
        editor.putInt("current_xp", currentXP + totalXP);
        
        int totalBattles = prefs.getInt("total_battles", 0) + 1;
        int totalWins = prefs.getInt("total_wins", 0) + (isVictory ? 1 : 0);
        int winRate = (totalWins * 100) / totalBattles;
        editor.putInt("total_battles", totalBattles);
        editor.putInt("total_wins", totalWins);
        editor.putInt("win_rate", winRate);
        editor.apply();
    }

    private void restartBattle() {
        // Reset variables
        currentQuestionIndex = 0;
        playerScore = 0;
        opponentScore = 0;
        
        // Restart from matchmaking
        showMatchmakingPhase();
    }

    private void shareResult() {
        android.widget.Toast.makeText(this, "Share feature coming soon!", android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (quizHandler != null) {
            quizHandler.removeCallbacksAndMessages(null);
        }
    }
}
