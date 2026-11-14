package co.edu.ucundinamarca.cafeteria.menu.error;

import co.edu.ucundinamarca.cafeteria.menu.producto.Mensajes;

public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException() {
        super(Mensajes.PRODUCTO_NO_ENCONTRADO);
    }
}
