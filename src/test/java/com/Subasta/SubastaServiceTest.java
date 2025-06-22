package com.Subasta;

import com.Subasta.DTOs.AgregarRequest;
import com.Subasta.DTOs.SubastaDTO;
import com.Subasta.DTOs.ChatRoomResponse;
import com.Subasta.Models.EstadoSubasta;
import com.Subasta.Models.Subasta;
import com.Subasta.Clients.ChatClient;
import com.Subasta.Clients.OfertaClient;
import com.Subasta.Clients.PedidoClient;
import com.Subasta.DTOs.OfertaDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SubastaServiceTest {

    @Autowired
    private SubastaService subastaService;

    @MockBean
    private ChatClient chatClient;

    @MockBean
    private OfertaClient ofertaClient;

    @MockBean
    private PedidoClient pedidoClient;

    private Subasta subasta;

    @BeforeEach
    void setUp() {
        // Configurar mocks
        ChatRoomResponse chatRoomResponse = new ChatRoomResponse();
        chatRoomResponse.setRoomId("chat-room-123");
        when(chatClient.crearSalaChat(anyString())).thenReturn(chatRoomResponse);
        
        // Mock para OfertaDTO
        OfertaDTO ofertaDTO = new OfertaDTO();
        ofertaDTO.setId(1L);
        ofertaDTO.setUserId(1L);
        ofertaDTO.setSubastaId(1L);
        ofertaDTO.setMonto(150.0);
        when(ofertaClient.obtenerMejorOfertaPorSubasta(anyLong())).thenReturn(ofertaDTO);
        
        when(pedidoClient.crearPedido(any())).thenReturn(null);

        // Crear subasta de prueba
        subasta = Subasta.builder()
                .id(1L)
                .nombre("Subasta Test")
                .descripcion("Descripción de prueba")
                .precioInicial(100.0)
                .precioActual(100.0)
                .aumentoMinimo(10.0)
                .estado(EstadoSubasta.ACTIVA)
                .user_id(1L)
                .fechaCreacion(new Date())
                .fechaCierre(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(2)))
                .build();
    }

    @Test
    void agregarSubasta_DeberiaCrearSubastaCorrectamente() {
        // Given
        AgregarRequest request = new AgregarRequest();
        request.setNombre("Nueva Subasta");
        request.setDescripcion("Descripción");
        request.setPrecioInicial(100.0);
        request.setAumentoMinimo(10.0);
        request.setFechaCierre(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));

        // When
        SubastaDTO result = subastaService.agregarSubasta(request, 1);

        // Then
        assertNotNull(result);
        assertEquals("Nueva Subasta", result.getNombre());
        assertEquals(100.0, result.getPrecioInicial());
        assertEquals("ACTIVA", result.getEstado());
    }

    @Test
    void obtenerRecomendacionesGenericas_DeberiaRetornarLista() {
        // Given - Crear algunas subastas en la base de datos
        AgregarRequest request1 = new AgregarRequest();
        request1.setNombre("Subasta 1");
        request1.setPrecioInicial(100.0);
        request1.setAumentoMinimo(10.0);
        subastaService.agregarSubasta(request1, 1);

        AgregarRequest request2 = new AgregarRequest();
        request2.setNombre("Subasta 2");
        request2.setPrecioInicial(200.0);
        request2.setAumentoMinimo(20.0);
        subastaService.agregarSubasta(request2, 2);

        // When
        List<SubastaDTO> result = subastaService.obtenerRecomendacionesGenericas();

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }

    @Test
    void obtenerSubastasPorUsuario_DeberiaRetornarLista() {
        // Given - Crear subasta para usuario específico
        AgregarRequest request = new AgregarRequest();
        request.setNombre("Subasta Usuario");
        request.setPrecioInicial(100.0);
        request.setAumentoMinimo(10.0);
        subastaService.agregarSubasta(request, 1);

        // When
        List<SubastaDTO> result = subastaService.obtenerSubastasPorUsuario(1);

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertEquals("Subasta Usuario", result.get(0).getNombre());
    }

    @Test
    void obtenerSubastaPorId_DeberiaRetornarSubasta() {
        // Given - Crear subasta
        AgregarRequest request = new AgregarRequest();
        request.setNombre("Subasta Por ID");
        request.setPrecioInicial(100.0);
        request.setAumentoMinimo(10.0);
        SubastaDTO subastaCreada = subastaService.agregarSubasta(request, 1);

        // When
        SubastaDTO result = subastaService.obtenerSubastaPorId(subastaCreada.getId().intValue());

        // Then
        assertNotNull(result);
        assertEquals("Subasta Por ID", result.getNombre());
    }

    @Test
    void obtenerSubastaPorId_SubastaNoEncontrada_DeberiaLanzarExcepcion() {
        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            subastaService.obtenerSubastaPorId(999);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void modificarSubasta_DeberiaModificarSubastaCorrectamente() {
        // Given - Crear subasta
        AgregarRequest request = new AgregarRequest();
        request.setNombre("Subasta Original");
        request.setPrecioInicial(100.0);
        request.setAumentoMinimo(10.0);
        SubastaDTO subastaCreada = subastaService.agregarSubasta(request, 1);

        // Modificar request
        AgregarRequest modificarRequest = new AgregarRequest();
        modificarRequest.setNombre("Subasta Modificada");
        modificarRequest.setPrecioInicial(150.0);

        // When
        SubastaDTO result = subastaService.modificarSubasta(subastaCreada.getId().intValue(), modificarRequest, 1);

        // Then
        assertNotNull(result);
        assertEquals("Subasta Modificada", result.getNombre());
        assertEquals(150.0, result.getPrecioInicial());
    }

    @Test
    void modificarSubasta_UsuarioNoAutorizado_DeberiaLanzarExcepcion() {
        // Given - Crear subasta
        AgregarRequest request = new AgregarRequest();
        request.setNombre("Subasta Original");
        request.setPrecioInicial(100.0);
        request.setAumentoMinimo(10.0);
        SubastaDTO subastaCreada = subastaService.agregarSubasta(request, 1);

        // Modificar request
        AgregarRequest modificarRequest = new AgregarRequest();
        modificarRequest.setNombre("Subasta No Autorizada");

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            subastaService.modificarSubasta(subastaCreada.getId().intValue(), modificarRequest, 999);
        });
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void cerrarSubasta_DeberiaCerrarSubastaCorrectamente() {
        // Given - Crear subasta
        AgregarRequest request = new AgregarRequest();
        request.setNombre("Subasta Para Cerrar");
        request.setPrecioInicial(100.0);
        request.setAumentoMinimo(10.0);
        SubastaDTO subastaCreada = subastaService.agregarSubasta(request, 1);

        // When
        SubastaDTO result = subastaService.cerrarSubasta(subastaCreada.getId().intValue(), 1);

        // Then
        assertNotNull(result);
        assertEquals("FINALIZADA", result.getEstado());
    }

    @Test
    void cerrarSubasta_SubastaNoEncontrada_DeberiaLanzarExcepcion() {
        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            subastaService.cerrarSubasta(999, 1);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void cancelarSubasta_DeberiaCancelarSubastaCorrectamente() {
        // Given - Crear subasta
        AgregarRequest request = new AgregarRequest();
        request.setNombre("Subasta Para Cancelar");
        request.setPrecioInicial(100.0);
        request.setAumentoMinimo(10.0);
        SubastaDTO subastaCreada = subastaService.agregarSubasta(request, 1);

        // When
        SubastaDTO result = subastaService.cancelarSubasta(subastaCreada.getId().intValue(), 1);

        // Then
        assertNotNull(result);
        assertEquals("CANCELADA", result.getEstado());
    }

    @Test
    void cancelarSubasta_SubastaYaFinalizada_DeberiaLanzarExcepcion() {
        // Given - Crear y cerrar subasta
        AgregarRequest request = new AgregarRequest();
        request.setNombre("Subasta Para Cancelar");
        request.setPrecioInicial(100.0);
        request.setAumentoMinimo(10.0);
        SubastaDTO subastaCreada = subastaService.agregarSubasta(request, 1);
        
        // Cerrar la subasta primero
        subastaService.cerrarSubasta(subastaCreada.getId().intValue(), 1);

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            subastaService.cancelarSubasta(subastaCreada.getId().intValue(), 1);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}