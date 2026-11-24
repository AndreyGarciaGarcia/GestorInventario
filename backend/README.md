# GestorInventario — Backend (Fase 2)

API REST con Spring Boot 3.5, JPA, MySQL/H2 y 30 tests de integración.

## Requisitos

- JDK 17+ (probado con JDK 26)
- Maven 3.9+

## Arrancar en local (H2)

```powershell
cd backend
$env:PATH += ";C:\tools\apache-maven-3.9.16\bin"
mvn spring-boot:run
```

## Tests

```powershell
mvn test
```

## Arrancar contra MySQL

```powershell
$env:SPRING_PROFILES_ACTIVE="mysql"
$env:MYSQL_HOST="localhost"
$env:MYSQL_PORT="3306"
$env:MYSQL_DATABASE="gestor_inventario"
$env:MYSQL_USER="gestor"
$env:MYSQL_PASSWORD="gestor123"
mvn spring-boot:run
```

## Endpoints (base `http://localhost:8080/api`)

| Método | Ruta |
|--------|------|
| GET | `/productos` |
| GET | `/productos/{id}` |
| GET | `/productos/buscar?texto=` |
| GET | `/productos/stock-bajo?limite=5` |
| POST | `/productos` |
| PUT | `/productos/{id}` |
| PATCH | `/productos/{id}/stock?stock=` |
| POST | `/productos/{id}/ventas` |
| DELETE | `/productos/{id}` |
| GET | `/inventario/valor-total` |
| GET | `/inventario/resumen?umbralStockBajo=5` |

Swagger UI: `http://localhost:8080/swagger-ui.html`
