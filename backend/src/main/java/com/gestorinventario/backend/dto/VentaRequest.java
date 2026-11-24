package com.gestorinventario.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VentaRequest(

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor que cero")
        Integer cantidad
) {
}
