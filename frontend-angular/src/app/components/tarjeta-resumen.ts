import { Component, input } from '@angular/core'

@Component({
  selector: 'app-tarjeta-resumen',
  styleUrl: './tarjeta-resumen.css',
  templateUrl: './tarjeta-resumen.html',
})
export class TarjetaResumen {
  readonly titulo = input.required<string>()
  readonly valor = input.required<string | number>()
  readonly icono = input('')
  readonly alerta = input(false)
}
