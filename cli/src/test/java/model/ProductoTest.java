package model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    void crearProductoCorrectamente() {

        Producto producto = new Producto(
                1,
                "Portatil",
                new BigDecimal("899.99"),
                10
        );

        assertEquals(1, producto.getId());
        assertEquals("Portatil", producto.getNombre());
        assertEquals(new BigDecimal("899.99"), producto.getPrecio());
        assertEquals(10, producto.getStock());
    }

    @Test
    void noPermitirIdInvalido() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Producto(0, "Portatil", new BigDecimal("899.99"), 10);
        });
    }

    @Test
    void noPermitirNombreVacio() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Producto(1, "", new BigDecimal("899.99"), 10);
        });
    }

    @Test
    void noPermitirPrecioNegativo() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Producto(1, "Portatil", new BigDecimal("-10"), 10);
        });
    }

    @Test
    void noPermitirStockNegativo() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Producto(1, "Portatil", new BigDecimal("899.99"), -5);
        });
    }

    @Test
    void copiarCreaUnaCopiaIndependiente() {

        Producto original = new Producto(1, "Portatil", new BigDecimal("899.99"), 10);

        Producto copia = original.copiar();

        assertEquals(original, copia);
        assertNotSame(original, copia);

        copia.setStock(3);

        assertEquals(10, original.getStock());
        assertEquals(3, copia.getStock());
    }

    @Test
    void dosPreciosConDistintaEscalaSonIguales() {

        Producto a = new Producto(1, "Portatil", new BigDecimal("10.0"), 5);
        Producto b = new Producto(1, "Portatil", new BigDecimal("10.00"), 5);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void productosConDistintoIdNoSonIguales() {

        Producto a = new Producto(1, "Portatil", new BigDecimal("899.99"), 10);
        Producto b = new Producto(2, "Portatil", new BigDecimal("899.99"), 10);

        assertNotEquals(a, b);
    }

    @Test
    void toStringIncluyeLosCampos() {

        Producto producto = new Producto(7, "Ratón", new BigDecimal("25.50"), 4);

        String texto = producto.toString();

        assertTrue(texto.contains("7"));
        assertTrue(texto.contains("Ratón"));
        assertTrue(texto.contains("25.50"));
        assertTrue(texto.contains("4"));
    }
}
