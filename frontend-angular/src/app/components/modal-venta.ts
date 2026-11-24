import { CurrencyPipe } from '@angular/common'
import { Component, effect, input, output, signal } from '@angular/core'
import { FormsModule } from '@angular/forms'
import type { Producto } from '../models/producto.model'


@Component({
  selector: 'app-modal-venta',
  imports: [FormsModule, CurrencyPipe],
  styleUrl: './modal-venta.css',
  templateUrl: './modal-venta.html',
})
export class ModalVenta {
  readonly producto = input.required<Producto>()
  readonly confirmar = output<number>()
  readonly cancelar = output<void>()

  readonly cantidad = signal(1)
  readonly enviando = signal(false)
  readonly error = signal<string | null>(null)

  constructor() {
    effect(() => {
      this.producto()
      this.cantidad.set(1)
      this.error.set(null)
      this.enviando.set( false)
    })
  }

  get excede(): boolean {
    return this.cantidad() > this.producto().stock
  }

  get total(): number {
    return this.producto().precio * Math.max(this.cantidad(), 0)
  }

  enviar(): void {
    if (this.excede || this.cantidad() < 1) return

    this.enviando.set(true)
    this.error.set(null)
    this.confirmar.emit(this.cantidad())
  }
}
