package com.example.aiblindapp;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class HelpScreen extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private TextToSpeech textToSpeech;
    private String selectedVoiceName;
    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;

    private Intent speechRecognizerIntent;
    private static final int SPEECH_REQUEST_CODE = 101;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_screen);
        Intent intent = getIntent();
        selectedVoiceName = intent.getStringExtra("selectedVoiceName");
        navigateToSecondPage();
        textToSpeech = new TextToSpeech(this, this);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSecondPage();
            }
        });
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
               // Texthint.setText("LISTENING...");
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

               // Texthint.setText("Tap again to speak");
               // micButton.setImageResource(R.drawable.baseline_mic_off_24);
            }

            @Override
            public void onError(int i) {

            }


            @Override
            public void onResults(Bundle bundle) {

                Log.d("SpeechRecognizer", "onResults");
               // micButton.setImageResource(R.drawable.baseline_mic_off_24);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null && !data.isEmpty()) {
                    String text = data.get(0);

                    Toast.makeText(HelpScreen.this, "said: " + text, Toast.LENGTH_SHORT).show();
                    if (text.toLowerCase().contains("next")) {
                        navigateToSecondPage();
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }


        });
//

    }

    private void navigateToSecondPage() {
        //  Vibration();
        // Handle navigation to the second page here
        Intent intent = new Intent(this, help.class);
       // intent.putExtra("selectedVoiceName", selectedVoiceName);
        startActivity(intent);
       // finish();


    }
    private void startListening() {
        Log.d(TAG, "Start listening");
        speechRecognizer.startListening(speechRecognizerIntent);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            textToSpeech.speak("This  help screen  is here to assist you in navigating the app.If you ever get stuck or have questions , refer to this guide for assistance by saying  Help  or clicking the  i  icon.    You can help use of voice assistance to navigate through the app by using basic navigation words like  'Next'  or  'Back'  or say button names read out at the beginning of every screen to access those screens. Please say 'Next' to navigate to the camera screen to detect objects", TextToSpeech.QUEUE_FLUSH, null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");

                    startListening();
                }
            }, 20000);
        }
    }
}