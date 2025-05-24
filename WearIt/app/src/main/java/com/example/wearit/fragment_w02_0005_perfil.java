package com.example.wearit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class fragment_w02_0005_perfil extends Fragment {

    private RecyclerView outfitsRecyclerView;
    private OutfitAdapter outfitAdapter;
    private List<Outfit> allOutfits;
    private List<Outfit> outfitList;
    private TextView profileName, scannedCountView;
    private MaterialButton settingsButton;
    private DatabaseReference clothesRef;
    private FirebaseUser currentUser;

    private List<fragment_w02_0001_home.Prenda> superiorList = new ArrayList<>();
    private List<fragment_w02_0001_home.Prenda> inferiorList = new ArrayList<>();
    private List<fragment_w02_0001_home.Prenda> zapatillaList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w02_0005_perfil, container, false);

        // Referencias UI
        settingsButton = view.findViewById(R.id.settingsButton);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        outfitsRecyclerView = view.findViewById(R.id.outfitsRecyclerView);
        profileName = view.findViewById(R.id.profileName);
        scannedCountView = view.findViewById(R.id.scannedCount);

        // Animaciones
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.findViewById(R.id.profileCard).startAnimation(fadeIn);
        view.findViewById(R.id.statsContainer).startAnimation(fadeIn);
        tabLayout.startAnimation(fadeIn);
        outfitsRecyclerView.startAnimation(fadeIn);

        // Carga nombre de usuario
        loadUserProfile();


        initializeFirebase();
        loadClothesFromFirebase();


        // Inicializa listas y adaptador
        allOutfits = new ArrayList<>();
        outfitList = new ArrayList<>();
        outfitAdapter = new OutfitAdapter(outfitList);
        outfitsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        outfitsRecyclerView.setAdapter(outfitAdapter);

        // Carga datos y actualiza contador
        updateScannedCount();

        // Pestaña por defecto
        showCategory(Outfit.CATEGORY_SUPERIOR);

        // Listener de pestañas
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int id = tab.getPosition();
                if (id == 0) showCategory(Outfit.CATEGORY_SUPERIOR);
                else if (id == 1) showCategory(Outfit.CATEGORY_INFERIOR);
                else if (id == 2) showCategory(Outfit.CATEGORY_ZAPATILLA);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Menú desplegable en settingsButton
        settingsButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v);
            popup.getMenuInflater().inflate(R.menu.menu_profile, popup.getMenu());
            popup.setOnMenuItemClickListener(this::onProfileMenuItem);
            popup.show();
        });

        return view;
    }

    /** Gestión de las opciones del menú */
    private boolean onProfileMenuItem(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_profile) {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profileRoot, new fragment_w02_0007_edit_profile())
                    .addToBackStack(null)
                    .commit();
            return true;
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireActivity(), fragment_w01_0001_inicio_registro.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
            return true;
        }
        return false;
    }

    private void updateScannedCount() {
        scannedCountView.setText(String.valueOf(allOutfits.size()));
    }

    private void loadUserProfile() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String uid = currentUser.getUid();

            // Si displayName es null, usa el email sin el dominio
            String nombreParaMostrar = displayName;
            if (nombreParaMostrar == null || nombreParaMostrar.isEmpty()) {
                nombreParaMostrar = email != null ? email.split("@")[0] : "Usuario";
            }

            profileName.setText(nombreParaMostrar);
        } else {
            profileName.setText("Usuario");
        }
    }




    private void showCategory(String category) {
        outfitList.clear();
        for (Outfit o : allOutfits) {
            if (category.equals(o.getCategory())) {
                outfitList.add(o);
            }
        }
        outfitAdapter.notifyDataSetChanged();
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
                        fragment_w02_0001_home.Prenda prenda = new fragment_w02_0001_home.Prenda(imageUrl, tipo, color, estilo, temporada);
                        switch (tipo.toLowerCase()) {
                            case "superior":
                                superiorList.add(prenda);
                                break;
                            case "inferior":
                                inferiorList.add(prenda);
                                break;
                            case "zapatilla":
                                zapatillaList.add(prenda);
                                break;
                        }
                    }
                }

                if (!superiorList.isEmpty() && !inferiorList.isEmpty() && !zapatillaList.isEmpty()) {
                    Toast.makeText(getContext(), "Ha cargado", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), "No hay suficientes prendas para generar un outfit", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar prendas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}