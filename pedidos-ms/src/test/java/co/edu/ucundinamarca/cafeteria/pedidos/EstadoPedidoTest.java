package co.edu.ucundinamarca.cafeteria.pedidos;

import co.edu.ucundinamarca.cafeteria.pedidos.pedido.EstadoPedido;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoPedidoTest {

    @Test
    @DisplayName("Solo se puede cancelar un pedido en estado Recibido (CF-PEDI-0008)")
    void cancelacion() {
        assertThat(EstadoPedido.RECIBIDO.puedeCambiarA(EstadoPedido.CANCELADO)).isTrue();
        assertThat(EstadoPedido.LISTO.puedeCambiarA(EstadoPedido.CANCELADO)).isFalse();
        assertThat(EstadoPedido.ENTREGADO.puedeCambiarA(EstadoPedido.CANCELADO)).isFalse();
    }

    @Test
    @DisplayName("No se entrega un pedido que no esté Listo (CF-PEDI-0007)")
    void entrega() {
        assertThat(EstadoPedido.RECIBIDO.puedeCambiarA(EstadoPedido.ENTREGADO)).isFalse();
        assertThat(EstadoPedido.LISTO.puedeCambiarA(EstadoPedido.ENTREGADO)).isTrue();
    }

    @Test
    @DisplayName("Un pedido cancelado no se puede marcar como Listo (CF-PEDI-0005)")
    void canceladoNoPasaAListo() {
        assertThat(EstadoPedido.CANCELADO.puedeCambiarA(EstadoPedido.LISTO)).isFalse();
    }
}
