package com.example.wearit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import android.widget.ImageView;


public class fragment_w01_0001_inicio_registro extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w01_0001_inicio_registro, container, false);

        // Inicializar vistas
        ImageView logo = view.findViewById(R.id.imageViewLogo);
        ImageView lema = view.findViewById(R.id.imageViewLema);
        MaterialButton btnLogin = view.findViewById(R.id.btnLogin);

        MaterialButton btnRegister = view.findViewById(R.id.btnRegister);

        // Cargar animaciones
        Animation fadeInScale = AnimationUtils.loadAnimation(getContext(), R.anim.entrada_logo);
        Animation buttonScale = AnimationUtils.loadAnimation(getContext(), R.anim.button_scale);

        // Animar logo y lema al cargar
        logo.startAnimation(fadeInScale);
        lema.startAnimation(fadeInScale);

        // Botón Registrarse
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonScale);
                //navegar hasta el formulario de registro
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_fragment_w01_0001_inicio_registro_to_fragment_w01_0003_registro3);




            }});

        // Botón Iniciar Sesión
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonScale);
                // Navegar al fragmento de inicio de sesión (ajusta según tu estructura)
                // Ejemplo: getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).addToBackStack(null).commit();
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_fragment_w01_0001_inicio_registro_to_fragment_w01_0002_iniciarsesion);


            }
        });

        return view;
    }
}