import { Component, effect, input, output, signal } from '@angular/core'
import { FormsModule } from '@angular/forms'

import type { Producto } from '../models/producto.model'

const VACIO = { nombre: '', precio: '', stock: '', categoria: '' }

@Component({
  selector: 'app-formulario-producto',
  imports: [FormsModule],
  styleUrl: './formulario-producto.css',
  templateUrl: './formulario-producto.html',
})
export class FormularioProducto {
  readonly editando = input<Producto | null>(null)

  readonly guardar = output<{
    nombre: string
    precio: number
    stock: number
    categoria: string | null
  }>()
  readonly cancelarEdicion = output<void>()

  readonly valores = signal({ ...VACIO })

  constructor() {
    effect(() => {
      const producto = this.editando()

      this.valores.set(
        producto
          ? {
              nombre: producto.nombre,
              precio: String(producto.precio),
              stock: String(producto.stock),
              categoria: producto.categoria ?? '',
            }
          : { ...VACIO },
      )
    })
  }

  actualizar(campo: keyof typeof VACIO, valor: string): void {
    this.valores.update((v) => ({ ...v, [campo]: valor }))
  }

  enviar(): void {
    const v = this.valores()

    this.guardar.emit({
      nombre: v.nombre.trim(),
      precio: Number(v.precio),
      stock: Number(v.stock),
      categoria: v.categoria.trim() || null,
    })
  }
}
