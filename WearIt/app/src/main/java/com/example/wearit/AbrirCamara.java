package com.example.wearit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private static final String IMGUR_CLIENT_ID = "eee6402d2d3ba0a"; // Reemplaza con tu Client-ID real
    private static final long MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB límite de Imgur

    private FirebaseAuth mAuth;
    private DatabaseReference usuariosRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usuariosRef = database.getReference("usuarios");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_abrir_camara, container, false);

        Button btnOpenCamera = view.findViewById(R.id.btn_open_camera);
        btnOpenCamera.setOnClickListener(v -> checkCameraPermission());

        return view;
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            showToast("Permiso de cámara denegado");
        }
    }

    private void openCamera() {
        if (!isNetworkAvailable()) {
            showToast("No hay conexión a internet");
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            showToast("No se encontró una aplicación de cámara");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
                    // Generar hash único para verificar que cada imagen es diferente
                    int imageHash = imageBitmap.hashCode();
                    Log.d(TAG, "Hash de la imagen capturada: " + imageHash);

                    uploadImageToImgur(imageBitmap);
                }
            }
        }
    }

    private void uploadImageToImgur(Bitmap bitmap) {
        if (!isNetworkAvailable()) {
            showToast("No hay conexión a internet");
            return;
        }

        // Generar nombre de archivo único con timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = "wearit_" + timestamp + ".jpg";

        byte[] imageBytes = bitmapToByteArray(bitmap);
        if (imageBytes == null || imageBytes.length == 0) {
            showToast("Error al procesar la imagen");
            return;
        }

        Log.d(TAG, "Subiendo imagen a Imgur. Tamaño: " + imageBytes.length + " bytes");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", filename,
                        RequestBody.create(imageBytes, MediaType.parse("image/jpeg")))
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/image")
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                logError("Error en la conexión: " + e.getMessage());
                showToast("Error de conexión con Imgur");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    String errorMsg = parseImgurError(responseBody, response.code());
                    logError("Error en Imgur (" + response.code() + "): " + errorMsg);
                    showToast("Error al subir imagen: " + errorMsg);
                    return;
                }

                try {
                    JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonObject data = jsonResponse.getAsJsonObject("data");
                    String imageUrl = data.get("link").getAsString();
                    String deleteHash = data.get("deletehash").getAsString();

                    Log.d(TAG, "Imagen subida exitosamente. URL: " + imageUrl);
                    Log.d(TAG, "Deletehash: " + deleteHash);

                    runOnUiThread(() -> {
                        showToast("Imagen subida exitosamente");
                        subirPrendaAFirebase(imageUrl, deleteHash);
                    });
                } catch (Exception e) {
                    logError("Error al procesar respuesta: " + e.getMessage());
                    showToast("Error al procesar respuesta de Imgur");
                }
            }
        });
    }

    private void subirPrendaAFirebase(String imageUrl, String deleteHash) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            showToast("Usuario no autenticado, inicia sesión de nuevo");
            return;
        }
        LocalDateTime fechaHoraActual = LocalDateTime.now();

        // Define el formato deseado: día/mes/año:horas:minutos
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy:HH:mm");

        String fechaFormateada = fechaHoraActual.format(formatter);


        Map<String, Object> prenda = new HashMap<>();
        prenda.put("tipo", "camisa");
        prenda.put("color", "azul");
        prenda.put("talla", "M");
        prenda.put("marca", "Zara");
        prenda.put("fecha_subida", fechaFormateada);
        prenda.put("imagen_url", imageUrl);
        prenda.put("delete_hash", deleteHash); // Guardar el deletehash por si necesitas borrarla después
        prenda.put("timestamp", System.currentTimeMillis());

        // Generar una nueva clave única para cada prenda
        DatabaseReference nuevaPrendaRef = usuariosRef.child(currentUser.getUid())
                .child("prendas")
                .push();

        nuevaPrendaRef.setValue(prenda)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Prenda guardada en Firebase con ID: " + nuevaPrendaRef.getKey());
                    showToast("Prenda guardada en Firebase");
                })
                .addOnFailureListener(e -> {
                    logError("Error al guardar prenda: " + e.getMessage());
                    showToast("Error al guardar prenda");
                });
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private String parseImgurError(String responseBody, int code) {
        try {
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            if (json.has("data")) {
                JsonObject data = json.getAsJsonObject("data");
                if (data.has("error")) {
                    return data.get("error").getAsString();
                }
            }
            return "Error desconocido (código " + code + ")";
        } catch (Exception e) {
            return "Error al parsear respuesta (código " + code + ")";
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show());
    }

    private void logError(String message) {
        Log.e(TAG, message);
    }

    private void runOnUiThread(Runnable action) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(action);
        }
    }
}