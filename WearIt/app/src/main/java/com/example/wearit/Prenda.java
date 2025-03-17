package com.example.wearit;

public class Prenda {
    private String tipo;
    private String color;
    private String talla;
    private String marca;
    private String fechaCompra;

    // Constructor, getters y setters
    public Prenda(String tipo, String color, String talla, String marca, String fechaCompra) {
        this.tipo = tipo;
        this.color = color;
        this.talla = talla;
        this.marca = marca;
        this.fechaCompra = fechaCompra;
    }

    // Getters y setters (opcional, pero recomendado)

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(String fechaCompra) {
        this.fechaCompra = fechaCompra;
    }
}