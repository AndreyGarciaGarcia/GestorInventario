package com.gestorinventario.backend.repository;

import com.gestorinventario.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /** Productos con stock menor o igual al límite dado. */
    List<Producto> findByStockLessThanEqual(Integer limite);

    /** Productos cuyo nombre contiene el texto indicado (ignorando mayúsculas). */
    List<Producto> findByNombreContainingIgnoreCase(String texto);

    /** Productos de una categoría exacta. */
    List<Producto> findByCategoriaIgnoreCase(String categoria);

    /**
     * Suma de {@code precio * stock} de todos los productos.
     *
     * @return el valor total del inventario (cero si no hay productos)
     */
    @Query("select coalesce(sum(p.precio * p.stock), 0) from Producto p")
    BigDecimal calcularValorTotalInventario();
}
