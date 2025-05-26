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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
import java.util.List;

public class fragment_w02_0005_perfil extends Fragment {

    private static final String TAG = "PerfilFragment";

    private RecyclerView outfitsRecyclerView;
    private OutfitAdapter outfitAdapter;
    private List<Outfit> allOutfits;
    private List<Outfit> outfitList;
    private TextView profileName, scannedCountView;
    private MaterialButton settingsButton;
    private TabLayout tabLayout;
    private DatabaseReference clothesRef;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w02_0005_perfil, container, false);

        // Referencias UI
        settingsButton      = view.findViewById(R.id.settingsButton);
        tabLayout           = view.findViewById(R.id.tabLayout);
        outfitsRecyclerView = view.findViewById(R.id.outfitsRecyclerView);
        profileName         = view.findViewById(R.id.profileName);
        scannedCountView    = view.findViewById(R.id.scannedCount);

        // Animaciones
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
       view.findViewById(R.id.profileCard).startAnimation(fadeIn);
       view.findViewById(R.id.statsContainer).startAnimation(fadeIn);
       tabLayout.startAnimation(fadeIn);
       outfitsRecyclerView.startAnimation(fadeIn);

        // Carga nombre de usuario
        loadUserProfile();

        // Inicializa listas y adaptador ANTES de la carga
        allOutfits    = new ArrayList<>();
        outfitList    = new ArrayList<>();
        outfitAdapter = new OutfitAdapter(outfitList);
        outfitsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        outfitsRecyclerView.setAdapter(outfitAdapter);

        // Configura comportamiento de tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1: showCategory(Outfit.CATEGORY_INFERIOR); break;
                    case 2: showCategory(Outfit.CATEGORY_ZAPATILLA); break;
                    default: showCategory(Outfit.CATEGORY_SUPERIOR); break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Menú de configuración
        settingsButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_fragment_w02_0005_perfil_to_fragment_w02_0007_edit_profile);

        });

        // Inicializa Firebase y descarga prendas
        initializeFirebase();
        loadClothesFromFirebase();

        return view;
    }

    /** Carga el nombre a mostrar del perfil */
    private void loadUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String nombre = "Usuario";
        if (user != null) {
            if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                nombre = user.getDisplayName();
            } else if (user.getEmail() != null) {
                nombre = user.getEmail().split("@")[0];
            }
        }
        profileName.setText(nombre);
    }

    /** Crea la referencia usando email (puntos -> guiones bajos) */
    private void initializeFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            String key = user.getEmail().replace(".", "_");
            clothesRef = FirebaseDatabase.getInstance()
                    .getReference("usuarios")
                    .child(key)
                    .child("prendas");
            Log.d(TAG, "Ref Firebase: usuarios/" + key + "/prendas");
        }
    }

    /** Lee todas las prendas de Firebase y las convierte en Outfit */
    private void loadClothesFromFirebase() {
        if (clothesRef == null) return;

        allOutfits.clear();

        clothesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Prendas recibidas: " + snapshot.getChildrenCount());
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String imageUrl = ds.child("imagen_url").getValue(String.class);
                    String tipo     = ds.child("tipo").getValue(String.class);
                    if (imageUrl != null && tipo != null) {
                        // Usa el constructor existente: (int imageResId, String title, String category, String imageUrl)
                        // Ponemos imageResId=0 y title vacío
                        String category = tipo.equalsIgnoreCase("inferior") ?
                                Outfit.CATEGORY_INFERIOR :
                                tipo.equalsIgnoreCase("zapatilla") ?
                                        Outfit.CATEGORY_ZAPATILLA :
                                        Outfit.CATEGORY_SUPERIOR;
                        allOutfits.add(new Outfit(
                                0,
                                "",
                                category,
                                imageUrl
                        ));
                    }
                }

                // Actualiza UI
                outfitAdapter.notifyDataSetChanged();
                updateScannedCount();

                // Refresca pestaña activa
                int sel = tabLayout.getSelectedTabPosition();
                if      (sel == 1) showCategory(Outfit.CATEGORY_INFERIOR);
                else if (sel == 2) showCategory(Outfit.CATEGORY_ZAPATILLA);
                else               showCategory(Outfit.CATEGORY_SUPERIOR);

                Toast.makeText(getContext(),
                        "Total de prendas: " + allOutfits.size(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error al cargar prendas", error.toException());
                Toast.makeText(getContext(),
                        "Error al cargar prendas: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Filtra y muestra solo los outfits de la categoría dada */
    private void showCategory(String category) {
        outfitList.clear();
        for (Outfit o : allOutfits) {
            if (category.equals(o.getCategory())) {
                outfitList.add(o);
            }
        }
        outfitAdapter.notifyDataSetChanged();
    }

    /** Actualiza el contador de prendas escaneadas */
    private void updateScannedCount() {
        scannedCountView.setText(String.valueOf(allOutfits.size()));
    }

    /** Maneja las opciones del menú de perfil */
    private boolean onProfileMenuItem(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_profile) {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profileRoot, new fragment_w02_0007_edit_profile())
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireActivity(),
                    fragment_w01_0001_inicio_registro.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
            return true;
        }
        return false;
    }
}
