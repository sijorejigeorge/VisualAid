package com.example.aiblindapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Locale;

public class VoiceSelection extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final int SPEECH_REQUEST_CODE = 101;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private ImageView imageView;
    private RadioButton radioMale;
    private RadioButton radioFemale;
    private Intent speechRecognizerIntent;
    String selectedVoice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_selection);

        imageView = findViewById(R.id.imageview);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        // Request microphone permission if not already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, SPEECH_REQUEST_CODE);
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        textToSpeech = new TextToSpeech(this, this);

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
                speak("Listening...");
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
                speak("Tap again to speak");
                imageView.setImageResource(R.drawable.baseline_mic_off_24);
            }

            @Override
            public void onError(int error) {
                Log.e("SpeechRecognizer", "onError: " + error);
            }

            @Override
            public void onResults(Bundle bundle) {
                Log.d("SpeechRecognizer", "onResults");
                imageView.setImageResource(R.drawable.baseline_mic_off_24);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null && !data.isEmpty()) {
                    String text = data.get(0).toLowerCase();  // Convert to lowercase for easier matching
                    Toast.makeText(VoiceSelection.this, "You said: " + text, Toast.LENGTH_SHORT).show();

                    if (text.contains("spanish") ) {
                        RadioButton selectedRadioButton = findViewById(R.id.radioMale);
                        radioMale.setChecked(true);
                        selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
                        imageView.setImageResource(R.drawable.man_with_playbutton);
                        textToSpeech.setVoice(getDesiredVoice(selectedVoice));
                        //setMaleVoice();
                        speak("Spanish voice selected");
                        textToSpeech.speak("Spanish voice selected", TextToSpeech.QUEUE_FLUSH, null);
                        speechRecognizer.stopListening();
                        navigateToCamera();
                       // finish();
                    } else if (text.contains("english") ) {
                        RadioButton selectedRadioButton = findViewById(R.id.radioFemale);
                        radioFemale.setChecked(true);
                        selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
                        imageView.setImageResource(R.drawable.women_with_play_button);
                        setFemaleVoice();
                        speak("english voice selected");
                        speechRecognizer.stopListening();
                        navigateToCamera();
                       // finish();
                    }else if (text.equals("back") || text.equals("go back")) {
                      //  Intent intent = new Intent(this, help.class);
                       // startActivity(intent);
                        finish();
                    }

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

        // Set up RadioGroup listener
        RadioGroup voiceRadioGroup = findViewById(R.id.voiceRadioGroup);
        voiceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioMale) {
                    imageView.setImageResource(R.drawable.man_with_playbutton);
                    RadioButton selectedRadioButton = findViewById(R.id.radioMale);
                        radioMale.setChecked(true);
                         selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
                      //  imageView.setImageResource(R.drawable.man_with_playbutton);
                        textToSpeech.setVoice(getDesiredVoice(selectedVoice));
                        //setMaleVoice();
                        speak("Spanish voice selected");
                        textToSpeech.speak("Spanish voice selected", TextToSpeech.QUEUE_FLUSH, null);
                        speechRecognizer.stopListening();
                        navigateToCamera();
                       // finish();
                } else if (checkedId == R.id.radioFemale) {
                    imageView.setImageResource(R.drawable.women_with_play_button);
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

        // Start listening when the user selects a voice command
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    imageView.setImageResource(R.drawable.baseline_mic_24);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });
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
                    runOnUiThread(() -> {
                        imageView.setImageResource(R.drawable.baseline_mic_24);
                        speechRecognizer.startListening(speechRecognizerIntent);
                    });
                }

                @Override
                public void onError(String utteranceId) {
                    Log.e("TextToSpeech", "Error occurred during synthesis");
                }
            });
            speak("Hi, welcome to language Selection. Please choose your voice preference by saying 'Male' or 'Female'.");
        } else {
            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void speak(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "uniqueId");
    }

    private void setMaleVoice() {
        for (Voice voice : textToSpeech.getVoices()) {
            // if (voice.getName().contains("male") || voice.getName().contains("en-us-x-iom-local")) {
            if (voice.getName().contains("male") || voice.getName().contains("hi-in-x-hie-local")) {

                textToSpeech.setVoice(voice);
                break;
            }
        }
        speak("Male voice selected");
    }

    //    private void setFemaleVoice() {
//        for (Voice voice : textToSpeech.getVoices()) {
//
//            if (voice.getName().contains("female") || voice.getName().contains("hi-in-x-hie-local")) {
//                textToSpeech.setVoice(voice);
//                textToSpeech.setLanguage(Locale.getDefault());
//                break;
//            }
//        }
//        speak("Female voice selected");
//    }
    private Voice getDesiredVoice(String selectedVoice) {
        if (selectedVoice.equals("spanish")) {
            // Assign the male voice using Voice
            return new Voice("es-US-default", new Locale("spa_USA_default"), 400, 200, false, new HashSet<>());
        } else {
            Voice defaultFemaleVoice = textToSpeech.getDefaultVoice();
            return defaultFemaleVoice;
        }
    }


    private void setFemaleVoice() {
        boolean femaleVoiceSet = false;
        for (Voice voice : textToSpeech.getVoices()) {
            if (voice.getName().toLowerCase().contains("female") ||
                    voice.getName().equalsIgnoreCase("hi-in-x-hif-local")) { // Correct female voice
                textToSpeech.setVoice(voice);
                textToSpeech.setLanguage(Locale.getDefault());
                femaleVoiceSet = true;
                break;
            }
        }
        speak(femaleVoiceSet ? "Female voice selected" : "Female voice not found");
    }


    private void navigateToCamera() {
        Intent intent = new Intent(this, help.class);
        intent.putExtra("selectedVoiceName", selectedVoice);
        startActivity(intent);
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
                // Permission granted
            } else {
                Toast.makeText(this, "Microphone permission is required for speech recognition", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.speech.RecognitionListener;
//import android.speech.RecognizerIntent;
//import android.speech.SpeechRecognizer;
//import android.speech.tts.TextToSpeech;
//import android.speech.tts.Voice;
//import android.speech.tts.UtteranceProgressListener;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Locale;
//
//public class VoiceSelection extends AppCompatActivity implements TextToSpeech.OnInitListener {
//
//    private static final int SPEECH_REQUEST_CODE = 101;
//    private SpeechRecognizer speechRecognizer;
//    private TextToSpeech textToSpeech;
//    private ImageView imageView;
//    private RadioButton radioMale;
//    private RadioButton radioFemale;
//    private Intent speechRecognizerIntent;
//    String selectedVoice;
//    private boolean isFirstTimeUser;
//    private Handler handler;
//    private Runnable speechTimeoutRunnable;
//    private boolean isListening = false;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_voice_selection);
//
//        imageView = findViewById(R.id.imageview);
//        radioMale = findViewById(R.id.radioMale);
//        radioFemale = findViewById(R.id.radioFemale);
//        handler = new Handler();
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
//       // speechRecognizer.stopListening();
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
//                speak("Listening...");
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
//                speak("Tap again to speak");
//                imageView.setImageResource(R.drawable.baseline_mic_off_24);
//            }
//
//            @Override
//            public void onError(int error) {
//                Log.e("SpeechRecognizer", "onError: " + error);
//            }
//
//            @Override
//            public void onResults(Bundle bundle) {
//                Log.d("SpeechRecognizer", "onResults");
//                imageView.setImageResource(R.drawable.baseline_mic_off_24);
//                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                if (data != null && !data.isEmpty()) {
//                    String text = data.get(0).toLowerCase();  // Convert to lowercase for easier matching
//                    Toast.makeText(VoiceSelection.this, "You said: " + text, Toast.LENGTH_SHORT).show();
//
//                    if (text.contains("spanish") ) {
//                        RadioButton selectedRadioButton = findViewById(R.id.radioMale);
//                        radioMale.setChecked(true);
//                        String selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
//                        imageView.setImageResource(R.drawable.man_with_playbutton);
//                        textToSpeech.setVoice(getDesiredVoice(selectedVoice));
//                        //setMaleVoice();
//                        speak("Spanish voice selected");
//                        textToSpeech.speak("This is a spanish sample voice. Do you want to select this voice? If yes, please say 'Select'. If no, please say 'No' so that we can play the female sample voice", TextToSpeech.QUEUE_FLUSH, null);
//                        speechRecognizer.stopListening();
//                        navigateToCamera();
//                       // finish();
//                    } else if (text.contains("english") ) {
//                        radioFemale.setChecked(true);
//                        imageView.setImageResource(R.drawable.women_with_play_button);
//                        setFemaleVoice();
//                        speak("english voice selected");
//                        speechRecognizer.stopListening();
//                        navigateToCamera();
//                       // finish();
//                    }else if (text.equals("back") || text.equals("go back")) {
//                      //  Intent intent = new Intent(this, help.class);
//                       // startActivity(intent);
//                        finish();
//                    }
//
//
//                }
////                else {
////                    speechRecognizer.stopListening();
////                    promptUserAgain();
////                  //  speak("Voice command not recognized. Please say 'spanish' or 'english'.");
////                }
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
//        // Set up RadioGroup listener
//        RadioGroup voiceRadioGroup = findViewById(R.id.voiceRadioGroup);
//        voiceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.radioMale) {
//
//
//                    for (Voice voice : textToSpeech.getVoices()) {
//                        Log.d("VoiceInfo", "ID: " + voice.getName() + ", Locale: " + voice.getLocale());
//                    }
//
//                    RadioButton selectedRadioButton = findViewById(R.id.radioMale);
//                    radioMale.setChecked(true);
//                    imageView.setImageResource(R.drawable.man_with_playbutton);
//                  //  RadioButton selectedRadioButton = findViewById(checkedId);
//                    selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
//                    textToSpeech.setVoice(getDesiredVoice(selectedVoice));
//                    textToSpeech.speak("You have selected the " + selectedVoice + " voice.", TextToSpeech.QUEUE_FLUSH, null);
//
//
//                    selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
//
//                   // imageView.setImageResource(R.drawable.man_with_playbutton);
//                  //  textToSpeech.setVoice(getDesiredVoice(selectedVoice));
//                    //setMaleVoice();
//                    getDesiredVoice(selectedVoice);
//
//                    speak("Spanish voice selected");
//                    speechRecognizer.stopListening();
//                   // setMaleVoice();
//                 //   speak("Spanish voice selected");
//
//                    navigateToCamera();
//                } else if (checkedId == R.id.radioFemale) {
//                    RadioButton selectedRadioButton = findViewById(R.id.radioFemale);
//                    selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
//                    getDesiredVoice(selectedVoice);
//                    //setFemaleVoice();
//                    speak("English voice selected");
//                    speechRecognizer.stopListening();
//                    navigateToCamera();
//                }
//            }
//        });
//
//        // Start listening when the user selects a voice command
//        imageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    speechRecognizer.stopListening();
//                }
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    imageView.setImageResource(R.drawable.baseline_mic_24);
//                    speechRecognizer.startListening(speechRecognizerIntent);
//                }
//                return false;
//            }
//        });
//    }
//
////    @Override
////    public void onInit(int status) {
////        if (status == TextToSpeech.SUCCESS) {
////            textToSpeech.setLanguage(Locale.getDefault());
////            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
////                @Override
////                public void onStart(String utteranceId) {}
////
////                @Override
////                public void onDone(String utteranceId) {
////                    runOnUiThread(() -> {
////                        imageView.setImageResource(R.drawable.baseline_mic_24);
////                        speechRecognizer.startListening(speechRecognizerIntent);
////                    });
////                }
////
////                @Override
////                public void onError(String utteranceId) {
////                    Log.e("TextToSpeech", "Error occurred during synthesis");
////                }
////            });
////            speak("Hi, welcome to Voice Selection. Please choose your voice preference by saying 'Male' or 'Female'.");
////        } else {
////            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
////        }
////    }
//
//    private void promptUserAgain() {
//        textToSpeech.speak("Please choose your voice preference by saying 'Spanish' or 'English", TextToSpeech.QUEUE_FLUSH, null, null);
//        startListeningWithTimeout();
//    }
//
//    private void startListeningWithTimeout() {
//       // imageView.setImageResource(R.drawable.baseline_mic_24);
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
//                    textToSpeech.speak("Please choose your voice preference by saying 'Spanish' or 'English", TextToSpeech.QUEUE_FLUSH, null, "timeoutWarning");
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            //speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");
//
//                            startListeningWithTimeout();
//                        }
//                    }, 10000);
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
//    @Override
//public void onInit(int status) {
//    if (status == TextToSpeech.SUCCESS) {
//        textToSpeech.setLanguage(Locale.getDefault());
//        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
//            @Override
//            public void onStart(String utteranceId) {}
//
//            @Override
//            public void onDone(String utteranceId) {
//                if ("uniqueId".equals(utteranceId)) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(VoiceSelection.this, "Text-to-Speech completed", Toast.LENGTH_SHORT).show();
//                           // Texthint.setText("Listening...");
//                          //  imageView.setImageResource(R.drawable.baseline_mic_24);
//                            promptUserAgain();
//                            // speechRecognizer.startListening(speechRecognizerIntent);
//                            //speechRecognizer.stopListening();
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onError(String utteranceId) {
//                Log.e("TextToSpeech", "Error occurred during synthesis");
//            }
//        });
//        speakOut();
//    } else {
//        Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
//    }
//}
//    private void speakOut() {
//        String text = "Hi, welcome to Voice Selection. Please choose your voice preference by saying 'Spanish' or 'English'.";
//        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "uniqueId");
//    }
//    private void speak(String text) {
//        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "uniqueId");
//    }
//
//    private void setMaleVoice() {
//        for (Voice voice : textToSpeech.getVoices()) {
//           // if (voice.getName().contains("male") || voice.getName().contains("en-us-x-iom-local")) {
//            if (voice.getName().contains("male") || voice.getName().contains("hi-in-x-hie-local")) {
//
//                textToSpeech.setVoice(voice);
//                break;
//            }
//        }
//        speak("Male voice selected");
//    }
//    private Voice getDesiredVoice(String selectedVoice) {
//        if (selectedVoice.equals("men")) {
//            // Assign the male voice using Voice
//            return new Voice("es-US-default", new Locale("spa_USA_default"), 400, 200, false, new HashSet<>());
//        } else {
//            Voice defaultFemaleVoice = textToSpeech.getDefaultVoice();
//            return defaultFemaleVoice;
//        }
//    }
////    private void setFemaleVoice() {
////        for (Voice voice : textToSpeech.getVoices()) {
////
////            if (voice.getName().contains("female") || voice.getName().contains("hi-in-x-hie-local")) {
////                textToSpeech.setVoice(voice);
////                textToSpeech.setLanguage(Locale.getDefault());
////                break;
////            }
////        }
////        speak("Female voice selected");
////    }
//private void setFemaleVoice() {
//    boolean femaleVoiceSet = false;
//    for (Voice voice : textToSpeech.getVoices()) {
//        if (voice.getName().toLowerCase().contains("female") ||
//                voice.getName().equalsIgnoreCase("hi-in-x-hif-local")) { // Correct female voice
//            textToSpeech.setVoice(voice);
//            textToSpeech.setLanguage(Locale.getDefault());
//            femaleVoiceSet = true;
//            break;
//        }
//    }
//    speak(femaleVoiceSet ? "Female voice selected" : "Female voice not found");
//}
//
//    private void navigateToCamera() {
//        Intent intent = new Intent(this, help.class);
//        intent.putExtra("selectedVoiceName", selectedVoice);
//        startActivity(intent);
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
//        super.onDestroy();
//    }
//
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

//    private void playSampleVoice() {
//        // Get the selected radio button
//        RadioButton selectedRadioButton = findViewById(voiceRadioGroup.getCheckedRadioButtonId());
//        // Get the text of the selected radio button
//        String selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
//        // Set the desired voice based on the selected voice
//        textToSpeech.setVoice(getDesiredVoice(selectedVoice));
//        // Define the sample text
//        String sampleText = (selectedVoice.equals("men")) ? "This is a sample male voice." : "This is a sample female voice.";
//        // Speak the sample text with the specified voice
//        HashMap<String, String> params = new HashMap<>();
//        String utteranceId = (selectedVoice.equals("men")) ? "maleVoice" : "femaleVoice";
//        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
//        textToSpeech.speak(sampleText, TextToSpeech.QUEUE_FLUSH, params);
//    }

    // Get the desired voice based on the selected voice
//    private Voice getDesiredVoice(String selectedVoice) {
//        if (selectedVoice.equals("men")) {
//            // Assign the male voice using Voice
//            return new Voice("hi-in-x-hie-local", new Locale("hi_IN"), 400, 200, false, new HashSet<>());
//        } else {
//            Voice defaultFemaleVoice = textToSpeech.getDefaultVoice();
//            return defaultFemaleVoice;
//        }
//    }




