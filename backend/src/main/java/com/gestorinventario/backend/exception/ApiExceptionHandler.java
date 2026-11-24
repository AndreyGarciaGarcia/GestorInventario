package com.gestorinventario.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    /** Producto inexistente → 404. */
    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(ProductoNoEncontradoException e) {
        return responder(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    /** Regla de negocio incumplida → 409. */
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> reglaNegocio(ReglaNegocioException e) {
        return responder(HttpStatus.CONFLICT, e.getMessage(), null);
    }

    /** Argumentos inválidos de negocio (stock negativo, límite…) → 400. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> argumentoInvalido(IllegalArgumentException e) {
        return responder(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    /** Errores de validación de los DTOs → 400 con el detalle por campo. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException e) {

        Map<String, String> campos = new LinkedHashMap<>();

        e.getBindingResult().getFieldErrors()
                .forEach(error -> campos.put(error.getField(), error.getDefaultMessage()));

        String mensaje = "La petición no es válida";

        return responder(HttpStatus.BAD_REQUEST, mensaje, campos);
    }

    /** JSON mal formado o de tipo incorrecto → 400. */
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> malformada(Exception e) {
        return responder(HttpStatus.BAD_REQUEST, "Cuerpo o parámetros de la petición no válidos", null);
    }

    /** Cualquier otro fallo → 500 sin filtrar detalles internos. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> inesperado(Exception e) {
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", null);
    }

    private ResponseEntity<Map<String, Object>> responder(
            HttpStatus estado, String mensaje, Map<String, String> campos
    ) {

        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now().toString());
        cuerpo.put("status", estado.value());
        cuerpo.put("error", estado.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);

        if (campos != null && !campos.isEmpty()) {
            cuerpo.put("campos", campos);
        }

        return ResponseEntity.status(estado).body(cuerpo);
    }
}
