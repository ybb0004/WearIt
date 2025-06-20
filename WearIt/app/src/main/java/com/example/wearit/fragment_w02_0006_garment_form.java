package com.example.wearit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

public class fragment_w02_0006_garment_form extends Fragment {
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final String TAG = "GarmentForm";
    private static final String IMGUR_CLIENT_ID = "eee6402d2d3ba0a"; // Reemplaza con tu Client-ID real

    private ShapeableImageView garmentImage;
    private AutoCompleteTextView sizeInput;
    private AutoCompleteTextView brandInput;
    private TextInputLayout brandInputLayout;
    private AutoCompleteTextView seasonInput;
    private AutoCompleteTextView colorInput;
    private TextInputLayout colorInputLayout;
    private View colorPreview;
    private AutoCompleteTextView partInput;
    private AutoCompleteTextView styleInput;
    private MaterialButton backButton;
    private MaterialButton saveButton;

    private Bitmap imageBitmap;
    private FirebaseAuth mAuth;
    private DatabaseReference usuariosRef;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private List<ColorItem> colorItems;

    private TextInputLayout sizeInputLayout;
    private TextInputLayout seasonInputLayout;
    private TextInputLayout partInputLayout;
    private TextInputLayout styleInputLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usuariosRef = database.getReference("usuarios");

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            imageBitmap = (Bitmap) extras.get("data");
                            if (imageBitmap != null) {
                                garmentImage.setImageBitmap(imageBitmap);
                                Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_in);
                                garmentImage.startAnimation(scaleAnimation);
                            }
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w02_0006_garment_form, container, false);

        initializeViews(view);
        setupAutoCompleteTextViews();
        setupColorAdapter();
        setupAnimations(view);
        setupListeners(view);

        return view;
    }

    private void initializeViews(View view) {
        garmentImage = view.findViewById(R.id.garmentImage);
        sizeInput = view.findViewById(R.id.sizeInput);
        sizeInputLayout = view.findViewById(R.id.sizeInputLayout);
        brandInput = view.findViewById(R.id.brandInput);
        brandInputLayout = view.findViewById(R.id.brandInputLayout);
        seasonInput = view.findViewById(R.id.seasonInput);
        seasonInputLayout = view.findViewById(R.id.seasonInputLayout);
        colorInput = view.findViewById(R.id.colorInput);
        colorInputLayout = view.findViewById(R.id.colorInputLayout);
        colorPreview = view.findViewById(R.id.colorPreview);
        partInput = view.findViewById(R.id.partInput);
        partInputLayout = view.findViewById(R.id.partInputLayout);
        styleInput = view.findViewById(R.id.styleInput);
        styleInputLayout = view.findViewById(R.id.styleInputLayout);
        backButton = view.findViewById(R.id.backButton);
        saveButton = view.findViewById(R.id.saveButton);
    }

    private void setupAutoCompleteTextViews() {
        // Talla
        String[] sizes = getResources().getStringArray(R.array.garment_sizes);
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                sizes
        );
        sizeInput.setAdapter(sizeAdapter);

        // Marca
        String[] brands = getResources().getStringArray(R.array.garment_brands);
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                brands
        );
        brandInput.setAdapter(brandAdapter);

        // Temporada
        String[] seasons = getResources().getStringArray(R.array.garment_seasons);
        ArrayAdapter<String> seasonAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                seasons
        );
        seasonInput.setAdapter(seasonAdapter);

        // Parte
        String[] parts = getResources().getStringArray(R.array.garment_parts);
        ArrayAdapter<String> partAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                parts
        );
        partInput.setAdapter(partAdapter);

        // Estilo
        String[] styles = getResources().getStringArray(R.array.garment_styles);
        ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                styles
        );
        styleInput.setAdapter(styleAdapter);
    }

    private void setupColorAdapter() {
        String[] colorNames = getResources().getStringArray(R.array.color_names);
        String[] colorHexCodes = getResources().getStringArray(R.array.color_hex_codes);
        colorItems = new ArrayList<>();
        for (int i = 0; i < colorNames.length; i++) {
            colorItems.add(new ColorItem(colorNames[i], colorHexCodes[i]));
        }

        ArrayAdapter<ColorItem> colorAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                colorItems
        );
        colorInput.setAdapter(colorAdapter);
    }

    private void setupAnimations(View view) {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        garmentImage.startAnimation(fadeInAnimation);
        sizeInput.startAnimation(fadeInAnimation);
        brandInput.startAnimation(fadeInAnimation);
        seasonInput.startAnimation(fadeInAnimation);
        colorInput.startAnimation(fadeInAnimation);
        partInput.startAnimation(fadeInAnimation);
        styleInput.startAnimation(fadeInAnimation);
        saveButton.startAnimation(fadeInAnimation);
    }

    private void setupListeners(View view) {
        garmentImage.setOnClickListener(v -> checkCameraPermission());

        colorInput.setOnItemClickListener((parent, view1, position, id) -> {
            ColorItem selectedColor = (ColorItem) parent.getItemAtPosition(position);
            updateColorPreview(selectedColor.getHexCode());
        });

        colorInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String colorText = s.toString().trim();
                for (ColorItem item : colorItems) {
                    if (item.getName().equalsIgnoreCase(colorText)) {
                        updateColorPreview(item.getHexCode());
                        return;
                    }
                }
                updateColorPreview(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        saveButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
            if (validateForm(view)) {
                uploadImageAndSaveGarment(view);
            }
        });
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
            cameraLauncher.launch(takePictureIntent);
        } else {
            showToast("No se encontró una aplicación de cámara");
        }
    }

    private boolean validateForm(View view) {
        boolean isValid = true;

        if (sizeInput.getText().toString().trim().isEmpty() || sizeInput.getText().toString().trim().equals("-")) {
            sizeInputLayout.setError("Selecciona una talla válida");
            isValid = false;
        } else {
            sizeInputLayout.setError(null);
        }

        if (brandInput.getText().toString().trim().isEmpty() || brandInput.getText().toString().trim().equals("-")) {
            brandInputLayout.setError("Ingresa una marca válida");
            isValid = false;
        } else {
            brandInputLayout.setError(null);
        }

        if (seasonInput.getText().toString().trim().isEmpty() || seasonInput.getText().toString().trim().equals("-")) {
            seasonInputLayout.setError("Selecciona una temporada válida");
            isValid = false;
        } else {
            seasonInputLayout.setError(null);
        }

        if (colorInput.getText().toString().trim().isEmpty()) {
            colorInputLayout.setError("Ingresa un color");
            isValid = false;
        } else {
            colorInputLayout.setError(null);
        }

        if (partInput.getText().toString().trim().isEmpty() || partInput.getText().toString().trim().equals("-")) {
            partInputLayout.setError("Selecciona una parte válida");
            isValid = false;
        } else {
            partInputLayout.setError(null);
        }

        if (styleInput.getText().toString().trim().isEmpty() || styleInput.getText().toString().trim().equals("-")) {
            styleInputLayout.setError("Selecciona un estilo válido");
            isValid = false;
        } else {
            styleInputLayout.setError(null);
        }

        if (imageBitmap == null) {
            Snackbar.make(view, "Por favor, toma una foto de la prenda", Snackbar.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void uploadImageAndSaveGarment(View view) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Snackbar.make(view, "Debes iniciar sesión para guardar la prenda", Snackbar.LENGTH_SHORT).show();
            return;
        }

        uploadImageToImgur(imageBitmap, new ImgurUploadCallback() {
            @Override
            public void onSuccess(String imageUrl, String deleteHash) {
                saveGarmentToFirebase(currentUser, imageUrl, deleteHash, view);
            }

            @Override
            public void onFailure(String errorMessage) {
                Snackbar.make(view, "Error al subir imagen: " + errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void uploadImageToImgur(Bitmap bitmap, ImgurUploadCallback callback) {
        if (!isNetworkAvailable()) {
            callback.onFailure("No hay conexión a internet");
            return;
        }

        byte[] imageBytes = bitmapToByteArray(bitmap);
        if (imageBytes == null || imageBytes.length == 0) {
            callback.onFailure("Error al procesar la imagen");
            return;
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = "wearit_" + timestamp + ".jpg";

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
                runOnUiThread(() -> callback.onFailure("Error de conexión con Imgur"));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    String errorMsg = parseImgurError(responseBody, response.code());
                    logError("Error en Imgur (" + response.code() + "): " + errorMsg);
                    runOnUiThread(() -> callback.onFailure(errorMsg));
                    return;
                }

                try {
                    JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonObject data = jsonResponse.getAsJsonObject("data");
                    String imageUrl = data.get("link").getAsString();
                    String deleteHash = data.get("deletehash").getAsString();

                    Log.d(TAG, "Imagen subida exitosamente. URL: " + imageUrl);
                    runOnUiThread(() -> callback.onSuccess(imageUrl, deleteHash));
                } catch (Exception e) {
                    logError("Error al procesar respuesta: " + e.getMessage());
                    runOnUiThread(() -> callback.onFailure("Error al procesar respuesta de Imgur"));
                }
            }
        });
    }

    private void saveGarmentToFirebase(FirebaseUser currentUser, String imageUrl, String deleteHash, View view) {
        if (currentUser == null || currentUser.getEmail() == null) {
            Snackbar.make(view, "Error: No se pudo obtener el correo del usuario", Snackbar.LENGTH_SHORT).show();
            return;
        }

        String userEmail = currentUser.getEmail();
        String emailKey = userEmail.replace(".", "_");

        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy:HH:mm", Locale.getDefault());
        String fechaFormateada = sdf.format(new Date());

        String colorName = colorInput.getText().toString().trim();
        String colorHex = "";
        for (ColorItem item : colorItems) {
            if (item.getName().equalsIgnoreCase(colorName)) {
                colorHex = item.getHexCode();
                break;
            }
        }

        if (colorHex.isEmpty()) {
            colorHex = "#FFFFFF";
            Snackbar.make(view, "Color no válido, usando blanco por defecto", Snackbar.LENGTH_SHORT).show();
        }

        Map<String, Object> prenda = new HashMap<>();
        prenda.put("tipo", partInput.getText().toString().trim());
        prenda.put("color", colorHex);
        prenda.put("talla", sizeInput.getText().toString().trim());
        prenda.put("marca", brandInput.getText().toString().trim());
        prenda.put("temporada", seasonInput.getText().toString().trim());
        prenda.put("estilo", styleInput.getText().toString().trim());
        prenda.put("fecha_subida", fechaFormateada);
        prenda.put("imagen_url", imageUrl);
        prenda.put("delete_hash", deleteHash);
        prenda.put("timestamp", System.currentTimeMillis());

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usuarios").child(emailKey);

        userRef.child("prendas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int nextPrendaNumber = 1;
                if (task.getResult().exists()) {
                    nextPrendaNumber = (int) task.getResult().getChildrenCount() + 1;
                }

                String prendaId = "prenda" + nextPrendaNumber;

                userRef.child("prendas").child(prendaId).setValue(prenda)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Prenda guardada en Firebase con ID: " + prendaId);
                            Snackbar.make(view, "Prenda guardada correctamente", Snackbar.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStack();
                        })
                        .addOnFailureListener(e -> {
                            logError("Error al guardar prenda: " + e.getMessage());
                            Snackbar.make(view, "Error al guardar prenda", Snackbar.LENGTH_LONG).show();
                        });
            } else {
                logError("Error al consultar prendas existentes: " + task.getException().getMessage());
                Snackbar.make(view, "Error al guardar prenda", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void updateColorPreview(String colorText) {
        try {
            int color = Color.parseColor(colorText != null ? colorText : "#FFFFFF");
            GradientDrawable drawable = (GradientDrawable) colorPreview.getBackground();
            drawable.setColor(color);
        } catch (IllegalArgumentException e) {
            GradientDrawable drawable = (GradientDrawable) colorPreview.getBackground();
            drawable.setColor(Color.GRAY);
        }
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

    interface ImgurUploadCallback {
        void onSuccess(String imageUrl, String deleteHash);
        void onFailure(String errorMessage);
    }
}