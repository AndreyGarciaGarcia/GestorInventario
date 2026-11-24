import { Component, inject, signal } from '@angular/core'
import { FormsModule } from '@angular/forms'

import { InventarioService } from './services/inventario.service'
import type { Producto, ProductoRequest, ResumenInventario } from './models/producto.model'

import { TarjetaResumen } from './components/tarjeta-resumen'
import { FormularioProducto } from './components/formulario-producto'
import { TablaProductos } from './components/tabla-productos'
import { ModalVenta } from './components/modal-venta'
import { Toast } from './components/toast'

const UMBRAL_STOCK_BAJO = 5

type TipoToast = 'exito' | 'error'

interface ToastItem {
  id: number
  tipo: TipoToast
  mensaje: string
}

@Component({
  selector: 'app-root',
  imports: [FormsModule, TarjetaResumen, FormularioProducto, TablaProductos, ModalVenta, Toast],
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  private readonly api = inject(InventarioService)

  readonly productos = signal<Producto[]>([])
  readonly resumen = signal<ResumenInventario | null>(null)
  readonly cargando = signal(true)

  readonly textoBusqueda = signal('')
  readonly soloStockBajo = signal(false)

  readonly pagina = signal(0)
  readonly tamanoPagina = signal(10)
  readonly totalPaginas = signal(0)
  readonly totalElementos = signal(0)

  readonly productoEnEdicion = signal<Producto | null>(null)
  readonly productoEnVenta = signal<Producto | null>(null)

  readonly toasts = signal<ToastItem[]>([])

  readonly umbral = UMBRAL_STOCK_BAJO

  private contadorToast = 0

  constructor() {
    this.refrescar()
  }

  paginaAnterior(): void {
    this.pagina.update((p) => Math.max(p - 1, 0))
  }

  paginaSiguiente(): void {
    this.pagina.update((p) => Math.min(p + 1, this.totalPaginas() - 1))
  }

  valorFormateado(): string {
    const resumen = this.resumen()

    if (!resumen) return '—'

    return resumen.valorTotal.toLocaleString('es-ES', {
      style: 'currency',
      currency: 'EUR',
    })
  }

  productosVisibles(): Producto[] {
    const texto = this.textoBusqueda().trim().toLowerCase()

    return this.productos()
      .filter((p) => (texto ? p.nombre.toLowerCase().includes(texto) : true))
      .filter((p) => (this.soloStockBajo() ? p.stock <= UMBRAL_STOCK_BAJO : true))
      .sort((a, b) => a.nombre.localeCompare(b.nombre, 'es'))
  }

  private mostrarToast(tipo: TipoToast, mensaje: string): void {
    const id = ++this.contadorToast
    this.toasts.update((ts) => [...ts, { id, tipo, mensaje }])

    setTimeout(() => {
      this.toasts.update((ts) => ts.filter((t) => t.id !== id))
    }, 4000)
  }

  cerrarToast(id: number): void {
    this.toasts.update((ts) => ts.filter((t) => t.id !== id))
  }

  refrescar(): void {
    this.api.listar(this.pagina(), this.tamanoPagina()).subscribe({
      next: (pagina) => {
        this.productos.set(pagina.contenido)
        this.totalPaginas.set(pagina.totalPags)
        this.totalElementos.set(pagina.totalElem)
        this.cargando.set(false)
      },
      error: (fallop: Error) => {
        this.cargando.set(false)
        this.mostrarToast(
          'error',
          fallop?.message || 'No se pudo contactar con el servidor. ¿Está el backend arrancado?',
        )
      },
    })

    this.api.resumen(UMBRAL_STOCK_BAJO).subscribe({
      next: (res) => this.resumen.set(res),
      error: (fallop: Error) => {
        this.mostrarToast(
          'error',
          fallop?.message || 'No se pudo cargar el resumen del inventario',
        )
      },
    })
  }

  guardar(datos: ProductoRequest): void {
    const editando = this.productoEnEdicion()

    const peticion = editando
      ? this.api.actualizar(editando.id, datos)
      : this.api.crear(datos)

    peticion.subscribe({
      next: (producto) => {
        this.mostrarToast(
          'exito',
          editando
            ? `Producto «${producto.nombre}» actualizado`
            : `Producto «${producto.nombre}» creado`,
        )
        this.productoEnEdicion.set(null)
        this.refrescar()
      },
      error: (fallop: Error) => {
        this.mostrarToast(
          'error',
          fallop?.message || 'Error al guardar el producto',
        )
      },
    })
  }

  editar(producto: Producto): void {
    this.productoEnEdicion.set(producto)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  eliminar(producto: Producto): void {
    if (!window.confirm(`¿Eliminar «${producto.nombre}» del inventario?`)) return

    this.api.eliminar(producto.id).subscribe({
      next: () => {
        this.mostrarToast('exito', `Producto «${producto.nombre}» eliminado`)
        this.refrescar()
      },
      error: (fallop: Error) => {
        this.mostrarToast(
          'error',
          fallop?.message || 'Error al eliminar el producto',
        )
      },
    })
  }

  recontar(producto: Producto): void {
    const valor = window.prompt(
      `Nuevo stock de «${producto.nombre}»:`,
      String(producto.stock),
    )

    if (valor === null) return

    this.api.actualizarStock(producto.id, Number(valor)).subscribe({
      next: () => {
        this.mostrarToast('exito', `Stock de «${producto.nombre}» = ${Number(valor)}`)
        this.refrescar()
      },
      error: (fallop: Error) => {
        this.mostrarToast(
          'error',
          fallop?.message || 'Error al actualizar el stock',
        )
      },
    })
  }

  confirmarVenta(cantidad: number): void {
    const producto = this.productoEnVenta()
    if (!producto) return

    this.api.vender(producto.id, cantidad).subscribe({
      next: (trasVenta) => {
        this.mostrarToast(
          'exito',
          `Venta registrada: ${cantidad} × «${trasVenta.nombre}» (quedan ${trasVenta.stock})`,
        )
        this.productoEnVenta.set(null)
        this.refrescar()
      },
      error: (fallop: Error) => {
        this.mostrarToast(
          'error',
          fallop?.message || 'Error al registrar la venta',
        )
        this.productoEnVenta.set({ ...producto })
      },
    })
  }
}
