import { useEffect, useRef, useState } from 'react'

export default function ModalVenta({ producto, onConfirmar, onCancelar }) {
  const [cantidad, setCantidad] = useState(1)
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState(null)
  const input = useRef(null)

  useEffect(() => {
    input.current?.focus()
    // Escape cierra el modal
    const onKey = (e) => e.key === 'Escape' && onCancelar()
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [onCancelar])

  async function confirmar() {
    setEnviando(true)
    setError(null)

    try {
      await onConfirmar(cantidad)
    } catch (e) {
      setError(e.message)
      setEnviando(false)
    }
  }

  const excede = cantidad > producto.stock

  return (
    <div className="modal-fondo" onClick={onCancelar}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h3>Registrar venta</h3>

        <p className="modal-producto">
          <strong>{producto.nombre}</strong>
          <br />
          Stock disponible: {producto.stock} · {Number(producto.precio).toFixed(2)} €
        </p>

        <label className="modal-cantidad">
          Unidades a vender
          <input
            ref={input}
            type="number"
            min="1"
            max={producto.stock}
            value={cantidad}
            onChange={(e) => setCantidad(Number(e.target.value))}
          />
        </label>

        <p className="modal-total">
          Total:{' '}
          {(producto.precio * Math.max(cantidad, 0)).toLocaleString('es-ES', {
            style: 'currency',
            currency: 'EUR',
          })}
        </p>

        {excede && <p className="modal-error">No hay suficientes existencias.</p>}
        {error && <p className="modal-error">{error}</p>}

        <div className="modal-acciones">
          <button
            className="boton"
            onClick={confirmar}
            disabled={enviando || excede || cantidad < 1}
          >
            {enviando ? 'Registrando…' : 'Confirmar venta'}
          </button>
          <button className="boton boton-secundario" onClick={onCancelar}>
            Cancelar
          </button>
        </div>
      </div>
    </div>
  )
}
