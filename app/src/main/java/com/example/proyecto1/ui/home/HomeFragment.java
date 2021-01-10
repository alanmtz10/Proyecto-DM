package com.example.proyecto1.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto1.Login;
import com.example.proyecto1.R;
import com.example.proyecto1.ui.fbbd.Usuarios;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private HomeViewModel homeViewModel;

    TextView usuario;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Button btnCerrar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {}
        });

        usuario = root.findViewById(R.id.usuarioHome);
        btnCerrar = root.findViewById(R.id.btnCerrarS);
        btnCerrar.setOnClickListener(this);

        //FirebaseApp.initializeApp(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth=FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String UID=currentUser.getUid();
        databaseReference.child("Usuarios").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuarios us = snapshot.getValue(Usuarios.class);
                if (us.getTipo().equals("Usuario")){
                    usuario.setText(us.getUsuario());
                }else{
                    usuario.setText(us.getUsuario()+"\r\n"+"Administrador");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnCerrarS:
                firebaseAuth.signOut();
                Toast.makeText(getContext(),"Cerró Sesión",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getContext(), Login.class));
                getActivity().finish();
                break;
        }
    }
}