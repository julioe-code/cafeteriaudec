package co.edu.ucundinamarca.cafeteria.menu.producto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(name = "productos",
        uniqueConstraints = @UniqueConstraint(name = "uk_producto_nombre", columnNames = "nombre"))
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Categoria categoria;

    /** Precio en pesos colombianos (sin decimales). */
    @Column(nullable = false)
    private Integer precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoProducto estado;

    @Column(nullable = false)
    private Instant actualizadoEn;

    protected Producto() {
        // requerido por JPA
    }

    public Producto(String nombre, Categoria categoria, Integer precio) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.estado = EstadoProducto.DISPONIBLE;
        this.actualizadoEn = Instant.now();
    }

    public void cambiarEstado(EstadoProducto nuevoEstado) {
        this.estado = nuevoEstado;
        this.actualizadoEn = Instant.now();
    }

    public boolean esSeleccionable() {
        return estado == EstadoProducto.DISPONIBLE;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Integer getPrecio() {
        return precio;
    }

    public EstadoProducto getEstado() {
        return estado;
    }

    public Instant getActualizadoEn() {
        return actualizadoEn;
    }
}
