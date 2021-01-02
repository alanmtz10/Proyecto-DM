package com.example.proyecto1.ui.usuarios;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto1.R;
import com.example.proyecto1.ui.fbbd.Usuarios;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UsuariosFragment extends Fragment {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    List<Usuarios> ListaUsuarios = new ArrayList<Usuarios>();

    ArrayList<String> imagenes;

    ArrayAdapter<Usuarios> arrayAdapterUsuario;
    Usuarios usuarioSelected;

    ListView lUsuarios;

    private FirebaseStorage storage;
    StorageReference storageReference;



    private UsuariosViewModel mViewModel;

    public static UsuariosFragment newInstance() {
        return new UsuariosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_usuarios, container, false);

        lUsuarios=root.findViewById(R.id.lVUsuarios);
        FirebaseApp.initializeApp(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        listarDatos();

        lUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                usuarioSelected=(Usuarios)parent.getItemAtPosition(position);
                ArrayList<String> ListData = new ArrayList<>();
                String item = "";
                item += "Id: "+usuarioSelected.getId()+ "\r\n";
                item += "Nombre: "+ usuarioSelected.getNombre()+"\r\n";
                item += "Nombre de usuario: "+ usuarioSelected.getUsuario()+"\r\n";
                item += "Tipo de usuario: "+ usuarioSelected.getTipo()+"\r\n";
                item += "Correo: "+ usuarioSelected.getCorreo()+"\r\n";
                item += "Contraseña: "+ usuarioSelected.getContrasenia()+"\r\n";
                item += "Dirección: "+ usuarioSelected.getDireccion()+"\r\n";
                item += "Edad: "+ usuarioSelected.getEdad()+"\r\n";
                item += "Teléfono: "+ usuarioSelected.getTelefono()+"\r\n";
                ListData.add(item);
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_reporte, null);
                ((TextView) dialogView.findViewById(R.id.tvInfoReporte)).setText(item);
                ImageView ivImagen = dialogView.findViewById(R.id.ivFotoReporte);
                AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                dialogo.setTitle("Usuario");
                dialogo.setView(dialogView);
                dialogo.setNeutralButton("Aceptar", null);
                dialogo.setPositiveButton("Cambiar Rango", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Usuarios u = new Usuarios();
                        u.setId(usuarioSelected.getId());
                        u.setNombre(usuarioSelected.getNombre());
                        u.setUsuario(usuarioSelected.getUsuario());
                        u.setCorreo(usuarioSelected.getCorreo());
                        u.setContrasenia(usuarioSelected.getContrasenia());
                        u.setDireccion(usuarioSelected.getDireccion());
                        u.setEdad(usuarioSelected.getEdad());
                        u.setTelefono(usuarioSelected.getTelefono());
                        if (usuarioSelected.getTipo().toUpperCase().equals("USUARIO")){
                            u.setTipo("Administrador");
                        }
                        else{
                            u.setTipo("Usuario");
                        }
                        databaseReference.child("Usuarios").child(u.getId()).setValue(u);
                        Toast.makeText(getContext(),"Rango Actualizado",Toast.LENGTH_LONG).show();

                    }
                });
                dialogo.setNegativeButton("Eliminar Usuario", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Usuarios u = new Usuarios();
                        u.setId(usuarioSelected.getId());
                        databaseReference.child("Usuarios").child(u.getId()).removeValue();
                        Toast.makeText(getContext(),"Usuario Eliminado",Toast.LENGTH_LONG).show();

                    }
                });
                dialogo.show();

            }
        });

        return root;
    }

    public void listarDatos(){
        databaseReference.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListaUsuarios.clear();
                for (DataSnapshot objSnapshot: snapshot.getChildren()){
                    Usuarios p = objSnapshot.getValue(Usuarios.class);
                    ListaUsuarios.add(p);
                    arrayAdapterUsuario = new ArrayAdapter<Usuarios>(getContext(), android.R.layout.simple_list_item_1,ListaUsuarios);
                    lUsuarios.setAdapter(arrayAdapterUsuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UsuariosViewModel.class);

    }

}