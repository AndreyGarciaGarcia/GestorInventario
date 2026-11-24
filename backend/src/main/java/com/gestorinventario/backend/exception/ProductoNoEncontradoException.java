package com.gestorinventario.backend.exception;

public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(Long id) {
        super("No existe ningún producto con id " + id);
    }
}
