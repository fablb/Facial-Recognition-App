package com.ensicaen.facialdetectionapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ensicaen.facialdetectionapp.R;
import com.ensicaen.facialdetectionapp.controller.DBController;

public class MainActivity extends AppCompatActivity {
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addRegisterButtonListener();
        addCameraButtonListener();
    }

    private void addRegisterButtonListener() {
        findViewById(R.id.registerButton).setOnClickListener(v -> {
            if (hasCameraPermission()) {
                enableRegisterView();
            } else {
                requestPermission();
                if (hasCameraPermission()) {
                    enableRegisterView();
                }
            }
        });
    }

    private void addCameraButtonListener() {
        findViewById(R.id.cameraButton).setOnClickListener(v -> {
            if (hasCameraPermission()) {
                enableFaceDetection();
            } else {
                requestPermission();
                if (hasCameraPermission()) {
                    enableFaceDetection();
                }
            }
        });
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    private void enableRegisterView() {
        Intent intent = new Intent(this, RegisterView.class);
        startActivity(intent);
    }

    private void enableFaceDetection() {
        Intent intent = new Intent(this, CameraView.class);
        intent.putExtra("type", "detection");
        startActivity(intent);
    }
}