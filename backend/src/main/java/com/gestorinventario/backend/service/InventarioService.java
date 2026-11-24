package com.gestorinventario.backend.service;

import com.gestorinventario.backend.dto.ProductoRequest;
import com.gestorinventario.backend.dto.ProductoResponse;
import com.gestorinventario.backend.dto.ResumenInventarioResponse;
import com.gestorinventario.backend.exception.ProductoNoEncontradoException;
import com.gestorinventario.backend.exception.ReglaNegocioException;
import com.gestorinventario.backend.model.Producto;
import com.gestorinventario.backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional
public class InventarioService {

    private static final String NO_EXISTE = "No existe ningún producto con ese ID";

    private final ProductoRepository repositorio;

    public InventarioService(ProductoRepository repositorio) {
        this.repositorio = repositorio;
    }

    // ------------------------------------------------------------------
    // Consultas (solo lectura)
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return repositorio.findAll().stream()
                .map(ProductoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductoResponse> listarPagina(Pageable pageable) {
        return repositorio.findAll(pageable).map(ProductoResponse::desde);
    }

    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {
        return ProductoResponse.desde(obtenerEntidad(id));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorNombre(String texto) {
        return repositorio.findByNombreContainingIgnoreCase(texto).stream()
                .map(ProductoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> stockBajo(int limite) {

        if (limite < 0) {
            throw new IllegalArgumentException("El límite no puede ser negativo");
        }

        return repositorio.findByStockLessThanEqual(limite).stream()
                .map(ProductoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal valorTotal() {
        return repositorio.calcularValorTotalInventario();
    }

    @Transactional(readOnly = true)
    public ResumenInventarioResponse resumen(int umbralStockBajo) {

        List<Producto> productos = repositorio.findAll();

        long unidades = productos.stream()
                .mapToLong(Producto::getStock)
                .sum();

        long stockBajo = productos.stream()
                .filter(p -> p.getStock() <= umbralStockBajo)
                .count();

        return new ResumenInventarioResponse(
                productos.size(),
                unidades,
                repositorio.calcularValorTotalInventario(),
                stockBajo
        );
    }

    // ------------------------------------------------------------------
    // Escrituras
    // ------------------------------------------------------------------

    public ProductoResponse crear(ProductoRequest request) {

        boolean duplicado = repositorio
                .findByNombreContainingIgnoreCase(request.nombre().trim())
                .stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(request.nombre().trim()));

        if (duplicado) {
            throw new ReglaNegocioException(
                    "Ya existe un producto con el nombre «" + request.nombre().trim() + "»"
            );
        }

        Producto producto = new Producto(
                request.nombre().trim(),
                request.precio(),
                request.stock(),
                normalizar(request.categoria())
        );

        return ProductoResponse.desde(repositorio.save(producto));
    }

    public ProductoResponse actualizar(Long id, ProductoRequest request) {

        Producto producto = obtenerEntidad(id);

        producto.setNombre(request.nombre().trim());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setCategoria(normalizar(request.categoria()));

        return ProductoResponse.desde(repositorio.save(producto));
    }

    public ProductoResponse actualizarStock(Long id, int nuevoStock) {

        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        Producto producto = obtenerEntidad(id);
        producto.setStock(nuevoStock);

        return ProductoResponse.desde(repositorio.save(producto));
    }

    public ProductoResponse vender(Long id, int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        Producto producto = obtenerEntidad(id);

        if (producto.getStock() < cantidad) {
            throw new ReglaNegocioException(
                    "No hay suficientes existencias: quedan " + producto.getStock()
                            + " unidades y se pidieron " + cantidad
            );
        }

        producto.setStock(producto.getStock() - cantidad);

        return ProductoResponse.desde(repositorio.save(producto));
    }

    public void eliminar(Long id) {
        repositorio.delete(obtenerEntidad(id));
    }

    // ------------------------------------------------------------------
    // Privados
    // ------------------------------------------------------------------

    private Producto obtenerEntidad(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    private String normalizar(String categoria) {

        if (categoria == null || categoria.isBlank()) {
            return null;
        }

        return categoria.trim();
    }
}
