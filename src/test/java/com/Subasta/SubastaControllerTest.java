package com.Subasta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.Subasta.DTOs.AgregarRequest;
import com.Subasta.DTOs.SubastaDTO;
import com.Subasta.Models.EstadoSubasta;
import com.Subasta.Models.Subasta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SubastaControllerTest {

    @Mock
    private SubastaService subastaService;

    @InjectMocks
    private SubastaController subastaController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subastaController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void obtenerRecomendaciones_ConUserIdValido_DeberiaRetornarLista() throws Exception {
        // Arrange
        Long userId = 1L;
        List<SubastaDTO> subastas = Arrays.asList(
            crearSubastaDTO(1L, "Subasta 1", 100.0),
            crearSubastaDTO(2L, "Subasta 2", 200.0)
        );
        when(subastaService.obtenerRecomendacionesGenericas()).thenReturn(subastas);

        // Act & Assert
        mockMvc.perform(get("/api/subasta/recomendaciones")
                .header("user-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Subasta 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nombre").value("Subasta 2"));
    }

    @Test
    void obtenerRecomendaciones_SinUserId_DeberiaRetornarLista() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/subasta/recomendaciones"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregarSubasta_ConDatosValidos_DeberiaRetornarSubastaCreada() throws Exception {
        // Arrange
        Long userId = 1L;
        AgregarRequest request = AgregarRequest.builder()
                .nombre("Nueva Subasta")
                .descripcion("Descripción de prueba")
                .precioInicial(100.0)
                .aumentoMinimo(10.0)
                .fechaCierre(new Date())
                .build();

        SubastaDTO subastaCreada = crearSubastaDTO(1L, "Nueva Subasta", 100.0);
        when(subastaService.agregarSubasta(any(AgregarRequest.class), eq(1))).thenReturn(subastaCreada);

        // Act & Assert
        mockMvc.perform(post("/api/subasta/agregar")
                .header("user-id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Nueva Subasta"));
    }

    @Test
    void agregarSubasta_SinUserId_DeberiaRetornarBadRequest() throws Exception {
        // Arrange
        AgregarRequest request = AgregarRequest.builder()
                .nombre("Nueva Subasta")
                .precioInicial(100.0)
                .aumentoMinimo(10.0)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/subasta/agregar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregarSubasta_ConAumentoMinimoInvalido_DeberiaRetornarBadRequest() throws Exception {
        // Arrange
        Long userId = 1L;
        AgregarRequest request = AgregarRequest.builder()
                .nombre("Nueva Subasta")
                .precioInicial(100.0)
                .aumentoMinimo(0.0) // Aumento mínimo inválido
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/subasta/agregar")
                .header("user-id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void consultarSubasta_ConIdValido_DeberiaRetornarSubasta() throws Exception {
        // Arrange
        int subastaId = 1;
        SubastaDTO subasta = crearSubastaDTO(1L, "Subasta Test", 100.0);
        when(subastaService.obtenerSubastaPorId(subastaId)).thenReturn(subasta);

        // Act & Assert
        mockMvc.perform(get("/api/subasta/{subastaId}", subastaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Subasta Test"));
    }

    @Test
    void modificarSubasta_ConDatosValidos_DeberiaRetornarSubastaModificada() throws Exception {
        // Arrange
        Long userId = 1L;
        int subastaId = 1;
        AgregarRequest request = AgregarRequest.builder()
                .nombre("Subasta Modificada")
                .descripcion("Nueva descripción")
                .precioInicial(150.0)
                .aumentoMinimo(15.0)
                .build();

        SubastaDTO subastaModificada = crearSubastaDTO(1L, "Subasta Modificada", 150.0);
        when(subastaService.modificarSubasta(eq(subastaId), any(AgregarRequest.class), eq(1)))
                .thenReturn(subastaModificada);

        // Act & Assert
        mockMvc.perform(put("/api/subasta/{subastaId}", subastaId)
                .header("user-id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Subasta Modificada"));
    }

    @Test
    void modificarSubasta_SinUserId_DeberiaRetornarBadRequest() throws Exception {
        // Arrange
        int subastaId = 1;
        AgregarRequest request = AgregarRequest.builder()
                .nombre("Subasta Modificada")
                .precioInicial(150.0)
                .aumentoMinimo(15.0)
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/subasta/{subastaId}", subastaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cerrarSubasta_ConDatosValidos_DeberiaRetornarSubastaCerrada() throws Exception {
        // Arrange
        Long userId = 1L;
        int subastaId = 1;
        SubastaDTO subastaCerrada = crearSubastaDTO(1L, "Subasta Cerrada", 100.0);
        subastaCerrada.setEstado("FINALIZADA");
        when(subastaService.cerrarSubasta(eq(subastaId), eq(1))).thenReturn(subastaCerrada);

        // Act & Assert
        mockMvc.perform(put("/api/subasta/finalizar/{subastaId}", subastaId)
                .header("user-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("FINALIZADA"));
    }

    @Test
    void cancelarSubasta_ConDatosValidos_DeberiaRetornarSubastaCancelada() throws Exception {
        // Arrange
        Long userId = 1L;
        int subastaId = 1;
        SubastaDTO subastaCancelada = crearSubastaDTO(1L, "Subasta Cancelada", 100.0);
        subastaCancelada.setEstado("CANCELADA");
        when(subastaService.cancelarSubasta(eq(subastaId), eq(1))).thenReturn(subastaCancelada);

        // Act & Assert
        mockMvc.perform(put("/api/subasta/cancelar/{subastaId}", subastaId)
                .header("user-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    void obtenerSubastasPorUsuario_ConUserIdValido_DeberiaRetornarLista() throws Exception {
        // Arrange
        Long userId = 1L;
        List<SubastaDTO> subastas = Arrays.asList(
            crearSubastaDTO(1L, "Subasta Usuario 1", 100.0),
            crearSubastaDTO(2L, "Subasta Usuario 2", 200.0)
        );
        when(subastaService.obtenerSubastasPorUsuario(1)).thenReturn(subastas);

        // Act & Assert
        mockMvc.perform(get("/api/subasta/usuario")
                .header("user-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void obtenerSubastasPorUsuario_SinUserId_DeberiaRetornarBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/subasta/usuario"))
                .andExpect(status().isBadRequest());
    }

    // Método auxiliar para crear SubastaDTO de prueba
    private SubastaDTO crearSubastaDTO(Long id, String nombre, Double precioInicial) {
        return SubastaDTO.builder()
                .id(id)
                .nombre(nombre)
                .descripcion("Descripción de prueba")
                .precioInicial(precioInicial)
                .precioActual(precioInicial)
                .aumentoMinimo(10.0)
                .fechaCreacion(new Date())
                .fechaCierre(new Date())
                .estado("ACTIVA")
                .userId(1L)
                .build();
    }
} 