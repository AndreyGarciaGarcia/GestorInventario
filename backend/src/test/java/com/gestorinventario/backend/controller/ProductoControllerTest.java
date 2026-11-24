package com.gestorinventario.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestorinventario.backend.dto.ProductoRequest;
import com.gestorinventario.backend.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de la API REST completos (MockMvc + H2): happy paths y errores.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ProductoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoRepository repositorio;

    @BeforeEach
    void limpiar() {
        repositorio.deleteAll();
    }

    private String json(String nombre, String precio, int stock) throws Exception {
        return objectMapper.writeValueAsString(
                new ProductoRequest(nombre, new BigDecimal(precio), stock, "Periféricos")
        );
    }

    // ------------------------------------------------------------------
    // Lecturas
    // ------------------------------------------------------------------

    @Test
    void listarDevuelveOkYLista() throws Exception {

        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Portatil", "899.99", 10)))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido", hasSize(1)))
                .andExpect(jsonPath("$.contenido[0].nombre").value("Portatil"))
                .andExpect(jsonPath("$.contenido[0].valorTotal").value(8999.90))
                .andExpect(jsonPath("$.totalElem").value(1));
    }

    @Test
    void buscarPorIdExistenteDevuelveElProducto() throws Exception {

        String respuesta = mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Ratón", "25.50", 5)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(respuesta).get("id").asLong();

        mvc.perform(get("/api/productos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ratón"));
    }

    @Test
    void buscarPorIdInexistenteDevuelve404() throws Exception {

        mvc.perform(get("/api/productos/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(
                        org.hamcrest.Matchers.containsString("99999")));
    }

    @Test
    void stockBajoFiltraCorrectamente() throws Exception {

        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Poco", "10.00", 2)))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Mucho", "10.00", 99)))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/productos/stock-bajo").param("limite", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre").value("Poco"));
    }

    @Test
    void buscarPorNombreDevuelveCoincidencias() throws Exception {

        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Teclado", "79.90", 3)))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/productos/buscar").param("texto", "tec"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    // ------------------------------------------------------------------
    // Escrituras
    // ------------------------------------------------------------------

    @Test
    void crearSinNombreDevuelve400ConDetalleDeCampo() throws Exception {

        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"precio\":10,\"stock\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("La petición no es válida"))
                .andExpect(jsonPath("$.campos.nombre").exists());
    }

    @Test
    void crearStockNegativoDevuelve400() throws Exception {

        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Malo", "10.00", -3)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.stock").exists());
    }

    @Test
    void duplicadoDevuelve409() throws Exception {

        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("USB", "8.75", 3)))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("usb", "9.75", 3)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void actualizarProductoConPut() throws Exception {

        String respuesta = mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Viejo", "10.00", 1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(respuesta).get("id").asLong();

        mvc.perform(put("/api/productos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Nuevo", "12.50", 7)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nuevo"))
                .andExpect(jsonPath("$.stock").value(7));
    }

    @Test
    void actualizarStockConPatch() throws Exception {

        String respuesta = mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Contado", "10.00", 1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(respuesta).get("id").asLong();

        mvc.perform(patch("/api/productos/" + id + "/stock").param("stock", "42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(42));
    }

    @Test
    void venderProductoYDescontarStock() throws Exception {

        String respuesta = mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Vendible", "10.00", 10)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(respuesta).get("id").asLong();

        mvc.perform(post("/api/productos/" + id + "/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(6));
    }

    @Test
    void venderSinStockSuficienteDevuelve409() throws Exception {

        String respuesta = mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Escaso", "10.00", 2)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(respuesta).get("id").asLong();

        mvc.perform(post("/api/productos/" + id + "/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\":10}"))
                .andExpect(status().isConflict());

        // El stock no ha cambiado
        mvc.perform(get("/api/productos/" + id))
                .andExpect(jsonPath("$.stock").value(2));
    }

    @Test
    void eliminarProductoDevuelve204() throws Exception {

        String respuesta = mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Borrable", "10.00", 1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(respuesta).get("id").asLong();

        mvc.perform(delete("/api/productos/" + id))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/productos/" + id))
                .andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------------
    // Agregados del inventario
    // ------------------------------------------------------------------

    @Test
    void valorTotalDelInventario() throws Exception {

        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("A", "10.00", 2)))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/inventario/valor-total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(20.00));
    }

    @Test
    void resumenDelInventario() throws Exception {

        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("A", "10.00", 2)))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/inventario/resumen").param("umbralStockBajo", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProductos").value(1))
                .andExpect(jsonPath("$.totalUnidades").value(2))
                .andExpect(jsonPath("$.valorTotal").value(20.00))
                .andExpect(jsonPath("$.stockBajo").value(1));
    }
}
