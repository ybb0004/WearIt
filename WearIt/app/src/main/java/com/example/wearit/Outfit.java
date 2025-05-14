package com.example.wearit;

/**
 * Modelo de prenda u outfit, con imagen, título y categoría.
 */
public class Outfit {
    // Constantes para categorías de prenda
    public static final String CATEGORY_SUPERIOR  = "Superior";
    public static final String CATEGORY_INFERIOR  = "Inferior";
    public static final String CATEGORY_ZAPATILLA = "Zapatilla";

    // Recursos de la imagen (ID del drawable) y título de la prenda
    private int imageResId;
    private String title;
    private String category;  // Nueva propiedad de categoría

    /**
     * Constructor legacy que mantiene compatibilidad.
     * Categoría por defecto queda nula.
     */
    public Outfit(int imageResId, String title) {
        this.imageResId = imageResId;
        this.title = title;
        this.category = null;
    }

    /**
     * Constructor completo con categoría.
     */
    public Outfit(int imageResId, String title, String category) {
        this.imageResId = imageResId;
        this.title = title;
        this.category = category;
    }

    /**
     * Devuelve el ID del recurso de la imagen.
     */
    public int getImageResId() {
        return imageResId;
    }

    /**
     * Devuelve el título de la prenda.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Devuelve la categoría de la prenda.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Permite cambiar la categoría de la prenda.
     */
    public void setCategory(String category) {
        this.category = category;
    }
}