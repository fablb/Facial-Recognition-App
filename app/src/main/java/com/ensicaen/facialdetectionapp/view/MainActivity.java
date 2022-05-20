package com.ensicaen.facialdetectionapp.view;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ensicaen.facialdetectionapp.R;
import com.ensicaen.facialdetectionapp.controler.Control;

public class MainActivity extends AppCompatActivity {

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
        addProfilButtonListener();
    }

    /**
     * Listener on the button to create profil
     */
    private void addProfilButtonListener() {
        ((Button)findViewById(R.id.addButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name ="";
                try {
                    name = _name.getText().toString();
                    recupProfil(name);
                } catch (Exception e) {}
                if (name == "") {
                    Toast.makeText(MainActivity.this, "Incorect name", Toast.LENGTH_SHORT).show();
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
}