package com.gestorinventario.backend.controller;

import com.gestorinventario.backend.dto.PaginaRespuesta;
import com.gestorinventario.backend.dto.ProductoRequest;
import com.gestorinventario.backend.dto.ProductoResponse;
import com.gestorinventario.backend.dto.VentaRequest;
import com.gestorinventario.backend.service.InventarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Productos", description = "Operaciones CRUD, ventas y consultas de stock de productos")
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final InventarioService servicio;

    public ProductoController(InventarioService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Listar productos (paginado)", description = "Devuelve una página del inventario ordenado por nombre.")
    @ApiResponse(responseCode = "200", description = "Página de productos", content = @Content(schema = @Schema(implementation = PaginaRespuesta.class)))
    @GetMapping
    public PaginaRespuesta<ProductoResponse> listar(
            @Parameter(description = "Número de página (base 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de la página", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductoResponse> pagina = servicio.listarPagina(PageRequest.of(page, size));
        return new PaginaRespuesta<>(
                pagina.getContent(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages()
        );
    }

    @Operation(summary = "Buscar producto por ID", description = "Devuelve un producto dado su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ProductoResponse buscar(
            @Parameter(description = "Identificador del producto", required = true) @PathVariable Long id
    ) {
        return servicio.buscarPorId(id);
    }

    @Operation(summary = "Buscar productos por nombre", description = "Búsqueda parcial ignorando mayúsculas y minúsculas.")
    @ApiResponse(responseCode = "200", description = "Coincidencias encontradas")
    @GetMapping("/buscar")
    public List<ProductoResponse> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre", required = true, example = "teclado") @RequestParam String texto
    ) {
        return servicio.buscarPorNombre(texto);
    }

    @Operation(summary = "Productos con stock bajo", description = "Devuelve productos cuyo stock es menor o igual al límite indicado.")
    @ApiResponse(responseCode = "200", description = "Lista de productos con stock bajo")
    @GetMapping("/stock-bajo")
    public List<ProductoResponse> stockBajo(
            @Parameter(description = "Límite máximo de stock", example = "5") @RequestParam(defaultValue = "5") int limite
    ) {
        return servicio.stockBajo(limite);
    }

    @Operation(summary = "Crear un producto", description = "Da de alta un producto nuevo en el inventario.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Producto duplicado (nombre ya existente)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest request) {
        return servicio.crear(request);
    }

    @Operation(summary = "Actualizar un producto", description = "Actualiza todos los campos de un producto existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "409", description = "Producto duplicado (nombre ya existente)")
    })
    @PutMapping("/{id}")
    public ProductoResponse actualizar(
            @Parameter(description = "Identificador del producto", required = true) @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request
    ) {
        return servicio.actualizar(id, request);
    }

    @Operation(summary = "Actualizar el stock de un producto", description = "Reconteo de existencias (reemplaza el valor actual).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock actualizado"),
            @ApiResponse(responseCode = "400", description = "Stock negativo no permitido"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PatchMapping("/{id}/stock")
    public ProductoResponse actualizarStock(
            @Parameter(description = "Identificador del producto", required = true) @PathVariable Long id,
            @Parameter(description = "Nuevo valor de stock", example = "42") @RequestParam int stock
    ) {
        return servicio.actualizarStock(id, stock);
    }

    @Operation(summary = "Registrar una venta", description = "Descuenta unidades del stock de un producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta registrada y stock descontado"),
            @ApiResponse(responseCode = "400", description = "Cantidad inválida"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "409", description = "Stock insuficiente")
    })
    @PostMapping("/{id}/ventas")
    public ProductoResponse vender(
            @Parameter(description = "Identificador del producto", required = true) @PathVariable Long id,
            @Valid @RequestBody VentaRequest request
    ) {
        return servicio.vender(id, request.cantidad());
    }

    @Operation(summary = "Eliminar un producto", description = "Da de baja un producto del inventario.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
