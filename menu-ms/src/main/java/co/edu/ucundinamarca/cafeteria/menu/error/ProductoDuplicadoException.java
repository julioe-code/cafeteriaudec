package co.edu.ucundinamarca.cafeteria.menu.error;

import co.edu.ucundinamarca.cafeteria.menu.producto.Mensajes;

public class ProductoDuplicadoException extends RuntimeException {

    public ProductoDuplicadoException() {
        super(Mensajes.PRODUCTO_DUPLICADO);
    }
}
