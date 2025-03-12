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
import com.google.android.material.button.MaterialButton;
import android.widget.ImageView;
import com.google.android.material.textfield.TextInputEditText;


public class fragment_w01_0002_iniciarsesion extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w01_0002_iniciarsesion, container, false);

        // Inicializar vistas
        MaterialButton iconButton = view.findViewById(R.id.iconButton);
        ImageView logo = view.findViewById(R.id.imageView);
        MaterialButton buttonLogin = view.findViewById(R.id.button);
        MaterialButton buttonGoogle = view.findViewById(R.id.buttonGoogle);
        MaterialButton buttonFacebook = view.findViewById(R.id.buttonFacebook);
        TextInputEditText userField = view.findViewById(R.id.textInputEditTextUser);
        TextInputEditText passwordField = view.findViewById(R.id.textInputEditTextPassword);

        // Cargar animaciones
        Animation fadeInScale = AnimationUtils.loadAnimation(getContext(), R.anim.entrada_logo);
        Animation buttonScale = AnimationUtils.loadAnimation(getContext(), R.anim.button_scale);

        // Animar logo al cargar
        logo.startAnimation(fadeInScale);

        // Botón Atrás
        iconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonScale);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Botón Iniciar Sesión
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonScale);
                // Aquí va la lógica de inicio de sesión con email/contraseña
            }
        });

        // Botón Google
        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonScale);
                // Aquí va la lógica de inicio de sesión con Google
            }
        });

        // Botón Facebook
        buttonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonScale);
                // Aquí va la lógica de inicio de sesión con Facebook
            }
        });

        // Animación de enfoque para campos de texto
        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.animate().scaleX(1.02f).scaleY(1.02f).setDuration(200).start();
                } else {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
                }
            }
        };
        userField.setOnFocusChangeListener(focusListener);
        passwordField.setOnFocusChangeListener(focusListener);

        return view;
    }
}