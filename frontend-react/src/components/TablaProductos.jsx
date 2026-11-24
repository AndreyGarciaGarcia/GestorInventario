import './TablaProductos.css'

const euros = (valor) =>
  Number(valor).toLocaleString('es-ES', {
    style: 'currency',
    currency: 'EUR',
  })

export default function TablaProductos({
  productos,
  cargando,
  umbral,
  onEditar,
  onVender,
  onEliminar,
  onRecontar,
}) {
  if (cargando) {
    return <p className="estado">Cargando inventario…</p>
  }

  if (productos.length === 0) {
    return <p className="estado">No hay productos que mostrar.</p>
  }

  return (
    <div className="tabla-contenedor">
      <table className="tabla">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th className="derecha">Precio</th>
            <th className="derecha">Stock</th>
            <th className="derecha">Valor</th>
            <th>Categoría</th>
            <th className="acciones">Acciones</th>
          </tr>
        </thead>

        <tbody>
          {productos.map((p) => {
            const bajo = p.stock <= umbral

            return (
              <tr key={p.id} className={bajo ? 'fila-bajo' : ''}>
                <td className="id">{p.id}</td>
                <td>
                  <strong>{p.nombre}</strong>
                  {bajo && <span className="chip chip-alerta">stock bajo</span>}
                  {p.stock === 0 && <span className="chip chip-agotado">agotado</span>}
                </td>
                <td className="derecha">{euros(p.precio)}</td>
                <td className="derecha">
                  <button
                    className="stock"
                    title="Recontar stock"
                    onClick={() => onRecontar(p)}
                  >
                    {p.stock}
                  </button>
                </td>
                <td className="derecha">{euros(p.valorTotal)}</td>
                <td>
                  {p.categoria ? (
                    <span className="chip">{p.categoria}</span>
                  ) : (
                    <span className="sin-categoria">—</span>
                  )}
                </td>
                <td className="acciones">
                  <button
                    className="boton-mini"
                    onClick={() => onVender(p)}
                    disabled={p.stock === 0}
                    title="Registrar venta"
                  >
                  </button>
                  <button
                    className="boton-mini"
                    onClick={() => onEditar(p)}
                    title="Editar"
                  >
                  </button>
                  <button
                    className="boton-mini boton-mini-peligro"
                    onClick={() => onEliminar(p)}
                    title="Eliminar"
                  >
                  </button>
                </td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
