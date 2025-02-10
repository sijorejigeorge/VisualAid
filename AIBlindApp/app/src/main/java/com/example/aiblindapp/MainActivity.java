package com.example.aiblindapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private ImageButton capture, toggleFlash, flipCamera;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private TextToSpeech textToSpeech;
    private Camera camera;
    Handler handler;
    Voice selectedVoice;
    private int cameraFacing = CameraSelector.LENS_FACING_BACK;
    private String selectedVoiceName;
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                promptOpenCamera();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        handler = new Handler(Looper.getMainLooper());
        previewView = findViewById(R.id.cameraPreview);
        capture = findViewById(R.id.capture);
        toggleFlash = findViewById(R.id.toggleFlash);
        flipCamera = findViewById(R.id.flipCamera);
        Intent intent = getIntent();
        selectedVoiceName = intent.getStringExtra("selectedVoiceName");
        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
//                textToSpeech.setLanguage(Locale.getDefault());
                selectedVoice = getDesiredVoice(selectedVoiceName);
                textToSpeech.setVoice(selectedVoice);

                initializeSpeechRecognizer();
                promptOpenCamera();
            }
        });

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        // Check and request permissions
        if (!hasPermissions()) {
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        } else {
            promptOpenCamera();
        }

        flipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
                    cameraFacing = CameraSelector.LENS_FACING_FRONT;
                } else {
                    cameraFacing = CameraSelector.LENS_FACING_BACK;
                }
                startCamera(cameraFacing);
            }
        });

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    takePicture();
                }
            }
        });

        toggleFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFlashIcon();
            }
        });
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera(int cameraFacing) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing)
                        .build();

                cameraProvider.unbindAll();

                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Prompt user to say "click" to capture image
                speak("Please say click to capture the image.");
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    //    private void takePicture() {
//        if (imageCapture == null) {
//            return;
//        }
//
//        File file = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
//        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
//        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
//            @Override
//            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Image saved at: " + file.getPath(), Toast.LENGTH_SHORT).show());
//                startCamera(cameraFacing);
//            }
//
//            @Override
//            public void onError(@NonNull ImageCaptureException exception) {
//                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to save: " + exception.getMessage(), Toast.LENGTH_SHORT).show());
//                startCamera(cameraFacing);
//            }
//        });
//    }
    private void takePicture() {
        if (imageCapture == null) {
            return;
        }

        File file = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Image saved at: " + file.getPath(), Toast.LENGTH_SHORT).show();

                    // Start DisplayImageActivity with the image path
                    Intent intent = new Intent(MainActivity.this, camerapicture.class);
                    intent.putExtra("image_path", file.getAbsolutePath());
                    intent.putExtra("selectedVoiceName", selectedVoiceName);
                    startActivity(intent);
                    finish();

                });
              //  startCamera(cameraFacing);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to save: " + exception.getMessage(), Toast.LENGTH_SHORT).show());
                startCamera(cameraFacing);
            }
        });
    }

    private void setFlashIcon() {
        if (camera != null && camera.getCameraInfo().hasFlashUnit()) {
            boolean isTorchOn = camera.getCameraInfo().getTorchState().getValue() == 1;
            camera.getCameraControl().enableTorch(!isTorchOn);
            toggleFlash.setImageResource(isTorchOn ? R.drawable.baseline_flash_on_24 : R.drawable.baseline_no_flash_24);
        } else {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Flash is not available currently", Toast.LENGTH_SHORT).show());
        }
    }

    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d(TAG, "Ready for speech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "Beginning of speech");
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
                Log.d(TAG, "End of speech");
            }

            @Override
            public void onError(int error) {
                Log.e(TAG, "Error: " + error);
                startListening(); // Restart listening after an error
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    for (String match : matches) {
                        Log.d(TAG, "Match: " + match);
                        if (match.equalsIgnoreCase("yes")) {
                            startCamera(cameraFacing);

                            capture.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    takePicture();
                                }
                            });
                        } else if (match.equalsIgnoreCase("click")) {
                            takePicture();
                            textToSpeech.speak("Thank you. The image has been clicked", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        else{
                           // speechRecognizer.stopListening();
                           // textToSpeech.speak("Please say yes to open the camera and if the camera is opened, please say click to capture the image", TextToSpeech.QUEUE_FLUSH, null, null);
                            // speechRecognizer.stopListening();
                            //startListening();
                           // speakAfterDelay("Please say yes to open the camera and if the camera is opened, please say click to capture the image.", 10000);
                        }

                    }
                }
                else{
                    textToSpeech.speak("Please say yes to open the camera", TextToSpeech.QUEUE_FLUSH, null, null);
                    startListening();
                }
                startListening();
                // Restart listening for further commands
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

        startListening();
    }
    private void speakAfterDelay(String text, long delayInMillis) {
        handler.postDelayed(() -> {
            if (textToSpeech != null) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_ID_DELAYED");
            }
        }, delayInMillis); // Delay in milliseconds
    }
    private void startListening() {
        Log.d(TAG, "Start listening");
        speechRecognizer.startListening(speechRecognizerIntent);
    }

    private void promptOpenCamera() {
        speak("Do you want me to open the camera? Please say yes. Until then you wont be able to move forward");
    }

    private void speak(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
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
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
