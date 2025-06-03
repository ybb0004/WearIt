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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class fragment_w02_0007_edit_profile extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1001;
    private ShapeableImageView avatarView;
    private TextInputEditText nameEdit;
    private Uri selectedImageUri;

    private Button botonAtras;
    private Button logoutButton1;

    // Firebase references
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String currentUserEmail;

    // Imgur API
    private static final String IMGUR_CLIENT_ID = "eee6402d2d3ba0a"; // Reemplaza con tu Client ID
    private OkHttpClient httpClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w02_0007_edit_profile, container, false);

        // Inicializar Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        httpClient = new OkHttpClient();

        // Obtener email del usuario actual
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserEmail = currentUser.getEmail().replace(".", "_").replace("@", "_");
        }

        avatarView  = view.findViewById(R.id.editProfileAvatar);
        nameEdit    = view.findViewById(R.id.editProfileName);
        MaterialButton saveButton = view.findViewById(R.id.saveProfileButton);
        botonAtras = view.findViewById(R.id.backButton);
        logoutButton1 = view.findViewById(R.id.logoutButton);

        // Cargar datos actuales del usuario
        loadCurrentUserData();

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

            // Guardar datos en Firebase
            saveUserProfile(nuevoNombre);
        });

        return view;
    }

    /**
     * Carga los datos actuales del usuario desde Firebase
     */
    private void loadCurrentUserData() {
        if (currentUserEmail == null) return;

        DatabaseReference userRef = databaseReference.child("usuarios").child(currentUserEmail);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Cargar nombre
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    if (nombre != null) {
                        nameEdit.setText(nombre);
                    }

                    // Cargar imagen de perfil
                    String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Picasso.get()
                                .load(avatarUrl)
                                .into(avatarView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar datos del perfil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Guarda el perfil del usuario en Firebase
     */
    private void saveUserProfile(String nombre) {
        if (currentUserEmail == null) {
            Toast.makeText(getContext(), "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si hay una nueva imagen seleccionada, subirla primero
        if (selectedImageUri != null) {
            uploadImageAndSaveProfile(nombre);
        } else {
            // Solo guardar el nombre sin cambiar la imagen
            saveUserData(nombre, null);
        }
    }

    /**
     * Sube la imagen a Imgur y luego guarda el perfil
     */
    private void uploadImageAndSaveProfile(String nombre) {
        if (selectedImageUri == null) return;

        try {
            // Convertir imagen a Base64
            InputStream imageStream = getContext().getContentResolver().openInputStream(selectedImageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = imageStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            // Subir a Imgur
            uploadToImgur(base64Image, nombre);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al procesar imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sube imagen a Imgur usando su API
     */
    private void uploadToImgur(String base64Image, String nombre) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", base64Image)
                .addFormDataPart("type", "base64")
                .build();

        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/image")
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .post(requestBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error al subir imagen a Imgur", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        if (jsonResponse.getBoolean("success")) {
                            JSONObject data = jsonResponse.getJSONObject("data");
                            String imageUrl = data.getString("link");

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    saveUserData(nombre, imageUrl);
                                });
                            }
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "Error en respuesta de Imgur", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                    } catch (Exception e) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Error al procesar respuesta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Error HTTP: " + response.code(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        });
    }

    /**
     * Guarda los datos del usuario en Firebase Realtime Database
     */
    private void saveUserData(String nombre, String avatarUrl) {
        DatabaseReference userRef = databaseReference.child("usuarios").child(currentUserEmail);

        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nombre);

        if (avatarUrl != null) {
            updates.put("avatarUrl", avatarUrl);
        }

        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    // Regresar al fragmento anterior
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al guardar perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

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
                    "Error al cerrar sesión: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}