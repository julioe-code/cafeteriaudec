package co.edu.ucundinamarca.cafeteria.menu.producto;

import jakarta.validation.constraints.NotNull;

/** Cambio de estado de un producto (CF-MENU-0003). */
public record EstadoRequest(
        @NotNull(message = "Indique el estado: DISPONIBLE o AGOTADO")
        EstadoProducto estado
) {
}
