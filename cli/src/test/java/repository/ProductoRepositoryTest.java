package repository;

import model.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void guardarProductosCreaArchivo() throws IOException {

        Path archivo = tempDir.resolve("productos.csv");
        ProductoRepository repository = new ProductoRepository(archivo);

        Producto producto = new Producto(
                1, "Teclado", new BigDecimal("25.99"), 10
        );

        repository.guardarTodos(List.of(producto));

        assertTrue(Files.exists(archivo));
    }

    @Test
    void guardarYCargarProductosCorrectamente() throws IOException {

        Path archivo = tempDir.resolve("productos.csv");
        ProductoRepository repository = new ProductoRepository(archivo);

        Producto producto = new Producto(
                1, "Teclado", new BigDecimal("25.99"), 10
        );

        repository.guardarTodos(List.of(producto));

        List<Producto> productos = repository.cargarTodos();

        assertEquals(1, productos.size());
        assertEquals(1, productos.get(0).getId());
        assertEquals("Teclado", productos.get(0).getNombre());
        assertEquals(new BigDecimal("25.99"), productos.get(0).getPrecio());
        assertEquals(10, productos.get(0).getStock());
    }

    @Test
    void archivoInexistenteDevuelveListaVacia() throws IOException {

        Path archivo = tempDir.resolve("inexistente.csv");
        ProductoRepository repository = new ProductoRepository(archivo);

        List<Producto> productos = repository.cargarTodos();

        assertTrue(productos.isEmpty());
    }

    @Test
    void ignorarLineasVacias() throws IOException {

        Path archivo = tempDir.resolve("productos.csv");

        Files.writeString(
                archivo,
                "1;Teclado;25.99;10\n\n"
        );

        ProductoRepository repository = new ProductoRepository(archivo);

        List<Producto> productos = repository.cargarTodos();

        assertEquals(1, productos.size());
        assertEquals("Teclado", productos.get(0).getNombre());
    }

    @Test
    void formatoIncorrectoLanzaIOException() throws IOException {

        Path archivo = tempDir.resolve("productos.csv");

        Files.writeString(archivo, "1;Teclado;25.99");

        ProductoRepository repository = new ProductoRepository(archivo);

        assertThrows(
                IOException.class,
                () -> repository.cargarTodos()
        );
    }

    @Test
    void datosNumericosIncorrectosLanzanIOException() throws IOException {

        Path archivo = tempDir.resolve("productos.csv");

        Files.writeString(archivo, "abc;Teclado;25.99;10");

        ProductoRepository repository = new ProductoRepository(archivo);

        assertThrows(
                IOException.class,
                () -> repository.cargarTodos()
        );
    }


    @Test
    void guardarDeNuevoSustituyeLosProductosAnteriores() throws IOException {

        Path archivo = tempDir.resolve("productos.csv");
        ProductoRepository repository = new ProductoRepository(archivo);

        Producto producto1 = new Producto(
                1, "Teclado", new BigDecimal("25.99"), 10
        );

        Producto producto2 = new Producto(
                2, "Ratón", new BigDecimal("15.50"), 20
        );

        repository.guardarTodos(List.of(producto1, producto2));

        Producto productoNuevo = new Producto(
                3, "Monitor", new BigDecimal("199.99"), 5
        );

        repository.guardarTodos(List.of(productoNuevo));

        List<Producto> productos = repository.cargarTodos();

        assertEquals(1, productos.size());
        assertEquals(3, productos.get(0).getId());
        assertEquals("Monitor", productos.get(0).getNombre());
    }

}
