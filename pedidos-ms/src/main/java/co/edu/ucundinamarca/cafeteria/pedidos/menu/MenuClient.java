package co.edu.ucundinamarca.cafeteria.pedidos.menu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente REST hacia menu-ms: es la interconexión entre los dos microservicios.
 * Dentro de Docker se resuelve por el hostname "menu-ms" en la red interna.
 */
@Component
public class MenuClient {

    private final RestClient restClient;

    public MenuClient(RestClient.Builder builder, @Value("${app.menu.url}") String menuUrl) {
        this.restClient = builder.baseUrl(menuUrl).build();
    }

    public ProductoMenu consultarProducto(Long id) {
        try {
            ProductoMenu producto = restClient.get()
                    .uri("/api/menu/productos/{id}", id)
                    .retrieve()
                    .body(ProductoMenu.class);
            if (producto == null) {
                throw new MenuNoDisponibleException(null);
            }
            return producto;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductoNoExisteException(id);
        } catch (RestClientException e) {
            throw new MenuNoDisponibleException(e);
        }
    }

    /** true si el producto existe y no está agotado. */
    public boolean estaDisponible(Long id) {
        return consultarProducto(id).seleccionable();
    }
}
