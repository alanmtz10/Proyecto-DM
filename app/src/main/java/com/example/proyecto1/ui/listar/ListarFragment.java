package com.example.proyecto1.ui.listar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.proyecto1.ui.fbbd.Reportes;
import com.example.proyecto1.ui.fbbd.Usuarios;
import com.example.proyecto1.ui.listar.ListarViewModel;

import com.example.proyecto1.R;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListarFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String tipoUs;

    List<Reportes> ListaReportes = new ArrayList<Reportes>();

    ArrayAdapter<Reportes> arrayAdapterReportes;
    Reportes reporteSelected;

    ListView lReportes;

    FirebaseStorage storage;
    StorageReference storageReference, imageref;

    private ListarViewModel listarViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listarViewModel =
                new ViewModelProvider(this).get(ListarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_listar, container, false);
        listarViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        FirebaseApp.initializeApp(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String UID=currentUser.getUid();
        databaseReference.child("Usuarios").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuarios usuario = snapshot.getValue(Usuarios.class);
                if (usuario.getTipo().equals("Usuario")){
                    tipoUs="USUARIO";
                }else{
                    tipoUs="ADMINISTRADOR";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images");

        lReportes = root.findViewById(R.id.lVReportes);

        listarDatos();

        lReportes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reporteSelected = (Reportes) parent.getItemAtPosition(position);
                ArrayList<String> ListData = new ArrayList<>();
                String item = "";
                        item += "ID: "+ reporteSelected.getPhotoPath()+"\r\n";
                        item += "ANOMALÍA:      "+ reporteSelected.getAnomalia()+"\r\n";
                        item += "DESCRIPCIÓN: "+ reporteSelected.getDescripcion()+"\r\n";
                        item += "FECHA:              "+ reporteSelected.getFecha()+"\r\n";
                        item += "UBICACIÓN:     "+ reporteSelected.getUbicacion()+"\r\n";
                        item += "ESTADO:           "+ reporteSelected.getStatus()+"\r\n";
                        ListData.add(item);

                imageref = storageReference.child(reporteSelected.getPhotoPath()+ ".jpg");

                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_reporte, null);
                ((TextView) dialogView.findViewById(R.id.tvInfoReporte)).setText(item);
                ImageView ivImagen = dialogView.findViewById(R.id.ivFotoReporte);
                Glide.with(getContext()).load(imageref).into(ivImagen);
                AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                dialogo.setTitle("Reportes");
                dialogo.setView(dialogView);
                if (tipoUs.equals("USUARIO")){
                    dialogo.setPositiveButton("Aceptar", null);
                }
                else
                {
                    dialogo.setNeutralButton("Aceptar", null);
                    if (reporteSelected.getStatus().toUpperCase().equals("PENDIENTE")){
                        dialogo.setPositiveButton("Atender Reporte", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Reportes r = new Reportes();
                                r.setAnomalia(reporteSelected.getAnomalia());
                                r.setDescripcion(reporteSelected.getDescripcion());
                                r.setUbicacion(reporteSelected.getUbicacion());
                                r.setFecha(reporteSelected.getFecha());
                                r.setPhotoPath(reporteSelected.getPhotoPath());
                                r.setUsuario(reporteSelected.getUsuario());
                                r.setStatus("Atendido");
                                databaseReference.child("Reportes").child(r.getPhotoPath()).setValue(r);
                                Toast.makeText(getContext(),"Reporte Atendido",Toast.LENGTH_LONG).show();
                                listarDatos();
                            }
                        });
                    }
                    else
                    {
                        dialogo.setNegativeButton("Eliminar Reporte", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                imageref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Reportes r = new Reportes();
                                        r.setPhotoPath(reporteSelected.getPhotoPath());
                                        databaseReference.child("Reportes").child(r.getPhotoPath()).removeValue();
                                        Toast.makeText(getContext(),"Reporte Eliminado",Toast.LENGTH_LONG).show();
                                        listarDatos();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),"Error al eliminar la foto",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }

                }
                dialogo.show();
            }
        });

        return root;
    }

    public void listarDatos(){
        databaseReference.child("Reportes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListaReportes.clear();
                for (DataSnapshot objSnapshot: snapshot.getChildren()){
                    Reportes p = objSnapshot.getValue(Reportes.class);
                    ListaReportes.add(p);
                    arrayAdapterReportes = new ArrayAdapter<Reportes>(getContext(), android.R.layout.simple_list_item_1,ListaReportes);
                    lReportes.setAdapter(arrayAdapterReportes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}