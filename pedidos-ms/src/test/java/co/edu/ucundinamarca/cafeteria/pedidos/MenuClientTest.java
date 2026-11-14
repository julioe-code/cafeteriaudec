package co.edu.ucundinamarca.cafeteria.pedidos;

import co.edu.ucundinamarca.cafeteria.pedidos.menu.MenuClient;
import co.edu.ucundinamarca.cafeteria.pedidos.menu.MenuNoDisponibleException;
import co.edu.ucundinamarca.cafeteria.pedidos.menu.ProductoNoExisteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/** Interconexión pedidos-ms -> menu-ms, simulando las respuestas de menu-ms. */
@RestClientTest(MenuClient.class)
@TestPropertySource(properties = "app.menu.url=http://menu-ms:8081")
class MenuClientTest {

    @Autowired
    private MenuClient cliente;

    @Autowired
    private MockRestServiceServer servidor;

    @Test
    @DisplayName("Producto disponible")
    void productoDisponible() {
        servidor.expect(requestTo("http://menu-ms:8081/api/menu/productos/1"))
                .andRespond(withSuccess("""
                        {"id":1,"nombre":"Almuerzo corriente","categoria":"ALMUERZO",
                         "precio":11000,"estado":"DISPONIBLE","seleccionable":true}
                        """, APPLICATION_JSON));

        assertThat(cliente.estaDisponible(1L)).isTrue();
    }

    @Test
    @DisplayName("CF-PEDI-0001 · Escenario 3: producto agotado")
    void productoAgotado() {
        servidor.expect(requestTo("http://menu-ms:8081/api/menu/productos/2"))
                .andRespond(withSuccess("""
                        {"id":2,"nombre":"Jugo de mora","categoria":"BEBIDA",
                         "precio":3000,"estado":"AGOTADO","seleccionable":false}
                        """, APPLICATION_JSON));

        assertThat(cliente.estaDisponible(2L)).isFalse();
    }

    @Test
    @DisplayName("Producto inexistente")
    void productoInexistente() {
        servidor.expect(requestTo("http://menu-ms:8081/api/menu/productos/99"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> cliente.consultarProducto(99L))
                .isInstanceOf(ProductoNoExisteException.class);
    }

    @Test
    @DisplayName("CF-PEDI-0001 · Escenario 4: el microservicio Menú no responde")
    void menuNoResponde() {
        servidor.expect(requestTo("http://menu-ms:8081/api/menu/productos/1"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> cliente.consultarProducto(1L))
                .isInstanceOf(MenuNoDisponibleException.class)
                .hasMessage(MenuNoDisponibleException.MENSAJE);
    }
}
