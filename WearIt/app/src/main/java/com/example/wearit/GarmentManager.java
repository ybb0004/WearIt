package com.example.wearit;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Date;

public class GarmentManager {

    private static DatabaseReference databaseReference;

    // Constructor
    public GarmentManager() {
        // Inicializar la referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference("garments");
    }

    // Método para guardar una prenda
    public static void saveGarment(String category, String color, String imageUrl, String length,
                            String name, String notes, String occasion, String season,
                            String style, String type, String userId, String username, String email) {
        // Generar un ID único para la prenda
        String id = databaseReference.push().getKey();

        // Crear el objeto Garment
        Garment garment = new Garment(
                id,
                category,
                color,
                new Date().toString(), // Fecha actual (puedes usar ISOString si lo prefieres)
                imageUrl,
                length,
                name,
                notes,
                occasion,
                season,
                style,
                type,
                userId,
                username,
                email
        );

        // Guardar en Firebase
        if (id != null) {
            databaseReference.child(id).setValue(garment)
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Prenda guardada correctamente con ID: " + id);
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("Error al guardar la prenda: " + e.getMessage());
                    });
        }
    }
}