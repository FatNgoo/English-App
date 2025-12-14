package com.example.englishapp;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AIBuddyChatActivity extends AppCompatActivity {

    private LinearLayout chatContainer;
    private ScrollView chatScrollView;
    private Button btnOption1, btnOption2, btnOption3;
    private ImageButton btnVoiceInput;
    private View micButtonFrame;
    private TextView tvBuddyStatus;
    
    private LinearLayout cardZoo, cardSupermarket, cardSchool, cardDaily;
    private LinearLayout btnVocab, btnHint, btnSlowMode;
    
    private String currentScenario = "zoo";
    private boolean isListening = false;
    private boolean slowModeEnabled = false;
    private Handler handler = new Handler();
    
    // Scenario data
    private Map<String, ScenarioData> scenarios = new HashMap<>();
    
    private static class ScenarioData {
        String welcome;
        List<ResponseOption> options;
        
        ScenarioData(String welcome, List<ResponseOption> options) {
            this.welcome = welcome;
            this.options = options;
        }
    }
    
    private static class ResponseOption {
        String text;
        String aiResponse;
        
        ResponseOption(String text, String aiResponse) {
            this.text = text;
            this.aiResponse = aiResponse;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_buddy_chat);

        initializeViews();
        initializeScenarios();
        setupScenarioCards();
        setupResponseButtons();
        setupVoiceButton();
        setupLearningTools();
    }

    private void initializeViews() {
        chatContainer = findViewById(R.id.chatContainer);
        chatScrollView = findViewById(R.id.chatScrollView);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnVoiceInput = findViewById(R.id.btnVoiceInput);
        micButtonFrame = findViewById(R.id.micButtonFrame);
        tvBuddyStatus = findViewById(R.id.tvBuddyStatus);
        
        cardZoo = findViewById(R.id.cardZoo);
        cardSupermarket = findViewById(R.id.cardSupermarket);
        cardSchool = findViewById(R.id.cardSchool);
        cardDaily = findViewById(R.id.cardDaily);
        
        btnVocab = findViewById(R.id.btnVocab);
        btnHint = findViewById(R.id.btnHint);
        btnSlowMode = findViewById(R.id.btnSlowMode);
    }

    private void initializeScenarios() {
        // Zoo scenario
        List<ResponseOption> zooOptions = new ArrayList<>();
        zooOptions.add(new ResponseOption(
            "I see a big elephant",
            "Great! Elephants are huge! They have long trunks. Can you make an elephant sound?"
        ));
        zooOptions.add(new ResponseOption(
            "There are monkeys playing",
            "Wonderful! Monkeys love to play and swing. What are they doing?"
        ));
        zooOptions.add(new ResponseOption(
            "A lion is sleeping",
            "Shhh! Yes, the lion is taking a nap. Lions sleep a lot during the day!"
        ));
        scenarios.put("zoo", new ScenarioData(
            "Hi! I'm your AI Buddy. Let's explore the zoo together! What do you see?",
            zooOptions
        ));
        
        // Supermarket scenario
        List<ResponseOption> marketOptions = new ArrayList<>();
        marketOptions.add(new ResponseOption(
            "I need to buy some apples",
            "Good choice! Apples are healthy. How many apples do you want?"
        ));
        marketOptions.add(new ResponseOption(
            "Where is the milk section?",
            "The milk is in the dairy section. Let's go there together!"
        ));
        marketOptions.add(new ResponseOption(
            "How much is this bread?",
            "This bread costs 3 dollars. Would you like to buy it?"
        ));
        scenarios.put("supermarket", new ScenarioData(
            "Welcome to the supermarket! What would you like to buy today?",
            marketOptions
        ));
        
        // School scenario
        List<ResponseOption> schoolOptions = new ArrayList<>();
        schoolOptions.add(new ResponseOption(
            "I have a math class",
            "Math is fun! What are you learning today? Addition or subtraction?"
        ));
        schoolOptions.add(new ResponseOption(
            "Can I borrow a pencil?",
            "Of course! Here's a pencil. Don't forget to return it!"
        ));
        schoolOptions.add(new ResponseOption(
            "It's lunch time",
            "Great! What's in your lunchbox today? I hope it's yummy!"
        ));
        scenarios.put("school", new ScenarioData(
            "Good morning! Welcome to school. How are you feeling today?",
            schoolOptions
        ));
        
        // Daily life scenario
        List<ResponseOption> dailyOptions = new ArrayList<>();
        dailyOptions.add(new ResponseOption(
            "Good morning!",
            "Good morning! Did you sleep well? What's your plan for today?"
        ));
        dailyOptions.add(new ResponseOption(
            "I'm feeling happy",
            "That's wonderful! Happiness is the best feeling. What made you happy?"
        ));
        dailyOptions.add(new ResponseOption(
            "Let's play a game",
            "Yes! I love games! What game would you like to play?"
        ));
        scenarios.put("daily", new ScenarioData(
            "Hi friend! Let's chat about your day. How are you doing?",
            dailyOptions
        ));
    }

    private void setupScenarioCards() {
        cardZoo.setOnClickListener(v -> switchScenario("zoo", cardZoo));
        cardSupermarket.setOnClickListener(v -> switchScenario("supermarket", cardSupermarket));
        cardSchool.setOnClickListener(v -> switchScenario("school", cardSchool));
        cardDaily.setOnClickListener(v -> switchScenario("daily", cardDaily));
    }

    private void switchScenario(String scenario, LinearLayout selectedCard) {
        currentScenario = scenario;
        
        // Reset all card backgrounds
        cardZoo.setBackgroundResource(R.drawable.bg_scenario_card);
        cardSupermarket.setBackgroundResource(R.drawable.bg_scenario_card);
        cardSchool.setBackgroundResource(R.drawable.bg_scenario_card);
        cardDaily.setBackgroundResource(R.drawable.bg_scenario_card);
        
        // Highlight selected card
        selectedCard.setBackgroundResource(R.drawable.bg_scenario_selected);
        
        // Animate card selection
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(selectedCard, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(selectedCard, "scaleY", 1f, 1.1f, 1f);
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        scaleX.start();
        scaleY.start();
        
        // Clear chat and restart
        chatContainer.removeAllViews();
        ScenarioData scenarioData = scenarios.get(scenario);
        if (scenarioData != null) {
            addAIMessage(scenarioData.welcome);
            updateResponseOptions(scenarioData.options);
        }
    }

    private void setupResponseButtons() {
        btnOption1.setOnClickListener(v -> handleResponse(0));
        btnOption2.setOnClickListener(v -> handleResponse(1));
        btnOption3.setOnClickListener(v -> handleResponse(2));
    }

    private void handleResponse(int optionIndex) {
        ScenarioData scenarioData = scenarios.get(currentScenario);
        if (scenarioData != null && optionIndex < scenarioData.options.size()) {
            ResponseOption option = scenarioData.options.get(optionIndex);
            
            // Add user message
            addUserMessage(option.text);
            
            // Show typing indicator
            tvBuddyStatus.setText("AI Buddy is typing...");
            
            // Add AI response after delay
            handler.postDelayed(() -> {
                addAIMessage(option.aiResponse);
                tvBuddyStatus.setText("Online • Ready to chat");
                
                // Generate new random options
                generateNewOptions();
            }, slowModeEnabled ? 2000 : 1000);
        }
    }

    private void generateNewOptions() {
        // Simple random response generation for demo
        String[][] randomResponses = {
            {"Tell me more", "That's interesting!", "What else?"},
            {"I understand", "Can you explain?", "Show me more"},
            {"Yes, please", "No, thank you", "Maybe later"}
        };
        
        int randomSet = (int) (Math.random() * randomResponses.length);
        btnOption1.setText(randomResponses[randomSet][0]);
        btnOption2.setText(randomResponses[randomSet][1]);
        btnOption3.setText(randomResponses[randomSet][2]);
    }

    private void updateResponseOptions(List<ResponseOption> options) {
        if (options.size() >= 3) {
            btnOption1.setText(options.get(0).text);
            btnOption2.setText(options.get(1).text);
            btnOption3.setText(options.get(2).text);
        }
    }

    private void addAIMessage(String message) {
        LinearLayout messageLayout = new LinearLayout(this);
        messageLayout.setOrientation(LinearLayout.HORIZONTAL);
        messageLayout.setGravity(android.view.Gravity.START);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, dpToPx(12));
        messageLayout.setLayoutParams(layoutParams);
        
        // AI avatar
        ImageView avatar = new ImageView(this);
        avatar.setImageResource(R.drawable.ic_robot_happy);
        avatar.setBackgroundResource(R.drawable.bg_tool_button);
        avatar.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dpToPx(40), dpToPx(40));
        avatar.setLayoutParams(avatarParams);
        
        // Message bubble
        TextView messageText = new TextView(this);
        messageText.setText(message);
        messageText.setTextSize(15);
        messageText.setTextColor(getResources().getColor(R.color.text_primary));
        messageText.setBackgroundResource(R.drawable.bg_chat_ai);
        messageText.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.setMargins(dpToPx(12), 0, 0, 0);
        textParams.width = dpToPx(250);
        messageText.setLayoutParams(textParams);
        
        messageLayout.addView(avatar);
        messageLayout.addView(messageText);
        chatContainer.addView(messageLayout);
        
        // Animate message
        messageLayout.setAlpha(0f);
        messageLayout.setTranslationX(-50f);
        messageLayout.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(300)
            .start();
        
        // Scroll to bottom
        handler.postDelayed(() -> chatScrollView.fullScroll(View.FOCUS_DOWN), 100);
    }

    private void addUserMessage(String message) {
        LinearLayout messageLayout = new LinearLayout(this);
        messageLayout.setOrientation(LinearLayout.HORIZONTAL);
        messageLayout.setGravity(android.view.Gravity.END);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, dpToPx(12));
        messageLayout.setLayoutParams(layoutParams);
        
        // Message bubble
        TextView messageText = new TextView(this);
        messageText.setText(message);
        messageText.setTextSize(15);
        messageText.setTextColor(getResources().getColor(R.color.text_primary));
        messageText.setBackgroundResource(R.drawable.bg_chat_user);
        messageText.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.width = dpToPx(250);
        messageText.setLayoutParams(textParams);
        
        messageLayout.addView(messageText);
        chatContainer.addView(messageLayout);
        
        // Animate message
        messageLayout.setAlpha(0f);
        messageLayout.setTranslationX(50f);
        messageLayout.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(300)
            .start();
        
        // Scroll to bottom
        handler.postDelayed(() -> chatScrollView.fullScroll(View.FOCUS_DOWN), 100);
    }

    private void setupVoiceButton() {
        btnVoiceInput.setOnClickListener(v -> {
            isListening = !isListening;
            
            if (isListening) {
                startListening();
            } else {
                stopListening();
            }
        });
    }

    private void startListening() {
        micButtonFrame.setBackgroundResource(R.drawable.bg_mic_active);
        tvBuddyStatus.setText("Listening...");
        
        // Animate mic button
        ObjectAnimator pulse = ObjectAnimator.ofFloat(btnVoiceInput, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator pulseY = ObjectAnimator.ofFloat(btnVoiceInput, "scaleY", 1f, 1.2f, 1f);
        pulse.setDuration(1000);
        pulseY.setDuration(1000);
        pulse.setRepeatCount(ObjectAnimator.INFINITE);
        pulseY.setRepeatCount(ObjectAnimator.INFINITE);
        pulse.start();
        pulseY.start();
        
        // Simulate voice recognition
        handler.postDelayed(() -> {
            if (isListening) {
                stopListening();
                handleResponse(0); // Simulate recognized response
            }
        }, 3000);
    }

    private void stopListening() {
        isListening = false;
        micButtonFrame.setBackgroundResource(R.drawable.bg_mic_button);
        tvBuddyStatus.setText("Online • Ready to chat");
        btnVoiceInput.clearAnimation();
        btnVoiceInput.setScaleX(1f);
        btnVoiceInput.setScaleY(1f);
    }

    private void setupLearningTools() {
        btnVocab.setOnClickListener(v -> {
            animateToolButton(btnVocab);
            addAIMessage("📚 Vocabulary: Let me explain the words we used...");
        });
        
        btnHint.setOnClickListener(v -> {
            animateToolButton(btnHint);
            addAIMessage("💡 Hint: Try to use complete sentences when you speak!");
        });
        
        btnSlowMode.setOnClickListener(v -> {
            slowModeEnabled = !slowModeEnabled;
            animateToolButton(btnSlowMode);
            
            if (slowModeEnabled) {
                addAIMessage("🐢 Slow mode ON. I'll speak more slowly for you.");
            } else {
                addAIMessage("🐇 Normal speed. Let's chat faster!");
            }
        });
    }

    private void animateToolButton(View button) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.9f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.9f, 1f);
        scaleX.setDuration(200);
        scaleY.setDuration(200);
        scaleX.start();
        scaleY.start();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
