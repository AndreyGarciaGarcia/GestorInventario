package com.gestorinventario.backend.dto;

import java.util.List;

public record PaginaRespuesta<T>(
        List<T> contenido,
        int numero,
        int tamaño,
        long totalElem,
        int totalPags
) {
    public boolean tieneAnterior() {
        return numero > 0;
    }

    public boolean tieneSiguiente() {
        return numero < totalPags - 1;
    }
}
