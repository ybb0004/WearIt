package com.example.wearit;

import android.content.Intent;
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

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class fragment_w01_0002_iniciarsesion extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w01_0002_iniciarsesion, container, false);

        // Inicializar vistas
        MaterialButton iconButton = view.findViewById(R.id.iconButton);
        ImageView logo = view.findViewById(R.id.imageView);
        MaterialButton buttonLogin = view.findViewById(R.id.button);

        TextInputEditText userField = view.findViewById(R.id.textInputEditTextUser);
        TextInputEditText passwordField = view.findViewById(R.id.textInputEditTextPassword);

        userField.setText("test@wearit.com"); //valores por defecto para pruebas
        passwordField.setText("wearit2025");//valores por defecto para pruebas

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


                // Obtener referencias a los campos de texto
                EditText editTextEmail = getView().findViewById(R.id.textInputEditTextUser); // Cambia al ID de tu campo de correo
                EditText editTextPassword = getView().findViewById(R.id.textInputEditTextPassword); // Cambia al ID de tu campo de contraseña

                // Obtener el correo y la contraseña
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validar que los campos no estén vacíos
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Iniciar sesión con Firebase Authentication
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), task -> {
                            if (task.isSuccessful()) {
                                // Inicio de sesión exitoso
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    // Navegar a la siguiente pantalla o realizar acciones adicionales

                                    // Ejemplo: Navegar a otro fragmento
                                        //NavController navController = Navigation.findNavController(v);
                                        //navController.navigate(R.id.action_fragment_w01_0002_iniciarsesion_to_fragment_w02_0001_home2);
                                    // Dentro del método onClick del botón de inicio de sesión
                                    Intent intent = new Intent(getActivity(), activity_w020000_home_app.class);
                                    startActivity(intent);
                                }
                            } else {
                                // Error en el inicio de sesión
                                Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
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