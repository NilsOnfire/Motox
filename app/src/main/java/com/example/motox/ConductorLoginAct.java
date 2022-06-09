package com.example.motox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ConductorLoginAct extends AppCompatActivity {

    private EditText nEmail, nContrasena;
    private Button nLogin, nRegistrarse;

    private FirebaseAuth nAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ImageView regresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conductor_login);
        this.regresar = (ImageView) findViewById(R.id.regresar);

        nAuth = FirebaseAuth.getInstance();


        firebaseAuthListener = firebaseAuth -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(ConductorLoginAct.this, ConductorMapAct.class);
                startActivity(intent);
                finish();
                return;

            }

        };

        this.regresar.setOnClickListener(l ->{
            Intent intent = new Intent(ConductorLoginAct.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


        nEmail = (EditText) findViewById(R.id.email);
        nContrasena = (EditText) findViewById(R.id.contrasena);

        nLogin = (Button) findViewById(R.id.login);
        nRegistrarse = (Button) findViewById(R.id.registrarse);

        nRegistrarse.setOnClickListener(v -> {

            final String email = nEmail.getText().toString();
            final String contrasena = nContrasena.getText().toString();
            nAuth.createUserWithEmailAndPassword(email, contrasena).addOnCompleteListener(ConductorLoginAct.this, task -> {
                if (!task.isSuccessful()){
                    Toast.makeText(ConductorLoginAct.this, "Error al Registrarse", Toast.LENGTH_SHORT).show();
                }else{
                    String user_id = nAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("usuarios").child("conductor").child(user_id);
                    current_user_db.setValue(true);
                }

            });

        });

        nLogin.setOnClickListener(v -> {
            final String email = nEmail.getText().toString();
            final String contrasena = nContrasena.getText().toString();
            nAuth.signInWithEmailAndPassword(email, contrasena).addOnCompleteListener(ConductorLoginAct.this, task -> {
               if (!task.isSuccessful()){
                   Toast.makeText(ConductorLoginAct.this, "Error de Conexion", Toast.LENGTH_SHORT).show();
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