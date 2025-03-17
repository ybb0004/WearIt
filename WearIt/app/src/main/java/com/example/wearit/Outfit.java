package com.example.wearit;

public class Outfit {
    // Recursos de la imagen (ID del drawable) y título de la prenda
    private int imageResId;
    private String title;

    // Constructor para crear una nueva prenda
    public Outfit(int imageResId, String title) {
        this.imageResId = imageResId;
        this.title = title;
    }

    // Método para obtener el ID del recurso de la imagen
    public int getImageResId() {
        return imageResId;
    }

    // Método para obtener el título de la prenda
    public String getTitle() {
        return title;
    }
}
