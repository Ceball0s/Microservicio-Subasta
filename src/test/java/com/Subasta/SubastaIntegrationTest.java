package com.Subasta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.Subasta.Models.EstadoSubasta;
import com.Subasta.Models.Subasta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class SubastaIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SubastaRepository subastaRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void flujoCompletoSubasta_DeberiaFuncionarCorrectamente() throws Exception {
        // 1. Crear una subasta
        AgregarRequest crearRequest = AgregarRequest.builder()
                .nombre("Subasta Integración")
                .descripcion("Descripción de integración")
                .precioInicial(100.0)
                .aumentoMinimo(10.0)
                .fechaCierre(new Date(System.currentTimeMillis() + 86400000))
                .build();

        String subastaCreada = mockMvc.perform(post("/api/subasta/agregar")
                .header("user-id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(crearRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Subasta Integración"))
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extraer el ID de la subasta creada
        SubastaDTO subastaDTO = objectMapper.readValue(subastaCreada, SubastaDTO.class);
        Long subastaId = subastaDTO.getId();

        // 2. Consultar la subasta creada
        mockMvc.perform(get("/api/subasta/{subastaId}", subastaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subastaId))
                .andExpect(jsonPath("$.nombre").value("Subasta Integración"));

        // 3. Modificar la subasta
        AgregarRequest modificarRequest = AgregarRequest.builder()
                .nombre("Subasta Integración Modificada")
                .descripcion("Descripción modificada")
                .precioInicial(150.0)
                .aumentoMinimo(15.0)
                .build();

        mockMvc.perform(put("/api/subasta/{subastaId}", subastaId)
                .header("user-id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modificarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Subasta Integración Modificada"))
                .andExpect(jsonPath("$.precioInicial").value(150.0));

        // 4. Obtener subastas del usuario
        mockMvc.perform(get("/api/subasta/usuario")
                .header("user-id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(subastaId));

        // 5. Cerrar la subasta
        mockMvc.perform(put("/api/subasta/finalizar/{subastaId}", subastaId)
                .header("user-id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("FINALIZADA"));
    }

    @Test
    void obtenerRecomendaciones_DeberiaRetornarSubastas() throws Exception {
        // Crear algunas subastas de prueba
        crearSubastasDePrueba();

        // Obtener recomendaciones
        mockMvc.perform(get("/api/subasta/recomendaciones")
                .header("user-id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void agregarSubasta_SinUserId_DeberiaRetornarForbidden() throws Exception {
        AgregarRequest request = AgregarRequest.builder()
                .nombre("Subasta Sin Usuario")
                .precioInicial(100.0)
                .aumentoMinimo(10.0)
                .build();

        mockMvc.perform(post("/api/subasta/agregar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void modificarSubasta_UsuarioNoAutorizado_DeberiaRetornarForbidden() throws Exception {
        // Crear una subasta
        Subasta subasta = crearSubastaEnBD();

        AgregarRequest request = AgregarRequest.builder()
                .nombre("Subasta No Autorizada")
                .precioInicial(100.0)
                .aumentoMinimo(10.0)
                .build();

        // Intentar modificar con usuario diferente
        mockMvc.perform(put("/api/subasta/{subastaId}", subasta.getId())
                .header("user-id", "999") // Usuario diferente
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void cerrarSubasta_SubastaNoEncontrada_DeberiaRetornarNotFound() throws Exception {
        mockMvc.perform(put("/api/subasta/finalizar/999")
                .header("user-id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void consultarSubasta_SubastaNoEncontrada_DeberiaRetornarNotFound() throws Exception {
        mockMvc.perform(get("/api/subasta/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelarSubasta_DeberiaFuncionarCorrectamente() throws Exception {
        // Crear una subasta
        Subasta subasta = crearSubastaEnBD();

        // Cancelar la subasta
        mockMvc.perform(put("/api/subasta/cancelar/{subastaId}", subasta.getId())
                .header("user-id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    void cancelarSubasta_SubastaYaFinalizada_DeberiaRetornarBadRequest() throws Exception {
        // Crear una subasta finalizada
        Subasta subasta = crearSubastaEnBD();
        subasta.setEstado(EstadoSubasta.FINALIZADA);
        subastaRepository.save(subasta);

        // Intentar cancelar
        mockMvc.perform(put("/api/subasta/cancelar/{subastaId}", subasta.getId())
                .header("user-id", "1"))
                .andExpect(status().isBadRequest());
    }

    // Métodos auxiliares
    private void crearSubastasDePrueba() {
        for (int i = 1; i <= 3; i++) {
            Subasta subasta = Subasta.builder()
                    .nombre("Subasta Test " + i)
                    .descripcion("Descripción " + i)
                    .precioInicial(100.0 * i)
                    .precioActual(100.0 * i)
                    .aumentoMinimo(10.0 * i)
                    .fechaCreacion(new Date())
                    .fechaCierre(new Date(System.currentTimeMillis() + 86400000))
                    .estado(EstadoSubasta.ACTIVA)
                    .user_id((long) i)
                    .build();
            subastaRepository.save(subasta);
        }
    }

    private Subasta crearSubastaEnBD() {
        Subasta subasta = Subasta.builder()
                .nombre("Subasta Test")
                .descripcion("Descripción de prueba")
                .precioInicial(100.0)
                .precioActual(100.0)
                .aumentoMinimo(10.0)
                .fechaCreacion(new Date())
                .fechaCierre(new Date(System.currentTimeMillis() + 86400000))
                .estado(EstadoSubasta.ACTIVA)
                .user_id(1L)
                .build();
        return subastaRepository.save(subasta);
    }
} 