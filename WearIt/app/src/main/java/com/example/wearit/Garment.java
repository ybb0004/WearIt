package com.example.wearit;

import java.io.Serializable;

// Clase que representa una prenda con sus atributos
public class Garment implements Serializable {

        private String id; // ID único (necesario para Firebase)
        private String size; // Talla
        private String brand; // Marca
        private String season; // Temporada
        private String color; // Color
        private String part; // Parte (Superior, Inferior, Zapatilla)
        private String style; // Estilo
        private String imageUrl; // URI de la imagen
        private String createdAt; // Fecha de creación (necesario para Firebase)
        private String userId; // ID del usuario (necesario para Firebase)
        private String username; // Nombre de usuario (necesario para Firebase)
        private String email; // Correo del usuario (necesario para Firebase)

        // Constructor vacío (necesario para Firebase)
        public Garment() {}

        // Constructor completo
        public Garment(String id, String size, String brand, String season, String color, String part, String style,
                       String imageUrl, String createdAt, String userId, String username, String email) {
                this.id = id;
                this.size = size;
                this.brand = brand;
                this.season = season;
                this.color = color;
                this.part = part;
                this.style = style;
                this.imageUrl = imageUrl;
                this.createdAt = createdAt;
                this.userId = userId;
                this.username = username;
                this.email = email;
        }

        // Getters y Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

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

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
}