//package com.example.aiblindapp;
//
//import android.os.Bundle;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.speech.RecognitionListener;
//import android.speech.RecognizerIntent;
//import android.speech.SpeechRecognizer;
//import android.speech.tts.TextToSpeech;
//import android.speech.tts.UtteranceProgressListener;
//import android.util.Log;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.ArrayList;
//import java.util.Locale;
//public class Tryactivity extends AppCompatActivity {
//
//
//
//        private TextToSpeech textToSpeech;
//        private SpeechRecognizer speechRecognizer;
//        private Handler handler;
//        private boolean isListening = false;
//        private boolean isSpeaking = false;
//        private final String targetKeyword = "start";
//        private Runnable repeatTTSRunnable;
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_main);
//
//            handler = new Handler(Looper.getMainLooper());
//
//            // Initialize TextToSpeech
//            textToSpeech = new TextToSpeech(this, status -> {
//                if (status == TextToSpeech.SUCCESS) {
//                    textToSpeech.setLanguage(Locale.US);
//                    speak("Please say start to continue.");
//                    startRepeatingTTS();
//                } else {
//                    Toast.makeText(this, "TTS Initialization failed", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            // Initialize SpeechRecognizer
//            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//            speechRecognizer.setRecognitionListener(new RecognitionListener() {
//                @Override
//                public void onReadyForSpeech(Bundle params) {
//                    Log.d("SpeechRecognizer", "Ready for speech...");
//                    isListening = true;
//                }
//
//                @Override
//                public void onBeginningOfSpeech() {
//                    Log.d("SpeechRecognizer", "Speech started...");
//                }
//
//                @Override
//                public void onRmsChanged(float rmsdB) {}
//
//                @Override
//                public void onBufferReceived(byte[] buffer) {}
//
//                @Override
//                public void onEndOfSpeech() {
//                    Log.d("SpeechRecognizer", "Speech ended...");
//                    isListening = false;
//                }
//
//                @Override
//                public void onError(int error) {
//                    Log.e("SpeechRecognizer", "Error: " + error);
//                    isListening = false;
//                    Toast.makeText(Tryactivity.this, "Error recognizing speech", Toast.LENGTH_SHORT).show();
//                    restartListening(); // Restart on error
//                }
//
//                @Override
//                public void onResults(Bundle results) {
//                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                    if (matches != null && !matches.isEmpty()) {
//                        String userResponse = matches.get(0).toLowerCase(Locale.ROOT);
//                        Log.d("SpeechRecognizer", "User response: " + userResponse);
//
//                        if (userResponse.contains(targetKeyword)) {
//                            Toast.makeText(Tryactivity.this, "Correct word detected: " + targetKeyword, Toast.LENGTH_SHORT).show();
//                            goToNextActivity(); // Proceed to next activity
//                        } else {
//                            Toast.makeText(Tryactivity.this, "Invalid word, please try again.", Toast.LENGTH_SHORT).show();
//                            restartListening(); // Restart for invalid input
//                        }
//                    } else {
//                        Toast.makeText(Tryactivity.this, "No input detected, please try again.", Toast.LENGTH_SHORT).show();
//                        restartListening(); // Restart for no input
//                    }
//                    isListening = false;
//                }
//
//                @Override
//                public void onPartialResults(Bundle partialResults) {}
//
//                @Override
//                public void onEvent(int eventType, Bundle params) {}
//            });
//        }
//
//        private void speak(String text) {
//            if (isSpeaking) {
//                return; // Prevent speaking if already speaking
//            }
//            isSpeaking = true;
//            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_ID");
//
//            // Add a listener to handle the end of speech
//            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
//                @Override
//                public void onStart(String utteranceId) {
//                    Log.d("TextToSpeech", "Speaking started...");
//                }
//
//                @Override
//                public void onDone(String utteranceId) {
//                    Log.d("TextToSpeech", "Speaking finished...");
//                    isSpeaking = false; // Reset flag when speaking is finished
//                    runOnUiThread(() -> startListeningWithTimeout(20000)); // Start listening after 20 seconds of TTS finish
//                }
//
//                @Override
//                public void onError(String utteranceId) {
//                    Log.e("TextToSpeech", "Error during TTS");
//                }
//            });
//        }
//
//        private void startRepeatingTTS() {
//            repeatTTSRunnable = new Runnable() {
//                @Override
//                public void run() {
//                    // Only start the TTS if it's not already speaking
//                    if (!isSpeaking) {
//                        speak("Please say start to continue.");
//                        handler.postDelayed(this, 20000); // Re-run this runnable after 20 seconds
//                    }
//                }
//            };
//
//            // Start the repeating task after the first 20 seconds
//            handler.postDelayed(repeatTTSRunnable, 20000);
//        }
//
//    private void startListeningWithTimeout(long timeout) {
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Only start listening after a delay to ensure TTS finishes
//                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
//                speechRecognizer.startListening(intent);
//
//                // Schedule to stop listening after the timeout
//                handler.postDelayed(() -> {
//                    if (isListening) {
//                        Log.d("SpeechRecognizer", "Timeout reached, stopping recognizer...");
//                        speechRecognizer.stopListening();
//                        Toast.makeText(Tryactivity.this, "Listening timeout reached", Toast.LENGTH_SHORT).show();
//                        restartListening(); // Restart listening after timeout
//                    }
//                }, timeout);
//            }
//        }, 3000); // Add a 3-second delay to avoid catching TTS output
//    }
//
//    private void restartListening() {
//            if (!isListening) {
//                Log.d("SpeechRecognizer", "Restarting listening...");
//                startListeningWithTimeout(20000); // Start listening again
//            }
//        }
//
//        private void goToNextActivity() {
//            Intent intent = new Intent(Tryactivity.this, VoiceSelection.class);
//            startActivity(intent);
//            finish();
//        }
//
//        @Override
//        protected void onDestroy() {
//            super.onDestroy();
//            if (textToSpeech != null) {
//                textToSpeech.stop();
//                textToSpeech.shutdown();
//            }
//            if (speechRecognizer != null) {
//                speechRecognizer.destroy();
//            }
//            handler.removeCallbacksAndMessages(null); // Clean up handler
//        }
//
//
//}
//
//
//
package com.example.aiblindapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
public class Tryactivity extends AppCompatActivity {

    private ImageView imageView;
    private RadioButton radioMale;
    private RadioButton radioFemale;
    String selectedVoice;
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;
    private Handler handler;
    private boolean isListening = false;
    private boolean isSpeaking = false;
    private final String targetKeyword = "start";
    private Runnable repeatTTSRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_selection);
        imageView = findViewById(R.id.imageview);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        handler = new Handler(Looper.getMainLooper());

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                speak("Hi, welcome to Voice Selection. Please choose your language preference by saying 'Spanish' or 'English after you hear a beep sound");
                startRepeatingTTS();
            } else {
               // Toast.makeText(this, "TTS Initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        RadioGroup voiceRadioGroup = findViewById(R.id.voiceRadioGroup);
        voiceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioMale) {
                    imageView.setImageResource(R.drawable.baseline_mic_off_24);
                    RadioButton selectedRadioButton = findViewById(R.id.radioMale);
                    radioMale.setChecked(true);
                    selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
                    //  imageView.setImageResource(R.drawable.man_with_playbutton);
                    textToSpeech.setVoice(getDesiredVoice(selectedVoice));
                    //setMaleVoice();
                    speak("Spanish voice selected");
                    textToSpeech.speak("Spanish voice selected", TextToSpeech.QUEUE_FLUSH, null);
                  //  speechRecognizer.stopListening();
                    navigateToCamera();
                    // finish();
                } else if (checkedId == R.id.radioFemale) {
                    imageView.setImageResource(R.drawable.baseline_mic_off_24);
                    RadioButton selectedRadioButton = findViewById(R.id.radioFemale);
                    selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
                    getDesiredVoice(selectedVoice);
                    //setFemaleVoice();
                    speak("English voice selected");
                    speechRecognizer.stopListening();
                    navigateToCamera();
                }
            }
        });

        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d("SpeechRecognizer", "Ready for speech...");
                isListening = true;
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("SpeechRecognizer", "Speech started...");
            }

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                Log.d("SpeechRecognizer", "Speech ended...");
                isListening = false;
                imageView.setImageResource(R.drawable.baseline_mic_off_24);
            }

            @Override
            public void onError(int error) {
                Log.e("SpeechRecognizer", "Error: " + error);
                isListening = false;
                //Toast.makeText(Tryactivity.this, "Error recognizing speech", Toast.LENGTH_SHORT).show();
                restartListening(); // Restart on error
            }

            @Override
            public void onResults(Bundle results) {
                imageView.setImageResource(R.drawable.baseline_mic_24);
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String userResponse = matches.get(0).toLowerCase(Locale.ROOT);
                    Log.d("SpeechRecognizer", "User response: " + userResponse);
                    if (userResponse.contains("spanish") ) {
                        RadioButton selectedRadioButton = findViewById(R.id.radioMale);
                        radioMale.setChecked(true);
                        imageView.setImageResource(R.drawable.baseline_mic_off_24);
                        selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
                       // imageView.setImageResource(R.drawable.man_with_playbutton);
                        textToSpeech.setVoice(getDesiredVoice(selectedVoice));
                        //setMaleVoice();
                        speak("Spanish voice selected");
                        textToSpeech.speak("Spanish voice selected", TextToSpeech.QUEUE_FLUSH, null);
                       // speechRecognizer.stopListening();
                        navigateToCamera();
                        // finish();
                    } else if (userResponse.contains("english") ) {
                        RadioButton selectedRadioButton = findViewById(R.id.radioFemale);
                        radioFemale.setChecked(true);
                        selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
                        imageView.setImageResource(R.drawable.baseline_mic_off_24);
                      //  setFemaleVoice();
                        speak("english voice selected");
                       // speechRecognizer.stopListening();
                        textToSpeech.speak("English voice selected", TextToSpeech.QUEUE_FLUSH, null);
                        navigateToCamera();
                        // finish();
                    }else if (userResponse.equals("back") || userResponse.equals("go back")) {
                         Intent intent = new Intent(Tryactivity.this, GetStarted.class);
                         startActivity(intent);
                         finish();
                    }
                    else {
                       // Toast.makeText(Tryactivity.this, "Invalid word, please try again.", Toast.LENGTH_SHORT).show();
                        textToSpeech.speak("Invalid input. Please say spanish or english to continue", TextToSpeech.QUEUE_FLUSH, null);
                        imageView.setImageResource(R.drawable.baseline_mic_24);
                        restartListening(); // Restart for invalid input
                    }

                } else {
                  //  Toast.makeText(Tryactivity.this, "No input detected, please try again.", Toast.LENGTH_SHORT).show();
                    restartListening(); // Restart for no input
                }
                isListening = false;
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void speak(String text) {
        imageView.setImageResource(R.drawable.baseline_mic_off_24);
        if (isSpeaking) {
            return; // Prevent speaking if already speaking
        }
        isSpeaking = true;
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_ID");

        // Add a listener to handle the end of speech
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.d("TextToSpeech", "Speaking started...");
            }

            @Override
            public void onDone(String utteranceId) {
                Log.d("TextToSpeech", "Speaking finished...");
                isSpeaking = false; // Reset flag when speaking is finished
                runOnUiThread(() -> startListeningWithTimeout(20000)); // Start listening after 20 seconds of TTS finish
            }

            @Override
            public void onError(String utteranceId) {
                Log.e("TextToSpeech", "Error during TTS");
            }
        });
    }
    private Voice getDesiredVoice(String selectedVoice) {
        if (selectedVoice.equals("spanish")) {
            // Assign the male voice using Voice
            return new Voice("es-US-default", new Locale("spa_USA_default"), 400, 200, false, new HashSet<>());
        } else {
            Voice defaultFemaleVoice = textToSpeech.getDefaultVoice();
            return defaultFemaleVoice;
        }
    }

    private void startRepeatingTTS() {
        repeatTTSRunnable = new Runnable() {
            @Override
            public void run() {
                // Only start the TTS if it's not already speaking
                if (!isSpeaking) {
                    speak("Please say spanish or english to continue.");
                    imageView.setImageResource(R.drawable.baseline_mic_24);
                    handler.postDelayed(this, 20000); // Re-run this runnable after 20 seconds
                }
            }
        };

        // Start the repeating task after the first 20 seconds
        handler.postDelayed(repeatTTSRunnable, 20000);
    }

    private void startListeningWithTimeout(long timeout) {
        imageView.setImageResource(R.drawable.baseline_mic_24);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Only start listening after a delay to ensure TTS finishes
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
                speechRecognizer.startListening(intent);

                // Schedule to stop listening after the timeout
                handler.postDelayed(() -> {
                    if (isListening) {
                        Log.d("SpeechRecognizer", "Timeout reached, stopping recognizer...");
                        //imageView.setImageResource(R.drawable.baseline_mic_off_24);
                        speechRecognizer.stopListening();
                      //  Toast.makeText(Tryactivity.this, "Listening timeout reached", Toast.LENGTH_SHORT).show();
                        restartListening(); // Restart listening after timeout
                    }
                }, timeout);
            }
        }, 2000); // Add a 3-second delay to avoid catching TTS output
    }

    private void restartListening() {
        imageView.setImageResource(R.drawable.baseline_mic_24);
        if (!isListening) {
            Log.d("SpeechRecognizer", "Restarting listening...");
            startListeningWithTimeout(40000); // Start listening again
        }
    }

    private void navigateToCamera() {

        Intent intent = new Intent(Tryactivity.this, help.class);
        intent.putExtra("selectedVoiceName", selectedVoice);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        handler.removeCallbacksAndMessages(null); // Clean up handler
    }


}



