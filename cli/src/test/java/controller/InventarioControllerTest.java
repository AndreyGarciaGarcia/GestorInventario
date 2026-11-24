package controller;

import repository.ProductoRepository;
import model.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class InventarioControllerTest {

    @TempDir
    Path tempDir;

    @Test
    void revierteLosCambiosCuandoFallaElGuardado() throws IOException {

        Path bloqueo = tempDir.resolve("bloqueo");
        Files.writeString(bloqueo, "No soy una carpeta");

        ProductoRepository repositorio =
                new ProductoRepository(bloqueo.resolve("productos.csv"));

        InventarioController controlador =
                new InventarioController(repositorio);

        Producto producto = new Producto(
                1,
                "Teclado",
                new BigDecimal("25.50"),
                10
        );

        assertThrows(RuntimeException.class, () ->
                controlador.agregarProducto(producto)
        );

        assertTrue(controlador.listarProductos().isEmpty());
    }
}
