const BASE = import.meta.env.VITE_API_URL ?? '/api'

export class ApiError extends Error {
  constructor(status, mensaje) {
    super(mensaje)
    this.name = 'ApiError'
    this.status = status
  }
}

async function pedir(ruta, opciones = {}) {
  const respuesta = await fetch(`${BASE}${ruta}`, {
    method: opciones.method ?? 'GET',
    headers: { 'Content-Type': 'application/json', ...opciones.headers },
    body: opciones.body,
  })

  if (respuesta.status === 204) return null

  const texto = await respuesta.text()
  const datos = texto ? JSON.parse(texto) : null

  if (!respuesta.ok) {
    const mensaje =
      datos?.mensaje ?? `Error ${respuesta.status}: ${respuesta.statusText}`

    // Detalle por campo de la validación (400)
    const campos = datos?.campos
      ? Object.entries(datos.campos)
          .map(([campo, error]) => `${campo}: ${error}`)
          .join(' · ')
      : ''

    throw new ApiError(
      respuesta.status,
      campos ? `${mensaje} — ${campos}` : mensaje,
    )
  }

  return datos
}

export const api = {
  listar: (page = 0, size = 10) => pedir(`/productos?page=${page}&size=${size}`),

  buscarPorNombre: (texto) =>
    pedir(`/productos/buscar?texto=${encodeURIComponent(texto)}`),

  stockBajo: (limite = 5) => pedir(`/productos/stock-bajo?limite=${limite}`),

  crear: (producto) =>
    pedir('/productos', { method: 'POST', body: JSON.stringify(producto) }),

  actualizar: (id, producto) =>
    pedir(`/productos/${id}`, { method: 'PUT', body: JSON.stringify(producto) }),

  actualizarStock: (id, stock) =>
    pedir(`/productos/${id}/stock?stock=${encodeURIComponent(stock)}`, {
      method: 'PATCH',
    }),

  vender: (id, cantidad) =>
    pedir(`/productos/${id}/ventas`, {
      method: 'POST',
      body: JSON.stringify({ cantidad }),
    }),

  eliminar: (id) => pedir(`/productos/${id}`, { method: 'DELETE' }),

  resumen: (umbral = 5) => pedir(`/inventario/resumen?umbralStockBajo=${umbral}`),
}
