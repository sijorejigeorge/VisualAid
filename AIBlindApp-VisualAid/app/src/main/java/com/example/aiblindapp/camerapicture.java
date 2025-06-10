package com.example.aiblindapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.aiblindapp.pojo.ImageResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class camerapicture extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final int SPEECH_REQUEST_CODE = 101;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FIRST_TIME_USER_KEY = "FirstTimeUser";

    private ImageView imageView, gifImageView;
    private TextToSpeech textToSpeech;
    private Button start;
    private SpeechRecognizer speechRecognizer;
    private TextView Texthint;
    private ImageView micButton;
    private boolean isListening = false;
    private Intent speechRecognizerIntent;
    APIInterface apiInterface;
    private String selectedVoiceName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerapicture);

        imageView = findViewById(R.id.capturedImageView);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        textToSpeech = new TextToSpeech((Context) this,this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Intent intent = getIntent();
        selectedVoiceName = intent.getStringExtra("selectedVoiceName");

        // Get the file path from the intent

        String imagePath = intent.getStringExtra("image_path");
//
//        if (imagePath != null) {
//            // Load the image and display it
//            File imgFile = new File(imagePath);
//            if (imgFile.exists()) {
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                imageView.setImageBitmap(myBitmap);
//                Bitmap photo = BitmapFactory.decodeResource(getResources(), R.drawable.test);
//                sendImageToFlask(myBitmap);
//            }
//        }
        if (imagePath != null) {
            // Load the image and display it
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                // Rotate the bitmap 90 degrees to the right
                Matrix matrix = new Matrix();
                matrix.postRotate(90); // Rotate by 90 degrees

                Bitmap rotatedBitmap = Bitmap.createBitmap(
                        myBitmap,
                        0,
                        0,
                        myBitmap.getWidth(),
                        myBitmap.getHeight(),
                        matrix,
                        true
                );

                // Set the rotated bitmap to the ImageView
                imageView.setImageBitmap(rotatedBitmap);

                // Optionally send the rotated bitmap to Flask
                sendImageToFlask(rotatedBitmap);
            }
        }


        //speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        textToSpeech = new TextToSpeech(this, this);
        Voice selectedVoice = getDesiredVoice(selectedVoiceName);
        textToSpeech.setVoice(selectedVoice);
       // speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

//        //speechRecognizer.setRecognitionListener(new RecognitionListener() {
//            @Override
//            public void onReadyForSpeech(Bundle bundle) {
//                Log.d("SpeechRecognizer", "onReadyForSpeech");
//            }
//
//            @Override
//            public void onBeginningOfSpeech() {
//                Log.d("SpeechRecognizer", "onBeginningOfSpeech");
//                Texthint.setText("LISTENING...");
//                String text = "hello";
//                speakOut(text);
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
////                Log.d("SpeechRecognizer", "onResults");
////                micButton.setImageResource(R.drawable.baseline_mic_off_24);
////                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
////                if (data != null && !data.isEmpty()) {
////                    String text = data.get(0);
////                    Texthint.setText(text);
////
////                    if (text.contains("exit") || text.contains("no")) {
////                        finish();  // Close the activity
////                    } else if (text.contains("yes")) {
////                        speakOut("Please ask your next question.");
//
//                        speechRecognizer.startListening(speechRecognizerIntent);
////                        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
////                            @Override
////                            public void onStart(String utteranceId) { }
////
////                            @Override
////                            public void onDone(String utteranceId) {
////                                if ("nextQuestion".equals(utteranceId)) {
////                                    runOnUiThread(() -> {
////                                        micButton.setImageResource(R.drawable.baseline_mic_24);
////                                        Texthint.setText("Listening...");
////                                        speechRecognizer.startListening(speechRecognizerIntent);
////                                    });
////                                }
////                            }
////
////                            @Override
////                            public void onError(String utteranceId) { }
////                        });
////                    } else {
////                        // Handle other queries here if needed
////                        speakOut("Do you have any additional questions? If yes, please ask. If no, say 'exit' to close the application.");
////                    }
//                    // Toast.makeText(GetStarted.this, "said: " + text, Toast.LENGTH_SHORT).show();
////                    if (text.toLowerCase().contains("start")) {
////                        navigateToNextPage();
////                    }
//              //  }
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
//
//        });
    }



    private void sendImageToFlask(Bitmap photo) {
        // Convert the Bitmap to a byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // Create a RequestBody for the image data
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray);

        // Create a MultipartBody.Part with the image data
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.png", requestFile);

        // Make the API call to process the image
        Call<ImageResponse> call = apiInterface.processImage(body);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                Log.d("ResponseCheck", "Response code: " + response.code());
                Log.d("ResponseCheck", "Response message: " + response.message());
                if (response.body() != null) {
                    Log.d("ResponseCheck", "Image: " + response.body().getImage());
                    Log.d("ResponseCheck", "Labels: " + response.body().getLabels());
                    Log.d("ResponseCheck", "Additional Text: " + response.body().getAdditionalText());
                }
                if (response.isSuccessful() && response.body() != null) {
                    ImageResponse imageResponse = response.body();

                    // Process the response data
                    String base64Image = imageResponse.getImage();
                    byte[] imageData = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);


                    File imageFile = saveImageToFile(bitmap);
                    if (imageFile == null) {
                        Toast.makeText(camerapicture.this, "Failed to save image.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Extract labels and additional text
                    List<String> labelList = imageResponse.getLabels();
                    String additionalText = imageResponse.getAdditionalText();
                    String labelText = labelList.isEmpty() ? "" : labelList.get(0);
                    //String additionalText = additionalTextList.isEmpty() ? "" : additionalTextList.get(0);
                    ArrayList<String> labelArrayList = new ArrayList<>(labelList);

                    // Pass data to ResultActivity
                    Intent intent = new Intent(camerapicture.this, ResultActivity.class);

                    // Convert bitmap to byte array to pass through the Intent
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                    byte[] bitmapData = byteStream.toByteArray();

                    // Add data to intent
                    //intent.putExtra("image", bitmapData);
                    intent.putExtra("image_path", imageFile.getAbsolutePath());
                    intent.putExtra("resultText", additionalText);
                    intent.putStringArrayListExtra("resultLabel", labelArrayList);
                    intent.putExtra("selectedVoiceName", selectedVoiceName);
                    startActivity(intent);
                } else {
                    Log.e("API Error", "Response code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(camerapicture.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                String errorMessage = (t instanceof IOException) ? "Network error: " + t.getMessage() : "Unexpected error: " + t.getMessage();
                Log.e("Network Error", errorMessage);
                Toast.makeText(camerapicture.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File saveImageToFile(Bitmap bitmap) {
        File imageFile = new File(getFilesDir(), "processed_image.png");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            Log.e("SaveImageError", "Failed to save image: " + e.getMessage());
            return null;
        }
        return imageFile;
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
                                // Toast.makeText(GetStarted.this, "Text-to-Speech completed", Toast.LENGTH_SHORT).show();
                              //  Texthint.setText("Listening...");
                            //    micButton.setImageResource(R.drawable.baseline_mic_24);
                             //   speechRecognizer.startListening(speechRecognizerIntent);

                            }
                        });
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    Log.e("TextToSpeech", "Error occurred during synthesis");
                }
            });
            speakOut("The image is being processed. Please wait");

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
        if (selectedVoice.equals("spanish")) {
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
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//        super.onPointerCaptureChanged(hasCapture);
//    }
}