import { CurrencyPipe } from '@angular/common'
import { Component, input, output } from '@angular/core'
import type { Producto } from '../models/producto.model'

@Component({
  selector: 'app-tabla-productos',
  imports: [CurrencyPipe],
  styleUrl: './tabla-productos.css',
  templateUrl: './tabla-productos.html',
})
export class TablaProductos {
  readonly productos = input.required<Producto[]>()
  readonly cargando = input(false)
  readonly umbral = input(5)

  readonly editar = output<Producto>()
  readonly vender = output<Producto>()
  readonly eliminar = output<Producto>()
  readonly recontar = output<Producto>()
}
