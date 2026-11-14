package co.edu.ucundinamarca.cafeteria.menu;

import co.edu.ucundinamarca.cafeteria.menu.producto.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Pruebas de los criterios de aceptación de CF-MENU-0001 y CF-SEGU-0001. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductoAdminControllerTest {

    private static final RequestPostProcessor ENCARGADO = httpBasic("encargado-test", "clave-de-prueba");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductoRepository repositorio;

    @BeforeEach
    void limpiar() {
        repositorio.deleteAll();
    }

    private String producto(String nombre, String categoria, String precio) {
        return "{\"nombre\":" + nombre + ",\"categoria\":" + categoria + ",\"precio\":" + precio + "}";
    }

    @Test
    @DisplayName("CF-MENU-0001 · Escenario 1: registro exitoso, queda Disponible y aparece en el menú")
    void registroExitoso() throws Exception {
        mvc.perform(post("/api/admin/productos").with(ENCARGADO).contentType(APPLICATION_JSON)
                        .content(producto("\"Almuerzo ejecutivo\"", "\"ALMUERZO\"", "12000")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"))
                .andExpect(jsonPath("$.seleccionable").value(true));

        mvc.perform(get("/api/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos[0].nombre").value("Almuerzo ejecutivo"))
                .andExpect(jsonPath("$.productos[0].precio").value(12000));
    }

    @Test
    @DisplayName("CF-MENU-0001 · Escenario 2: campos obligatorios vacíos, señala los campos que faltan")
    void camposObligatoriosVacios() throws Exception {
        mvc.perform(post("/api/admin/productos").with(ENCARGADO).contentType(APPLICATION_JSON)
                        .content(producto("\"\"", "null", "2500")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Complete los campos obligatorios"))
                .andExpect(jsonPath("$.campos.nombre").exists())
                .andExpect(jsonPath("$.campos.categoria").exists());
    }

    @ParameterizedTest(name = "precio = {0}")
    @ValueSource(strings = {"0", "-500", "\"abc\"", "12.5"})
    @DisplayName("CF-MENU-0001 · Escenario 3: precio 0, negativo o no numérico")
    void precioInvalido(String precio) throws Exception {
        mvc.perform(post("/api/admin/productos").with(ENCARGADO).contentType(APPLICATION_JSON)
                        .content(producto("\"Empanada\"", "\"SNACK\"", precio)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El precio debe ser un número mayor a 0"));
    }

    @Test
    @DisplayName("CF-MENU-0001 · Escenario 4: producto duplicado (sin importar mayúsculas ni espacios)")
    void productoDuplicado() throws Exception {
        mvc.perform(post("/api/admin/productos").with(ENCARGADO).contentType(APPLICATION_JSON)
                        .content(producto("\"Jugo de mora\"", "\"BEBIDA\"", "3000")))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/admin/productos").with(ENCARGADO).contentType(APPLICATION_JSON)
                        .content(producto("\"  jugo  DE mora \"", "\"BEBIDA\"", "3500")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un producto con ese nombre"));
    }

    @Test
    @DisplayName("Seguridad: el nombre rechaza etiquetas HTML/JavaScript")
    void nombreConCodigoMalicioso() throws Exception {
        mvc.perform(post("/api/admin/productos").with(ENCARGADO).contentType(APPLICATION_JSON)
                        .content(producto("\"<script>alert(1)</script>\"", "\"SNACK\"", "1000")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nombre").exists());
    }

    @Test
    @DisplayName("CF-SEGU-0001 · Sin credenciales no se puede registrar productos")
    void sinCredenciales() throws Exception {
        mvc.perform(post("/api/admin/productos").contentType(APPLICATION_JSON)
                        .content(producto("\"Tinto\"", "\"BEBIDA\"", "1500")))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist("WWW-Authenticate"));
    }

    @Test
    @DisplayName("CF-SEGU-0001 · Contraseña incorrecta: mensaje genérico")
    void contrasenaIncorrecta() throws Exception {
        mvc.perform(get("/api/admin/productos").with(httpBasic("encargado-test", "otra-clave")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Usuario o contraseña incorrectos"));
    }

    @Test
    @DisplayName("Seguridad: las rutas no declaradas se niegan por defecto")
    void rutaNoDeclarada() throws Exception {
        mvc.perform(get("/actuator/env").with(ENCARGADO))
                .andExpect(status().isForbidden());
    }
}
