package model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidad que representa un producto del inventario.
 *
 * <p>Toda la validación se realiza en los setters, de modo que un objeto
 * {@code Producto} siempre está en un estado válido.</p>
 *
 * @author GestorInventario
 * @version 1.0.0
 */
public class Producto {

    private int id;
    private String nombre;
    private BigDecimal precio;
    private int stock;

    public Producto(int id, String nombre, BigDecimal precio, int stock) {
        setId(id);
        setNombre(nombre);
        setPrecio(precio);
        setStock(stock);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    // Setters
    public void setId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser positivo");
        }

        this.id = id;
    }

    public void setNombre(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        this.nombre = nombre;
    }

    public void setPrecio(BigDecimal precio) {

        if (precio == null) {
            throw new IllegalArgumentException("El precio no puede ser null");
        }

        if (precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }

        this.precio = precio;
    }

    public void setStock(int stock) {

        if (stock < 0){
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        this.stock = stock;
    }

    /**
     * Devuelve una copia idéntica e independiente del producto.
     *
     * @return copia del producto
     */
    public Producto copiar() {
        return new Producto(id, nombre, precio, stock);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Producto otro)) {
            return false;
        }

        return id == otro.id
                && stock == otro.stock
                && Objects.equals(nombre, otro.nombre)
                && precio.compareTo(otro.precio) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, precio.stripTrailingZeros(), stock);
    }

    @Override
    public String toString() {
        return "Producto{id=" + id
                + ", nombre='" + nombre + '\''
                + ", precio=" + precio
                + ", stock=" + stock
                + '}';
    }
}
