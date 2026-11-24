/** Tarjeta de cifra del panel superior. */
export default function TarjetaResumen({ titulo, valor, icono, alerta = false }) {
  return (
    <div className={`tarjeta ${alerta ? 'tarjeta-alerta' : ''}`}>
      <span className="tarjeta-icono" aria-hidden="true">
        {icono}
      </span>
      <div>
        <p className="tarjeta-valor">{valor}</p>
        <p className="tarjeta-titulo">{titulo}</p>
      </div>
    </div>
  )
}
