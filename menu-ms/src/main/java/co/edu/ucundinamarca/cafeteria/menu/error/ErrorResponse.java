package co.edu.ucundinamarca.cafeteria.menu.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * Respuesta de error para el usuario: un mensaje claro y, si aplica,
 * el campo que falló. Nunca incluye trazas ni detalles internos.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(String mensaje, Map<String, String> campos) {

    public static ErrorResponse de(String mensaje) {
        return new ErrorResponse(mensaje, Map.of());
    }
}
