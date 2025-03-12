package com.example.wearit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class fragment_w02_0005_perfil extends Fragment {

    private RecyclerView outfitsRecyclerView; // Declarado como campo de clase
    private OutfitAdapter outfitAdapter;
    private List<Outfit> outfitList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w02_0005_perfil, container, false);

        // Inicializar vistas
        MaterialButton backButton = view.findViewById(R.id.backButton);
        MaterialButton settingsButton = view.findViewById(R.id.settingsButton);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        outfitsRecyclerView = view.findViewById(R.id.outfitsRecyclerView); // Inicialización

        // Cargar animación
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

        // Animar entrada de elementos (usando IDs existentes)
        view.findViewById(R.id.profileCard).startAnimation(fadeInAnimation);
        view.findViewById(R.id.statsContainer).startAnimation(fadeInAnimation);
        tabLayout.startAnimation(fadeInAnimation);
        outfitsRecyclerView.startAnimation(fadeInAnimation);

        // Evento del botón Atrás
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(fadeInAnimation);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Evento del botón de Configuración
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(fadeInAnimation);
                // Navegar a pantalla de configuración (ajusta según tu lógica)
            }
        });

        // Configurar RecyclerView
        outfitList = new ArrayList<>();
        // Datos de ejemplo (reemplaza con datos reales)
        outfitList.add(new Outfit(R.drawable.camiseta, R.drawable.pantalon));
        outfitList.add(new Outfit(R.drawable.camiseta, R.drawable.pantalon));

        outfitAdapter = new OutfitAdapter(outfitList);
        outfitsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        outfitsRecyclerView.setAdapter(outfitAdapter);

        // Configurar TabLayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Cambiar contenido según la pestaña seleccionada
                switch (tab.getPosition()) {
                    case 0: // Prendas
                        loadOutfits();
                        break;
                    case 1: // Colecciones
                        loadCollections();
                        break;
                    case 2: // Guardados
                        loadSaved();
                        break;
                    case 3: // Me gusta
                        loadLikes();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    private void loadOutfits() {
        // Cargar prendas (ejemplo)
        outfitList.clear();
        outfitList.add(new Outfit(R.drawable.camiseta, R.drawable.pantalon));
        outfitAdapter.notifyDataSetChanged();
    }

    private void loadCollections() {
        // Cargar colecciones (ejemplo)
        outfitList.clear();
        outfitList.add(new Outfit(R.drawable.camiseta, R.drawable.pantalon));
        outfitAdapter.notifyDataSetChanged();
    }

    private void loadSaved() {
        // Cargar guardados (ejemplo)
        outfitList.clear();
        outfitList.add(new Outfit(R.drawable.camiseta, R.drawable.pantalon));
        outfitAdapter.notifyDataSetChanged();
    }

    private void loadLikes() {
        // Cargar me gusta (ejemplo)
        outfitList.clear();
        outfitList.add(new Outfit(R.drawable.camiseta, R.drawable.pantalon));
        outfitAdapter.notifyDataSetChanged();
    }
}

// Clase para los datos del Outfit
class Outfit {
    private int image1ResId;
    private int image2ResId;

    public Outfit(int image1ResId, int image2ResId) {
        this.image1ResId = image1ResId;
        this.image2ResId = image2ResId;
    }

    public int getImage1ResId() {
        return image1ResId;
    }

    public int getImage2ResId() {
        return image2ResId;
    }
}

// Adaptador para el RecyclerView
class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder> {
    private List<Outfit> outfitList;

    public OutfitAdapter(List<Outfit> outfitList) {
        this.outfitList = outfitList;
    }

    @Override
    public OutfitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outfit, parent, false);
        return new OutfitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OutfitViewHolder holder, int position) {
        Outfit outfit = outfitList.get(position);
        holder.image1.setImageResource(outfit.getImage1ResId());
        holder.image2.setImageResource(outfit.getImage2ResId());
    }

    @Override
    public int getItemCount() {
        return outfitList.size();
    }

    static class OutfitViewHolder extends RecyclerView.ViewHolder {
        ImageView image1, image2; // Ahora con importación correcta

        public OutfitViewHolder(View itemView) {
            super(itemView);
            image1 = itemView.findViewById(R.id.outfitImage1);
            image2 = itemView.findViewById(R.id.outfitImage2);
        }
    }
}