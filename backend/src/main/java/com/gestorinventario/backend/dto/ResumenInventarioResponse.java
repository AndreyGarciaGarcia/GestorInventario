package com.gestorinventario.backend.dto;

import java.math.BigDecimal;

public record ResumenInventarioResponse(
        int totalProductos,
        long totalUnidades,
        BigDecimal valorTotal,
        long stockBajo
) {
}
