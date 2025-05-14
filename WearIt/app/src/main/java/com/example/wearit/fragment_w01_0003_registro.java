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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import android.widget.CheckBox;
import android.widget.ProgressBar;

public class fragment_w01_0003_registro extends Fragment {

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
                // Navegar hacia atrás usando Navigation Component
                Navigation.findNavController(v).navigateUp();
            }
        });

        // Evento del botón Continuar
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(scaleAnimation);

                // Obtener los valores de los campos
                String name = nameField.getText().toString().trim();
                String username = usernameField.getText().toString().trim();
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                String confirmPassword = confirmPasswordField.getText().toString().trim();

                // Validar campos
                if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkboxPrivacidad.isChecked()) {
                    Toast.makeText(getContext(), "Debes aceptar la política de privacidad", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Mostrar progreso
                progressBar.setVisibility(View.VISIBLE);
                continueButton.setEnabled(false);

                // Crear usuario en Firebase Auth
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Registro exitoso, actualizar perfil con nombre
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressBar.setVisibility(View.GONE);
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                                                        // Navegar al fragmento principal
                                                        navigateToHomeFragment(v);
                                                    } else {
                                                        continueButton.setEnabled(true);
                                                        Toast.makeText(getContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    continueButton.setEnabled(true);
                                    // Manejar errores
                                    String errorMessage = task.getException() != null ?
                                            task.getException().getMessage() : "Error desconocido";
                                    Toast.makeText(getContext(), "Error en el registro: " + errorMessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ejemplo.com/politica-privacidad"));
                startActivity(intent);
            }
        };

        int start = textoCompleto.indexOf("ley de privacidad");
        if (start >= 0) {
            int end = start + "ley de privacidad".length();
            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            checkboxPrivacidad.setText(spannableString);
            checkboxPrivacidad.setMovementMethod(LinkMovementMethod.getInstance());
        }

        return view;
    }

    private void navigateToHomeFragment(View view) {
        try {
            NavController navController = Navigation.findNavController(view);

            // Verifica que esta acción exista en tu navigation graph
            navController.navigate(R.id.action_fragment_w01_0003_registro3_to_fragment_w01_0002_iniciarsesion);

            // Opcional: limpiar el back stack para no volver al registro
            //navController.popBackStack(R.id.fragment_w01_0003_registro, true);

        } catch (IllegalArgumentException e) {
            Log.e("NavigationError", "Error en navegación: " + e.getMessage());
            // Fallback: iniciar nueva actividad
            startActivity(new Intent(getActivity(), activity_w020000_home_app.class));
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }
}