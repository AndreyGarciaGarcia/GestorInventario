package com.gestorinventario.backend.exception;

public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
