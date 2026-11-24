package com.gestorinventario.backend.service;

import com.gestorinventario.backend.dto.ProductoRequest;
import com.gestorinventario.backend.dto.ProductoResponse;
import com.gestorinventario.backend.dto.ResumenInventarioResponse;
import com.gestorinventario.backend.exception.ProductoNoEncontradoException;
import com.gestorinventario.backend.exception.ReglaNegocioException;
import com.gestorinventario.backend.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests de integración del servicio contra la base de datos H2 real
 * (perfil {@code local}).
 */
@SpringBootTest
@ActiveProfiles("local")
class InventarioServiceTest {

    @Autowired
    private InventarioService servicio;

    @Autowired
    private ProductoRepository repositorio;

    @BeforeEach
    void limpiar() {
        repositorio.deleteAll();
    }

    private ProductoRequest producto(String nombre, String precio, int stock) {
        return new ProductoRequest(nombre, new BigDecimal(precio), stock, "Test");
    }

    @Test
    void crearProductoLoPersisteConId() {

        ProductoResponse creado = servicio.crear(producto("Portatil", "899.99", 10));

        assertNotNull(creado.id());
        assertEquals("Portatil", creado.nombre());
        assertEquals(new BigDecimal("899.99"), creado.precio());
        assertEquals(10, creado.stock());
        assertEquals(new BigDecimal("8999.90"), creado.valorTotal());
    }

    @Test
    void crearNombreDuplicadoLanzaConflicto() {

        servicio.crear(producto("Ratón", "25.50", 5));

        assertThrows(ReglaNegocioException.class,
                () -> servicio.crear(producto("RATÓN", "30.00", 2)));
    }

    @Test
    void listarDevuelveTodos() {

        servicio.crear(producto("A", "1.00", 1));
        servicio.crear(producto("B", "2.00", 2));

        assertEquals(2, servicio.listar().size());
    }

    @Test
    void buscarPorIdInexistenteLanza404() {

        assertThrows(ProductoNoEncontradoException.class,
                () -> servicio.buscarPorId(9999L));
    }

    @Test
    void venderDescuentaStock() {

        ProductoResponse creado = servicio.crear(producto("Teclado", "79.90", 10));

        ProductoResponse trasVenta = servicio.vender(creado.id(), 3);

        assertEquals(7, trasVenta.stock());
        assertEquals(10, servicio.buscarPorId(creado.id()).stock() + 3);
    }

    @Test
    void venderMasDeLoHayLanzaConflictoYNoCambia() {

        ProductoResponse creado = servicio.crear(producto("Webcam", "39.95", 2));

        assertThrows(ReglaNegocioException.class,
                () -> servicio.vender(creado.id(), 5));

        assertEquals(2, servicio.buscarPorId(creado.id()).stock());
    }

    @Test
    void venderCantidadNoPositivaLanzaError() {

        ProductoResponse creado = servicio.crear(producto("USB", "8.75", 5));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.vender(creado.id(), 0));
    }

    @Test
    void actualizarStockRechazaNegativo() {

        ProductoResponse creado = servicio.crear(producto("SSD", "99.90", 5));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarStock(creado.id(), -1));

        assertEquals(5, servicio.buscarPorId(creado.id()).stock());
    }

    @Test
    void eliminarQuitaElProducto() {

        ProductoResponse creado = servicio.crear(producto("Silla", "249.00", 1));

        servicio.eliminar(creado.id());

        assertThrows(ProductoNoEncontradoException.class,
                () -> servicio.buscarPorId(creado.id()));
    }

    @Test
    void stockBajoDevuelveLosQueEstanBajo() {

        servicio.crear(producto("Bajo", "10.00", 2));
        servicio.crear(producto("Alto", "10.00", 50));

        var resultado = servicio.stockBajo(5);

        assertEquals(1, resultado.size());
        assertEquals("Bajo", resultado.get(0).nombre());
    }

    @Test
    void stockBajoConLimiteNegativoLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> servicio.stockBajo(-1));
    }

    @Test
    void buscarPorNombreEncuentraIgnorandoMayusculas() {

        servicio.crear(producto("Teclado Mecánico", "79.90", 3));
        servicio.crear(producto("Monitor", "189.00", 3));

        var resultado = servicio.buscarPorNombre("MECÁNICO");

        assertEquals(1, resultado.size());
        assertEquals("Teclado Mecánico", resultado.get(0).nombre());
    }

    @Test
    void valorTotalEsPrecioPorStock() {

        servicio.crear(producto("A", "10.00", 2));
        servicio.crear(producto("B", "5.00", 3));

        assertEquals(new BigDecimal("35.00"), servicio.valorTotal());
    }

    @Test
    void resumenCuentaProductosUnidadesYStockBajo() {

        servicio.crear(producto("A", "10.00", 2));
        servicio.crear(producto("B", "10.00", 20));
        servicio.crear(producto("C", "10.00", 5));

        ResumenInventarioResponse resumen = servicio.resumen(5);

        assertEquals(3, resumen.totalProductos());
        assertEquals(27, resumen.totalUnidades());
        assertEquals(new BigDecimal("270.00"), resumen.valorTotal());
        assertEquals(2, resumen.stockBajo());
    }

    @Test
    void inventarioVacioTieneValorCero() {

        assertTrue(servicio.listar().isEmpty());
        assertEquals(BigDecimal.ZERO, servicio.valorTotal());

        ResumenInventarioResponse resumen = servicio.resumen(5);

        assertEquals(0, resumen.totalProductos());
        assertFalse(servicio.listar().isEmpty() && resumen.totalProductos() > 0);
    }
}
