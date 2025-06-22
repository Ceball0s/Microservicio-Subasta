package com.Subasta;

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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubastaServiceTest {

    @Mock
    private SubastaRepository subastaRepository;

    @InjectMocks
    private SubastaService subastaService;

    private Subasta subasta;
    private AgregarRequest agregarRequest;
    private final int USER_ID = 1;
    private final int OTHER_USER_ID = 2;
    private final int SUBASTA_ID = 1;

    @BeforeEach
    void setUp() {
        subasta = Subasta.builder()
                .id((long) SUBASTA_ID)
                .nombre("Subasta de prueba")
                .descripcion("Descripción de la subasta")
                .precioInicial(100.0)
                .precioActual(100.0)
                .aumentoMinimo(10.0)
                .fechaCreacion(new Date())
                .fechaCierre(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)))
                .estado(EstadoSubasta.ACTIVA)
                .user_id((long) USER_ID)
                .build();

        agregarRequest = AgregarRequest.builder()
                .nombre("Nueva Subasta")
                .descripcion("Descripción de prueba")
                .precioInicial(150.0)
                .aumentoMinimo(15.0)
                .fechaCierre(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(2)))
                .build();
    }

    @Test
    void obtenerRecomendacionesGenericas_DeberiaRetornarListaDTO() {
        when(subastaRepository.findTop10ByOrderByFechaCreacionDesc()).thenReturn(Arrays.asList(subasta));
        List<SubastaDTO> resultado = subastaService.obtenerRecomendacionesGenericas();
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Subasta de prueba", resultado.get(0).getNombre());
    }

    @Test
    void agregarSubasta_ConDatosValidos_DeberiaRetornarDTO() {
        when(subastaRepository.save(any(Subasta.class))).thenAnswer(invocation -> {
            Subasta s = invocation.getArgument(0);
            s.setId(2L); // Simular que la BD asigna un ID
            return s;
        });
        SubastaDTO resultado = subastaService.agregarSubasta(agregarRequest, USER_ID);
        assertNotNull(resultado);
        assertEquals("Nueva Subasta", resultado.getNombre());
        verify(subastaRepository).save(any(Subasta.class));
    }

    @Test
    void agregarSubasta_ConFechaCierreNula_DeberiaAsignarFechaPorDefecto() {
        agregarRequest.setFechaCierre(null);
        when(subastaRepository.save(any(Subasta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SubastaDTO resultado = subastaService.agregarSubasta(agregarRequest, USER_ID);

        assertNotNull(resultado);
        assertNotNull(resultado.getFechaCierre());
        long ahora = System.currentTimeMillis();
        long sieteDias = TimeUnit.DAYS.toMillis(7);
        long fechaCierre = resultado.getFechaCierre().getTime();
        assertTrue(Math.abs((ahora + sieteDias) - fechaCierre) < 1000, "La fecha de cierre debe ser ~7 días en el futuro");
        verify(subastaRepository).save(any(Subasta.class));
    }

    @Test
    void modificarSubasta_ConDatosValidos_DeberiaModificarSubasta() {
        when(subastaRepository.findById((long) SUBASTA_ID)).thenReturn(Optional.of(subasta));
        when(subastaRepository.save(any(Subasta.class))).thenReturn(subasta);

        SubastaDTO resultado = subastaService.modificarSubasta(SUBASTA_ID, agregarRequest, USER_ID);

        assertNotNull(resultado);
        verify(subastaRepository).save(any(Subasta.class));
    }

    @Test
    void modificarSubasta_ConSubastaNoEncontrada_DeberiaLanzarExcepcion() {
        when(subastaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
            subastaService.modificarSubasta(999, agregarRequest, USER_ID);
        });
    }

    @Test
    void modificarSubasta_ConUsuarioNoAutorizado_DeberiaLanzarExcepcion() {
        when(subastaRepository.findById((long) SUBASTA_ID)).thenReturn(Optional.of(subasta));
        assertThrows(ResponseStatusException.class, () -> {
            subastaService.modificarSubasta(SUBASTA_ID, agregarRequest, OTHER_USER_ID);
        });
    }

    @Test
    void modificarSubasta_ConPrecioInicialMayorAlActual_DeberiaActualizarPrecioActual() {
        subasta.setPrecioActual(120.0);
        agregarRequest.setPrecioInicial(200.0);
        when(subastaRepository.findById((long) SUBASTA_ID)).thenReturn(Optional.of(subasta));
        when(subastaRepository.save(any(Subasta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SubastaDTO resultado = subastaService.modificarSubasta(SUBASTA_ID, agregarRequest, USER_ID);

        assertEquals(200.0, resultado.getPrecioInicial());
        assertEquals(200.0, resultado.getPrecioActual());
        verify(subastaRepository).save(argThat(s -> s.getPrecioActual() == 200.0));
    }

    @Test
    void modificarSubasta_SoloConAlgunosCampos_DeberiaModificarSoloEsosCampos() {
        AgregarRequest requestParcial = AgregarRequest.builder().nombre("Nombre Modificado").build();
        when(subastaRepository.findById((long) SUBASTA_ID)).thenReturn(Optional.of(subasta));
        when(subastaRepository.save(any(Subasta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SubastaDTO resultado = subastaService.modificarSubasta(SUBASTA_ID, requestParcial, USER_ID);

        assertEquals("Nombre Modificado", resultado.getNombre());
        assertEquals("Descripción de la subasta", resultado.getDescripcion()); // No debe cambiar
        assertEquals(100.0, resultado.getPrecioInicial()); // No debe cambiar
    }
    
    @Test
    void cerrarSubasta_ConSubastaActiva_DeberiaCerrarSubasta() {
        when(subastaRepository.findById((long) SUBASTA_ID)).thenReturn(Optional.of(subasta));
        when(subastaRepository.save(any(Subasta.class))).thenAnswer(invocation -> invocation.getArgument(0));
        SubastaDTO resultado = subastaService.cerrarSubasta(SUBASTA_ID, USER_ID);
        assertEquals(EstadoSubasta.FINALIZADA.toString(), resultado.getEstado());
    }

    @Test
    void cerrarSubasta_ConSubastaNoActiva_DeberiaLanzarExcepcion() {
        subasta.setEstado(EstadoSubasta.FINALIZADA);
        when(subastaRepository.findById((long) SUBASTA_ID)).thenReturn(Optional.of(subasta));
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> subastaService.cerrarSubasta(SUBASTA_ID, USER_ID));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    }

    @Test
    void cancelarSubasta_ConSubastaNoActiva_DeberiaLanzarExcepcion() {
        subasta.setEstado(EstadoSubasta.FINALIZADA);
        when(subastaRepository.findById((long) SUBASTA_ID)).thenReturn(Optional.of(subasta));
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> subastaService.cancelarSubasta(SUBASTA_ID, USER_ID));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    }

    @Test
    void obtenerSubastasPorUsuario_DeberiaRetornarListaDeSubastas() {
        when(subastaRepository.findByUserId((long) USER_ID)).thenReturn(Arrays.asList(subasta));
        List<SubastaDTO> resultado = subastaService.obtenerSubastasPorUsuario(USER_ID);
        assertEquals(1, resultado.size());
        assertEquals(subasta.getId(), resultado.get(0).getId());
    }

    @Test
    void obtenerSubastaPorId_ConIdValido_DeberiaRetornarSubasta() {
        when(subastaRepository.findById((long) SUBASTA_ID)).thenReturn(Optional.of(subasta));
        SubastaDTO resultado = subastaService.obtenerSubastaPorId(SUBASTA_ID);
        assertNotNull(resultado);
        assertEquals(subasta.getId(), resultado.getId());
    }

    @Test
    void modificarSubasta_CuandoNoEncuentra_DeberiaLanzarExcepcion() {
        when(subastaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
            subastaService.modificarSubasta(1, agregarRequest, USER_ID);
        });
    }

    @Test
    void modificarSubasta_CuandoUsuarioNoAutorizado_DeberiaLanzarExcepcion() {
        when(subastaRepository.findById(anyLong())).thenReturn(Optional.of(subasta));
        int otroUsuarioId = 999;
        assertThrows(ResponseStatusException.class, () -> {
            subastaService.modificarSubasta(1, agregarRequest, otroUsuarioId);
        });
    }

    @Test
    void modificarSubasta_ConPrecioActualMenorAlInicial_DeberiaActualizarlo() {
        // Arrange
        subasta.setPrecioActual(50.0); // Precio actual por debajo del inicial (100.0)
        when(subastaRepository.findById(anyLong())).thenReturn(Optional.of(subasta));
        when(subastaRepository.save(any(Subasta.class))).thenAnswer(i -> i.getArgument(0));
        
        AgregarRequest request = AgregarRequest.builder()
            .precioInicial(150.0) // Nuevo precio inicial más alto
            .build();

        // Act
        SubastaDTO resultado = subastaService.modificarSubasta(1, request, USER_ID);

        // Assert
        assertEquals(150.0, resultado.getPrecioInicial());
        assertEquals(150.0, resultado.getPrecioActual()); // El precio actual debe igualar al nuevo precio inicial
    }

    @Test
    void cerrarSubasta_CuandoNoActiva_DeberiaLanzarExcepcion() {
        subasta.setEstado(EstadoSubasta.FINALIZADA);
        when(subastaRepository.findById(anyLong())).thenReturn(Optional.of(subasta));
        assertThrows(ResponseStatusException.class, () -> {
            subastaService.cerrarSubasta(1, USER_ID);
        });
    }

    @Test
    void cancelarSubasta_CuandoNoActiva_DeberiaLanzarExcepcion() {
        subasta.setEstado(EstadoSubasta.CANCELADA);
        when(subastaRepository.findById(anyLong())).thenReturn(Optional.of(subasta));
        assertThrows(ResponseStatusException.class, () -> {
            subastaService.cancelarSubasta(1, USER_ID);
        });
    }

    @Test
    void obtenerSubastaPorId_CuandoNoEncuentra_DeberiaLanzarExcepcion() {
        when(subastaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
            subastaService.obtenerSubastaPorId(1);
        });
    }
}