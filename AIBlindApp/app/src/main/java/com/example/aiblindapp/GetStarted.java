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
//
//public class GetStarted extends AppCompatActivity implements TextToSpeech.OnInitListener {
//    private static final int SPEECH_REQUEST_CODE = 101;
//    private static final String PREFS_NAME = "MyPrefsFile";
//    private static final String FIRST_TIME_USER_KEY = "FirstTimeUser";
//
//    private SpeechRecognizer speechRecognizer;
//    private TextToSpeech textToSpeech;
//    private Button start;
//    private TextView Texthint;
//    private ImageView micButton;
//    private boolean isListening = false;
//    private Intent speechRecognizerIntent;
//    private boolean isFirstTimeUser;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_get_started);
//
//        start = findViewById(R.id.start);
//        Texthint = findViewById(R.id.text);
//        micButton = findViewById(R.id.mic);
//
//        // Check if the user is opening the app for the first time
//        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//        isFirstTimeUser = settings.getBoolean(FIRST_TIME_USER_KEY, true);
//
//        // Request microphone permission if not already granted
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
//        speechRecognizer.setRecognitionListener(new RecognitionListener() {
//            @Override
//            public void onReadyForSpeech(Bundle bundle) {
//                Log.d("SpeechRecognizer", "onReadyForSpeech");
//            }
//
//            @Override
//            public void onBeginningOfSpeech() {
//                Log.d("SpeechRecognizer", "onBeginningOfSpeech");
//                Texthint.setText("LISTENING...");
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
//                Texthint.setText("Tap again to speak");
//                micButton.setImageResource(R.drawable.baseline_mic_off_24);
//            }
//
//            @Override
//            public void onError(int error) {
//                Log.e("SpeechRecognizer", "onError: " + error);
//            }
//
//            @Override
//            public void onResults(Bundle bundle) {
//
//                Log.d("SpeechRecognizer", "onResults");
//                micButton.setImageResource(R.drawable.baseline_mic_off_24);
//                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                if (data != null && !data.isEmpty()) {
//                    String text = data.get(0);
//                    Texthint.setText(text);
//                    Toast.makeText(GetStarted.this, "said: " + text, Toast.LENGTH_SHORT).show();
//                    if (text.toLowerCase().contains("start")) {
//                        navigateToNextPage();
//                       // speechRecognizer.stopListening();
//                    }
//                    else{
//                        textToSpeech.speak("Please say start to navigate to the next page", TextToSpeech.QUEUE_FLUSH, null);
//                    }
//                }
//                else{
//
//
//                    textToSpeech.speak("Please say start to navigate to the next page", TextToSpeech.QUEUE_FLUSH, null);
//                    //speechRecognizer.startListening(speechRecognizerIntent);
//                }
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
//
//        start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                navigateToNextPage();
//            }
//        });
//
//        micButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    speechRecognizer.stopListening();
//                }
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    micButton.setImageResource(R.drawable.baseline_mic_24);
//                    speechRecognizer.startListening(speechRecognizerIntent);
//                }
//                return false;
//            }
//        });
//    }
//
//    @Override
//    public void onInit(int status) {
//        if (status == TextToSpeech.SUCCESS) {
//            textToSpeech.setLanguage(Locale.getDefault());
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
//                                Toast.makeText(GetStarted.this, "Text-to-Speech completed", Toast.LENGTH_SHORT).show();
//                                Texthint.setText("Listening...");
//                                micButton.setImageResource(R.drawable.baseline_mic_24);
//                                speechRecognizer.startListening(speechRecognizerIntent);
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
//    private void speakOut() {
//        String text = "Hi, Welcome to the Visual Aid. Let us help you get started. Click on the 'Start' button or say 'Start' to initiate the process.";
//        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "uniqueId");
//    }
//
//    private void navigateToNextPage() {
//        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//        boolean isFirstTimeUser = settings.getBoolean(FIRST_TIME_USER_KEY, true);
//
//      //  if (isFirstTimeUser) {
//            // Navigate to VoiceSelection page
//        //    SharedPreferences.Editor editor = settings.edit();
//        //    editor.putBoolean(FIRST_TIME_USER_KEY, false);
//         //   editor.apply();
//            Intent intent = new Intent(GetStarted.this, VoiceSelection.class);
//            startActivity(intent);
//     //   } else {
//            // Navigate directly to MainActivity page
//      //      Intent intent = new Intent(GetStarted.this, MainActivity.class);
//       //     startActivity(intent);
//        }
//
//        // Finish the GetStarted activity
//      //  finish();
//
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
//        super.onDestroy();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == SPEECH_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, you can start the speech recognizer
//            } else {
//                // Permission denied, notify the user
//                Toast.makeText(this, "Microphone permission is required for speech recognition", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}
package com.example.aiblindapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class GetStarted extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static final int SPEECH_REQUEST_CODE = 101;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FIRST_TIME_USER_KEY = "FirstTimeUser";

    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private Button start;
    private TextView Texthint;
    private ImageView micButton;
    private boolean isListening = false;
    private Intent speechRecognizerIntent;
    private boolean isFirstTimeUser;
    private Handler handler;
    private Runnable speechTimeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        start = findViewById(R.id.start);
        Texthint = findViewById(R.id.text);
        micButton = findViewById(R.id.mic);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        isFirstTimeUser = settings.getBoolean(FIRST_TIME_USER_KEY, true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, SPEECH_REQUEST_CODE);
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        textToSpeech = new TextToSpeech(this, this);

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        handler = new Handler();
        setupSpeechRecognition();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextPage();
            }
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    micButton.setImageResource(R.drawable.baseline_mic_24);
                    startListeningWithTimeout();
                }
                return false;
            }
        });
    }

//    @Override
//    public void onInit(int status) {
//        if (status == TextToSpeech.SUCCESS) {
//            textToSpeech.setLanguage(Locale.getDefault());
//            speakOut();
//        } else {
//            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void setupSpeechRecognition() {
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Log.d("SpeechRecognizer", "onReadyForSpeech");
                Texthint.setText("Listening...");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("SpeechRecognizer", "onBeginningOfSpeech");
            }

            @Override
            public void onRmsChanged(float v) {
                Log.d("SpeechRecognizer", "onRmsChanged: " + v);
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
                Log.d("SpeechRecognizer", "onBufferReceived");
            }

            @Override
            public void onEndOfSpeech() {
                Log.d("SpeechRecognizer", "onEndOfSpeech");
                micButton.setImageResource(R.drawable.baseline_mic_off_24);
            }

            @Override
            public void onError(int error) {
                Log.e("SpeechRecognizer", "onError: " + error);
                startListeningWithTimeout(); // Restart listening on error
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null && !data.isEmpty()) {
                    String text = data.get(0);
                    Texthint.setText(text);
                    if (text.toLowerCase().contains("start")) {
                        navigateToNextPage();
                    } else {
                        speechRecognizer.stopListening();
                        promptUserAgain();
                    }
                } else {
                    speechRecognizer.stopListening();
                    promptUserAgain();
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                Log.d("SpeechRecognizer", "onPartialResults");
            }

            @Override
            public void onEvent(int eventType, Bundle bundle) {
                Log.d("SpeechRecognizer", "onEvent: " + eventType);
            }
        });
    }

    private void promptUserAgain() {
        textToSpeech.speak("Please say the correct command to navigate to the next page", TextToSpeech.QUEUE_FLUSH, null, null);
      //  speechRecognizer.stopListening();
        startListeningWithTimeout();
    }

    private void startListeningWithTimeout() {
        micButton.setImageResource(R.drawable.baseline_mic_24);
        isListening = true;
        speechRecognizer.startListening(speechRecognizerIntent);

        if (speechTimeoutRunnable != null) {
            handler.removeCallbacks(speechTimeoutRunnable);
        }

        speechTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if (isListening) {
                    isListening = false; // Mark listening as inactive
                    textToSpeech.speak("Please say start to navigate to the next page", TextToSpeech.QUEUE_FLUSH, null, "timeoutWarning");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");

                            startListeningWithTimeout();
                        }
                    }, 6000);
                }
            }




        };

        handler.postDelayed(speechTimeoutRunnable, 20000); // 20-second timeout
    }

    private void speakOut() {
        String text = "Hi, Welcome to the Visual Aid. Let us help you get started. Click on the 'Start' button or say 'Start' to initiate the process.";
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "uniqueId");
    }

    private void navigateToNextPage() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(FIRST_TIME_USER_KEY, false);
        editor.apply();

        Intent intent = new Intent(GetStarted.this, Tryactivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.destroy();
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.getDefault());
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {}

                @Override
                public void onDone(String utteranceId) {
                    if ("uniqueId".equals(utteranceId)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GetStarted.this, "Text-to-Speech completed", Toast.LENGTH_SHORT).show();
                                Texthint.setText("Listening...");
                                micButton.setImageResource(R.drawable.baseline_mic_24);
                                promptUserAgain();
                               // speechRecognizer.startListening(speechRecognizerIntent);
                                //speechRecognizer.stopListening();
                            }
                        });
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    Log.e("TextToSpeech", "Error occurred during synthesis");
                }
            });
            speakOut();
        } else {
            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SPEECH_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                Toast.makeText(this, "Microphone permission is required for speech recognition", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
