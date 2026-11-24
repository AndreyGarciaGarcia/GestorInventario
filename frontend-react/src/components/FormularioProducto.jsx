export default function FormularioProducto({
  valores,
  onCambio,
  onGuardar,
  editando,
  onCancelar,
}) {
  return (
    <section className="formulario">
      <h2>{editando ? 'Editar producto' : 'Nuevo producto'}</h2>

      <form onSubmit={onGuardar} className="formulario-campos">
        <label>
          Nombre *
          <input
            type="text"
            value={valores.nombre}
            onChange={(e) => onCambio('nombre', e.target.value)}
            placeholder="Portatil Lenovo"
            maxLength={120}
            required
          />
        </label>

        <label>
          Precio (€) *
          <input
            type="number"
            value={valores.precio}
            onChange={(e) => onCambio('precio', e.target.value)}
            placeholder="899.99"
            min="0.01"
            step="0.01"
            required
          />
        </label>

        <label>
          Stock *
          <input
            type="number"
            value={valores.stock}
            onChange={(e) => onCambio('stock', e.target.value)}
            placeholder="10"
            min="0"
            step="1"
            required
          />
        </label>

        <label>
          Categoría
          <input
            type="text"
            value={valores.categoria}
            onChange={(e) => onCambio('categoria', e.target.value)}
            placeholder="Informática"
            maxLength={60}
          />
        </label>

        <div className="formulario-acciones">
          <button type="submit" className="boton">
            {editando ? 'Guardar cambios' : 'Añadir producto'}
          </button>

          {editando && (
            <button
              type="button"
              className="boton boton-secundario"
              onClick={onCancelar}
            >
              Cancelar
            </button>
          )}
        </div>
      </form>
    </section>
  )
}
