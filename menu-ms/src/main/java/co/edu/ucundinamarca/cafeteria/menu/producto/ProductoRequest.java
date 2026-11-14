package co.edu.ucundinamarca.cafeteria.menu.producto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Datos para registrar un producto (CF-MENU-0001).
 * Toda entrada se valida en el servidor, aunque la interfaz también la valide.
 */
public record ProductoRequest(

        @NotBlank(message = Mensajes.CAMPOS_OBLIGATORIOS)
        @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
        @Pattern(regexp = "^[\\p{L}\\p{N} .,()'-]*$",
                message = "El nombre solo puede tener letras, números, espacios y . , ( ) ' -")
        String nombre,

        @NotNull(message = Mensajes.CAMPOS_OBLIGATORIOS)
        Categoria categoria,

        @NotNull(message = Mensajes.CAMPOS_OBLIGATORIOS)
        @Positive(message = Mensajes.PRECIO_INVALIDO)
        @Max(value = 1_000_000, message = "El precio no puede superar $1.000.000")
        Integer precio
) {
}
