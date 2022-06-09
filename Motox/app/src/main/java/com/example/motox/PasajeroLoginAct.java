package com.example.motox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motox.pasajero.PasajeroMapAct;
import com.example.motox.pasajero.RegistroPasajeroAct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasajeroLoginAct extends AppCompatActivity {
    private EditText nEmail, nContrasena;
    private Button nLogin, nRegistrarse;
    private ImageView regresar;

    private FirebaseAuth nAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasajero_login);

        nAuth = FirebaseAuth.getInstance();
        this.regresar = findViewById(R.id.regresar2);

        firebaseAuthListener = firebaseAuth -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(PasajeroLoginAct.this, PasajeroMapAct.class);
                startActivity(intent);
                finish();

            }

        };

        this.regresar.setOnClickListener(l->{
            Intent intent = new Intent(PasajeroLoginAct.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


        nEmail =  findViewById(R.id.email);
        nContrasena =  findViewById(R.id.contrasena);

        nLogin =  findViewById(R.id.login);
        nRegistrarse = findViewById(R.id.registro_cond);

        nRegistrarse.setOnClickListener(v -> {

            Intent intent = new Intent(PasajeroLoginAct.this, RegistroPasajeroAct.class);
            startActivity(intent);
            finish();

        });

        nLogin.setOnClickListener(v -> {
            final String email = nEmail.getText().toString();
            final String contrasena = nContrasena.getText().toString();
            nAuth.signInWithEmailAndPassword(email, contrasena).addOnCompleteListener(PasajeroLoginAct.this, task -> {
                if (!task.isSuccessful()){
                    Toast.makeText(PasajeroLoginAct.this, "Error de Conexion", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        nAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        nAuth.removeAuthStateListener(firebaseAuthListener);

    }
}
