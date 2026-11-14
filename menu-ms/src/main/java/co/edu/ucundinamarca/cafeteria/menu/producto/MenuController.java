package co.edu.ucundinamarca.cafeteria.menu.producto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consultas públicas del menú (solo lectura).
 * La usan los estudiantes y el microservicio de Pedidos.
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final ProductoService servicio;

    public MenuController(ProductoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public MenuDelDiaResponse menuDelDia() {
        return MenuDelDiaResponse.de(servicio.listar());
    }

    /** Usado por pedidos-ms para verificar disponibilidad al confirmar un pedido (CF-PEDI-0001). */
    @GetMapping("/productos/{id}")
    public ProductoResponse producto(@PathVariable Long id) {
        return servicio.buscar(id);
    }
}
