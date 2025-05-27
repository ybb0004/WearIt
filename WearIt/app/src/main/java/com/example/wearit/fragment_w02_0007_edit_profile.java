package com.example.wearit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;



public class fragment_w02_0007_edit_profile extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1001;
    private ShapeableImageView avatarView;
    private TextInputEditText nameEdit;
    private Uri selectedImageUri;

    private Button botonAtras;
    private Button logoutButton1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w02_0007_edit_profile, container, false);

        avatarView  = view.findViewById(R.id.editProfileAvatar);
        nameEdit    = view.findViewById(R.id.editProfileName);
        MaterialButton saveButton = view.findViewById(R.id.saveProfileButton);
        botonAtras = view.findViewById(R.id.backButton);
        logoutButton1 = view.findViewById(R.id.logoutButton);

        // Botón para regresar al perfil
        botonAtras.setOnClickListener(v2 -> {
            NavController navController = Navigation.findNavController(v2);
            navController.navigate(R.id.action_fragment_w02_0007_edit_profile_to_fragment_w02_0005_perfil);
        });

        // Botón de logout
        logoutButton1.setOnClickListener(v -> performLogout());

        // Al pulsar avatar, abrir galería
        avatarView.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK);
            pick.setType("image/*");
            startActivityForResult(pick, PICK_IMAGE_REQUEST);
        });

        // Guardar cambios del perfil
        saveButton.setOnClickListener(v -> {
            String nuevoNombre = nameEdit.getText().toString().trim();
            if (nuevoNombre.isEmpty()) {
                Toast.makeText(getContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: guardar nuevoNombre y selectedImageUri en BD o Firebase
            Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        });

        // TODO: cargar datos actuales en avatarView y nameEdit

        return view;
    }

    /**
     * Realiza el logout del usuario y navega a otra actividad
     */


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            avatarView.setImageURI(selectedImageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Realiza el logout del usuario y navega a la pantalla de inicio
     */
    private void performLogout() {
        try {
            // Cerrar sesión en Firebase
            FirebaseAuth.getInstance().signOut();


            // Crear intent para ir a la pantalla de inicio/registro
            Intent intent = new Intent(requireActivity(), W01_0000_Activity_Inicio_App.class);
            startActivity(intent);

            // Limpiar stack de actividades para evitar que el usuario regrese con el botón atrás
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Iniciar la actividad de login
            startActivity(intent);

            // Finalizar la actividad actual
            requireActivity().finish();

        } catch (Exception e) {
            // Manejar cualquier error durante el logout
            Toast.makeText(getContext(),
                    "Error al cerrar sesión: ",
                    Toast.LENGTH_SHORT).show();
        }
    }
}