package com.example.wearit;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import androidx.annotation.NonNull;

public class fragment_w02_0001_home extends Fragment {
    private static final String TAG = "HomeFragment";
    private Handler handler;
    private Runnable updateTimeRunnable;
    private TextView timeText;
    private ImageView dayNightIcon;

    // Imágenes de prendas
    private ImageView topGarmentImage;
    private ImageView bottomGarmentImage;
    private ImageView shoesGarmentImage;

    // Referencias a Firebase
    private DatabaseReference prendasRef;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w02_0001_home, container, false);

        // Inicializar vistas
        dayNightIcon = view.findViewById(R.id.dayNightIcon);
        timeText = view.findViewById(R.id.timeText);
        ImageView weatherIcon = view.findViewById(R.id.weatherIcon);
        TextView temperatureText = view.findViewById(R.id.temperatureText);

        // Inicializar imágenes de prendas
        topGarmentImage = view.findViewById(R.id.imageView2);
        bottomGarmentImage = view.findViewById(R.id.imageView3);
        shoesGarmentImage = view.findViewById(R.id.imageView4);

        // Configuración inicial
        updateTimeAndDayNight();

        // Configurar Clima y Temperatura (simulación)
        weatherIcon.setImageResource(R.drawable.cloud_24px);
        temperatureText.setText("25°C");

        // Inicializar Firebase y cargar prendas
        initializeFirebase();
        loadGarmentsFromFirebase();

        return view;
    }

    private void initializeFirebase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            String emailKey = currentUser.getEmail().replace(".", "_");
            prendasRef = FirebaseDatabase.getInstance().getReference("usuarios").child(emailKey).child("prendas");
        }
    }

    private void loadGarmentsFromFirebase() {
        if (prendasRef == null) {
            Log.e(TAG, "La referencia a Firebase es nula");
            return;
        }

        prendasRef.addListenerForSingleValueEvent(new ValueEventListener() { // Cambiado a single event
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> prendasMap = new HashMap<>();

                for (DataSnapshot prendaSnapshot : dataSnapshot.getChildren()) {
                    String tipo = prendaSnapshot.child("tipo").getValue(String.class);
                    String imageUrl = prendaSnapshot.child("imagen_url").getValue(String.class);

                    if (tipo != null && imageUrl != null) {
                        tipo = tipo.toLowerCase(Locale.ROOT);
                        prendasMap.put(tipo, imageUrl);
                    }
                }

                // Asignar después de tener todas las prendas
                loadImageWithGlide(prendasMap.get("superior"), topGarmentImage, R.drawable.camiseta);
                loadImageWithGlide(prendasMap.get("inferior"), bottomGarmentImage, R.drawable.pantalon);
                loadImageWithGlide(prendasMap.get("calzado"), shoesGarmentImage, R.drawable.zapatilla);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error al cargar datos: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Corrige la URL de Imgur para asegurar el formato correcto
     */
    private String fixImgurUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        // Si la URL no tiene protocolo, añadirlo
        if (!url.startsWith("http")) {
            url = "https://" + url;
        }

        // Si es un enlace a la página en lugar de a la imagen directa
        if (url.contains("imgur.com") && !url.contains("i.imgur.com")) {
            url = url.replace("imgur.com", "i.imgur.com") + ".jpg";
        }

        // Si no tiene extensión, añadir .jpg
        if (!url.contains(".")) {
            url += ".jpg";
        }

        Log.d(TAG, "URL corregida: " + url);
        return url;
    }

    private void loadImageWithGlide(String imageUrl, ImageView imageView, int defaultImageResId) {
        if (getContext() == null) return;

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            Log.d(TAG, "Cargando imagen desde: " + imageUrl);

            // Asegurar que la URL sea válida
            if (!imageUrl.startsWith("http")) {
                imageUrl = "https://" + imageUrl;
            }

            Glide.with(getContext())
                    .load(imageUrl)
                    .placeholder(defaultImageResId)
                    .error(defaultImageResId)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Error al cargar imagen: " + (e != null ? e.getMessage() : ""));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource,
                                                       boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            Log.w(TAG, "URL vacía para " + imageView.getContentDescription());
            imageView.setImageResource(defaultImageResId);
        }
    }

    private boolean isPrendaSuperior(String tipo) {
        String tipoLower = tipo.toLowerCase();
        return tipoLower.contains("camisa") || tipoLower.contains("camiseta") ||
                tipoLower.contains("blusa") || tipoLower.contains("jersey") ||
                tipoLower.contains("sudadera") || tipoLower.contains("chaqueta");
    }

    private boolean isPrendaInferior(String tipo) {
        String tipoLower = tipo.toLowerCase();
        return tipoLower.contains("pantalón") || tipoLower.contains("pantalon") ||
                tipoLower.contains("falda") || tipoLower.contains("short") ||
                tipoLower.contains("bermuda") || tipoLower.contains("jeans");
    }

    private boolean isCalzado(String tipo) {
        String tipoLower = tipo.toLowerCase();
        return tipoLower.contains("zapato") || tipoLower.contains("zapatilla") ||
                tipoLower.contains("bota") || tipoLower.contains("sandalia") ||
                tipoLower.contains("calzado");
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
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(calendar.getTime());
        /*
        if (hour >= 6 && hour < 18) {
            dayNightIcon.setImageResource(R.drawable.reloj);
        } else {
            dayNightIcon.setImageResource(R.drawable.reloj);
        }
        */
        dayNightIcon.setImageResource(R.drawable.reloj); // poner icono reloj

        timeText.setText(currentTime); //Actualizamos la hora cada min
    }
}