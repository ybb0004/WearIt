package com.example.wearit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< Updated upstream
=======
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

>>>>>>> Stashed changes

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_w01_0002_iniciarsesion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_w01_0002_iniciarsesion extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_w01_0002_iniciarsesion() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_w01_0002_iniciarsesion.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_w01_0002_iniciarsesion newInstance(String param1, String param2) {
        fragment_w01_0002_iniciarsesion fragment = new fragment_w01_0002_iniciarsesion();
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

<<<<<<< Updated upstream
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_w01_0002_iniciarsesion, container, false);
=======
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
                                    Toast.makeText(getContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                                    // Ejemplo: Navegar a otro fragmento
                                    NavController navController = Navigation.findNavController(v);
                                    navController.navigate(R.id.action_fragment_w01_0002_iniciarsesion_to_fragment_w02_0005_perfil2);
                                }
                            } else {
                                // Error en el inicio de sesión
                                Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
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
>>>>>>> Stashed changes
    }
}