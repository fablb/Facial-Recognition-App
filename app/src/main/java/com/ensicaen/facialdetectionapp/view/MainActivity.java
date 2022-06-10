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
import android.widget.Toast;

import com.ensicaen.facialdetectionapp.R;
import com.ensicaen.facialdetectionapp.controller.Control;

public class MainActivity extends AppCompatActivity {
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private EditText _name;
    private Control _control;

    private void init() {
        _name = (EditText)findViewById(R.id.name);
        _control = Control.get_instance(this);
        addRegisterButtonListener();
        addCameraButtonListener();
    }

    /**
     * Listener on the button to register a profile
     */
    private void addRegisterButtonListener() {
        ((Button)findViewById(R.id.registerButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (hasCameraPermission()) {
                    enableFaceAcquisition();
                } else {
                    requestPermission();
                    if (hasCameraPermission()) {
                        enableFaceAcquisition();
                    }
                }
                /*
                String name ="";
                try {
                    name = _name.getText().toString();
                    recupProfil(name);
                } catch (Exception e) {}
                if (name == "") {
                    Toast.makeText(MainActivity.this, "Incorect name", Toast.LENGTH_SHORT).show();
                }
                */
            }
        });
    }

    /**
     * Listener on the button to show camera
     */
    private void addCameraButtonListener() {
        ((Button)findViewById(R.id.cameraButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (hasCameraPermission()) {
                    enableFaceDetection();
                } else {
                    requestPermission();
                    if (hasCameraPermission()) {
                        enableFaceDetection();
                    }
                }
            }
        });
    }

    /**
     * Recuperation of the information to create a profil
     * @param name
     */
    private void recupProfil(String name) {
        _control.createProfil(name, this);
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

    private void enableFaceDetection() {
        Intent intent = new Intent(this, CameraView.class);
        intent.putExtra("type", "detection");
        startActivity(intent);
    }

    private void enableFaceAcquisition() {
        Intent intent = new Intent(this, CameraView.class);
        intent.putExtra("type", "acquisition");
        startActivity(intent);
    }
}