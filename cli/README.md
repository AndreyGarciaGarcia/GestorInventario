# GestorInventario — CLI (Fase 1)

Aplicación de consola en Java para gestionar un inventario de productos.

## Requisitos

- JDK 17+
- Maven 3.9+

## Ejecutar

```powershell
cd cli
$env:PATH += ";C:\tools\apache-maven-3.9.16\bin"
mvn test
mvn package
java -jar target/gestor-inventario-cli.jar
```

## Menú

```
1. Añadir producto
2. Listar productos
3. Buscar producto por ID
4. Actualizar stock
5. Registrar venta
6. Eliminar producto
7. Productos con stock bajo
8. Calcular valor del inventario
0. Salir
```
