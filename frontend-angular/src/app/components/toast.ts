import { Component, effect, input, output, signal } from '@angular/core'

@Component({
  selector: 'app-toast',
  styleUrl: './toast.css',
  templateUrl: './toast.html',
})
export class Toast {
  readonly id = input.required<number>()
  readonly tipo = input.required<'exito' | 'error'>()
  readonly mensaje = input.required<string>()
  readonly cerrar = output<number>()

  readonly duracion = 4000
  readonly visible = signal(true)

  constructor() {
    effect(() => {
      this.tipo()
      this.mensaje()
      setTimeout(() => {
        this.visible.set(false)
        setTimeout(() => this.cerrar.emit(this.id()), 300)
      }, this.duracion)
    })
  }

  get esExito(): boolean {
    return this.tipo() === 'exito'
  }

  get icono(): string {
    return this.esExito ? '✅' : '⚠️'
  }
}
