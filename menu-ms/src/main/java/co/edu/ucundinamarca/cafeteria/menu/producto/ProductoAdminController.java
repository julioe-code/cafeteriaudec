package co.edu.ucundinamarca.cafeteria.menu.producto;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Operaciones del encargado de la cafetería.
 * Todas las rutas /api/admin/** exigen autenticación con rol ENCARGADO (CF-SEGU-0001).
 */
@RestController
@RequestMapping("/api/admin/productos")
public class ProductoAdminController {

    private final ProductoService servicio;

    public ProductoAdminController(ProductoService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> registrar(@Valid @RequestBody ProductoRequest datos) {
        ProductoResponse creado = servicio.registrar(datos);
        return ResponseEntity.created(URI.create("/api/menu/productos/" + creado.id())).body(creado);
    }

    @GetMapping
    public List<ProductoResponse> listar() {
        return servicio.listar();
    }

    @PatchMapping("/{id}/estado")
    public ProductoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest datos) {
        return servicio.cambiarEstado(id, datos.estado());
    }
}
