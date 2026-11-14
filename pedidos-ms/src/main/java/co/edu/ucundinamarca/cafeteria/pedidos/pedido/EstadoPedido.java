package co.edu.ucundinamarca.cafeteria.pedidos.pedido;

/**
 * Ciclo de vida de un pedido (backlog: CF-PEDI-0001 a CF-PEDI-0008).
 * RECIBIDO -> LISTO -> ENTREGADO, o RECIBIDO -> CANCELADO.
 */
public enum EstadoPedido {
    RECIBIDO,
    LISTO,
    ENTREGADO,
    CANCELADO;

    public boolean puedeCambiarA(EstadoPedido destino) {
        return switch (this) {
            case RECIBIDO -> destino == LISTO || destino == CANCELADO;
            case LISTO -> destino == ENTREGADO;
            case ENTREGADO, CANCELADO -> false;
        };
    }
}
