package com.example.proyecto1.ui.listar;

import android.app.AlertDialog;
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

import com.example.proyecto1.ui.fbbd.Reportes;
import com.example.proyecto1.ui.listar.ListarViewModel;

import com.example.proyecto1.R;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
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

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    List<Reportes> ListaReportes = new ArrayList<Reportes>();

    ArrayList<String> imagenes;

    ArrayAdapter<Reportes> arrayAdapterReportes;
    Reportes reporteSelected;

    ListView lReportes;

    private FirebaseStorage storage;
    StorageReference storageReference;

    Long img;


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
                        item += "Id: ["+ reporteSelected.getPhotoPath()+"]\r\n";
                        item += "Anomalía: ["+ reporteSelected.getAnomalia()+"]\r\n";
                        item += "Descripción: ["+ reporteSelected.getDescripcion()+"]\r\n";
                        item += "Fecha: ["+ reporteSelected.getFecha()+"]\r\n";
                        item += "Ubicación: ["+ reporteSelected.getUbicacion()+"]\r\n";
                        item += "Estado: ["+ reporteSelected.getStatus()+"]\r\n";
                        ListData.add(item);

                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_reporte, null);
                ((TextView) dialogView.findViewById(R.id.tvInfoReporte)).setText(item);
                ImageView ivImagen = dialogView.findViewById(R.id.ivFotoReporte);
                try{
                    descargarImg();

                }catch (Exception e){
                    //Toast.makeText(getContext(),"Error con la foto",Toast.LENGTH_LONG).show();
                }
                //cargarImagen(imagenes.get(position), ivImagen);
                AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                dialogo.setTitle("Reportes");
                dialogo.setView(dialogView);
                dialogo.setPositiveButton("Aceptar", null);
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

    public void descargarImg () throws IOException {

        storageReference.child(reporteSelected.getPhotoPath()+ ".jpg");
        File localFile = File.createTempFile("images","jpg");

        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>(){

            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(),"Imagen Descargada",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Error en descarga",Toast.LENGTH_LONG).show();
            }
        });
    }




    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}