package com.example.wearit;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class fragment_w02_0001_home extends Fragment {
    private static final String TAG = "HomeFragment";
    private Handler handler;
    private Runnable updateTimeRunnable;
    private TextView timeText;
    private ImageView dayNightIcon;

    // Imágenes de prendas
    private ImageView tshirtImage;
    private ImageView pantsImage;
    private ImageView shoesImage;
    private Button generateOutfitButton;

    // Referencias a Firebase
    private DatabaseReference clothesRef;
    private FirebaseUser currentUser;

    // Listas para almacenar las prendas
    private List<String> superiorUrls = new ArrayList<>();
    private List<String> inferiorUrls = new ArrayList<>();
    private List<String> zapatillaUrls = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w02_0001_home, container, false);

        // Inicializar vistas
        dayNightIcon = view.findViewById(R.id.dayNightIcon);
        timeText = view.findViewById(R.id.timeText);
        tshirtImage = view.findViewById(R.id.imageView2);
        pantsImage = view.findViewById(R.id.imageView3);
        shoesImage = view.findViewById(R.id.imageView4);
        generateOutfitButton = view.findViewById(R.id.iconButton);

        // Configuración inicial
        updateTimeAndDayNight();

        // Inicializar Firebase y cargar prendas
        initializeFirebase();
        loadClothesFromFirebase();

        // Configurar botón para generar outfit
        generateOutfitButton.setOnClickListener(v -> generateRandomOutfit());

        return view;
    }

    private void initializeFirebase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Usar un usuario de prueba si queremos forzar los datos
            String emailKey = "test@wearit_com"; // Usuario fijo para pruebas
            // O usar el email del usuario actual (descomentar la siguiente línea)
            // String emailKey = currentUser.getEmail().replace(".", "_");
            clothesRef = FirebaseDatabase.getInstance().getReference("usuarios").child(emailKey).child("prendas");
        }
    }

    private void loadClothesFromFirebase() {
        if (clothesRef == null) {
            Log.e(TAG, "La referencia a Firebase es nula");
            return;
        }

        clothesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                superiorUrls.clear();
                inferiorUrls.clear();
                zapatillaUrls.clear();

                for (DataSnapshot garmentSnapshot : dataSnapshot.getChildren()) {
                    String imageUrl = garmentSnapshot.child("imagen_url").getValue(String.class);
                    String garmentType = garmentSnapshot.child("tipo").getValue(String.class);

                    if (imageUrl != null && garmentType != null) {
                        // Log para depuración
                        Log.d(TAG, "Prenda cargada: " + garmentType + " - " + imageUrl);

                        switch (garmentType.toLowerCase()) {
                            case "superior":
                                superiorUrls.add(imageUrl);
                                break;
                            case "inferior":
                                inferiorUrls.add(imageUrl);
                                break;
                            case "zapatilla":
                            case "v": // Este tipo aparece en tus datos
                                zapatillaUrls.add(imageUrl);
                                break;
                        }
                    }
                }

                // Log para depuración
                Log.d(TAG, "Prendas cargadas: Superior=" + superiorUrls.size() +
                        ", Inferior=" + inferiorUrls.size() +
                        ", Zapatilla=" + zapatillaUrls.size());

                // Mostrar un outfit inicial si hay suficientes prendas
                if (!superiorUrls.isEmpty() && !inferiorUrls.isEmpty() && !zapatillaUrls.isEmpty()) {
                    generateRandomOutfit();
                } else {
                    Toast.makeText(getContext(), "No hay suficientes prendas para generar un outfit completo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error al cargar datos: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Error al cargar prendas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateRandomOutfit() {
        // Comprobar si hay contexto disponible
        if (getContext() == null) {
            Log.e(TAG, "El contexto es nulo");
            return;
        }

        // Comprobar si hay suficientes prendas
        StringBuilder missing = new StringBuilder();
        if (superiorUrls.isEmpty()) missing.append("superior ");
        if (inferiorUrls.isEmpty()) missing.append("inferior ");
        if (zapatillaUrls.isEmpty()) missing.append("zapatillas");

        if (!missing.toString().isEmpty()) {
            Toast.makeText(getContext(), "Faltan prendas: " + missing, Toast.LENGTH_LONG).show();
            Log.d(TAG, "Faltan prendas: " + missing);
            return;
        }

        try {
            // Seleccionar aleatoriamente una prenda de cada tipo
            Random random = new Random();
            String randomSuperior = superiorUrls.get(random.nextInt(superiorUrls.size()));
            String randomInferior = inferiorUrls.get(random.nextInt(inferiorUrls.size()));
            String randomZapatilla = zapatillaUrls.get(random.nextInt(zapatillaUrls.size()));

            Log.d(TAG, "Outfit generado: " +
                    "\nSuperior: " + randomSuperior +
                    "\nInferior: " + randomInferior +
                    "\nZapatilla: " + randomZapatilla);

            // Cargar las imágenes
            loadImageWithGlide(randomSuperior, tshirtImage, R.drawable.camiseta);
            loadImageWithGlide(randomInferior, pantsImage, R.drawable.pantalon);
            loadImageWithGlide(randomZapatilla, shoesImage, R.drawable.zapatilla);

            // Mostrar mensaje de éxito
            Toast.makeText(getContext(), "¡Outfit generado con éxito!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error al generar outfit: " + e.getMessage());
            Toast.makeText(getContext(), "Error al generar outfit", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImageWithGlide(String imageUrl, ImageView imageView, int defaultImageResId) {
        if (getContext() == null) return;

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            // Log para depuración
            Log.d(TAG, "Cargando imagen: " + imageUrl);

            Glide.with(getContext())
                    .load(imageUrl)
                    .placeholder(defaultImageResId)
                    .error(defaultImageResId)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Error al cargar imagen: " + imageUrl + " - " + (e != null ? e.getMessage() : "desconocido"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Imagen cargada correctamente: " + imageUrl);
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            Log.w(TAG, "URL de imagen vacía, usando imagen predeterminada");
            imageView.setImageResource(defaultImageResId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startUpdatingTime();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopUpdatingTime();
    }

    private void startUpdatingTime() {
        handler = new Handler();
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateTimeAndDayNight();
                handler.postDelayed(this, 30000); // Actualizar cada 30s
            }
        };
        handler.post(updateTimeRunnable);
    }

    private void stopUpdatingTime() {
        if (handler != null && updateTimeRunnable != null) {
            handler.removeCallbacks(updateTimeRunnable);
        }
    }

    private void updateTimeAndDayNight() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(calendar.getTime());
        timeText.setText(currentTime);
    }
}