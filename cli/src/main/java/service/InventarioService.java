package service;

import model.Producto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lógica de negocio del inventario: alta, consulta, venta y cálculos.
 *
 * <p>Esta clase no persiste nada: la persistencia es responsabilidad de
 * {@code repository.ProductoRepository}.</p>
 *
 * @author GestorInventario
 * @version 1.0.0
 */
public class InventarioService {

    /** LinkedHashMap mantiene el orden de inserción al listar. */
    private final Map<Integer, Producto> productos = new LinkedHashMap<>();

    public void agregarProducto(Producto producto){

        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null");
        }

        int id = producto.getId();

        if (productos.containsKey(id)) {
            throw new IllegalArgumentException("Ya existe un producto con ese ID");
        }

        productos.put(id, producto);

    }

    public Producto buscarProductoPorId(int id){
        Producto producto = productos.get(id);

        if (producto == null) {
            throw new IllegalArgumentException(
                    "No existe ningún producto con ese ID"
            );
        }

        return producto;
    }

    public List<Producto> listarProductos() {
        return new ArrayList<>(productos.values());
    }

    public void eliminarProducto(int id) {

        if (!productos.containsKey(id)) {
            throw new IllegalArgumentException(
                    "No existe ningún producto con ese ID"
            );
        }

        productos.remove(id);

    }

    public void actualizarStock(int id, int nuevoStock) {

        Producto producto = productos.get(id);

        if (producto == null) {
            throw new IllegalArgumentException(
                    "No existe ningún producto con ese ID"
            );
        }

        producto.setStock(nuevoStock);

    }

    public void venderProducto(int id, int cantidad) {


        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }

        Producto producto = productos.get(id);

        if (producto == null) {
            throw new IllegalArgumentException(
                    "No existe ningún producto con ese ID"
            );
        }

        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException(
                    "No hay suficientes existencias"
            );
        }

        producto.setStock(producto.getStock() - cantidad);

    }

    public BigDecimal calcularValorInventario() {

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (Producto producto : productos.values()) {

            BigDecimal valorProducto = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(producto.getStock()));

            valorTotal = valorTotal.add(valorProducto);
        }

        return valorTotal;
    }

    public List<Producto> obtenerProductosConStockBajo(int limite) {

        if (limite < 0) {
            throw new IllegalArgumentException(
                    "El límite no puede ser negativo"
            );
        }

        List<Producto> productosConStockBajo = new ArrayList<>();

        for (Producto producto : productos.values()) {

            if (producto.getStock() <= limite) {
                productosConStockBajo.add(producto);
            }
        }

        return productosConStockBajo;
    }


    public void restaurarProductos(List<Producto> productosAnteriores) {

        productos.clear();

        for (Producto producto : productosAnteriores) {

            productos.put(producto.getId(), producto.copiar());
        }
    }


}
