package com.gestorinventario.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nombre;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer stock;

    @Column(length = 60)
    private String categoria;

    protected Producto() {
        // Requerido por JPA
    }

    public Producto(String nombre, BigDecimal precio, Integer stock, String categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Producto otro)) {
            return false;
        }

        return id != null && Objects.equals(id, otro.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Producto{id=" + id + ", nombre='" + nombre
                + "', precio=" + precio + ", stock=" + stock
                + ", categoria='" + categoria + "'}";
    }
}
