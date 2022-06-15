package com.ensicaen.facialdetectionapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ensicaen.facialdetectionapp.R;
import com.ensicaen.facialdetectionapp.controller.DBController;

import java.util.Arrays;

public class RegisterView extends Activity {
    private EditText _nameInput;
    private static final int FEATURES_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_view);
        _nameInput = findViewById(R.id.registerNameInput);
        addContinueButtonListener();
    }

    private void addContinueButtonListener() {
        findViewById(R.id.registerContinueButton).setOnClickListener(v -> {
            String name = _nameInput.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(RegisterView.this, "Name is empty", Toast.LENGTH_SHORT).show();
            } else {
                enableFaceAcquisition();
            }
        });
    }

    private void enableFaceAcquisition() {
        Intent intent = new Intent(this, CameraView.class);
        intent.putExtra("type", "acquisition");
        startActivityForResult(intent, FEATURES_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FEATURES_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                int[] features = data.getIntArrayExtra("FEATURES_RESULT");
                Log.i("FaceDetectionApp", Arrays.toString(features));
                DBController db = DBController.getInstance(this);
                String userName = _nameInput.getText().toString();
                db.createProfile(_nameInput.getText().toString(), features);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("USER_NAME", userName);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    }
}
