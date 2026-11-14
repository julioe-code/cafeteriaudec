'use strict';

// CF-MENU-0002: el estudiante consulta el menú del día.
(async function cargarMenu() {
    const contenedor = document.getElementById('menu');
    const mensaje = document.getElementById('mensaje');

    try {
        const respuesta = await fetch('/api/menu');
        if (!respuesta.ok) throw new Error('HTTP ' + respuesta.status);
        const datos = await respuesta.json();

        if (datos.mensaje) {
            mensaje.textContent = datos.mensaje;
            mensaje.hidden = false;
        }

        for (const p of datos.productos) {
            const tarjeta = crear('article', null, 'producto' + (p.seleccionable ? '' : ' agotado'));
            tarjeta.append(
                crear('span', CATEGORIAS[p.categoria] || p.categoria, 'categoria'),
                crear('h3', p.nombre),
                crear('p', formatoPesos.format(p.precio), 'precio')
            );
            if (!p.seleccionable) tarjeta.append(crear('span', 'Agotado', 'etiqueta-agotado'));
            contenedor.append(tarjeta);
        }
    } catch (e) {
        mensaje.textContent = 'No fue posible cargar el menú. Intenta de nuevo en unos minutos.';
        mensaje.hidden = false;
    }
})();
