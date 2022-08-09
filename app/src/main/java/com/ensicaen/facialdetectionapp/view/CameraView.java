package com.ensicaen.facialdetectionapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.preference.PreferenceManager;

import com.ensicaen.facialdetectionapp.R;
import com.ensicaen.facialdetectionapp.SettingsActivity;
import com.ensicaen.facialdetectionapp.controller.FaceAcquisitionListener;
import com.ensicaen.facialdetectionapp.controller.FaceAuthenticationListener;
import com.ensicaen.facialdetectionapp.controller.FaceDetectorListener;
import com.ensicaen.facialdetectionapp.controller.FrameAnalyzer;
import com.ensicaen.facialdetectionapp.controller.LivenessDetectorListener;
import com.ensicaen.facialdetectionapp.model.Profile;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class CameraView extends AppCompatActivity {
    private PreviewView previewView;
    private CameraOverlay cameraOverlay;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private String _cameraType;
    private ImageAnalysis _imageAnalysis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_view);

        _cameraType = getIntent().getExtras().getString("type");
        previewView = findViewById(R.id.previewView);
        cameraOverlay = findViewById(R.id.camera_overlay);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {}
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        _imageAnalysis = new ImageAnalysis.Builder().build();

        FrameAnalyzer frameAnalyzer = new FrameAnalyzer();

        if (_cameraType.equals("detection")) {
            frameAnalyzer.addFaceListener(new FaceDetectorListener(cameraOverlay, PreferenceManager.getDefaultSharedPreferences(this)));
        } else if (_cameraType.equals("acquisition")) {
            frameAnalyzer.addFaceListener(new FaceAcquisitionListener(cameraOverlay, this));
        } else if (_cameraType.equals("authentication")) {
            frameAnalyzer.addFaceListener(new FaceAuthenticationListener(cameraOverlay, this, (Profile)getIntent().getSerializableExtra("user")));
        } else if (_cameraType.equals("liveness")) {
            frameAnalyzer.addFaceListener(new LivenessDetectorListener(cameraOverlay, this));
        }

        _imageAnalysis.setAnalyzer(Runnable::run, frameAnalyzer);

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, _imageAnalysis);
    }

    public void close(int[] features) {
        _imageAnalysis.clearAnalyzer();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("FEATURES_RESULT", features);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void close(boolean success) {
        _imageAnalysis.clearAnalyzer();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("AUTHENTICATE_RESULT", success);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
