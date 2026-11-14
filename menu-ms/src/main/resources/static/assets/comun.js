'use strict';

// Utilidades compartidas. Todo el texto que viene del servidor se inserta con
// textContent (nunca innerHTML) para evitar inyección de código (XSS).

const CATEGORIAS = { ALMUERZO: 'Almuerzo', BEBIDA: 'Bebida', SNACK: 'Snack' };

const formatoPesos = new Intl.NumberFormat('es-CO', {
    style: 'currency', currency: 'COP', maximumFractionDigits: 0
});

function crear(etiqueta, texto, clase) {
    const el = document.createElement(etiqueta);
    if (texto !== undefined && texto !== null) el.textContent = texto;
    if (clase) el.className = clase;
    return el;
}
