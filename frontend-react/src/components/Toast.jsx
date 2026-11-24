import { useCallback, useEffect, useState } from 'react'

export default function Toast({ id, tipo, mensaje, duracion = 4000, onCerrar }) {
  const [visible, setVisible] = useState(true)

  useEffect(() => {
    const t = setTimeout(() => {
      setVisible(false)
      setTimeout(() => onCerrar(id), 300)
    }, duracion)
    return () => clearTimeout(t)
  }, [id, duracion, onCerrar])

  const esExito = tipo === 'exito'

  if (!visible) return null

  return (
    <div className={`toast toast-${tipo}`} role="alert" aria-live="polite">
      <span className="toast-mensaje">{mensaje}</span>
      <button
        type="button"
        className="toast-cerrar"
        onClick={() => {
          setVisible(false)
          onCerrar(id)
        }}
        aria-label="Cerrar notificación"
      >
        ✕
      </button>
    </div>
  )
}
