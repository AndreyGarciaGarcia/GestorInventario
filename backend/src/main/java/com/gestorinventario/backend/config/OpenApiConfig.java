package com.gestorinventario.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gestorInventarioOpenAPI() {

        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor local de desarrollo")
                ))
                .info(new Info()
                        .title("GestorInventario API")
                        .description("""
                            API REST para la gestión de un inventario de productos.
                            
                            Permite dar de alta, consultar, actualizar y eliminar productos,
                            registrar ventas (descuento de stock), consultar alertas de stock
                            bajo y obtener el valor total del inventario.
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("GestorInventario")
                                .email("soporte@gestorinventario.local"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
