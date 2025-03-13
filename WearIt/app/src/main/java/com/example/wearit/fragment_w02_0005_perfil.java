package com.example.wearit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

// Fragmento que muestra el perfil del usuario con una lista de prendas
public class fragment_w02_0005_perfil extends Fragment {

    // Declaración de vistas y variables
    private RecyclerView outfitsRecyclerView; // RecyclerView para mostrar la lista de prendas
    private OutfitAdapter outfitAdapter; // Adaptador para el RecyclerView
    private List<Outfit> outfitList; // Lista de prendas

    // Método que se llama al crear la vista del fragmento
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla el layout del fragmento (fragment_w02_0005_perfil.xml)
        View view = inflater.inflate(R.layout.fragment_w02_0005_perfil, container, false);

        // Inicializa las vistas del layout
        MaterialButton backButton = (MaterialButton) view.findViewById(R.id.backButton);
        MaterialButton settingsButton = (MaterialButton) view.findViewById(R.id.settingsButton);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        outfitsRecyclerView = (RecyclerView) view.findViewById(R.id.outfitsRecyclerView);

        // Carga la animación de entrada (fade_in.xml)
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

        // Aplica la animación a los elementos principales del layout
        view.findViewById(R.id.profileCard).startAnimation(fadeInAnimation);
        view.findViewById(R.id.statsContainer).startAnimation(fadeInAnimation);
        tabLayout.startAnimation(fadeInAnimation);
        outfitsRecyclerView.startAnimation(fadeInAnimation);

        // Configura el listener para el botón "Atrás"
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aplica la animación al botón
                v.startAnimation(fadeInAnimation);
                // Regresa al fragmento anterior en la pila de navegación
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Configura el listener para el botón de configuración
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aplica la animación al botón
                v.startAnimation(fadeInAnimation);
                // Aquí puedes añadir la lógica para navegar a la pantalla de configuración
                // Por ejemplo: navegar a otro fragmento o actividad
            }
        });

        // Inicializa la lista de prendas
        outfitList = new ArrayList<>();
        // Añade algunas prendas de ejemplo (puedes reemplazarlas con datos reales)
        outfitList.add(new Outfit(R.drawable.camiseta, "Camiseta Ejemplo"));
        outfitList.add(new Outfit(R.drawable.pantalon, "Pantalón Ejemplo"));

        // Crea el adaptador para el RecyclerView y lo asigna
        outfitAdapter = new OutfitAdapter(outfitList);
        outfitsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Cuadrícula de 2 columnas
        outfitsRecyclerView.setAdapter(outfitAdapter);

        // Configura el listener para las pestañas del TabLayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Según la pestaña seleccionada, carga el contenido correspondiente
                switch (tab.getPosition()) {
                    case 0: // Pestaña "Prendas"
                        loadOutfits();
                        break;
                    case 1: // Pestaña "Colecciones"
                        loadCollections();
                        break;
                    case 2: // Pestaña "Guardados"
                        loadSaved();
                        break;
                    case 3: // Pestaña "Me gusta"
                        loadLikes();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No hacemos nada cuando se deselecciona una pestaña
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No hacemos nada cuando se vuelve a seleccionar una pestaña
            }
        });

        // Devuelve la vista inflada
        return view;
    }

    // Método para cargar las prendas (pestaña "Prendas")
    private void loadOutfits() {
        // Limpia la lista actual
        outfitList.clear();
        // Añade prendas de ejemplo (reemplaza con datos reales)
        outfitList.add(new Outfit(R.drawable.camiseta, "Camiseta Ejemplo"));
        // Notifica al adaptador que los datos han cambiado
        outfitAdapter.notifyDataSetChanged();
    }

    // Método para cargar las colecciones (pestaña "Colecciones")
    private void loadCollections() {
        // Limpia la lista actual
        outfitList.clear();
        // Añade colecciones de ejemplo (reemplaza con datos reales)
        outfitList.add(new Outfit(R.drawable.camiseta, "Colección Ejemplo"));
        // Notifica al adaptador que los datos han cambiado
        outfitAdapter.notifyDataSetChanged();
    }

    // Método para cargar los guardados (pestaña "Guardados")
    private void loadSaved() {
        // Limpia la lista actual
        outfitList.clear();
        // Añade elementos guardados de ejemplo (reemplaza con datos reales)
        outfitList.add(new Outfit(R.drawable.camiseta, "Guardado Ejemplo"));
        // Notifica al adaptador que los datos han cambiado
        outfitAdapter.notifyDataSetChanged();
    }

    // Método para cargar los "Me gusta" (pestaña "Me gusta")
    private void loadLikes() {
        // Limpia la lista actual
        outfitList.clear();
        // Añade elementos "Me gusta" de ejemplo (reemplaza con datos reales)
        outfitList.add(new Outfit(R.drawable.camiseta, "Me Gusta Ejemplo"));
        // Notifica al adaptador que los datos han cambiado
        outfitAdapter.notifyDataSetChanged();
    }
}