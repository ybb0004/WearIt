package com.example.wearit;

public class Garment {

        private String id;
        private String category;
        private String color;
        private String createdAt;
        private String imageUrl;
        private String length;
        private String name;
        private String notes;
        private String occasion;
        private String season;
        private String style;
        private String type;
        private String userId;
        private String username;
        private String email;

        // Constructor vacío (necesario para Firebase)
        public Garment() {}

        // Constructor con parámetros
        public Garment(String id, String category, String color, String createdAt, String imageUrl,
                       String length, String name, String notes, String occasion, String season,
                       String style, String type, String userId,String username, String email) {
                this.id = id;
                this.category = category;
                this.color = color;
                this.createdAt = createdAt;
                this.imageUrl = imageUrl;
                this.length = length;
                this.name = name;
                this.notes = notes;
                this.occasion = occasion;
                this.season = season;
                this.style = style;
                this.type = type;
                this.userId = userId;
                this.username = username;
                this.email = email;
        }

        // Getters y Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public String getLength() { return length; }
        public void setLength(String length) { this.length = length; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public String getOccasion() { return occasion; }
        public void setOccasion(String occasion) { this.occasion = occasion; }

        public String getSeason() { return season; }
        public void setSeason(String season) { this.season = season; }

        public String getStyle() { return style; }
        public void setStyle(String style) { this.style = style; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

}
