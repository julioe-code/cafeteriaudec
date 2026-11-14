package co.edu.ucundinamarca.cafeteria.pedidos.menu;

public class ProductoNoExisteException extends RuntimeException {

    public ProductoNoExisteException(Long id) {
        super("El producto " + id + " no existe en el menú");
    }
}
