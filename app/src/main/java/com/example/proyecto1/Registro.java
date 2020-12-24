package com.example.proyecto1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.proyecto1.databinding.ActivityMainBinding;
import com.google.android.gms.common.data.DataBufferSafeParcelable;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    EditText nom, dir, edad, tel, us, correo, contrasenia;
    Button btnAceptar, btnCancelar;
    ToggleButton btnTipo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference();

        iniciarComp();


        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nom.getText().toString().equals("") || dir.getText().toString().equals("") ||
                        edad.getText().toString().equals("") || tel.getText().toString().equals("") ||
                        us.getText().toString().equals("") || correo.getText().toString().equals("") ||
                        contrasenia.getText().toString().equals("")) {
                    Toast.makeText(Registro.this, "Campos incompletos", Toast.LENGTH_SHORT).show();
                } else {
                    registrarUsuario();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public void iniciarComp() {
        nom = findViewById(R.id.eTNombreR);
        dir = findViewById(R.id.eTDireccionR);
        edad = findViewById(R.id.eTEdadR);
        tel = findViewById(R.id.eTTelefonoR);
        us = findViewById(R.id.eTUsuarioR);
        correo = findViewById(R.id.eTCorreoR);
        contrasenia = findViewById(R.id.eTContraseniaR);
        btnAceptar = findViewById(R.id.btnRegistraR);
        btnCancelar = findViewById(R.id.btnCerrarR);
        btnTipo = findViewById(R.id.tBTipoR);
    }

    public void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void registrarUsuario() {
        String email = correo.getText().toString();
        String password = correo.getText().toString();
        String tipo = btnTipo.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = firebaseAuth.getCurrentUser().getUid();

                    Map<String, Object> map = new HashMap<>();
                    map.put("nombre", nom.getText().toString());
                    map.put("usuario", us.getText().toString());
                    map.put("correo", correo.getText().toString());
                    map.put("contrasenia", contrasenia.getText().toString());
                    map.put("tipo", tipo);
                    map.put("direccion", dir.getText().toString());
                    map.put("edad", edad.getText().toString());
                    map.put("telefono", tel.getText().toString());

                    databaseReference.child("Usuarios").child(id).setValue(map);

                    databaseReference.child("Usuarios");

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    Toast.makeText(Registro.this, "No se pudo registrar el usuario", Toast.LENGTH_LONG).show();
                    updateUI(null);
                }
            }
        });
    }
}