package com.example.proyecto1.ui.reporte;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto1.R;

public class ReporteFragment extends Fragment implements  AdapterView.OnItemSelectedListener, View.OnClickListener {

    private ReporteViewModel reporteViewModel;

    private Spinner spAnomalia;

    private EditText descripcion,ubicacion, fecha;

    private Button aceptar, cancelar;

    private String a;

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

        return root;
    }

    private void iniciaComponentes(View root){
        descripcion = root.findViewById(R.id.eTDescripcionC);
        ubicacion = root.findViewById(R.id.eTUbicacionC);
        fecha = root.findViewById(R.id.eTFechaC);
        aceptar = root.findViewById(R.id.btnAceptarRep);
        cancelar = root.findViewById(R.id.btnCerrarRep);
    }

    private void spinnerComponents(View root){
        ArrayAdapter<CharSequence> anomaliaAdapter;
        anomaliaAdapter = ArrayAdapter.createFromResource(getContext(), R.array.opciones, android.R.layout.simple_spinner_item);

        spAnomalia = (Spinner) root.findViewById(R.id.spnAnomaliaC);
        spAnomalia.setAdapter(anomaliaAdapter);
        spAnomalia.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spnAnomaliaC:
                if (position != 0){
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
        switch (v.getId()){
            case R.id.btnAceptarRep:
                break;
            case R.id.btnCerrarRep:
                break;

        }
    }
}