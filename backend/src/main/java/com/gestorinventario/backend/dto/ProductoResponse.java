package com.gestorinventario.backend.dto;

import com.gestorinventario.backend.model.Producto;

import java.math.BigDecimal;

/**
 * Respuesta JSON de un producto.
 *
 * @param id        identificador generado por la base de datos
 * @param nombre    nombre del producto
 * @param precio    precio por unidad
 * @param stock     unidades disponibles
 * @param categoria categoría opcional
 * @param valorTotal valor de la posición (precio × stock)
 */
public record ProductoResponse(
        Long id,
        String nombre,
        BigDecimal precio,
        Integer stock,
        String categoria,
        BigDecimal valorTotal
) {

    /** Convierte una entidad en su representación JSON. */
    public static ProductoResponse desde(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getCategoria(),
                producto.getPrecio().multiply(BigDecimal.valueOf(producto.getStock()))
        );
    }
}
