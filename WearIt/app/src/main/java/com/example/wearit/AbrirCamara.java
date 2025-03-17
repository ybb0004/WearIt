package com.example.wearit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AbrirCamara extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final String TAG = "AbrirCamara";

    private FirebaseAuth mAuth;
    private DatabaseReference usuariosRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Firebase Auth y Database
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usuariosRef = database.getReference("usuarios");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_abrir_camara, container, false);

        // Referencia al botón
        Button btnOpenCamera = view.findViewById(R.id.btn_open_camera);

        // Configurar el OnClickListener
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar permisos de la cámara
                checkCameraPermission();


            }
        });

        return view;
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getActivity(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(getActivity(), "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                // Obtener la imagen capturada como un Bitmap
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

                // Subir la imagen a Imgur
                uploadImageToImgur(imageBitmap);
            }
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadImageToImgur(Bitmap bitmap) {
        // Convertir el Bitmap a un arreglo de bytes
        byte[] imageBytes = bitmapToByteArray(bitmap);

        // Crear el cuerpo de la solicitud
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image.jpg", RequestBody.create(imageBytes, MediaType.parse("image/jpeg")))
                .build();

        // Crear la solicitud HTTP
        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/image")
                .header("Authorization", "eee6402d2d3ba0a") // Reemplaza con tu Client-ID
                .post(requestBody)
                .build();

        // Enviar la solicitud
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Manejar el error
                Log.e(TAG, "Error al subir la imagen: " + e.getMessage());
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error al subir la imagen", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Procesar la respuesta
                    String responseBody = response.body().string();
                    Log.d(TAG, "Respuesta de Imgur: " + responseBody);
                    getActivity().runOnUiThread(() -> handleImgurResponse(responseBody));
                } else {
                    // Manejar el error
                    Log.e(TAG, "Error en la respuesta de Imgur: " + response.message());
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error en la respuesta de Imgur", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void handleImgurResponse(String responseBody) {
        try {
            // Analizar la respuesta JSON
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonObject data = jsonResponse.getAsJsonObject("data");
            String imageUrl = data.get("link").getAsString();

            // Mostrar el enlace de la imagen
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getActivity(), "Imagen subida: " + imageUrl, Toast.LENGTH_LONG).show();
                subirPrendaAFirebase(imageUrl); // Subir la prenda a Firebase
            });
        } catch (Exception e) {
            // Manejar el error
            Log.e(TAG, "Error al procesar la respuesta: " + e.getMessage());
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show());
        }
    }

    private void subirPrendaAFirebase(String imageUrl) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            String nombre = currentUser.getDisplayName(); // Obtener el nombre del usuario
            String email = currentUser.getEmail();       // Obtener el email del usuario

            // Crear un objeto Prenda
            Map<String, Object> prenda = new HashMap<>();
            prenda.put("tipo", "camisa");
            prenda.put("color", "azul");
            prenda.put("talla", "M");
            prenda.put("marca", "Zara");
            prenda.put("fecha_compra", "2023-01-15");
            prenda.put("imagen_url", imageUrl); // Agregar la URL de la imagen

            // Crear un mapa para las prendas
            Map<String, Object> prendas = new HashMap<>();
            prendas.put("prenda1", prenda);

            // Crear un mapa para el usuario
            Map<String, Object> usuario = new HashMap<>();
            usuario.put("id", uid);
            usuario.put("nombre", nombre);
            usuario.put("email", email);
            usuario.put("prendas", prendas);

            // Subir los datos a Firebase
            usuariosRef.child(uid).setValue(usuario)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Prenda subida a Firebase", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Error al subir la prenda: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getActivity(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }
}