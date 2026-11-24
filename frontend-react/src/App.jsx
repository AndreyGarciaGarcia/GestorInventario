import { useCallback, useEffect, useMemo, useState } from 'react'
import { api, ApiError } from './api/client'
import './App.css'

import TarjetaResumen from './components/TarjetaResumen'
import FormularioProducto from './components/FormularioProducto'
import TablaProductos from './components/TablaProductos'
import ModalVenta from './components/ModalVenta'
import Toast from './components/Toast'

const UMBRAL_STOCK_BAJO = 5

const VACIO = { nombre: '', precio: '', stock: '', categoria: '' }

export default function App() {
  const [productos, setProductos] = useState([])
  const [resumen, setResumen] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [textoBusqueda, setTextoBusqueda] = useState('')
  const [soloStockBajo, setSoloStockBajo] = useState(false)
  const [pagina, setPagina] = useState(0)
  const [tamanoPagina, setTamanoPagina] = useState(10)
  const [totalPaginas, setTotalPaginas] = useState(0)
  const [totalElementos, setTotalElementos] = useState(0)
  const [formulario, setFormulario] = useState(VACIO)
  const [editandoId, setEditandoId] = useState(null)
  const [ventaAbierta, setVentaAbierta] = useState(null)
  const [toasts, setToasts] = useState([])

  const proximoId = useCallback(() => Date.now(), [])

  const mostrarToast = useCallback((tipo, mensaje) => {
    const id = proximoId()
    setToasts((ts) => [...ts, { id, tipo, mensaje }])
    setTimeout(() => setToasts((ts) => ts.filter((t) => t.id !== id)), 4000)
  }, [proximoId])

  const cerrarToast = useCallback((id) => {
    setToasts((ts) => ts.filter((t) => t.id !== id))
  }, [])

  const refrescar = useCallback(async () => {
    try {
      const [paginaData, res] = await Promise.all([
        api.listar(pagina, tamanoPagina),
        api.resumen(UMBRAL_STOCK_BAJO),
      ])
      setProductos(paginaData.contenido)
      setTotalPaginas(paginaData.totalPags)
      setTotalElementos(paginaData.totalElem)
      setResumen(res)
    } catch (e) {
      const texto =
        e instanceof ApiError ? e.message : 'No se pudo contactar con el servidor'
      mostrarToast('error', texto)
    } finally {
      setCargando(false)
    }
  }, [pagina, tamanoPagina, mostrarToast])

  useEffect(() => {
    void refrescar()
  }, [refrescar])

  async function guardarProducto(evento) {
    evento.preventDefault()

    const datos = {
      nombre: formulario.nombre.trim(),
      precio: Number(formulario.precio),
      stock: Number(formulario.stock),
      categoria: formulario.categoria.trim() || null,
    }

    try {
      if (editandoId) {
        await api.actualizar(editandoId, datos)
        mostrarToast('exito', `Producto «${datos.nombre}» actualizado`)
      } else {
        await api.crear(datos)
        mostrarToast('exito', `Producto «${datos.nombre}» creado`)
      }

      setFormulario(VACIO)
      setEditandoId(null)
      await refrescar()
    } catch (e) {
      const texto =
        e instanceof ApiError ? e.message : 'Error al guardar el producto'
      mostrarToast('error', texto)
    }
  }

  function editar(producto) {
    setEditandoId(producto.id)
    setFormulario({
      nombre: producto.nombre,
      precio: String(producto.precio),
      stock: String(producto.stock),
      categoria: producto.categoria ?? '',
    })
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  function cancelarEdicion() {
    setEditandoId(null)
    setFormulario(VACIO)
  }

  async function eliminar(producto) {
    if (!window.confirm(`¿Eliminar «${producto.nombre}» del inventario?`)) return

    try {
      await api.eliminar(producto.id)
      mostrarToast('exito', `Producto «${producto.nombre}» eliminado`)
      await refrescar()
    } catch (e) {
      const texto =
        e instanceof ApiError ? e.message : 'Error al eliminar el producto'
      mostrarToast('error', texto)
    }
  }

  async function confirmarVenta(cantidad) {
    try {
      const producto = await api.vender(ventaAbierta.id, cantidad)
      mostrarToast(
        'exito',
        `Venta registrada: ${cantidad} × «${producto.nombre}» (quedan ${producto.stock})`,
      )
      setVentaAbierta(null)
      await refrescar()
    } catch (e) {
      const texto =
        e instanceof ApiError ? e.message : 'Error al registrar la venta'
      mostrarToast('error', texto)
      throw e
    }
  }

  async function recontar(producto) {
    const valor = window.prompt(
      `Nuevo stock de «${producto.nombre}»:`,
      String(producto.stock),
    )

    if (valor === null) return

    try {
      await api.actualizarStock(producto.id, Number(valor))
      mostrarToast('exito', `Stock de «${producto.nombre}» = ${Number(valor)}`)
      await refrescar()
    } catch (e) {
      const texto =
        e instanceof ApiError ? e.message : 'Error al actualizar el stock'
      mostrarToast('error', texto)
    }
  }

  const visibles = useMemo(() => {
    let lista = productos

    if (textoBusqueda.trim()) {
      const t = textoBusqueda.trim().toLowerCase()
      lista = lista.filter((p) => p.nombre.toLowerCase().includes(t))
    }

    if (soloStockBajo) {
      lista = lista.filter((p) => p.stock <= UMBRAL_STOCK_BAJO)
    }

    return [...lista].sort((a, b) => a.nombre.localeCompare(b.nombre, 'es'))
  }, [productos, textoBusqueda, soloStockBajo])

  return (
    <div className="app">
      <header className="cabecera">
        <div>
          <h1>Gestor de Inventario</h1>
          <p className="subtitulo">API REST · Spring Boot + React</p>
        </div>
        <button className="boton boton-secundario" onClick={refrescar}>
          ⟳ Actualizar
        </button>
      </header>

      <div className="toasts-contenedor">
        {toasts.map((t) => (
          <Toast
            key={t.id}
            id={t.id}
            tipo={t.tipo}
            mensaje={t.mensaje}
            onCerrar={cerrarToast}
          />
        ))}
      </div>

      <section className="resumen">
        <TarjetaResumen
          titulo="Productos"
          valor={resumen?.totalProductos ?? '—'}
        />
        <TarjetaResumen
          titulo="Unidades"
          valor={resumen?.totalUnidades ?? '—'}
        />
        <TarjetaResumen
          titulo="Valor del inventario"
          valor={
            resumen
              ? resumen.valorTotal.toLocaleString('es-ES', {
                  style: 'currency',
                  currency: 'EUR',
                })
              : '—'
          }
        />
        <TarjetaResumen
          titulo={`Stock bajo (≤ ${UMBRAL_STOCK_BAJO})`}
          valor={resumen?.stockBajo ?? '—'}
          alerta={(resumen?.stockBajo ?? 0) > 0}
        />
      </section>

      <FormularioProducto
        valores={formulario}
        onCambio={(campo, valor) =>
          setFormulario((f) => ({ ...f, [campo]: valor }))
        }
        onGuardar={guardarProducto}
        editando={Boolean(editandoId)}
        onCancelar={cancelarEdicion}
      />

      <section className="listado">
        <div className="listado-cabecera">
          <h2>Inventario ({visibles.length})</h2>

          <div className="filtros">
            <input
              type="search"
              placeholder="Buscar por nombre…"
              value={textoBusqueda}
              onChange={(e) => setTextoBusqueda(e.target.value)}
            />
            <label className="casilla">
              <input
                type="checkbox"
                checked={soloStockBajo}
                onChange={(e) => setSoloStockBajo(e.target.checked)}
              />
              Solo stock bajo
            </label>
          </div>
        </div>

        <TablaProductos
          productos={visibles}
          cargando={cargando}
          umbral={UMBRAL_STOCK_BAJO}
          onEditar={editar}
          onVender={setVentaAbierta}
          onEliminar={eliminar}
          onRecontar={recontar}
        />
        <div className="paginador">
          <span className="paginador-info">
            Página {pagina + 1} de {totalPaginas} ({totalElementos} elementos)
          </span>
          <div className="paginador-botones">
            <button
              type="button"
              className="boton-mini"
              onClick={() => setPagina((p) => Math.max(p - 1, 0))}
              disabled={pagina === 0}
            >
              Anterior
            </button>
            <button
              type="button"
              className="boton-mini"
              onClick={() => setPagina((p) => Math.min(p + 1, totalPaginas - 1))}
              disabled={pagina >= totalPaginas - 1 || totalPaginas === 0}
            >
              Siguiente
            </button>
          </div>
        </div>
      </section>

      {ventaAbierta && (
        <ModalVenta
          producto={ventaAbierta}
          onConfirmar={confirmarVenta}
          onCancelar={() => setVentaAbierta(null)}
        />
      )}

      <footer className="pie">
        GestorInventario · Fase 3 — React + Vite contra la API de la Fase 2
      </footer>
    </div>
  )
}
