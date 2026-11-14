package co.edu.ucundinamarca.cafeteria.pedidos.menu;

/** Producto tal como lo entrega la API de menu-ms (GET /api/menu/productos/{id}). */
public record ProductoMenu(
        Long id,
        String nombre,
        String categoria,
        Integer precio,
        String estado,
        boolean seleccionable
) {
}
