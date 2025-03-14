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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_abrir_camara, container, false);

        Button btnOpenCamera = view.findViewById(R.id.btn_open_camera);
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                .header("Authorization", "eee6402d2d3ba0a") // Reemplaza YOUR_CLIENT_ID con tu Client-ID
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
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Imagen subida: " + imageUrl, Toast.LENGTH_LONG).show());
        } catch (Exception e) {
            // Manejar el error
            Log.e(TAG, "Error al procesar la respuesta: " + e.getMessage());
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show());
        }
    }
}