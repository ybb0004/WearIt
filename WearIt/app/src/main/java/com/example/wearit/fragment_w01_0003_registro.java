package com.example.wearit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.ProgressBar;



public class fragment_w01_0003_registro extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_w01_0003_registro, container, false);

        // Initialize views
        MaterialButton backButton = view.findViewById(R.id.iconButton);
        MaterialButton continueButton = view.findViewById(R.id.button2Registro);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TextInputEditText nameField = view.findViewById(R.id.editTextUsuarioRegistro);
        TextInputEditText usernameField = view.findViewById(R.id.editTextNombreRegistro);
        TextInputEditText emailField = view.findViewById(R.id.editTextMailRegistro);
        TextInputEditText passwordField = view.findViewById(R.id.editTextContrasenaRegistro);
        TextInputEditText confirmPasswordField = view.findViewById(R.id.editTextContrasena2Registro);

        // Load animation
        Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.button_scale);

        // Back button click with animation
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(scaleAnimation);
                // Add navigation logic here (e.g., pop back stack)
                // getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Continue button click with animation and progress
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(scaleAnimation);
                progressBar.setVisibility(View.VISIBLE);
                continueButton.setEnabled(false);

                // Simulate form submission (replace with actual logic)
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        continueButton.setEnabled(true);
                        // Proceed with next step (e.g., navigate or show success)
                    }
                }, 2000); // 2-second delay for demo
            }
        });

        // Focus animations for input fields
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

        return view;
    }
}