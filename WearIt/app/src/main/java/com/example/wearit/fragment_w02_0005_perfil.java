package com.example.wearit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class fragment_w02_0005_perfil extends Fragment {

    private RecyclerView outfitsRecyclerView;
    private OutfitAdapter outfitAdapter;
    private List<Outfit> allOutfits;
    private List<Outfit> outfitList;
    private TextView profileName, scannedCountView;
    private MaterialButton settingsButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w02_0005_perfil, container, false);

        // Referencias UI
        settingsButton    = view.findViewById(R.id.settingsButton);
        TabLayout tabLayout  = view.findViewById(R.id.tabLayout);
        outfitsRecyclerView  = view.findViewById(R.id.outfitsRecyclerView);
        profileName          = view.findViewById(R.id.profileName);
        scannedCountView     = view.findViewById(R.id.scannedCount);

        // Animaciones
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.findViewById(R.id.profileCard).startAnimation(fadeIn);
        view.findViewById(R.id.statsContainer).startAnimation(fadeIn);
        tabLayout.startAnimation(fadeIn);
        outfitsRecyclerView.startAnimation(fadeIn);

        // Carga nombre de usuario
        loadUserProfile();

        // Inicializa listas y adaptador
        allOutfits    = new ArrayList<>();
        outfitList    = new ArrayList<>();
        outfitAdapter = new OutfitAdapter(outfitList);
        outfitsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        outfitsRecyclerView.setAdapter(outfitAdapter);

        // Carga datos y actualiza contador
        loadAllOutfitsFromDB();
        updateScannedCount();

        // Pestaña por defecto
        showCategory(Outfit.CATEGORY_SUPERIOR);

        // Listener de pestañas
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                int id = tab.getPosition();
                if (id == 0) showCategory(Outfit.CATEGORY_SUPERIOR);
                else if (id == 1) showCategory(Outfit.CATEGORY_INFERIOR);
                else if (id == 2) showCategory(Outfit.CATEGORY_ZAPATILLA);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
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
            // Reemplaza R.id.fragment_container por el ID real de tu contenedor de fragments
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profileRoot, new fragment_w02_0007_edit_profile())
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        else if (id == R.id.action_logout) {
            // TODO: tu lógica de logout real
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
        // TODO: leer usuario real de BD
        String nombreDesdeBD = "Usuario Ejemplo";
        profileName.setText(nombreDesdeBD);
    }

    private void loadAllOutfitsFromDB() {
        // TODO: tu consulta real
        allOutfits.clear();
        allOutfits.add(new Outfit(R.drawable.camiseta, "Camiseta Azul", Outfit.CATEGORY_SUPERIOR));
        allOutfits.add(new Outfit(R.drawable.pantalon, "Pantalón Negro", Outfit.CATEGORY_INFERIOR));
        allOutfits.add(new Outfit(R.drawable.zapatilla, "Zapatilla Blanca", Outfit.CATEGORY_ZAPATILLA));
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
}
