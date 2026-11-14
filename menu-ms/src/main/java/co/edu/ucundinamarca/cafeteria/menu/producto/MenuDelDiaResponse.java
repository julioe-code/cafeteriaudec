package co.edu.ucundinamarca.cafeteria.menu.producto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Respuesta del menú del día (CF-MENU-0002). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MenuDelDiaResponse(String mensaje, List<ProductoResponse> productos) {

    public static MenuDelDiaResponse de(List<ProductoResponse> productos) {
        String mensaje = productos.isEmpty() ? Mensajes.MENU_SIN_PUBLICAR : null;
        return new MenuDelDiaResponse(mensaje, productos);
    }
}
