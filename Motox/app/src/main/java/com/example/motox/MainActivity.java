package com.example.motox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.motox.pasajero.RegistroPasajeroAct;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button nConductor = findViewById(R.id.conductor);
        Button nPasajero = findViewById(R.id.pasajero);

        nConductor.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ConductorLoginAct.class);

            startActivity(intent);

            finish();
        });

        nPasajero.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PasajeroLoginAct.class);
            startActivity(intent);
            finish();
        });
    }
}