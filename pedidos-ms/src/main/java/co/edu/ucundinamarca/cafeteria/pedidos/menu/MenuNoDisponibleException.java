package co.edu.ucundinamarca.cafeteria.pedidos.menu;

/** menu-ms no respondió o respondió con error (CF-PEDI-0001, escenario 4). */
public class MenuNoDisponibleException extends RuntimeException {

    public static final String MENSAJE = "No fue posible verificar el menú, intenta de nuevo en unos minutos";

    public MenuNoDisponibleException(Throwable causa) {
        super(MENSAJE, causa);
    }
}
