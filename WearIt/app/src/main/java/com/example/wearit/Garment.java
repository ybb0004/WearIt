package com.example.wearit;

import java.io.Serializable;

// Clase que representa una prenda con sus atributos
public class Garment implements Serializable {

        private String size; // Talla
        private String brand; // Marca
        private String season; // Temporada
        private String color; // Color
        private String part; // Parte (Superior, Inferior, Zapatilla)
        private String style; // Estilo
        private String imageUrl; // URI de la imagen

        // Constructor vacío (necesario para Firebase)
        public Garment() {}

        // Constructor con parámetros
        public Garment(String size, String brand, String season, String color, String part, String style, String imageUrl) {
                this.size = size;
                this.brand = brand;
                this.season = season;
                this.color = color;
                this.part = part;
                this.style = style;
                this.imageUrl = imageUrl;
        }

        // Getters y Setters
        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }

        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }

        public String getSeason() { return season; }
        public void setSeason(String season) { this.season = season; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public String getPart() { return part; }
        public void setPart(String part) { this.part = part; }

        public String getStyle() { return style; }
        public void setStyle(String style) { this.style = style; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}