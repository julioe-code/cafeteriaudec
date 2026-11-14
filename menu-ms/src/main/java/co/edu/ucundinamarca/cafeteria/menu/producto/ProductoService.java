package co.edu.ucundinamarca.cafeteria.menu.producto;

import co.edu.ucundinamarca.cafeteria.menu.error.ProductoDuplicadoException;
import co.edu.ucundinamarca.cafeteria.menu.error.ProductoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository repositorio;

    public ProductoService(ProductoRepository repositorio) {
        this.repositorio = repositorio;
    }

    /** CF-MENU-0001: registrar producto. Queda "Disponible" y aparece en el menú. */
    public ProductoResponse registrar(ProductoRequest datos) {
        String nombre = normalizar(datos.nombre());
        if (repositorio.existsByNombreIgnoreCase(nombre)) {
            throw new ProductoDuplicadoException();
        }
        Producto producto = new Producto(nombre, datos.categoria(), datos.precio());
        return ProductoResponse.de(repositorio.saveAndFlush(producto));
    }

    /** CF-MENU-0002: menú del día, ordenado por categoría y nombre. */
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return repositorio.findAllByOrderByCategoriaAscNombreAsc().stream()
                .map(ProductoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse buscar(Long id) {
        return ProductoResponse.de(obtener(id));
    }

    /** CF-MENU-0003: marcar como agotado o volver a disponible. */
    public ProductoResponse cambiarEstado(Long id, EstadoProducto nuevoEstado) {
        Producto producto = obtener(id);
        producto.cambiarEstado(nuevoEstado);
        return ProductoResponse.de(producto);
    }

    private Producto obtener(Long id) {
        return repositorio.findById(id).orElseThrow(ProductoNoEncontradoException::new);
    }

    /** Quita espacios sobrantes para que "Jugo  de mora " y "Jugo de mora" sean el mismo nombre. */
    private static String normalizar(String nombre) {
        return nombre.trim().replaceAll("\\s+", " ");
    }
}
