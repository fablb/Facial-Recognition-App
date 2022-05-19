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
        this._control = Control.get_instance(this);
        addProfilButtonListener();
    }

    private void addProfilButtonListener() {
        ((Button)findViewById(R.id.addButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Profil ajouté", Toast.LENGTH_SHORT).show();
                String name ="";
                try {
                    name = _name.toString();
                } catch (Exception e) {}
                if (name == "") {
                    Toast.makeText(MainActivity.this, "Incorect name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void recupProfil(String name) {
        this._control.createProfil(name, this);
    }
}