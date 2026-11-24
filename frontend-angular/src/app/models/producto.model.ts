export interface Producto {
  id: number
  nombre: string
  precio: number
  stock: number
  categoria: string | null
  valorTotal: number
}

export interface ProductoRequest {
  nombre: string
  precio: number
  stock: number
  categoria: string | null
}

export interface ResumenInventario {
  totalProductos: number
  totalUnidades: number
  valorTotal: number
  stockBajo: number
}

export interface ErrorApi {
  timestamp: string
  status: number
  error: string
  mensaje: string
  campos?: Record<string, string>
}

export interface PaginaRespuesta<T> {
  contenido: T[]
  numero: number
  tamaño: number
  totalElem: number
  totalPags: number
}
