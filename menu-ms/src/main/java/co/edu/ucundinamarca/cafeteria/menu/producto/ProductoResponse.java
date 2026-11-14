package co.edu.ucundinamarca.cafeteria.menu.producto;

public record ProductoResponse(
        Long id,
        String nombre,
        Categoria categoria,
        Integer precio,
        EstadoProducto estado,
        boolean seleccionable
) {

    public static ProductoResponse de(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getPrecio(),
                producto.getEstado(),
                producto.esSeleccionable());
    }
}
