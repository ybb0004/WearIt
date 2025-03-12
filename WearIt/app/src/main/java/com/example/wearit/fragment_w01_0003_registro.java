package com.example.wearit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.CheckBox;
import android.widget.ProgressBar;

public class fragment_w01_0003_registro extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflamos el layout
        View view = inflater.inflate(R.layout.fragment_w01_0003_registro, container, false);

        // Inicializamos las vistas
        MaterialButton backButton = view.findViewById(R.id.iconButton);
        MaterialButton continueButton = view.findViewById(R.id.button2Registro);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TextInputEditText nameField = view.findViewById(R.id.editTextUsuarioRegistro);
        TextInputEditText usernameField = view.findViewById(R.id.editTextNombreRegistro);
        TextInputEditText emailField = view.findViewById(R.id.editTextMailRegistro);
        TextInputEditText passwordField = view.findViewById(R.id.editTextContrasenaRegistro);
        TextInputEditText confirmPasswordField = view.findViewById(R.id.editTextContrasena2Registro);
        CheckBox checkboxPrivacidad = view.findViewById(R.id.checkboxPrivacidadRegistro);

        // Cargamos la animación
        Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.button_scale);

        // Evento del botón Atrás
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(scaleAnimation);
                // Regresar al fragmento anterior
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Evento del botón Continuar
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(scaleAnimation);
                progressBar.setVisibility(View.VISIBLE);
                continueButton.setEnabled(false);

                // Simulación de envío del formulario (reemplazar con lógica real)
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        continueButton.setEnabled(true);
                        // Aquí puedes añadir la lógica para validar y enviar los datos
                        // Ejemplo: navigateToNextFragment();
                    }
                }, 2000); // Retraso de 2 segundos para demo
            }
        });

        // Animaciones de enfoque para los campos de texto
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

        nameField.setOnFocusChangeListener(focusListener);
        usernameField.setOnFocusChangeListener(focusListener);
        emailField.setOnFocusChangeListener(focusListener);
        passwordField.setOnFocusChangeListener(focusListener);
        confirmPasswordField.setOnFocusChangeListener(focusListener);

        // Hacer el texto "Política de Privacidad" clickable en el CheckBox
        String textoCompleto = getString(R.string.leyPrivacidad);
        SpannableString spannableString = new SpannableString(textoCompleto);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Abrir la URL de la política de privacidad
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ejemplo.com/politica-privacidad"));
                startActivity(intent);
            }
        };

        // Definir el rango clickable ("Política de Privacidad")
        int start = textoCompleto.indexOf("Política de Privacidad");
        int end = start + "Política de Privacidad".length();
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        checkboxPrivacidad.setText(spannableString);
        checkboxPrivacidad.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

    // Método opcional para navegar al siguiente fragmento (ejemplo)
    private void navigateToNextFragment() {
        // Reemplazar con tu lógica de navegación
        // Ejemplo:
        // getActivity().getSupportFragmentManager()
        //         .beginTransaction()
        //         .replace(R.id.fragment_container, new NextFragment())
        //         .addToBackStack(null)
        //         .commit();
    }
}