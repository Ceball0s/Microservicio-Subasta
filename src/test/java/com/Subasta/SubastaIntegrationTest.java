package com.Subasta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.Subasta.DTOs.AgregarRequest;
import com.Subasta.DTOs.SubastaDTO;
import com.Subasta.DTOs.ChatRoomResponse;
import com.Subasta.Models.EstadoSubasta;
import com.Subasta.Models.Subasta;
import com.Subasta.Clients.ChatClient;
import com.Subasta.Clients.OfertaClient;
import com.Subasta.Clients.PedidoClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubastaController.class)
@ActiveProfiles("test")
class SubastaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatClient chatClient;

    @MockBean
    private OfertaClient ofertaClient;

    @MockBean
    private PedidoClient pedidoClient;

    @MockBean
    private SubastaService subastaService;

    @MockBean
    private SubastaRepository subastaRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Configurar mocks básicos
        ChatRoomResponse chatRoomResponse = new ChatRoomResponse();
        chatRoomResponse.setRoomId("chat-room-123");
        when(chatClient.crearSalaChat(anyString())).thenReturn(chatRoomResponse);
        when(ofertaClient.obtenerMejorOfertaPorSubasta(anyLong())).thenReturn(null);
        when(pedidoClient.crearPedido(any())).thenReturn(null);
    }

    @Test
    void flujoCompletoSubasta_DeberiaFuncionarCorrectamente() throws Exception {
        // Mock del servicio para agregar subasta
        SubastaDTO subastaCreada = new SubastaDTO();
        subastaCreada.setId(1L);
        subastaCreada.setNombre("Subasta Integración");
        subastaCreada.setDescripcion("Descripción de integración");
        subastaCreada.setPrecioInicial(100.0);
        subastaCreada.setPrecioActual(100.0);
        subastaCreada.setEstado("ACTIVA");
        
        when(subastaService.agregarSubasta(any(AgregarRequest.class), anyInt())).thenReturn(subastaCreada);

        AgregarRequest request = new AgregarRequest();
        request.setNombre("Subasta Integración");
        request.setDescripcion("Descripción de integración");
        request.setPrecioInicial(100.0);
        request.setAumentoMinimo(10.0);
        request.setFechaCierre(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));

        mockMvc.perform(post("/api/subasta/agregar")
                .contentType(MediaType.APPLICATION_JSON)
                .header("user-id", "1")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Subasta Integración"));
    }

    @Test
    void consultarSubasta_SubastaNoEncontrada_DeberiaRetornarNotFound() throws Exception {
        when(subastaService.obtenerSubastaPorId(999)).thenThrow(
            new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Subasta no encontrada")
        );

        mockMvc.perform(get("/api/subasta/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cerrarSubasta_SubastaNoEncontrada_DeberiaRetornarNotFound() throws Exception {
        when(subastaService.cerrarSubasta(999, 1)).thenThrow(
            new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Subasta no encontrada")
        );

        mockMvc.perform(put("/api/subasta/finalizar/999")
                .header("user-id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelarSubasta_DeberiaFuncionarCorrectamente() throws Exception {
        SubastaDTO subastaCancelada = new SubastaDTO();
        subastaCancelada.setId(1L);
        subastaCancelada.setNombre("Subasta Test");
        subastaCancelada.setEstado("CANCELADA");
        
        when(subastaService.cancelarSubasta(1, 1)).thenReturn(subastaCancelada);

        mockMvc.perform(put("/api/subasta/cancelar/1")
                .header("user-id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    void cancelarSubasta_SubastaYaFinalizada_DeberiaRetornarBadRequest() throws Exception {
        when(subastaService.cancelarSubasta(1, 1)).thenThrow(
            new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "La subasta no está activa y no se puede cancelar")
        );

        mockMvc.perform(put("/api/subasta/cancelar/1")
                .header("user-id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void modificarSubasta_UsuarioNoAutorizado_DeberiaRetornarForbidden() throws Exception {
        when(subastaService.modificarSubasta(eq(1), any(AgregarRequest.class), eq(2))).thenThrow(
            new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "No tienes permisos para modificar esta subasta")
        );

        AgregarRequest request = new AgregarRequest();
        request.setNombre("Subasta No Autorizada");
        request.setPrecioInicial(100.0);
        request.setAumentoMinimo(10.0);

        mockMvc.perform(put("/api/subasta/modificar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("user-id", "2")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void agregarSubasta_SinUserId_DeberiaRetornarForbidden() throws Exception {
        AgregarRequest request = new AgregarRequest();
        request.setNombre("Subasta Sin Usuario");
        request.setPrecioInicial(100.0);
        request.setAumentoMinimo(10.0);

        mockMvc.perform(post("/api/subasta/agregar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerRecomendaciones_DeberiaRetornarSubastas() throws Exception {
        List<SubastaDTO> subastas = Arrays.asList(
            crearSubastaDTO(1L, "Subasta Test 1"),
            crearSubastaDTO(2L, "Subasta Test 2"),
            crearSubastaDTO(3L, "Subasta Test 3")
        );
        
        when(subastaService.obtenerRecomendacionesGenericas()).thenReturn(subastas);

        mockMvc.perform(get("/api/subasta/recomendaciones")
                .header("user-id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].nombre").value("Subasta Test 1"));
    }

    private SubastaDTO crearSubastaDTO(Long id, String nombre) {
        SubastaDTO dto = new SubastaDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setDescripcion("Descripción " + id);
        dto.setPrecioInicial(100.0 * id);
        dto.setPrecioActual(100.0 * id);
        dto.setEstado("ACTIVA");
        return dto;
    }
} 