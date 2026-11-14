package co.edu.ucundinamarca.cafeteria.menu;

import co.edu.ucundinamarca.cafeteria.menu.producto.Categoria;
import co.edu.ucundinamarca.cafeteria.menu.producto.EstadoProducto;
import co.edu.ucundinamarca.cafeteria.menu.producto.Producto;
import co.edu.ucundinamarca.cafeteria.menu.producto.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Pruebas de los criterios de aceptación de CF-MENU-0002 y CF-MENU-0003. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MenuControllerTest {

    private static final RequestPostProcessor ENCARGADO = httpBasic("encargado-test", "clave-de-prueba");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductoRepository repositorio;

    @BeforeEach
    void limpiar() {
        repositorio.deleteAll();
    }

    @Test
    @DisplayName("CF-MENU-0002 · Escenario 1: menú con nombre, categoría y precio, ordenado por categoría")
    void menuConProductos() throws Exception {
        repositorio.save(new Producto("Gaseosa", Categoria.BEBIDA, 2500));
        repositorio.save(new Producto("Almuerzo corriente", Categoria.ALMUERZO, 11000));

        mvc.perform(get("/api/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").doesNotExist())
                .andExpect(jsonPath("$.productos", hasSize(2)))
                .andExpect(jsonPath("$.productos[0].categoria").value("ALMUERZO"))
                .andExpect(jsonPath("$.productos[0].precio").value(11000))
                .andExpect(jsonPath("$.productos[1].nombre").value("Gaseosa"));
    }

    @Test
    @DisplayName("CF-MENU-0002 · Escenario 3: menú sin publicar")
    void menuSinPublicar() throws Exception {
        mvc.perform(get("/api/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("La cafetería aún no ha publicado el menú de hoy"))
                .andExpect(jsonPath("$.productos", hasSize(0)));
    }

    @Test
    @DisplayName("CF-MENU-0003 · Escenario 1 y CF-MENU-0002 · Escenario 2: producto agotado no seleccionable")
    void marcarComoAgotado() throws Exception {
        Long id = repositorio.save(new Producto("Arepa con queso", Categoria.SNACK, 3000)).getId();

        mvc.perform(patch("/api/admin/productos/{id}/estado", id).with(ENCARGADO)
                        .contentType(APPLICATION_JSON).content("{\"estado\":\"AGOTADO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("AGOTADO"));

        mvc.perform(get("/api/menu"))
                .andExpect(jsonPath("$.productos[0].estado").value("AGOTADO"))
                .andExpect(jsonPath("$.productos[0].seleccionable").value(false));
    }

    @Test
    @DisplayName("CF-MENU-0003 · Escenario 2: volver a disponible")
    void volverADisponible() throws Exception {
        Producto producto = new Producto("Tinto", Categoria.BEBIDA, 1500);
        producto.cambiarEstado(EstadoProducto.AGOTADO);
        Long id = repositorio.save(producto).getId();

        mvc.perform(patch("/api/admin/productos/{id}/estado", id).with(ENCARGADO)
                        .contentType(APPLICATION_JSON).content("{\"estado\":\"DISPONIBLE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seleccionable").value(true));
    }

    @Test
    @DisplayName("CF-MENU-0003 · Solo el encargado puede cambiar el estado")
    void cambiarEstadoSinCredenciales() throws Exception {
        Long id = repositorio.save(new Producto("Pan", Categoria.SNACK, 800)).getId();

        mvc.perform(patch("/api/admin/productos/{id}/estado", id)
                        .contentType(APPLICATION_JSON).content("{\"estado\":\"AGOTADO\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Consulta de un producto inexistente (usada por pedidos-ms)")
    void productoInexistente() throws Exception {
        mvc.perform(get("/api/menu/productos/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No se encontró el producto"));
    }

    @Test
    @DisplayName("Seguridad: cabeceras de protección en las respuestas")
    void cabecerasDeSeguridad() throws Exception {
        mvc.perform(get("/api/menu"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().exists("Content-Security-Policy"));
    }
}
