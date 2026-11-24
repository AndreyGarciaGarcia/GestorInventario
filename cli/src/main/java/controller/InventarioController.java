
package controller;

import repository.ProductoRepository;
import model.Producto;
import service.InventarioService;

import java.io.IOException;
import java.util.List;

/**
 * Capa de entrada (consola) de la aplicación: coordina el
 * {@link InventarioService} con el {@link ProductoRepository} y gestiona
 * la escritura con «rollback» en memoria si la persistencia falla.
 *
 * @author GestorInventario
 * @version 1.0.0
 */
public class InventarioController {

    private final InventarioService inventario;
    private final ProductoRepository repositorio;

    public InventarioController() {
        this(new ProductoRepository());
    }

    public InventarioController(ProductoRepository repositorio) {
        this.inventario = new InventarioService();
        this.repositorio = repositorio;
    }

    public void cargarInventario() throws IOException {

        List<Producto> productos = repositorio.cargarTodos();

        for (Producto producto : productos) {
            inventario.agregarProducto(producto);
        }
    }

    public List<Producto> listarProductos() {
        return inventario.listarProductos();
    }

    public Producto buscarProducto(int id) {
        return inventario.buscarProductoPorId(id);
    }

    public void agregarProducto(Producto producto) {
        List<Producto> copia = copiarInventario();

        inventario.agregarProducto(producto);

        guardarConRollback(copia);
    }

    public void actualizarStock(int id, int nuevoStock) {
        List<Producto> copia = copiarInventario();

        inventario.actualizarStock(id, nuevoStock);

        guardarConRollback(copia);
    }

    public void venderProducto(int id, int cantidad) {
        List<Producto> copia = copiarInventario();

        inventario.venderProducto(id, cantidad);

        guardarConRollback(copia);
    }

    public void eliminarProducto(int id) {
        List<Producto> copia = copiarInventario();

        inventario.eliminarProducto(id);

        guardarConRollback(copia);
    }

    public List<Producto> productosConStockBajo(int limite) {
        return inventario.obtenerProductosConStockBajo(limite);
    }

    public java.math.BigDecimal calcularValorInventario() {
        return inventario.calcularValorInventario();
    }

    private List<Producto> copiarInventario() {

        List<Producto> copia = new java.util.ArrayList<>();

        for (Producto producto : inventario.listarProductos()) {
            copia.add(producto.copiar());
        }

        return copia;
    }

    private void guardarConRollback(List<Producto> copiaAnterior) {

        try {
            repositorio.guardarTodos(inventario.listarProductos());

        } catch (IOException e) {

            inventario.restaurarProductos(copiaAnterior);

            throw new RuntimeException(
                    "No se pudo guardar el inventario. Se revirtieron los cambios.",
                    e
            );
        }
    }
}
