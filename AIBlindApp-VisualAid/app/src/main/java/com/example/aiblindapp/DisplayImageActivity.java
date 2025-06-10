package com.example.aiblindapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class DisplayImageActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static final int SPEECH_REQUEST_CODE = 101;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FIRST_TIME_USER_KEY = "FirstTimeUser";

    private ImageView imageView;
    private TextToSpeech textToSpeech;
    private Button start;
    private SpeechRecognizer speechRecognizer;
    private TextView Texthint;
    private ImageView micButton;
    private boolean isListening = false;
    private Intent speechRecognizerIntent;

    String selectedVoiceName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        Texthint = findViewById(R.id.texts);
        micButton = findViewById(R.id.mics);
        imageView = findViewById(R.id.capturedImageView);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        textToSpeech = new TextToSpeech(this, this);
        Intent intent = getIntent();
        selectedVoiceName = intent.getStringExtra("selectedVoiceName");
        // Get the file path from the intent

        String imagePath = intent.getStringExtra("image_path");

        if (imagePath != null) {
            // Load the image and display it
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
      //  textToSpeech = new TextToSpeech(this, this);

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Log.d("SpeechRecognizer", "onReadyForSpeech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("SpeechRecognizer", "onBeginningOfSpeech");
                Texthint.setText("LISTENING...");
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
                Texthint.setText("Tap again to speak");
                micButton.setImageResource(R.drawable.baseline_mic_off_24);
            }

            @Override
            public void onError(int error) {
                Log.e("SpeechRecognizer", "onError: " + error);
            }

            @Override
            public void onResults(Bundle bundle) {

                Log.d("SpeechRecognizer", "onResults");
                micButton.setImageResource(R.drawable.baseline_mic_off_24);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null && !data.isEmpty()) {
                    String text = data.get(0);
                    Texthint.setText(text);

                    if (text.contains("exit") || text.contains("no")) {
                        finish();  // Close the activity
                    } else if (text.contains("yes")) {
                        speakOut("Please ask your next question.");

                        speechRecognizer.startListening(speechRecognizerIntent);
//                        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
//                            @Override
//                            public void onStart(String utteranceId) { }
//
//                            @Override
//                            public void onDone(String utteranceId) {
//                                if ("nextQuestion".equals(utteranceId)) {
//                                    runOnUiThread(() -> {
//                                        micButton.setImageResource(R.drawable.baseline_mic_24);
//                                        Texthint.setText("Listening...");
//                                        speechRecognizer.startListening(speechRecognizerIntent);
//                                    });
//                                }
//                            }
//
//                            @Override
//                            public void onError(String utteranceId) { }
//                        });
                    } else {
                        // Handle other queries here if needed
                        speakOut("Do you have any additional questions? If yes, please ask. If no, say 'exit' to close the application.");
                    }
                    // Toast.makeText(GetStarted.this, "said: " + text, Toast.LENGTH_SHORT).show();
//                    if (text.toLowerCase().contains("start")) {
//                        navigateToNextPage();
//                    }
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

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
//            textToSpeech.setLanguage(Locale.getDefault());
            Voice selectedVoice = getDesiredVoice(selectedVoiceName);
            textToSpeech.setVoice(selectedVoice);
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {}

                @Override
                public void onDone(String utteranceId) {
                    if ("uniqueId".equals(utteranceId)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               // Toast.makeText(GetStarted.this, "Text-to-Speech completed", Toast.LENGTH_SHORT).show();

                                speechRecognizer.startListening(speechRecognizerIntent);
                            }
                        });
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    Log.e("TextToSpeech", "Error occurred during synthesis");
                }
            });
            String text = "This is a dummy text. Captions egnerated wil be dispalyed here. Do you wanna ask any additional questions. Please ask a question when u hear a sound or else pelase say exit to exit the application";

            speakOut(text);
        } else {
            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void speakOut(String text) {
        Voice selectedVoice = getDesiredVoice(selectedVoiceName);
        textToSpeech.setVoice(selectedVoice);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "uniqueId");
    }

//    private void navigateToNextPage() {
//        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//        boolean isFirstTimeUser = settings.getBoolean(FIRST_TIME_USER_KEY, true);
//
//        //  if (isFirstTimeUser) {
//        // Navigate to VoiceSelection page
//        //    SharedPreferences.Editor editor = settings.edit();
//        //    editor.putBoolean(FIRST_TIME_USER_KEY, false);
//        //   editor.apply();
//        Intent intent = new Intent(GetStarted.this, VoiceSelection.class);
//        startActivity(intent);
//        //   } else {
//        // Navigate directly to MainActivity page
//        //      Intent intent = new Intent(GetStarted.this, MainActivity.class);
//        //     startActivity(intent);
//    }

    // Finish the GetStarted activity
    //  finish();

    private Voice getDesiredVoice(String selectedVoice) {
        if (selectedVoice.equals("men")) {
            // Assign the male voice using Voice
            return new Voice("es-US-default", new Locale("spa_USA_default"), 400, 200, false, new HashSet<>());
        } else {
            Voice defaultFemaleVoice = textToSpeech.getDefaultVoice();
            return defaultFemaleVoice;
        }
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
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SPEECH_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can start the speech recognizer
            } else {
                // Permission denied, notify the user
                Toast.makeText(this, "Microphone permission is required for speech recognition", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

}

