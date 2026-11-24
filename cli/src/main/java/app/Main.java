package app;

import model.Producto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import controller.InventarioController;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        InventarioController controlador = new InventarioController();

        try {
            controlador.cargarInventario();

            System.out.println("Inventario cargado correctamente.");
            System.out.println(
                    "Productos registrados: " + controlador.listarProductos().size()
            );

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Error al cargar el inventario: " + e.getMessage());
            sc.close();
            return;
        }

        int opcion = -1;

        do {

            System.out.println("\nGESTOR DE INVENTARIO");
            System.out.println("1. Añadir producto");
            System.out.println("2. Listar productos");
            System.out.println("3. Buscar producto por ID");
            System.out.println("4. Actualizar stock");
            System.out.println("5. Registrar venta");
            System.out.println("6. Eliminar producto");
            System.out.println("7. Productos con stock bajo");
            System.out.println("8. Calcular valor del inventario");
            System.out.println("0. Salir");

            opcion = leerEntero(sc, "Selecciona una opción: ");

            switch (opcion) {

                // 1. Añadir producto
                case 1: {

                    try {
                        int id = leerEntero(sc, "Introduce el ID: ");

                        System.out.print("Introduce el nombre: ");
                        String nombre = sc.nextLine();

                        BigDecimal precio = leerDecimal(sc, "Introduce el precio: ");

                        int stock = leerEntero(sc, "Introduce el stock: ");

                        Producto producto = new Producto(
                                id, nombre, precio, stock
                        );

                        try {
                            controlador.agregarProducto(producto);
                            System.out.println("Producto agregado correctamente.");
                        } catch (RuntimeException e) {
                            System.out.println("Error: " + e.getMessage());
                        }

                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }

                    break;
                }

                // 2. Listar productos
                case 2: {

                    List<Producto> lista = controlador.listarProductos();

                    if (lista.isEmpty()) {
                        System.out.println("El inventario está vacío.");
                    } else {

                        System.out.println("\nLISTADO DE PRODUCTOS");

                        for (Producto producto : lista) {

                            System.out.println("-------------------------");
                            System.out.println("ID: " + producto.getId());
                            System.out.println("Nombre: " + producto.getNombre());
                            System.out.println("Precio: " + producto.getPrecio() + " €");
                            System.out.println("Stock: " + producto.getStock());
                        }
                    }

                    break;
                }

                // 3. Buscar producto
                case 3: {

                    try {
                        System.out.print("Introduce el ID del producto: ");
                        int id = Integer.parseInt(sc.nextLine());

                        Producto producto = controlador.buscarProducto(id);

                        System.out.println("\n--- PRODUCTO ENCONTRADO ---");
                        System.out.println("ID: " + producto.getId());
                        System.out.println("Nombre: " + producto.getNombre());
                        System.out.println("Precio: " + producto.getPrecio() + " €");
                        System.out.println("Stock: " + producto.getStock());

                    } catch (RuntimeException e) {
                        System.out.println("Error: " + e.getMessage());
                    }

                    break;
                }

                // 4. Actualizar stock
                case 4: {

                    try {
                        System.out.print("Introduce el ID del producto: ");
                        int id = Integer.parseInt(sc.nextLine());

                        System.out.print("Introduce el nuevo stock: ");
                        int nuevoStock = Integer.parseInt(sc.nextLine());

                        try {
                            controlador.actualizarStock(id, nuevoStock);
                            System.out.println("Stock actualizado correctamente.");
                        } catch (RuntimeException e) {
                            System.out.println("Error: " + e.getMessage());
                        }

                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }

                    break;
                }

                // 5. Registrar venta
                case 5: {

                    try {
                        System.out.print("Introduce el ID del producto: ");
                        int id = Integer.parseInt(sc.nextLine());

                        System.out.print("¿Cuántas unidades quieres vender?: ");
                        int cantidad = Integer.parseInt(sc.nextLine());

                        try {
                            controlador.venderProducto(id, cantidad);
                            System.out.println("Venta realizada correctamente.");
                        } catch (RuntimeException e) {
                            System.out.println("Error: " + e.getMessage());
                        }

                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }

                    break;
                }

                // 6. Eliminar producto
                case 6: {

                    try {
                        System.out.print("Introduce el ID del producto que quieres eliminar: ");
                        int id = Integer.parseInt(sc.nextLine());

                        try {
                            controlador.eliminarProducto(id);
                            System.out.println("Producto eliminado correctamente.");
                        } catch (RuntimeException e) {
                            System.out.println("Error: " + e.getMessage());
                        }

                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }

                    break;
                }

                // 7. Productos con stock bajo
                case 7: {

                    try {
                        System.out.print("Introduce el límite de stock: ");
                        int limite = Integer.parseInt(sc.nextLine());

                        List<Producto> productosBajoStock =
                                controlador.productosConStockBajo(limite);

                        if (productosBajoStock.isEmpty()) {
                            System.out.println("No hay productos con stock bajo.");
                        } else {

                            System.out.println("\n--- PRODUCTOS CON STOCK BAJO ---");

                            for (Producto producto : productosBajoStock) {
                                System.out.println(
                                        producto.getNombre() +
                                                " - Stock: " + producto.getStock()
                                );
                            }
                        }

                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }

                    break;
                }

                // 8. Calcular valor del inventario
                case 8: {

                    BigDecimal valorTotal = controlador.calcularValorInventario();

                    System.out.println(
                            "Valor total del inventario: " + valorTotal + " €"
                    );

                    break;
                }

                // 0. Salir
                case 0: {
                    System.out.println("Cerrando aplicación...");
                    break;
                }

                default:
                    System.out.println("Opción no válida.");
            }

        } while (opcion != 0);

        sc.close();
    }

    private static int leerEntero(Scanner sc, String mensaje) {

        while (true) {

            System.out.print(mensaje);

            try {
                return Integer.parseInt(sc.nextLine());

            } catch (NumberFormatException e) {
                System.out.println("Error: introduce un número entero válido.");
            }
        }
    }
    private static BigDecimal leerDecimal(Scanner sc, String mensaje) {

        while (true) {

            System.out.print(mensaje);

            try {
                String entrada = sc.nextLine().replace(",", ".");
                return new BigDecimal(entrada);

            } catch (NumberFormatException e) {
                System.out.println("Error: introduce un precio válido.");
            }
        }
    }

}
