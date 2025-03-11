package com.example.wearit;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class fragment_w200000_inicio_app extends Fragment {

    private GarmentManager garmentManager;
    private DatabaseReference databaseReference;
    private ImageView imageView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Inicializar GarmentManager
        garmentManager = new GarmentManager();
        databaseReference = FirebaseDatabase.getInstance().getReference("garments");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout del Fragment
        View view = inflater.inflate(R.layout.fragment_w200000_inicio_app, container, false);

        // Inicializar ImageView
        imageView = view.findViewById(R.id.imageView2);  // Asegúrate de tener un ImageView en tu XML

        // Vincular el botón
        Button btnSaveGarment = view.findViewById(R.id.btnSaveGarment);
        btnSaveGarment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                garmentManager.saveGarment(
                        "superior",
                        "negro",
                        "https://i.imgur.com/LmTtiBD.jpeg",
                        "corto",
                        "Camiseta blanca",
                        "Es mi camiseta favorita",
                        "casual",
                        "verano",
                        "recto",
                        "camiseta",
                        "userId2",
                        "marcossanchez",
                        "marcossanchez@gmail.com"
                );

                // Cargar imagen después de guardar
                loadImageFromFirebase("userId2");
            }
        });

        return view;
    }

    // Método para cargar imagen de Firebase
    private void loadImageFromFirebase(String userId) {
        databaseReference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(imageUrl)
                                        .into(imageView);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Manejo de error
                    }
                });
    }
}
