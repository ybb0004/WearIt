package com.example.wearit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.transition.MaterialElevationScale;
import java.util.List;

// Adaptador para mostrar una lista de prendas en un RecyclerView
public class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder> {

    // Lista de prendas que se mostrarán en el RecyclerView
    private List<Outfit> outfitList;

    // Constructor del adaptador, recibe la lista de prendas
    public OutfitAdapter(List<Outfit> outfitList) {
        this.outfitList = outfitList;
    }

    // Método que crea un nuevo ViewHolder cuando se necesita mostrar un ítem
    @NonNull
    @Override
    public OutfitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout del ítem (item_outfit_outlined.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outfit_outlined, parent, false);
        return new OutfitViewHolder(view);
    }

    // Método que vincula los datos de una prenda al ViewHolder
    @Override
    public void onBindViewHolder(@NonNull OutfitViewHolder holder, int position) {
        // Obtiene la prenda en la posición actual
        Outfit outfit = outfitList.get(position);
        // Configura la imagen y el título en las vistas del ViewHolder
        holder.outfitImage.setImageResource(outfit.getImageResId());
        holder.outfitTitle.setText(outfit.getTitle());

        // Configura un listener para el clic en el MaterialCardView
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea una animación de agrandamiento (MaterialElevationScale)
                MaterialElevationScale transition = new MaterialElevationScale(true);
                transition.setDuration(300); // Duración de la animación (300ms)

                // Obtiene el ViewGroup padre del MaterialCardView
                ViewGroup parent = (ViewGroup) holder.card.getParent();
                if (parent != null) {
                    // Inicia la transición de agrandamiento
                    TransitionManager.beginDelayedTransition(parent, transition);
                    holder.card.setScaleX(1.2f); // Escala a 1.2x
                    holder.card.setScaleY(1.2f);

                    // Programa una animación de regreso después de 1 segundo
                    holder.card.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Crea una animación de regreso
                            MaterialElevationScale reverseTransition = new MaterialElevationScale(false);
                            reverseTransition.setDuration(250); // Duración de la animación (250ms)

                            // Inicia la transición de regreso
                            TransitionManager.beginDelayedTransition(parent, reverseTransition);
                            holder.card.setScaleX(1.0f); // Vuelve a la escala original
                            holder.card.setScaleY(1.0f);
                        }
                    }, 1000); // Retraso de 1 segundo
                }
            }
        });
    }

    // Método que devuelve la cantidad de ítems en la lista
    @Override
    public int getItemCount() {
        return outfitList.size();
    }

    // Clase interna que representa el ViewHolder para cada ítem del RecyclerView
    static class OutfitViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card; // Contenedor principal del ítem
        ShapeableImageView outfitImage; // Imagen de la prenda
        TextView outfitTitle; // Título de la prenda

        // Constructor del ViewHolder
        public OutfitViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializa las vistas del layout item_outfit_outlined.xml
            card = (MaterialCardView) itemView.findViewById(R.id.card);
            outfitImage = (ShapeableImageView) itemView.findViewById(R.id.outfitImage);

        }
    }
}