package com.example.wearit;

/**
 * Modelo de prenda u outfit, con imagen, título y categoría.
 */
public class Outfit {
    public static final String CATEGORY_SUPERIOR  = "Superior";
    public static final String CATEGORY_INFERIOR  = "Inferior";
    public static final String CATEGORY_ZAPATILLA = "Zapatilla";

    private int imageResId;    // Solo para recursos locales (no usado con Firebase normalmente)
    private String title;
    private String category;
    private String imageUrl;

    // Constructor vacío requerido por Firebase
    public Outfit() {
    }

    // Constructor completo
    public Outfit(int imageResId, String title, String category, String imageUrl) {
        this.imageResId = imageResId;
        this.title = title;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    // Getters y setters

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
