package com.example.proyecto1.ui.reporte;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.CarrierConfigManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto1.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReporteFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private ReporteViewModel reporteViewModel;

    private Spinner spAnomalia;

    private EditText descripcion, ubicacion, fecha;

    private Button aceptar, cancelar;

    private ImageButton calendario, btnUbicacion;

    private String a;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private FirebaseStorage storage;

    private StorageReference storageReference;

    private final int REQUEST_CODE = 101;

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
                break;
            case R.id.btnCerrarRep:
                break;
            case R.id.btnCalendario:
                getFecha();
                break;
            case R.id.btnUbicacion:
                getUbicacion();
                break;
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
                    ubicacion.setText("lat: " + location.getLatitude() + " lon: " + location.getLongitude());
                }
            }
        });

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