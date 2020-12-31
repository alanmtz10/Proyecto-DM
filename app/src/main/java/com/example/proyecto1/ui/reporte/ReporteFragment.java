package com.example.proyecto1.ui.reporte;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto1.R;
import com.example.proyecto1.ui.fbbd.Reportes;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ReporteFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {


    private ReporteViewModel reporteViewModel;

    private Spinner spAnomalia;

    private EditText descripcion, ubicacion, fecha;

    private Button aceptar, cancelar;

    private ImageButton calendario, btnUbicacion;

    private ImageView imagenCamara;

    private String a, currentPhotoPath, img;

    private Uri photoUri;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private FirebaseStorage storage;

    private StorageReference storageReference;

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference databaseReference;

    private final int REQUEST_CODE = 101;

    private static final int REQUEST_TAKE_PHOTO = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        reporteViewModel =
                new ViewModelProvider(this).get(ReporteViewModel.class);
        View root = inflater.inflate(R.layout.fragment_reporte, container, false);
        reporteViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        iniciaComponentes(root);
        spinnerComponents(root);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getContext());

        FirebaseApp.initializeApp(getContext());

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images");

        return root;
    }

    private void iniciaComponentes(View root) {
        descripcion = root.findViewById(R.id.eTDescripcionC);
        ubicacion = root.findViewById(R.id.eTUbicacionC);
        fecha = root.findViewById(R.id.eTFechaC);

        aceptar = root.findViewById(R.id.btnAceptarRep);
        aceptar.setOnClickListener(this);

        cancelar = root.findViewById(R.id.btnCerrarRep);
        cancelar.setOnClickListener(this);

        calendario = root.findViewById(R.id.btnCalendario);
        calendario.setOnClickListener(this);

        btnUbicacion = root.findViewById(R.id.btnUbicacion);
        btnUbicacion.setOnClickListener(this);

        imagenCamara = root.findViewById(R.id.imagenCamara);
        imagenCamara.setOnClickListener(this);
    }

    private void spinnerComponents(View root) {
        ArrayAdapter<CharSequence> anomaliaAdapter;
        anomaliaAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opciones, android.R.layout.simple_spinner_item);

        spAnomalia = (Spinner) root.findViewById(R.id.spnAnomaliaC);
        spAnomalia.setAdapter(anomaliaAdapter);
        spAnomalia.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spnAnomaliaC:
                if (position != 0) {
                    a = parent.getItemAtPosition(position).toString();
                } else {
                    a = "";
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAceptarRep:
                crearReporte();
                break;
            case R.id.btnCerrarRep:
                limpiar();
                break;
            case R.id.btnCalendario:
                getFecha();
                break;
            case R.id.btnUbicacion:
                getUbicacion();
                break;
            case R.id.imagenCamara:
                tomarFoto();
                break;
        }
    }

    private void crearReporte() {
        if (!descripcion.getText().toString().equals("") && !ubicacion.getText().toString().equals("") && !fecha.getText().toString().equals("") && !currentPhotoPath.equals("")) {

            String id = UUID.randomUUID().toString();

            storageReference.child(id + ".jpg").putFile(photoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        Reportes reporte = new Reportes();

                        reporte.setDescripcion(descripcion.getText().toString());
                        reporte.setUbicacion(ubicacion.getText().toString());
                        reporte.setFecha(fecha.getText().toString());
                        reporte.setAnomalia(a);
                        reporte.setStatus("Pendiente");
                        reporte.setPhotoPath(id);
                        reporte.setUsuario(FirebaseAuth.getInstance().getCurrentUser().getUid());


                        databaseReference.child("Reportes").child(id).setValue(reporte);
                        limpiar();

                        Toast.makeText(getContext(), "Se almaceno el reporte", Toast.LENGTH_LONG).show();


                    }
                }
            });

        } else {
            Toast.makeText(getContext(), "Complete los campos", Toast.LENGTH_LONG).show();
        }

    }

    private void limpiar() {
        descripcion.setText("");
        ubicacion.setText("");
        fecha.setText("");

        imagenCamara.setImageResource(R.drawable.ic_menu_camera);

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void tomarFoto() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getContext(), "Error al tomar la foto!", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(getContext(),
                        "com.example.proyecto1",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }


    }

    public void getFecha() {

        SimpleDateFormat fecha = new SimpleDateFormat("d/M/y", Locale.getDefault());
        String fechaString = fecha.format(new Date());

        this.fecha.setText(fechaString);

    }

    public void getUbicacion() {
        Toast.makeText(getContext(), "Metodo ubicacion", Toast.LENGTH_LONG).show();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    ubicacion.setText("lat: " + location.getLatitude() + "      lon: " + location.getLongitude());
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            imagenCamara.setImageURI(photoUri);
            img = currentPhotoPath;

            Toast.makeText(getContext(), "Se genero la foto", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getContext(), "Error al tomar foto", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUbicacion();
                }
                break;
        }
    }


}