package com.ensicaen.facialdetectionapp.view;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ensicaen.facialdetectionapp.R;

public class MainActivity extends Activity {
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    private static final int REGISTER_CODE = 2;
    private static final int AUTHENTICATE_CODE = 3;
    private ImageView _imageStatus;
    private TextView _textStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _imageStatus = findViewById(R.id.statusImage);
        _textStatus = findViewById(R.id.statusText);
        addRegisterButtonListener();
        addAuthenticateButtonListener();
        addLivenessButtonListener();
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

    private void addAuthenticateButtonListener() {
        findViewById(R.id.cameraButton).setOnClickListener(v -> {
            if (hasCameraPermission()) {
                enableAuthenticateView();
            } else {
                requestPermission();
                if (hasCameraPermission()) {
                    enableAuthenticateView();
                }
            }
        });
    }

    private void addLivenessButtonListener() {
        findViewById(R.id.livenessButton).setOnClickListener(v -> {
            if (hasCameraPermission()) {
                enableLivenessDetection();
            } else {
                requestPermission();
                if (hasCameraPermission()) {
                    enableLivenessDetection();
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

    private void enableAuthenticateView() {
        Intent intent = new Intent(this, AuthenticateView.class);
        startActivityForResult(intent, AUTHENTICATE_CODE);
    }

    private void enableRegisterView() {
        Intent intent = new Intent(this, RegisterView.class);
        startActivityForResult(intent, REGISTER_CODE);
    }

    private void enableLivenessDetection() {
        Intent intent = new Intent(this, CameraView.class);
        intent.putExtra("type", "liveness");
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REGISTER_CODE) {
                String userName = data.getStringExtra("USER_NAME");
                _imageStatus.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);
                _textStatus.setText(userName + " has been added");
                _imageStatus.setVisibility(View.VISIBLE);
                _textStatus.setVisibility(View.VISIBLE);
            } else if (requestCode == AUTHENTICATE_CODE) {
                boolean success = data.getBooleanExtra("AUTHENTICATE_RESULT", false);
                if (success) {
                    _imageStatus.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);
                    _textStatus.setText("Successful authentication!");
                } else {
                    _imageStatus.setImageResource(R.drawable.ic_baseline_cancel_24);
                    _textStatus.setText("Authentication failed!");
                }
                _imageStatus.setVisibility(View.VISIBLE);
                _textStatus.setVisibility(View.VISIBLE);
            }
        }
    }
}