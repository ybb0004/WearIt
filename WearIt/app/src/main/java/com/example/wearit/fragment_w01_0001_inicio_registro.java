package com.example.wearit;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class fragment_w01_0001_inicio_registro extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public fragment_w01_0001_inicio_registro() {
        // Required empty public constructor
    }

    public static fragment_w01_0001_inicio_registro newInstance(String param1, String param2) {
        fragment_w01_0001_inicio_registro fragment = new fragment_w01_0001_inicio_registro();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_w01_0001_inicio_registro, container, false);

        // Obtener referencia al botón
        Button btnNext = view.findViewById(R.id.btnLogin); // Asegúrate de tener un botón en tu XML con este ID

        // Configurar el evento OnClickListener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el NavController
                NavController navController = Navigation.findNavController(v);

                // Navegar al siguiente fragmento usando la acción definida en el nav_graph.xml
                navController.navigate(R.id.action_fragment_w01_0001_inicio_registro_to_fragment_w01_0002_iniciarsesion);
            }
        });

        return view;
    }
}