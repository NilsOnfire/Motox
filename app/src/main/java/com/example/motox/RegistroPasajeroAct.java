package com.example.motox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class RegistroPasajeroAct extends AppCompatActivity {

    private ImageView regresar;
    private Button registro;
    private EditText nombre,cedula,telefono,direccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_pasajero);

        this.regresar = (ImageView) findViewById(R.id.regresar3);

        this.registro = (Button) findViewById(R.id.btn_registro_pasajero);

        this.regresar.setOnClickListener(l->{
            Intent intent = new Intent(RegistroPasajeroAct.this, PasajeroLoginAct.class);
            startActivity(intent);
            finish();
        });




    }
}