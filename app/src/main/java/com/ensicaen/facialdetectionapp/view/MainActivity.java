package com.ensicaen.facialdetectionapp.view;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ensicaen.facialdetectionapp.R;
import com.ensicaen.facialdetectionapp.controller.DBController;

import java.util.Arrays;

public class MainActivity extends Activity {
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    private static final int USER_NAME_CODE = 2;

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
        intent.putExtra("type", "acquisition");
        startActivityForResult(intent, USER_NAME_CODE);
    }

    private void enableFaceDetection() {
        Intent intent = new Intent(this, CameraView.class);
        intent.putExtra("type", "detection");
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_NAME_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String userName = data.getStringExtra("USER_NAME");
                findViewById(R.id.registerDoneImage).setVisibility(View.VISIBLE);
                TextView registerDoneText = findViewById(R.id.registerDoneText);
                registerDoneText.setText(userName + " has been added");
                registerDoneText.setVisibility(View.VISIBLE);
            }
        }
    }
}