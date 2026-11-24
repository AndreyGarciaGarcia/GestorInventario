package service;

import model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventarioServiceTest {

    private InventarioService inventario;

    @BeforeEach
    void setUp() {
        inventario = new InventarioService();
    }

    @Test
    void agregarProductoCorrectamente() {

        Producto producto = new Producto(
                1, "Portatil", new BigDecimal("899.99"), 10
        );

        inventario.agregarProducto(producto);

        assertEquals(1, inventario.listarProductos().size());
        assertEquals(producto, inventario.buscarProductoPorId(1));
    }

    @Test
    void noPermitirProductosDuplicados() {

        Producto producto = new Producto(
                1, "Portatil", new BigDecimal("899.99"), 10
        );

        inventario.agregarProducto(producto);

        assertThrows(IllegalArgumentException.class, () -> {
            inventario.agregarProducto(producto);
        });
    }

    @Test
    void noPermitirProductoNull() {

        assertThrows(IllegalArgumentException.class, () -> {
            inventario.agregarProducto(null);
        });
    }

    @Test
    void listarProductosCorrectamente() {

        Producto producto1 = new Producto(
                1, "Portatil", new BigDecimal("899.99"), 10
        );

        Producto producto2 = new Producto(
                2, "Ratón", new BigDecimal("25.50"), 20
        );

        inventario.agregarProducto(producto1);
        inventario.agregarProducto(producto2);

        List<Producto> productos = inventario.listarProductos();

        assertEquals(2, productos.size());
        assertTrue(productos.contains(producto1));
        assertTrue(productos.contains(producto2));
    }

    @Test
    void buscarProductoPorIdCorrectamente() {

        Producto producto = new Producto(
                1, "Portatil", new BigDecimal("899.99"), 10
        );

        inventario.agregarProducto(producto);

        Producto resultado = inventario.buscarProductoPorId(1);

        assertEquals(producto, resultado);
    }

    @Test
    void buscarProductoInexistente() {

        assertThrows(IllegalArgumentException.class, () -> {
            inventario.buscarProductoPorId(99);
        });
    }

    @Test
    void eliminarProductoCorrectamente() {

        Producto producto = new Producto(
                1, "Portatil", new BigDecimal("899.99"), 10
        );

        inventario.agregarProducto(producto);
        inventario.eliminarProducto(1);

        assertEquals(0, inventario.listarProductos().size());
    }

    @Test
    void actualizarStockCorrectamente() {

        Producto producto = new Producto(
                1, "Portatil", new BigDecimal("899.99"), 10
        );

        inventario.agregarProducto(producto);
        inventario.actualizarStock(1, 25);

        assertEquals(25, producto.getStock());
    }

    @Test
    void venderProductoCorrectamente() {

        Producto producto = new Producto(
                1, "Portatil", new BigDecimal("899.99"), 10
        );

        inventario.agregarProducto(producto);
        inventario.venderProducto(1, 3);

        assertEquals(7, producto.getStock());
    }

    @Test
    void noPermitirVentaSinStockSuficiente() {

        Producto producto = new Producto(
                1, "Portatil", new BigDecimal("899.99"), 5
        );

        inventario.agregarProducto(producto);

        assertThrows(IllegalArgumentException.class, () -> {
            inventario.venderProducto(1, 10);
        });

        assertEquals(5, producto.getStock());
    }

    @Test
    void calcularValorInventarioCorrectamente() {

        Producto producto1 = new Producto(
                1, "Portatil", new BigDecimal("100.00"), 2
        );

        Producto producto2 = new Producto(
                2, "Ratón", new BigDecimal("50.00"), 3
        );

        inventario.agregarProducto(producto1);
        inventario.agregarProducto(producto2);

        BigDecimal resultado = inventario.calcularValorInventario();

        assertEquals(new BigDecimal("350.00"), resultado);
    }

    @Test
    void obtenerProductosConStockBajoCorrectamente() {

        Producto producto1 = new Producto(
                1, "Portatil", new BigDecimal("899.99"), 2
        );

        Producto producto2 = new Producto(
                2, "Ratón", new BigDecimal("25.50"), 20
        );

        inventario.agregarProducto(producto1);
        inventario.agregarProducto(producto2);

        List<Producto> resultado =
                inventario.obtenerProductosConStockBajo(5);

        assertEquals(1, resultado.size());
        assertTrue(resultado.contains(producto1));
    }

    @Test
    void noPermitirLimiteNegativo() {

        assertThrows(IllegalArgumentException.class, () -> {
            inventario.obtenerProductosConStockBajo(-1);
        });
    }

    @Test
    void noPermitirVentaDeCeroUnidades() {

        Producto producto = new Producto(
                1, "Portatil", new BigDecimal("899.99"), 10
        );

        inventario.agregarProducto(producto);

        assertThrows(IllegalArgumentException.class, () -> {
            inventario.venderProducto(1, 0);
        });

        assertEquals(10, producto.getStock());
    }

    @Test
    void noPermitirVentaNegativa() {

        Producto producto = new Producto(
                1, "Portatil", new BigDecimal("899.99"), 10
        );

        inventario.agregarProducto(producto);

        assertThrows(IllegalArgumentException.class, () -> {
            inventario.venderProducto(1, -3);
        });
    }

    @Test
    void noPermitirActualizarProductoInexistente() {

        assertThrows(IllegalArgumentException.class, () -> {
            inventario.actualizarStock(99, 20);
        });
    }

    @Test
    void noPermitirStockNegativoAlActualizar() {

        Producto producto = new Producto(
                1, "Portatil", new BigDecimal("899.99"), 10
        );

        inventario.agregarProducto(producto);

        assertThrows(IllegalArgumentException.class, () -> {
            inventario.actualizarStock(1, -5);
        });

        assertEquals(10, producto.getStock());
    }

    @Test
    void noPermitirEliminarProductoInexistente() {

        assertThrows(IllegalArgumentException.class, () -> {
            inventario.eliminarProducto(99);
        });
    }

    @Test
    void noPermitirVenderProductoInexistente() {

        assertThrows(IllegalArgumentException.class, () -> {
            inventario.venderProducto(99, 1);
        });
    }

    @Test
    void calcularValorInventarioVacio() {

        BigDecimal resultado = inventario.calcularValorInventario();

        assertEquals(BigDecimal.ZERO, resultado);
    }

    @Test
    void restaurarProductosRecuperaElStockAnterior() {

        InventarioService inventario = new InventarioService();

        Producto producto = new Producto(
                1, "Teclado", new BigDecimal("25.99"), 10
        );

        inventario.agregarProducto(producto);

        List<Producto> copiaAnterior = new ArrayList<>();

        for (Producto p : inventario.listarProductos()) {
            copiaAnterior.add(new Producto(
                    p.getId(),
                    p.getNombre(),
                    p.getPrecio(),
                    p.getStock()
            ));
        }

        inventario.actualizarStock(1, 3);

        assertEquals(3, inventario.buscarProductoPorId(1).getStock());

        inventario.restaurarProductos(copiaAnterior);

        assertEquals(10, inventario.buscarProductoPorId(1).getStock());
    }


}
