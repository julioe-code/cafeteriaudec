package co.edu.ucundinamarca.cafeteria.menu.producto;

/** Mensajes al usuario, tomados de los criterios de aceptación de las historias. */
public final class Mensajes {

    public static final String CAMPOS_OBLIGATORIOS = "Complete los campos obligatorios";
    public static final String PRECIO_INVALIDO = "El precio debe ser un número mayor a 0";
    public static final String CATEGORIA_INVALIDA = "La categoría debe ser ALMUERZO, BEBIDA o SNACK";
    public static final String PRODUCTO_DUPLICADO = "Ya existe un producto con ese nombre";
    public static final String PRODUCTO_NO_ENCONTRADO = "No se encontró el producto";
    public static final String MENU_SIN_PUBLICAR = "La cafetería aún no ha publicado el menú de hoy";

    private Mensajes() {
    }
}
