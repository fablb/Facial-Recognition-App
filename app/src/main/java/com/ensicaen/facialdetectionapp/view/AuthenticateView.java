package com.ensicaen.facialdetectionapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.ensicaen.facialdetectionapp.R;
import com.ensicaen.facialdetectionapp.controller.DBController;
import com.ensicaen.facialdetectionapp.model.Profile;

import java.io.Serializable;
import java.util.Arrays;

public class AuthenticateView extends Activity {
    private EditText _nameInput;
    private static final int AUTHENTICATION_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticate_view);
        _nameInput = findViewById(R.id.authenticateNameInput);
        addContinueButtonListener();
    }

    private void addContinueButtonListener() {
        findViewById(R.id.authenticateContinueButton).setOnClickListener(v -> {
            String name = _nameInput.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(AuthenticateView.this, "Name is empty", Toast.LENGTH_SHORT).show();
            } else {
                Log.i("FaceDetectionApp", "authView");
                Profile user = DBController.getInstance(this).getProfile(name);
                if (user == null) {
                    Toast.makeText(AuthenticateView.this, "User \'"+ name +"\' is not in the database", Toast.LENGTH_LONG).show();
                    return;
                }
                enableFaceAuthentication(user);
            }
        });
    }

    private void enableFaceAuthentication(Profile user) {
        Log.i("FaceDetectionApp", "enableFaceAuth");
        Intent intent = new Intent(this, CameraView.class);
        intent.putExtra("type", "authentication");
        intent.putExtra("user", user);
        startActivityForResult(intent, AUTHENTICATION_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTHENTICATION_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean success = data.getBooleanExtra("AUTHENTICATE_RESULT", false);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("AUTHENTICATE_RESULT", success);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    }
}
