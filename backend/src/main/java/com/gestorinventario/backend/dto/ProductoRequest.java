package com.gestorinventario.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductoRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
        String nombre,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0", inclusive = false, message = "El precio debe ser mayor que cero")
        BigDecimal precio,

        @NotNull(message = "El stock es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo")
        Integer stock,

        @Size(max = 60, message = "La categoría no puede superar los 60 caracteres")
        String categoria
) {
}
