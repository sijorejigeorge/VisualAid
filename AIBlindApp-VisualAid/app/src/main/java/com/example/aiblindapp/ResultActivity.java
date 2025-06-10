package com.example.aiblindapp;

import static android.content.ContentValues.TAG;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiblindapp.pojo.ImageQuery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends AppCompatActivity implements TextToSpeech.OnInitListener  {
    private TextView resultTextView;
    private TextView labelTextView;
    private ImageView resultImageView;
    private EditText queryInput;
    private TextView queryResultTextView;
    private SpeechRecognizer speechRecognizer;
    String additionalTexts;
    APIInterface apiInterface;
    private static final int SPEECH_REQUEST_CODE = 101;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FIRST_TIME_USER_KEY = "FirstTimeUser";
    String resultText;
    String additionalText;
    boolean isListening = false;
    int i=2;
    Handler handler;
    private TextToSpeech textToSpeech;
    private Intent speechRecognizerIntent;
    String selectedVoiceName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Intent intent = getIntent();
        handler = new Handler();
        selectedVoiceName = intent.getStringExtra("selectedVoiceName");
        resultTextView = findViewById(R.id.resultTextView);
        resultImageView = findViewById(R.id.resultImageView);
        labelTextView = findViewById(R.id.labelTextView); // New TextView for labels
        queryInput = findViewById(R.id.queryInput);
        queryResultTextView = findViewById(R.id.queryResultTextView);
        Button queryButton = findViewById(R.id.queryButton);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        textToSpeech = new TextToSpeech(this, this);
        Voice selectedVoice = getDesiredVoice(selectedVoiceName);
        textToSpeech.setVoice(selectedVoice);

        // Get data from Intent
        //byte[] imageBytes = getIntent().getByteArrayExtra("image");
        String imagePath = getIntent().getStringExtra("image_path");
         resultText = getIntent().getStringExtra("resultText");
        ArrayList<String> labelArrayList = getIntent().getStringArrayListExtra("resultLabel");

        // Convert byte array to Bitmap and display in ImageView
        /*if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            resultImageView.setImageBitmap(bitmap);
        }*/
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                // Rotate the ImageView by 90 degrees on the Y-axis
                resultImageView.setImageBitmap(bitmap);
               // resultImageView.setRotationY(90f);
            } else {
               // Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show();
            }
        }

        // Display result text and labels
        resultTextView.setText(resultText);

        showTextWithAnimation();

        labelTextView.setText("Labels: ");

        if (labelArrayList != null && !labelArrayList.isEmpty()) {
            StringBuilder labels = new StringBuilder();
            for (String label : labelArrayList) {
                labels.append(label).append(", ");
            }
            labelTextView.setText("Labels: " + labels.toString());
        }
        if (labelArrayList != null) {
            labelArrayList.clear();
        }

        // Button to send query to second API
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = queryInput.getText().toString();
                sendQueryToSecondAPI(query);
            }
        });

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d(TAG, "Ready for speech");
             //   Toast.makeText(ResultActivity.this, "onready for speeach", Toast.LENGTH_SHORT).show();
              //  speakOut("onreadyfor speech");

            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "Beginning of speech");
              //  Toast.makeText(ResultActivity.this, "onbegibbing for speeach", Toast.LENGTH_SHORT).show();
               // speakOut("onbegibbing speech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.d(TAG, "Buffer received");
            }

            @Override
            public void onEndOfSpeech() {
                //Toast.makeText(ResultActivity.this, "end for speeach ", Toast.LENGTH_SHORT).show();
               // textToSpeech.speak("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.", TextToSpeech.QUEUE_FLUSH, null, "TTS_ID_DELAYED");
               // textToSpeech.stop();
                //speechRecognizer.stopListening();

             //  speechRecognizer.stopListening();
               // speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");

               //startListening();
               // speechRecognizer.stopListening();

                Log.d(TAG, "End of speech");
            }

            @Override
            public void onError(int error) {
               // Toast.makeText(ResultActivity.this, "on error"+error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: " + error);
                //startListening(); // Restart listening after an error
                if (error == 7){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");


                        }
                    }, 4000);

                }
                    //speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");

            }

            @Override
            public void onResults(Bundle bundle) {

                Log.d("SpeechRecognizer", "onResults");
               // micButton.setImageResource(R.drawable.baseline_mic_off_24);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null && !data.isEmpty()) {
                  //  Toast.makeText(ResultActivity.this, "speech recognized", Toast.LENGTH_SHORT).show();
                    String text = data.get(0);





                   // Texthint.setText(text);
                   // Toast.makeText(GetStarted.this, "said: " + text, Toast.LENGTH_SHORT).show();
                    if (text.toLowerCase().contains("capture")) {
                      //  Toast.makeText(ResultActivity.this, "capture", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                        intent.putExtra("selectedVoiceName", selectedVoiceName);
                        startActivity(intent);
                        finish();
                      //  navigateToNextPage();
                    } else if (text.toLowerCase().contains("exit")) {
                        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                        am.killBackgroundProcesses(getPackageName());
                        finish(); // Close the current activity
                        System.exit(0);
                    }

                    else{
                        //speechRecognizer.stopListening();
                        queryInput.setText(text);
                        String query = queryInput.getText().toString();
                        sendQueryToSecondAPI(query);


                       // Toast.makeText(ResultActivity.this, additionalText, Toast.LENGTH_SHORT).show();
                       // speakOut(additionalText);
                        Log.d("lol", "Error: " + additionalText);


//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                speechRecognizer.stopListening();
//                                speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to load a different image, please say capture, or if you want to exit the application, please say exit.");
//                                speechRecognizer.stopListening();
//
//                            }
//                        }, 5000);
//                        startListening();
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                //speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");
//
//                                startListening();
//                            }
//                        }, 20000);


                    }
                }else{
                   // Toast.makeText(ResultActivity.this, "No speech recognized", Toast.LENGTH_SHORT).show();
                    //startListening();
                }


            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.d(TAG, "Partial results");
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.d(TAG, "Event: " + eventType);
            }
        });

       // startListening();
//        if (labelArrayList != null) {
//            labelArrayList.clear();
//        }
    }

    private void startListening() {
        Log.d(TAG, "Start listening");
        speechRecognizer.startListening(speechRecognizerIntent);
    }
    private void startListeningWithTimeout(long timeoutInMillis) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);

        // Start listening
        speechRecognizer.startListening(intent);
        isListening = true;

        // Schedule to stop listening after the timeout
        handler.postDelayed(() -> {
            if (isListening) {
                Log.d("SpeechRecognizer", "Timeout reached, stopping recognizer...");
                speechRecognizer.stopListening();
              //  Toast.makeText(ResultActivity.this, "Listening timeout reached", Toast.LENGTH_SHORT).show();
                isListening = false;
            }
        }, timeoutInMillis); // Timeout duration in milliseconds
    }
    private void showTextWithAnimation() {
        if (resultText != null && !resultText.isEmpty()) {
            resultTextView.setText(""); // Clear the responseText TextView

            final int[] currentIndex = {0}; // Using an array to make it effectively final

            // Use a handler to post delayed messages to update the TextView gradually
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // Update the responseText TextView with one letter at a time
                    resultTextView.append(String.valueOf(resultText.charAt(currentIndex[0])));
                    currentIndex[0]++;
                    int redColor = Color.parseColor("#D3D3D3");
                    int whiteColor = Color.parseColor("#000000");
                    resultTextView.setBackgroundColor(redColor);
                    resultTextView.setTextColor(whiteColor);

                    // Schedule the next update if there are more letters
                    if (currentIndex[0] < resultText.length()) {
                        handler.postDelayed(this, 100);
                    }
                }
            };

            handler.postDelayed(runnable, 100);
        }
    }
    private void showTextWithAnimations() {
        if (additionalText != null && !additionalText.isEmpty()) {
            queryResultTextView.setText(""); // Clear the responseText TextView

            final int[] currentIndex = {0}; // Using an array to make it effectively final

            // Use a handler to post delayed messages to update the TextView gradually
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // Update the responseText TextView with one letter at a time
                    queryResultTextView.append(String.valueOf(additionalText.charAt(currentIndex[0])));
                    currentIndex[0]++;
                    int redColor = Color.parseColor("#D3D3D3");
                    int whiteColor = Color.parseColor("#000000");
                    queryResultTextView.setBackgroundColor(redColor);
                    queryResultTextView.setTextColor(whiteColor);

                    // Schedule the next update if there are more letters
                    if (currentIndex[0] < additionalText.length()) {
                        handler.postDelayed(this, 100);
                    }
                }
            };

            handler.postDelayed(runnable, 100);
        }
    }


    private void sendQueryToSecondAPI(String query) {
        // API function to process query
        sendQueryToFlask(query);
    }

    private void sendQueryToFlask(String query) {
         speechRecognizer.stopListening();
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), query);
        MultipartBody.Part body = MultipartBody.Part.createFormData("query", query);

        Call<ImageQuery> call = apiInterface.processQuery(body);
        call.enqueue(new Callback<ImageQuery>() {
            @Override
            public void onResponse(Call<ImageQuery> call, Response<ImageQuery> response) {
                if (response.isSuccessful()) {
                    ImageQuery imageQuery = response.body();
                    additionalText = imageQuery.getAdditionalText();



                   // parseAndDisplayAdditionalText(additionalTexts);
                    //String additionalText = additionalTextList.isEmpty() ? null : additionalTextList.get(0);
                   queryResultTextView.setText(additionalText);
                //    Toast.makeText(ResultActivity.this, additionalText, Toast.LENGTH_SHORT).show();
                    showTextWithAnimations();
                  //  speechRecognizer.stopListening();
                    String ssmlText = "<speak>" +
                            "<prosody rate=\"normal\"> " + additionalText + "</prosody>" +
                            "<break time=\"2s\"/>" + // 2-second pause
                            "<prosody rate=\"normal\">And do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.</prosody>" +
                            "</speak>";

                    // Speak using SSML (queue the speech to be spoken)
                    speakOut(ssmlText);
                    //speakOut(additionalText+"  And do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");
                    //speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");
                    //startListening();
                   // speechRecognizer.stopListening();
                 //   speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow up question. Or if you want to capture a different image, please say capture or if you want to exit and application, please say exit");
                    // textToSpeech.stop();
                  //  startListeningWithTimeout(20000);
                   // textToSpeech.speak("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.", TextToSpeech.QUEUE_FLUSH, null, "TTS_ID_DELAYED");
                  //  textToSpeech.speak("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");

                   // Toast.makeText(ResultActivity.this, "Caption: " + additionalText, Toast.LENGTH_SHORT).show();
                    Log.d("ResponceCheck", "Response code: " + response.code() + ", Message: " + response.message());
                } else {
                  //  Toast.makeText(ResultActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("API Error", "Response code: " + response.code() + ", Message: " + response.message());
                }

            }

            @Override
            public void onFailure(Call<ImageQuery> call, Throwable t) {
                String errorMessage = (t instanceof IOException) ? "Network error: " + t.getMessage() : "Unexpected error: " + t.getMessage();
               // Toast.makeText(ResultActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("Failure API Error", " Message: " + errorMessage);
            }
        });
    }
    private void parseAndDisplayAdditionalText(String additionalText) {
        // Check if the text matches the pattern
        String regex = "\\[(.+?)\\]\\s*(\\w+):\\s*(.+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(additionalText);

        if (matcher.find()) {
            String label = matcher.group(1);        // e.g., "grey" or any other word
            String description = matcher.group(2);  // e.g., "description"
            String text = matcher.group(3);         // e.g., "sample text" or "None"

            // If the description contains "None", only display the label
            if (text.equalsIgnoreCase("None")) {
                additionalTexts = label; // Only show the label if the text is "None"
            } else {
                // Show the full description text
                additionalTexts = "[" + label + "] " + description + ": " + text;
            }

            // Set the TextView to display the result
            Log.d(TAG, "Processed Additional Text: " + additionalTexts);
            queryResultTextView.setText(additionalTexts); // Update the UI
        } else {
            // Handle the case where the text format doesn't match
            additionalText = "Invalid format: " + additionalTexts;
            queryResultTextView.setText(additionalTexts); // Update the UI
        }
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
           // textToSpeech.setLanguage(Locale.getDefault());
            Voice selectedVoice = getDesiredVoice(selectedVoiceName);
            textToSpeech.setVoice(selectedVoice);

            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    //Toast.makeText(ResultActivity.this, "on start ok", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDone(String utteranceId) {
                    if ("uniqueId".equals(utteranceId)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                              //  Toast.makeText(ResultActivity.this, "on done ok", Toast.LENGTH_SHORT).show();
                              //  speakOut("end spekkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkech");

                               // Toast.makeText(ResultActivity.this, "Text-to-Speech completed", Toast.LENGTH_SHORT).show();
//                               speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow up question. Or if you want to capture a different image, please say capture or if you want to exit and application, please say exit");
//                              // textToSpeech.stop();
//                                startListeningWithTimeout(20000);
                                /*if (i==2) {
                                    speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startListening();
                                           // speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");

                                        }
                                    }, 15500);


                                    //i=3;
                                     i =2;
                                }*/
                                startListening();
                            }
                        });
                    }

                }

                @Override
                public void onError(String utteranceId) {
                    Log.e("TextToSpeech", "Error occurred during synthesis");
                }
            });
            String ssmlText = "<speak>" +
                    "<prosody rate=\"normal\"> " + resultText + "</prosody>" +
                    "<break time=\"2s\"/>" + // 2-second pause
                    "<prosody rate=\"normal\">And do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.</prosody>" +
                    "</speak>";

            // Speak using SSML (queue the speech to be spoken)
            speakOut(ssmlText);
            //speakOut("There's a "+resultText+" And Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");
            //speakOut("Do you want to ask any additional questions on this? If yes, please ask a follow-up question. Or if you want to capture a different image, please say capture, or if you want to exit the application, please say exit.");
            //startListening();

        } else {
           // Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
        }

    }


    private void speakOut(String text) {
       // speechRecognizer.stopListening();
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
    private Voice getDesiredVoice(String selectedVoice) {
        if (selectedVoice.equals("spanish")) {
            // Assign the male voice using Voice
            return new Voice("es-US-default", new Locale("spa_USA_default"), 400, 200, false, new HashSet<>());
        } else {
            Voice defaultFemaleVoice = textToSpeech.getDefaultVoice();
            return defaultFemaleVoice;
        }
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
}
