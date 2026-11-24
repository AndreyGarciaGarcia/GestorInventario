package repository;

import model.Producto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Persistencia del inventario en un archivo CSV con separador «;».
 *
 * <p>Los escrituras se hacen de forma atómica: primero se escribe en un
 * archivo temporal y después se mueve al definitivo, para no dejar el
 * archivo corrupto si el proceso falla a mitad.</p>
 *
 * @author GestorInventario
 * @version 1.0.0
 */
public class ProductoRepository {

    private final Path rutaArchivo;

    public ProductoRepository() {
        this(Path.of("data", "productos.csv"));
    }

    public ProductoRepository(Path rutaArchivo) {
        this.rutaArchivo = Objects.requireNonNull(rutaArchivo);
    }

    public void guardarTodos(List<Producto> productos)
            throws IOException {

        Files.createDirectories(rutaArchivo.getParent());

        Path archivoTemporal = Files.createTempFile(
                rutaArchivo.getParent(),
                "productos_",
                ".tmp"
        );

        try {
            try (BufferedWriter writer = Files.newBufferedWriter(
                    archivoTemporal, StandardCharsets.UTF_8)) {

                for (Producto producto : productos) {

                    String linea = producto.getId() + ";"
                            + producto.getNombre() + ";"
                            + producto.getPrecio().toPlainString() + ";"
                            + producto.getStock();

                    writer.write(linea);
                    writer.newLine();
                }
            }

            try {
                Files.move(
                        archivoTemporal,
                        rutaArchivo,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                        java.nio.file.StandardCopyOption.ATOMIC_MOVE
                );

            } catch (java.nio.file.AtomicMoveNotSupportedException e) {

                Files.move(
                        archivoTemporal,
                        rutaArchivo,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
            }

        } finally {
            Files.deleteIfExists(archivoTemporal);
        }
    }

    public List<Producto> cargarTodos() throws IOException {

        List<Producto> productos = new ArrayList<>();

        if (!Files.exists(rutaArchivo)) {
            return productos;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                rutaArchivo, StandardCharsets.UTF_8)) {

            String linea;
            int numeroLinea = 0;

            while ((linea = reader.readLine()) != null) {

                numeroLinea++;

                if (linea.isBlank()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length != 4) {
                    throw new IOException(
                            "Formato incorrecto en la línea " + numeroLinea
                    );
                }

                try {
                    int id = Integer.parseInt(datos[0]);
                    String nombre = datos[1];
                    BigDecimal precio = new BigDecimal(datos[2]);
                    int stock = Integer.parseInt(datos[3]);

                    Producto producto = new Producto(
                            id, nombre, precio, stock
                    );

                    productos.add(producto);

                } catch (IllegalArgumentException e) {
                    throw new IOException(
                            "Datos incorrectos en la línea " + numeroLinea, e
                    );
                }
            }
        }

        return productos;
    }
}
