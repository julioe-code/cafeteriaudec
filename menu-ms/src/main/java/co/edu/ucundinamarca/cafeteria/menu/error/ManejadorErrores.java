package co.edu.ucundinamarca.cafeteria.menu.error;

import co.edu.ucundinamarca.cafeteria.menu.producto.Mensajes;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduce los errores a los mensajes definidos en los criterios de aceptación.
 * Los errores inesperados se registran en el log y al usuario solo se le
 * muestra un mensaje genérico (no se filtran detalles internos).
 */
@RestControllerAdvice
public class ManejadorErrores extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ManejadorErrores.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        String mensaje = campos.containsValue(Mensajes.CAMPOS_OBLIGATORIOS)
                ? Mensajes.CAMPOS_OBLIGATORIOS
                : campos.values().stream().findFirst().orElse(Mensajes.CAMPOS_OBLIGATORIOS);
        return ResponseEntity.badRequest().body(new ErrorResponse(mensaje, campos));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        if (ex.getCause() instanceof MismatchedInputException mie) {
            for (JsonMappingException.Reference ref : mie.getPath()) {
                if ("precio".equals(ref.getFieldName())) {
                    return ResponseEntity.badRequest().body(
                            new ErrorResponse(Mensajes.PRECIO_INVALIDO, Map.of("precio", Mensajes.PRECIO_INVALIDO)));
                }
                if ("categoria".equals(ref.getFieldName())) {
                    return ResponseEntity.badRequest().body(
                            new ErrorResponse(Mensajes.CATEGORIA_INVALIDA, Map.of("categoria", Mensajes.CATEGORIA_INVALIDA)));
                }
            }
        }
        return ResponseEntity.badRequest().body(ErrorResponse.de("La solicitud no tiene un formato válido"));
    }

    @ExceptionHandler(ProductoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> duplicado(ProductoDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.de(ex.getMessage()));
    }

    /** Si dos registros simultáneos pasan la validación, la restricción UNIQUE de la base de datos los detiene. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.de(Mensajes.PRODUCTO_DUPLICADO));
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(ProductoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.de(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> inesperado(Exception ex) {
        LOG.error("Error inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.de("Ocurrió un error inesperado. Intente de nuevo más tarde."));
    }
}
