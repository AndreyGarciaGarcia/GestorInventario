# GestorInventario

Proyecto de gestión de inventario en 5 fases:

| Fase | Qué | Tecnología | Estado |
|------|-----|------------|--------|
| 1 | CLI de consola | Java + Maven + JUnit 5 | 39 tests |
| 2 | API REST | Spring Boot 3.5 + MySQL + JPA | 30 tests |
| 3 | Frontend React | React 19 + Vite | build OK |
| 4 | Frontend Angular | Angular 22 | build + 4 tests |
| 5 | Dockerización | Docker Compose | listo |

## Estructura

```
GestorInventario/
├── cli/              # Fase 1 — CLI Java
├── backend/          # Fase 2 — API REST Spring Boot
├── frontend-react/   # Fase 3 — React + Vite
├── frontend-angular/ # Fase 4 — Angular 22
├── docker-compose.yml
├── .env.example
└── README.md
```

## Ejecutar

### Sin Docker

Backend (terminal 1):
```powershell
cd backend
$env:PATH += ";C:\tools\apache-maven-3.9.16\bin"
mvn spring-boot:run
```

React (terminal 2):
```powershell
cd frontend-react
cmd /c npm run dev
```

Angular (terminal 3):
```powershell
cd frontend-angular
cmd /c npx ng serve --proxy-config proxy.conf.json
```

### Con Docker

```powershell
docker compose up --build
```

Puertos: backend `8080`, React `5173`, Angular `4200`.

## Endpoints API (base `http://localhost:8080/api`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/productos` | Listado paginado |
| GET | `/productos/{id}` | Detalle |
| GET | `/productos/buscar?texto=` | Búsqueda por nombre |
| GET | `/productos/stock-bajo?limite=5` | Stock bajo |
| POST | `/productos` | Alta |
| PUT | `/productos/{id}` | Actualización |
| PATCH | `/productos/{id}/stock?stock=` | Reconteo de stock |
| POST | `/productos/{id}/ventas` | Venta |
| DELETE | `/productos/{id}` | Baja |
| GET | `/inventario/valor-total` | Valor total |
| GET | `/inventario/resumen?umbralStockBajo=5` | Resumen |

Documentación Swagger: `http://localhost:8080/swagger-ui.html`

## Tests

```powershell
cd cli         && mvn test
cd backend     && mvn test
cd frontend-angular && ng test --watch=false
```
