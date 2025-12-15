package com.shop.englishapp.masterchef.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

/**
 * Audio Manager for Master Chef
 * Handles Text-to-Speech for spoken orders with kid-friendly settings
 */
public class AudioManager {
    
    private static final String TAG = "MasterChef_Audio";
    private static AudioManager instance;
    
    private TextToSpeech tts;
    private boolean isInitialized = false;
    private boolean isSlowMode = false;
    private int replayCount = 0;
    private static final int MAX_REPLAYS = 2;
    
    private OnAudioCallback currentCallback;
    private MediaPlayer backgroundMusic;
    
    private AudioManager() {}
    
    public static synchronized AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * Initializes Text-to-Speech engine
     */
    public void initialize(Context context, OnInitCallback callback) {
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Set US English as default
                int result = tts.setLanguage(Locale.US);
                
                if (result == TextToSpeech.LANG_MISSING_DATA || 
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "English language not supported");
                    callback.onFailure("English language not available");
                } else {
                    // Kid-friendly settings
                    configureTTSForKids();
                    isInitialized = true;
                    callback.onSuccess();
                }
            } else {
                Log.e(TAG, "TTS initialization failed");
                callback.onFailure("Text-to-Speech initialization failed");
            }
        });
    }
    
    /**
     * Configures TTS with kid-friendly settings
     * - Slower speech rate
     * - Clear pronunciation
     * - Friendly pitch
     */
    private void configureTTSForKids() {
        if (tts == null) return;
        
        // Normal mode: slightly slower than default
        tts.setSpeechRate(0.85f);
        
        // Slightly higher pitch for friendly tone
        tts.setPitch(1.1f);
        
        // Set utterance listener for callbacks
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    if (currentCallback != null) {
                        currentCallback.onStart();
                    }
                }
                
                @Override
                public void onDone(String utteranceId) {
                    if (currentCallback != null) {
                        currentCallback.onComplete();
                    }
                }
                
                @Override
                public void onError(String utteranceId) {
                    if (currentCallback != null) {
                        currentCallback.onError("Audio playback error");
                    }
                }
            });
        }
    }
    
    /**
     * Speaks the order text
     * Automatically manages replay count
     */
    public void speakOrder(String text, OnAudioCallback callback) {
        if (!isInitialized) {
            callback.onError("Audio system not initialized");
            return;
        }
        
        if (replayCount >= MAX_REPLAYS) {
            callback.onError("Maximum replays reached");
            return;
        }
        
        this.currentCallback = callback;
        replayCount++;
        
        // Apply slow mode if enabled
        if (isSlowMode) {
            tts.setSpeechRate(0.65f); // Much slower for learning
        } else {
            tts.setSpeechRate(0.85f); // Normal kid-friendly speed
        }
        
        // Speak the text
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ORDER_UTTERANCE");
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    
    /**
     * Enables or disables slow mode
     */
    public void setSlowMode(boolean enabled) {
        this.isSlowMode = enabled;
        if (tts != null) {
            if (enabled) {
                tts.setSpeechRate(0.65f);
            } else {
                tts.setSpeechRate(0.85f);
            }
        }
    }
    
    /**
     * Resets replay counter for new order
     */
    public void resetReplayCount() {
        this.replayCount = 0;
    }
    
    /**
     * Gets remaining replays
     */
    public int getRemainingReplays() {
        return MAX_REPLAYS - replayCount;
    }
    
    /**
     * Checks if more replays available
     */
    public boolean canReplay() {
        return replayCount < MAX_REPLAYS;
    }
    
    /**
     * Stops current speech
     */
    public void stop() {
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
        }
    }
    
    /**
     * Plays celebration sound effect
     */
    public void playCelebrationSound(Context context) {
        // Use a pre-recorded celebration sound
        // For now, we'll use TTS
        if (isInitialized) {
            String[] celebrations = {
                "Perfect!",
                "Excellent!",
                "Great job!",
                "You're amazing!",
                "Wonderful!"
            };
            
            int randomIndex = (int) (Math.random() * celebrations.length);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(celebrations[randomIndex], TextToSpeech.QUEUE_ADD, null, "CELEBRATION");
            } else {
                tts.speak(celebrations[randomIndex], TextToSpeech.QUEUE_ADD, null);
            }
        }
    }
    
    /**
     * Plays error/hint sound
     */
    public void playHintSound(String hint) {
        if (isInitialized) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(hint, TextToSpeech.QUEUE_ADD, null, "HINT");
            } else {
                tts.speak(hint, TextToSpeech.QUEUE_ADD, null);
            }
        }
    }
    
    /**
     * Starts background music (optional, calm music for kids)
     */
    public void startBackgroundMusic(Context context, int musicResId) {
        try {
            if (backgroundMusic == null) {
                backgroundMusic = MediaPlayer.create(context, musicResId);
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(0.3f, 0.3f); // Low volume for background
            }
            
            if (!backgroundMusic.isPlaying()) {
                backgroundMusic.start();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to start background music", e);
        }
    }
    
    /**
     * Stops background music
     */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }
    
    /**
     * Releases all audio resources
     */
    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
            isInitialized = false;
        }
        
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }
    
    // Callback Interfaces
    public interface OnInitCallback {
        void onSuccess();
        void onFailure(String error);
    }
    
    public interface OnAudioCallback {
        void onStart();
        void onComplete();
        void onError(String error);
    }
}
