package com.gestorinventario.backend.controller;

import com.gestorinventario.backend.dto.ResumenInventarioResponse;
import com.gestorinventario.backend.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Tag(name = "Inventario", description = "Resumen del inventario y valor total")
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService servicio;

    public InventarioController(InventarioService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Valor total del inventario", description = "Suma de precio × stock de todos los productos.")
    @ApiResponse(responseCode = "200", description = "Valor monetario total", content = @Content(schema = @Schema(implementation = BigDecimal.class)))
    @GetMapping("/valor-total")
    public BigDecimal valorTotal() {
        return servicio.valorTotal();
    }

    @Operation(summary = "Resumen del inventario", description = "Cifras globales: productos, unidades, valor total y conteo de stock bajo.")
    @ApiResponse(responseCode = "200", description = "Resumen del inventario", content = @Content(schema = @Schema(implementation = ResumenInventarioResponse.class)))
    @GetMapping("/resumen")
    public ResumenInventarioResponse resumen(
            @Parameter(description = "Umbral para considerar stock bajo", example = "5") @RequestParam(defaultValue = "5") int umbralStockBajo
    ) {
        return servicio.resumen(umbralStockBajo);
    }
}
