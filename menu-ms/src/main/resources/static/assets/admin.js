'use strict';

// Panel del encargado: CF-SEGU-0001 (login), CF-MENU-0001 (registrar) y CF-MENU-0003 (agotado/disponible).
// La credencial se guarda solo en memoria: al recargar la página hay que volver a iniciar sesión.
let credencial = null;

const formLogin = document.getElementById('form-login');
const panel = document.getElementById('panel');
const formProducto = document.getElementById('form-producto');
const tabla = document.getElementById('tabla-productos');
const errorTabla = document.getElementById('error-tabla');

async function api(url, opciones = {}) {
    const respuesta = await fetch(url, {
        ...opciones,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic ' + credencial,
            ...(opciones.headers || {})
        }
    });
    let cuerpo = null;
    try { cuerpo = await respuesta.json(); } catch (e) { /* sin cuerpo */ }
    if (respuesta.status === 401) cerrarSesion();
    return { ok: respuesta.ok, status: respuesta.status, cuerpo };
}

function codificar(usuario, contrasena) {
    // btoa no admite tildes/ñ directamente: se pasa primero a UTF-8
    const bytes = new TextEncoder().encode(usuario + ':' + contrasena);
    return btoa(String.fromCharCode(...bytes));
}

formLogin.addEventListener('submit', async (ev) => {
    ev.preventDefault();
    const datos = new FormData(formLogin);
    credencial = codificar(datos.get('usuario'), datos.get('contrasena'));
    const r = await api('/api/admin/productos');
    const error = formLogin.querySelector('[data-error]');
    if (r.ok) {
        error.textContent = '';
        formLogin.reset();
        formLogin.hidden = true;
        panel.hidden = false;
        pintarTabla(r.cuerpo);
    } else {
        credencial = null;
        error.textContent = (r.cuerpo && r.cuerpo.mensaje) || 'No fue posible iniciar sesión';
    }
});

function cerrarSesion() {
    credencial = null;
    panel.hidden = true;
    formLogin.hidden = false;
    tabla.replaceChildren();
}

document.getElementById('salir').addEventListener('click', cerrarSesion);

formProducto.addEventListener('submit', async (ev) => {
    ev.preventDefault();
    const datos = new FormData(formProducto);
    const error = formProducto.querySelector('[data-error]');
    const ok = formProducto.querySelector('[data-ok]');
    error.textContent = '';
    ok.textContent = '';

    const precioTexto = String(datos.get('precio') || '').trim();
    const cuerpo = {
        nombre: datos.get('nombre'),
        categoria: datos.get('categoria') || null,
        // Si no es un número entero se envía tal cual y el servidor responde "precio inválido"
        precio: precioTexto === '' ? null : (/^-?\d+$/.test(precioTexto) ? Number(precioTexto) : precioTexto)
    };

    const r = await api('/api/admin/productos', { method: 'POST', body: JSON.stringify(cuerpo) });
    if (r.ok) {
        ok.textContent = 'Producto guardado: ' + r.cuerpo.nombre;
        formProducto.reset();
        recargarTabla();
    } else if (r.cuerpo) {
        const campos = r.cuerpo.campos ? ' (' + Object.keys(r.cuerpo.campos).join(', ') + ')' : '';
        error.textContent = r.cuerpo.mensaje + campos;
    }
});

async function recargarTabla() {
    const r = await api('/api/admin/productos');
    if (r.ok) pintarTabla(r.cuerpo);
}

function pintarTabla(productos) {
    tabla.replaceChildren();
    for (const p of productos) {
        const fila = document.createElement('tr');
        const agotado = p.estado === 'AGOTADO';
        const boton = crear('button', agotado ? 'Marcar como disponible' : 'Marcar como agotado',
            agotado ? 'secundario' : '');
        boton.addEventListener('click', () => cambiarEstado(p.id, agotado ? 'DISPONIBLE' : 'AGOTADO'));

        const celdaBoton = document.createElement('td');
        celdaBoton.append(boton);
        fila.append(
            crear('td', p.nombre),
            crear('td', CATEGORIAS[p.categoria] || p.categoria),
            crear('td', formatoPesos.format(p.precio)),
            crear('td', agotado ? 'Agotado' : 'Disponible'),
            celdaBoton
        );
        tabla.append(fila);
    }
}

async function cambiarEstado(id, estado) {
    errorTabla.textContent = '';
    const r = await api('/api/admin/productos/' + encodeURIComponent(id) + '/estado',
        { method: 'PATCH', body: JSON.stringify({ estado }) });
    if (r.ok) {
        recargarTabla();
    } else if (r.cuerpo) {
        errorTabla.textContent = r.cuerpo.mensaje;
    }
}
