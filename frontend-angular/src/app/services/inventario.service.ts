import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http'
import { Injectable, inject } from '@angular/core'
import { Observable, catchError, throwError } from 'rxjs'

import type {
  ErrorApi,
  PaginaRespuesta,
  Producto,
  ProductoRequest,
  ResumenInventario,
} from '../models/producto.model'

@Injectable({ providedIn: 'root' })
export class InventarioService {
  private readonly http = inject(HttpClient)

  private readonly base = '/api'

  listar(page: number = 0, size: number = 10): Observable<PaginaRespuesta<Producto>> {
    return this.get<PaginaRespuesta<Producto>>(`/productos?page=${page}&size=${size}`)
  }

  buscar(texto: string): Observable<Producto[]> {
    return this.get<Producto[]>(`/productos/buscar?texto=${encodeURIComponent(texto)}`)
  }

  stockBajo(limite: number): Observable<Producto[]> {
    return this.get<Producto[]>(`/productos/stock-bajo?limite=${limite}`)
  }

  crear(producto: ProductoRequest): Observable<Producto> {
    return this.post<Producto>('/productos', producto)
  }

  actualizar(id: number, producto: ProductoRequest): Observable<Producto> {
    return this.put<Producto>(`/productos/${id}`, producto)
  }

  actualizarStock(id: number, stock: number): Observable<Producto> {
    const params = new HttpParams().set('stock', stock)
    return this.http
      .patch<Producto>(`${this.base}/productos/${id}/stock`, null, { params })
      .pipe(catchError(this.traducir))
  }

  vender(id: number, cantidad: number): Observable<Producto> {
    return this.post<Producto>(`/productos/${id}/ventas`, { cantidad })
  }

  eliminar(id: number): Observable<void> {
    return this.http
      .delete<void>(`${this.base}/productos/${id}`)
      .pipe(catchError(this.traducir))
  }

  resumen(umbral: number): Observable<ResumenInventario> {
    const params = new HttpParams().set('umbralStockBajo', umbral)
    return this.http
      .get<ResumenInventario>(`${this.base}/inventario/resumen`, { params })
      .pipe(catchError(this.traducir))
  }

  private get<T>(ruta: string): Observable<T> {
    return this.http.get<T>(`${this.base}${ruta}`).pipe(catchError(this.traducir))
  }

  private post<T>(ruta: string, cuerpo: unknown): Observable<T> {
    return this.http.post<T>(`${this.base}${ruta}`, cuerpo).pipe(catchError(this.traducir))
  }

  private put<T>(ruta: string, cuerpo: unknown): Observable<T> {
    return this.http.put<T>(`${this.base}${ruta}`, cuerpo).pipe(catchError(this.traducir))
  }

  private readonly traducir = (fallop: HttpErrorResponse) => {
    const cuerpo = fallop.error as ErrorApi | null

    const mensaje =
      cuerpo?.mensaje ??
      (fallop.status === 0
        ? 'No se pudo contactar con el servidor. ¿Está el backend arrancado?'
        : `Error ${fallop.status}: ${fallop.statusText}`)

    const campos = cuerpo?.campos
      ? Object.entries(cuerpo.campos)
          .map(([campo, error]) => `${campo}: ${error}`)
          .join(' · ')
      : ''

    return throwError(() => new Error(campos ? `${mensaje} — ${campos}` : mensaje))
  }
}
