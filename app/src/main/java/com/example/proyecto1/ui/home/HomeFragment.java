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
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private HomeViewModel homeViewModel;

    TextView usuario;

    FirebaseAuth firebaseAuth;

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

        firebaseAuth=FirebaseAuth.getInstance();
        btnCerrar = root.findViewById(R.id.btnCerrarS);
        btnCerrar.setOnClickListener(this);

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