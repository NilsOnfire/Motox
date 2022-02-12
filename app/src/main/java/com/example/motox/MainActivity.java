package com.example.motox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button nConductor, nPasajero;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nConductor = (Button) findViewById(R.id.conductor);
        nPasajero = (Button) findViewById(R.id.pasajero);

        nConductor.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ConductorLoginAct.class);
            startActivity(intent);
            finish();
            return;
        });

        nPasajero.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PasajeroLoginAct.class);
            startActivity(intent);
            finish();
            return;
        });
    }
}