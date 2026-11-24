import { TestBed } from '@angular/core/testing'
import { provideHttpClient } from '@angular/common/http'
import { provideHttpClientTesting } from '@angular/common/http/testing'

import { App } from './app'

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents()
  })

  it('crea la aplicación', () => {
    const fixture = TestBed.createComponent(App)
    const app = fixture.componentInstance
    expect(app).toBeTruthy()
  })

  it('muestra el título del inventario', async () => {
    const fixture = TestBed.createComponent(App)
    await fixture.whenStable()

    const compiled = fixture.nativeElement as HTMLElement
    expect(compiled.querySelector('h1')?.textContent).toContain('Gestor de Inventario')
  })

  it('empieza en estado de carga', () => {
    const fixture = TestBed.createComponent(App)
    const app = fixture.componentInstance

    expect(app.cargando()).toBe(true)
    expect(app.productos()).toEqual([])
  })

  it('filtra por texto y por stock bajo', () => {
    const fixture = TestBed.createComponent(App)
    const app = fixture.componentInstance

    app.productos.set([
      { id: 1, nombre: 'Teclado', precio: 10, stock: 2, categoria: null, valorTotal: 20 },
      { id: 2, nombre: 'Monitor', precio: 100, stock: 50, categoria: null, valorTotal: 5000 },
    ])

    app.textoBusqueda.set('tec')
    expect(app.productosVisibles().map((p) => p.id)).toEqual([1])

    app.textoBusqueda.set('')
    app.soloStockBajo.set(true)
    expect(app.productosVisibles().map((p) => p.id)).toEqual([1])
  })
})
