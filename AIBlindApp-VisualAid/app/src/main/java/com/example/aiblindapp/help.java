//package com.example.aiblindapp;
//
//import static android.content.ContentValues.TAG;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.speech.RecognitionListener;
//import android.speech.RecognizerIntent;
//import android.speech.SpeechRecognizer;
//import android.speech.tts.TextToSpeech;
//import android.speech.tts.UtteranceProgressListener;
//import android.speech.tts.Voice;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Locale;
//
//public class help extends AppCompatActivity implements TextToSpeech.OnInitListener {
//
//        private TextToSpeech textToSpeech;
//        private String selectedVoiceName;
//        private SpeechRecognizer speechRecognizer;
//        private boolean isListening = false;
//;
//
//    private Intent speechRecognizerIntent;
//    private static final int SPEECH_REQUEST_CODE = 101;
//    Button next;
//
//    private boolean instructionsSpoken = false;
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState)  {
//            super.onCreate(savedInstanceState);
//            EdgeToEdge.enable(this);
//            setContentView(R.layout.activity_help_screen);
//            Intent intent = getIntent();
//            selectedVoiceName = intent.getStringExtra("selectedVoiceName");
//            textToSpeech = new TextToSpeech(this, this);
//            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//            next = findViewById(R.id.next);
//            next.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    speechRecognizer.stopListening();
//                    navigateToSecondPage();
//                }
//            });
//            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//            textToSpeech = new TextToSpeech(this, this);
//
//            speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//
//            speechRecognizer.setRecognitionListener(new RecognitionListener() {
//                @Override
//                public void onReadyForSpeech(Bundle bundle) {
//                    Log.d("SpeechRecognizer", "onReadyForSpeech");
//                }
//
//                @Override
//                public void onBeginningOfSpeech() {
//                    Log.d("SpeechRecognizer", "onBeginningOfSpeech");
//                    // Texthint.setText("LISTENING...");
//                }
//
//                @Override
//                public void onRmsChanged(float v) {
//                    Log.d("SpeechRecognizer", "onRmsChanged: " + v);
//                }
//
//                @Override
//                public void onBufferReceived(byte[] bytes) {
//                    Log.d("SpeechRecognizer", "onBufferReceived");
//                }
//
//                @Override
//                public void onEndOfSpeech() {
//                    Log.d("SpeechRecognizer", "onEndOfSpeech");
//                    if (instructionsSpoken) {
//                        startListening();  // Start listening after instructions are spoken
//                    }
//                    // Texthint.setText("Tap again to speak");
//                    // micButton.setImageResource(R.drawable.baseline_mic_off_24);
//                }
//
//                @Override
//                public void onError(int i) {
//
//                }
//
//
//                @Override
//                public void onResults(Bundle bundle) {
//
//                    Log.d("SpeechRecognizer", "onResults");
//                    // micButton.setImageResource(R.drawable.baseline_mic_off_24);
//                    ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                    if (data != null && !data.isEmpty()) {
//                        String text = data.get(0);
//
//                        Toast.makeText(help.this, "said: " + text, Toast.LENGTH_SHORT).show();
//                        if (text.toLowerCase().contains("next")) {
//                            speechRecognizer.stopListening();
//                            navigateToSecondPage();
//                        }
//                        else  {
//                            textToSpeech.speak("Please say next to navigate to the next page", TextToSpeech.QUEUE_FLUSH, null);
//                            speechRecognizer.stopListening();
//                            startListening();
//                        }
//                    } else {
//                        textToSpeech.speak("Please say next to navigate to the next page", TextToSpeech.QUEUE_FLUSH, null);
//                        speechRecognizer.stopListening();
//                        startListening();
//                    }
//                }
//
//                @Override
//                public void onPartialResults(Bundle bundle) {
//
//                }
//
//                @Override
//                public void onEvent(int i, Bundle bundle) {
//
//                }
//
//
//            });
//
//           // speechRecognizer.startListening(speechRecognizerIntent);
////
//
//        }
//
//        private void navigateToSecondPage() {
//            //  Vibration();
//            // Handle navigation to the second page here
//            speechRecognizer.stopListening();
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.putExtra("selectedVoiceName", selectedVoiceName);
//            startActivity(intent);
//            finish();
//
//
//        }
//    private Voice getDesiredVoice(String selectedVoice) {
//        if (selectedVoice.equals("men")) {
//            // Assign the male voice using Voice
//            return new Voice("es-US-default", new Locale("spa_USA_default"), 400, 200, false, new HashSet<>());
//        } else {
//            Voice defaultFemaleVoice = textToSpeech.getDefaultVoice();
//            return defaultFemaleVoice;
//        }
//    }
//    private void startListening() {
//        Toast.makeText(this, "start listening", Toast.LENGTH_SHORT).show();
//        if (!isListening) {
//            isListening = true;
//            speechRecognizer.startListening(speechRecognizerIntent);  // Start listening
//        }
//
//    }
////        @Override
////        public void onInit(int status) {
////            if (status == TextToSpeech.SUCCESS) {
////                Voice selectedVoice = getDesiredVoice(selectedVoiceName);
////                textToSpeech.setVoice(selectedVoice);
////
////                textToSpeech.speak("This  help screen  is here to assist you in navigating the app.If you ever get stuck or have questions , refer to this guide for assistance by saying  Help  or clicking the  i  icon.    You can help use of voice assistance to navigate through the app by using basic navigation words like  'Next'  or  'Back'  or say button names read out at the beginning of every screen to access those screens. Please say 'Next' to navigate to the camera screen to detect objects", TextToSpeech.QUEUE_FLUSH, null);
//////                new Handler().postDelayed(new Runnable() {
//////                    @Override
//////                    public void run() {
//////                        //speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");
//////
//////                        startListening();
//////                    }
//////                }, 28000);
////            }
////        }
//@Override
//public void onInit(int status) {
//    if (status == TextToSpeech.SUCCESS) {
//        Voice selectedVoice = getDesiredVoice(selectedVoiceName);
//        textToSpeech.setVoice(selectedVoice);
//
//        textToSpeech.speak("This help screen is here to assist you in navigating the app. If you ever get stuck or have questions, refer to this guide for assistance by saying Help or clicking the i icon. You can use voice assistance to navigate through the app by using basic navigation words like 'Next' or 'Back', or say button names read out at the beginning of every screen to access those screens. Please say 'Next' to navigate to the camera screen to detect objects.", TextToSpeech.QUEUE_FLUSH, null);
//
//        // Set the flag that instructions have been spoken
//        instructionsSpoken = true;
//
//        // Start speech recognition after a delay to ensure the instructions are spoken
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (instructionsSpoken) {
//                    speechRecognizer.startListening(speechRecognizerIntent);  // Start speech recognition
//                }
//            }
//        }, 34000);  // Delay for 5 seconds to ensure instructions are spoken
//    }
//    startListening();
//}
//    }














//package com.example.aiblindapp;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.speech.RecognitionListener;
//import android.speech.RecognizerIntent;
//import android.speech.SpeechRecognizer;
//import android.speech.tts.TextToSpeech;
//import android.speech.tts.UtteranceProgressListener;
//import android.speech.tts.Voice;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.Locale;
//import java.util.HashSet;
//public class help extends AppCompatActivity implements TextToSpeech.OnInitListener {
//    private static final int SPEECH_REQUEST_CODE = 101;
//    private static final String PREFS_NAME = "MyPrefsFile";
//    private static final String FIRST_TIME_USER_KEY = "FirstTimeUser";
//    Button next;
//    private String selectedVoiceName;
//    private SpeechRecognizer speechRecognizer;
//    private TextToSpeech textToSpeech;
//    private Button start;
//    private TextView Texthint;
//    private ImageView micButton;
//    private boolean isListening = false;
//    private Intent speechRecognizerIntent;
//    private boolean isFirstTimeUser;
//    private Handler handler;
//    private Runnable speechTimeoutRunnable;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_help_screen);
//
//       next = findViewById(R.id.next);
////        Texthint = findViewById(R.id.text);
////        micButton = findViewById(R.id.mic);
//        Intent intent = getIntent();
//        selectedVoiceName = intent.getStringExtra("selectedVoiceName");
//        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//        isFirstTimeUser = settings.getBoolean(FIRST_TIME_USER_KEY, true);
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, SPEECH_REQUEST_CODE);
//        }
//
//        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//        textToSpeech = new TextToSpeech(this, this);
//
//        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//
//        handler = new Handler();
//        setupSpeechRecognition();
//
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                navigateToNextPage();
//            }
//        });
//
////        micButton.setOnTouchListener(new View.OnTouchListener() {
////            @Override
////            public boolean onTouch(View view, MotionEvent motionEvent) {
////                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
////                    speechRecognizer.stopListening();
////                }
////                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
////                    micButton.setImageResource(R.drawable.baseline_mic_24);
////                    startListeningWithTimeout();
////                }
////                return false;
////            }
////        });
//    }
//
////    @Override
////    public void onInit(int status) {
////        if (status == TextToSpeech.SUCCESS) {
////            textToSpeech.setLanguage(Locale.getDefault());
////            speakOut();
////        } else {
////            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
////        }
////    }
//
//    private void setupSpeechRecognition() {
//        speechRecognizer.setRecognitionListener(new RecognitionListener() {
//            @Override
//            public void onReadyForSpeech(Bundle bundle) {
//                Log.d("SpeechRecognizer", "onReadyForSpeech");
//               // Texthint.setText("Listening...");
//            }
//
//            @Override
//            public void onBeginningOfSpeech() {
//                Log.d("SpeechRecognizer", "onBeginningOfSpeech");
//            }
//
//            @Override
//            public void onRmsChanged(float v) {
//                Log.d("SpeechRecognizer", "onRmsChanged: " + v);
//            }
//
//            @Override
//            public void onBufferReceived(byte[] bytes) {
//                Log.d("SpeechRecognizer", "onBufferReceived");
//            }
//
//            @Override
//            public void onEndOfSpeech() {
//                Log.d("SpeechRecognizer", "onEndOfSpeech");
//               // micButton.setImageResource(R.drawable.baseline_mic_off_24);
//            }
//
//            @Override
//            public void onError(int error) {
//                Log.e("SpeechRecognizer", "onError: " + error);
//                startListeningWithTimeout(); // Restart listening on error
//            }
//
//            @Override
//            public void onResults(Bundle bundle) {
//                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                if (data != null && !data.isEmpty()) {
//                    String text = data.get(0);
//                    //Texthint.setText(text);
//                    if (text.toLowerCase().contains("next")) {
//                        navigateToNextPage();
//                    } else {
//                        speechRecognizer.stopListening();
//                        promptUserAgain();
//                    }
//                } else {
//                    speechRecognizer.stopListening();
//                    promptUserAgain();
//                }
//              //  promptUserAgain();
//            }
//
//            @Override
//            public void onPartialResults(Bundle bundle) {
//                Log.d("SpeechRecognizer", "onPartialResults");
//            }
//
//            @Override
//            public void onEvent(int eventType, Bundle bundle) {
//                Log.d("SpeechRecognizer", "onEvent: " + eventType);
//            }
//        });
//    }
//
//    private void promptUserAgain() {
//        textToSpeech.speak("Please say next to navigate to the next page", TextToSpeech.QUEUE_FLUSH, null, null);
//        startListeningWithTimeout();
//    }
////        private Voice getDesiredVoice(String selectedVoice) {
////        if (selectedVoice.equals("men")) {
////            // Assign the male voice using Voice
////            return new Voice("es-US-default", new Locale("spa_USA_default"), 400, 200, false, new HashSet<>());
////        } else {
////            Voice defaultFemaleVoice = textToSpeech.getDefaultVoice();
////            return defaultFemaleVoice;
////        }
////    }
//    private void startListeningWithTimeout() {
//        micButton.setImageResource(R.drawable.baseline_mic_24);
//        isListening = true;
//        speechRecognizer.startListening(speechRecognizerIntent);
//
//        if (speechTimeoutRunnable != null) {
//            handler.removeCallbacks(speechTimeoutRunnable);
//        }
//
//        speechTimeoutRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if (isListening) {
//                    isListening = false; // Mark listening as inactive
//                    textToSpeech.speak("Please say next to navigate to the next page", TextToSpeech.QUEUE_FLUSH, null, "timeoutWarning");
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            //speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");
//
//                            startListeningWithTimeout();
//                        }
//                    }, 6000);
//                }
//            }
//
//
//
//
//        };
//
//        handler.postDelayed(speechTimeoutRunnable, 20000); // 20-second timeout
//    }
//
//    private void speakOut() {
//        String text = "Hi, Welcome to the Visual Aid. Let us help you get started. Click on the 'Start' button or say 'Start' to initiate the process.";
//        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "uniqueId");
//    }
//
//    private void navigateToNextPage() {
////        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
////        SharedPreferences.Editor editor = settings.edit();
////        editor.putBoolean(FIRST_TIME_USER_KEY, false);
////        editor.apply();
//
//        Intent intent = new Intent(help.this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (textToSpeech != null) {
//            textToSpeech.stop();
//            textToSpeech.shutdown();
//        }
//        if (speechRecognizer != null) {
//            speechRecognizer.stopListening();
//            speechRecognizer.destroy();
//        }
//        handler.removeCallbacksAndMessages(null);
//        super.onDestroy();
//    }
//    @Override
//    public void onInit(int status) {
//        if (status == TextToSpeech.SUCCESS) {
//          //  textToSpeech.setLanguage(Locale.getDefault());
//            Voice selectedVoice = getDesiredVoice(selectedVoiceName);
//              textToSpeech.setVoice(selectedVoice);
//            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
//                @Override
//                public void onStart(String utteranceId) {}
//
//                @Override
//                public void onDone(String utteranceId) {
//                    if ("uniqueId".equals(utteranceId)) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(help.this, "Text-to-Speech completed", Toast.LENGTH_SHORT).show();
//                              //  Texthint.setText("Listening...");
//                               // micButton.setImageResource(R.drawable.baseline_mic_24);
//                                promptUserAgain();
//                                // speechRecognizer.startListening(speechRecognizerIntent);
//                                //speechRecognizer.stopListening();
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onError(String utteranceId) {
//                    Log.e("TextToSpeech", "Error occurred during synthesis");
//                }
//            });
//            speakOut();
//        } else {
//            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//            private Voice getDesiredVoice(String selectedVoice) {
//        if (selectedVoice.equals("men")) {
//            // Assign the male voice using Voice
//            return new Voice("es-US-default", new Locale("spa_USA_default"), 400, 200, false, new HashSet<>());
//        } else {
//            Voice defaultFemaleVoice = textToSpeech.getDefaultVoice();
//            return defaultFemaleVoice;
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == SPEECH_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted
//            } else {
//                Toast.makeText(this, "Microphone permission is required for speech recognition", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
public class help extends AppCompatActivity {

    private ImageView imageView;
    private RadioButton radioMale;
    private RadioButton radioFemale;
    String selectedVoice;
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;
    private Handler handler;
    private String selectedVoiceName;

    private boolean isListening = false;
    ;

    private Intent speechRecognizerIntent;
    private static final int SPEECH_REQUEST_CODE = 101;
    Button next;

    private boolean isSpeaking = false;
    private final String targetKeyword = "next";
    private Runnable repeatTTSRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);

        Intent intent = getIntent();
        selectedVoiceName = intent.getStringExtra("selectedVoiceName");
      //  textToSpeech = new TextToSpeech(this, this);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechRecognizer.stopListening();
                navigateToSecondPage();
            }
        });
        handler = new Handler(Looper.getMainLooper());

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
//                textToSpeech.setLanguage(Locale.US);
                Voice selectedVoice = getDesiredVoice(selectedVoiceName);
                textToSpeech.setVoice(selectedVoice);

                speak("This help screen is here to assist you in navigating the app. If you ever get stuck or have questions, refer to this guide for assistance by saying Help or clicking the i icon. You can use voice assistance to navigate through the app by using basic navigation words like 'Next' or 'Back', or say button names read out at the beginning of every screen to access those screens. Please say 'Next' to navigate to the camera screen to detect objects.");

                startRepeatingTTS();
            } else {
                Toast.makeText(this, "TTS Initialization failed", Toast.LENGTH_SHORT).show();
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
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
                Log.d("SpeechRecognizer", "Speech ended...");
                isListening = false;
                //imageView.setImageResource(R.drawable.baseline_mic_off_24);
            }

            @Override
            public void onError(int error) {
                Log.e("SpeechRecognizer", "Error: " + error);
                isListening = false;
               // Toast.makeText(help.this, "Error recognizing speech", Toast.LENGTH_SHORT).show();
                restartListening(); // Restart on error
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String userResponse = matches.get(0).toLowerCase(Locale.ROOT);
                    Log.d("SpeechRecognizer", "User response: " + userResponse);

                    if (userResponse.contains(targetKeyword)) {
                       // Toast.makeText(help.this, "Correct word detected: " + targetKeyword, Toast.LENGTH_SHORT).show();
                        navigateToSecondPage(); // Proceed to next activity
                    }
                    else if(userResponse.contains("back")){
                         Intent intent = new Intent(help.this, Tryactivity.class);
                         startActivity(intent);
                        finish();
                    }
                    else {
                      //  Toast.makeText(help.this, "Invalid word, please try again.", Toast.LENGTH_SHORT).show();
                       // speak("Invalid input, please say the correct command to continue.");
                        textToSpeech.speak("Please say the correct command to continue", TextToSpeech.QUEUE_FLUSH, null, "uniqueId");
                        restartListening(); // Restart for invalid input
                    }
                } else {
                   // Toast.makeText(help.this, "No input detected, please try again.", Toast.LENGTH_SHORT).show();
                    restartListening(); // Restart for no input
                }
                isListening = false;
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });
    }


    private void speak(String text) {
        // imageView.setImageResource(R.drawable.baseline_mic_off_24);
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
                    speak("Please say the correct command to continue.");
                    //   imageView.setImageResource(R.drawable.baseline_mic_24);
                    handler.postDelayed(this, 18000); // Re-run this runnable after 20 seconds
                }
            }
        };

        // Start the repeating task after the first 20 seconds
        handler.postDelayed(repeatTTSRunnable, 18000);
    }

    private void startListeningWithTimeout(long timeout) {
        // imageView.setImageResource(R.drawable.baseline_mic_24);
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
                       // Toast.makeText(help.this, "Listening timeout reached", Toast.LENGTH_SHORT).show();
                        restartListening(); // Restart listening after timeout
                    }
                }, timeout);
            }
        }, 2000); // Add a 3-second delay to avoid catching TTS output
    }

    private void restartListening() {
        // imageView.setImageResource(R.drawable.baseline_mic_24);
        if (!isListening) {
            Log.d("SpeechRecognizer", "Restarting listening...");
            startListeningWithTimeout(40000); // Start listening again
        }
    }

    private void navigateToSecondPage() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("selectedVoiceName", selectedVoiceName);
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