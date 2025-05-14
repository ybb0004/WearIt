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
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class fragment_w02_0001_home extends Fragment {

    private static final String TAG = "HomeFragment";
    private Handler handler;
    private Runnable updateTimeRunnable;
    private TextView timeText;
    private ImageView dayNightIcon;

    private ImageView tshirtImage, pantsImage, shoesImage;
    private Button generateOutfitButton;

    private DatabaseReference clothesRef;
    private FirebaseUser currentUser;

    // Nuevas listas de prendas con metadatos
    private List<Prenda> superiorList = new ArrayList<>();
    private List<Prenda> inferiorList = new ArrayList<>();
    private List<Prenda> zapatillaList = new ArrayList<>();

    // Clase auxiliar para representar una prenda
    public static class Prenda {
        public String imagenUrl;
        public String tipo;
        public String color;
        public String estilo;
        public String temporada;

        public Prenda(String imagenUrl, String tipo, String color, String estilo, String temporada) {
            this.imagenUrl = imagenUrl;
            this.tipo = tipo;
            this.color = color;
            this.estilo = estilo;
            this.temporada = temporada;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w02_0001_home, container, false);

        dayNightIcon = view.findViewById(R.id.dayNightIcon);
        timeText = view.findViewById(R.id.timeText);
        tshirtImage = view.findViewById(R.id.imageView2);
        pantsImage = view.findViewById(R.id.imageView3);
        shoesImage = view.findViewById(R.id.imageView4);
        generateOutfitButton = view.findViewById(R.id.iconButton);

        updateTimeAndDayNight();

        initializeFirebase();
        loadClothesFromFirebase();

        generateOutfitButton.setOnClickListener(v -> generateRandomOutfit());

        return view;
    }

    private void initializeFirebase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String emailKey = "test@wearit_com"; // o usar currentUser.getEmail().replace(".", "_")
            clothesRef = FirebaseDatabase.getInstance().getReference("usuarios").child(emailKey).child("prendas");
        }
    }

    private void loadClothesFromFirebase() {
        if (clothesRef == null) return;

        clothesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                superiorList.clear();
                inferiorList.clear();
                zapatillaList.clear();

                for (DataSnapshot garmentSnapshot : snapshot.getChildren()) {
                    String imageUrl = garmentSnapshot.child("imagen_url").getValue(String.class);
                    String tipo = garmentSnapshot.child("tipo").getValue(String.class);
                    String color = garmentSnapshot.child("color").getValue(String.class);
                    String estilo = garmentSnapshot.child("estilo").getValue(String.class);
                    String temporada = garmentSnapshot.child("temporada").getValue(String.class);

                    if (imageUrl != null && tipo != null && color != null && estilo != null && temporada != null) {
                        Prenda prenda = new Prenda(imageUrl, tipo, color, estilo, temporada);
                        switch (tipo.toLowerCase()) {
                            case "superior":
                                superiorList.add(prenda);
                                break;
                            case "inferior":
                                inferiorList.add(prenda);
                                break;
                            case "zapatilla":
                            case "v":
                                zapatillaList.add(prenda);
                                break;
                        }
                    }
                }

                if (!superiorList.isEmpty() && !inferiorList.isEmpty() && !zapatillaList.isEmpty()) {
                    generateRandomOutfit();
                } else {
                    Toast.makeText(getContext(), "No hay suficientes prendas para generar un outfit", Toast.LENGTH_SHORT).show();
                }
                mostrarOutfitPatrocinado(); // 👈 aquí se muestra al iniciar
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar prendas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateRandomOutfit() {
        if (getContext() == null) return;

        if (superiorList.isEmpty() || inferiorList.isEmpty() || zapatillaList.isEmpty()) {
            Toast.makeText(getContext(), "Faltan prendas, añade más para generar otro outfit", Toast.LENGTH_SHORT).show();
            return;
        }

        Random random = new Random();
        Prenda top = superiorList.get(random.nextInt(superiorList.size()));

        List<Prenda> compatiblesInferior = new ArrayList<>();
        for (Prenda p : inferiorList) {
            if (p.temporada.equalsIgnoreCase(top.temporada) && p.estilo.equalsIgnoreCase(top.estilo)) {
                compatiblesInferior.add(p);
            }
        }

        List<Prenda> compatiblesZapatilla = new ArrayList<>();
        for (Prenda p : zapatillaList) {
            if (p.temporada.equalsIgnoreCase(top.temporada) && p.estilo.equalsIgnoreCase(top.estilo)) {
                compatiblesZapatilla.add(p);
            }
        }

        if (compatiblesInferior.isEmpty() || compatiblesZapatilla.isEmpty()) {
            Toast.makeText(getContext(), "No hay combinación con las prendas superiores actualmente, ¡añade más para generar outfits! ", Toast.LENGTH_SHORT).show();
            return;
        }

        Prenda bottom = compatiblesInferior.get(random.nextInt(compatiblesInferior.size()));
        Prenda shoe = compatiblesZapatilla.get(random.nextInt(compatiblesZapatilla.size()));

        loadImageWithGlide(top.imagenUrl, tshirtImage, R.drawable.camiseta);
        loadImageWithGlide(bottom.imagenUrl, pantsImage, R.drawable.pantalon);
        loadImageWithGlide(shoe.imagenUrl, shoesImage, R.drawable.zapatilla);

        //Toast.makeText(getContext(), "¡Outfit generado!", Toast.LENGTH_SHORT).show();
    }

    private void loadImageWithGlide(String imageUrl, ImageView imageView, int defaultImageResId) {
        if (getContext() == null) return;

        Glide.with(getContext())
                .load(imageUrl)
                .placeholder(defaultImageResId)
                .error(defaultImageResId)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Error al cargar imagen: " + imageUrl);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
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
        updateTimeRunnable = () -> {
            updateTimeAndDayNight();
            handler.postDelayed(updateTimeRunnable, 30000);
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

    private void mostrarOutfitPatrocinado() {
        Prenda top = new Prenda("https://i.imgur.com/ntw8nFq.png", "Superior", "Verano", "Regular", "#9E9E9E");
        Prenda bottom = new Prenda("https://i.imgur.com/DNH3Ajh.png", "Inferior", "Verano", "Regular", "#000000");
        Prenda shoes = new Prenda("https://i.imgur.com/YrkptDC.png", "Zapatilla", "Verano", "Regular", "#FFFFFF");

        loadImageWithGlide(top.imagenUrl, tshirtImage, R.drawable.camiseta);
        loadImageWithGlide(bottom.imagenUrl, pantsImage, R.drawable.pantalon);
        loadImageWithGlide(shoes.imagenUrl, shoesImage, R.drawable.zapatilla);

        Toast.makeText(getContext(), "Outfit patrocinado por ZARA y MORRISON", Toast.LENGTH_SHORT).show();
    }


}
