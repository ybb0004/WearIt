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
    public static void saveGarment(String size, String brand, String season, String color, String part,
                                   String style, String imageUrl, String userId, String username, String email,
                                   SaveGarmentCallback callback) {
        // Generar un ID único para la prenda
        String id = databaseReference.push().getKey();

        // Crear el objeto Garment
        Garment garment = new Garment(
                id,
                size,
                brand,
                season,
                color,
                part,
                style,
                imageUrl,
                new Date().toString(), // Fecha actual (puedes usar ISOString si lo prefieres)
                userId,
                username,
                email
        );

        // Guardar en Firebase
        if (id != null) {
            databaseReference.child(id).setValue(garment)
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Prenda guardada correctamente con ID: " + id);
                        callback.onSuccess(id); // Llama al callback en caso de éxito
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("Error al guardar la prenda: " + e.getMessage());
                        callback.onFailure(e.getMessage()); // Llama al callback en caso de fallo
                    });
        } else {
            callback.onFailure("Error al generar el ID de la prenda");
        }
    }

    public interface SaveGarmentCallback {
        void onSuccess(String garmentId);
        void onFailure(String errorMessage);
    }
}
