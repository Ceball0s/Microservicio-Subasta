package com.Subasta;

import com.Subasta.Models.EstadoSubasta;
import com.Subasta.Models.Subasta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.Subasta.Models.SubastaDTO;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class SubastaDTOTest {

    private Subasta subasta;
    private Date fechaCreacion;
    private Date fechaCierre;

    @BeforeEach
    void setUp() {
        fechaCreacion = new Date();
        fechaCierre = new Date(System.currentTimeMillis() + 86400000); // +1 día
        
        subasta = Subasta.builder()
                .id(1L)
                .nombre("Subasta Test")
                .descripcion("Descripción de prueba")
                .precioInicial(100.0)
                .precioActual(150.0)
                .aumentoMinimo(10.0)
                .fechaCreacion(fechaCreacion)
                .fechaCierre(fechaCierre)
                .estado(EstadoSubasta.ACTIVA)
                .user_id(1L)
                .build();
    }

    @Test
    void constructor_ConSubastaActiva_DeberiaMapearCorrectamente() {
        // Act
        SubastaDTO dto = new SubastaDTO(subasta);

        // Assert
        assertEquals(subasta.getId(), dto.getId());
        assertEquals(subasta.getNombre(), dto.getNombre());
        assertEquals(subasta.getDescripcion(), dto.getDescripcion());
        assertEquals(subasta.getPrecioInicial(), dto.getPrecioInicial());
        assertEquals(subasta.getPrecioActual(), dto.getPrecioActual());
        assertEquals(subasta.getAumentoMinimo(), dto.getAumentoMinimo());
        assertEquals(subasta.getFechaCreacion(), dto.getFechaCreacion());
        assertEquals(subasta.getFechaCierre(), dto.getFechaCierre());
        assertEquals(subasta.getUser_id(), dto.getUserId());
        assertEquals("ACTIVA", dto.getEstado());
    }

    @Test
    void constructor_ConSubastaFinalizada_DeberiaMapearEstadoCorrectamente() {
        // Arrange
        subasta.setEstado(EstadoSubasta.FINALIZADA);

        // Act
        SubastaDTO dto = new SubastaDTO(subasta);

        // Assert
        assertEquals("FINALIZADA", dto.getEstado());
    }

    @Test
    void constructor_ConSubastaCancelada_DeberiaMapearEstadoCorrectamente() {
        // Arrange
        subasta.setEstado(EstadoSubasta.CANCELADA);

        // Act
        SubastaDTO dto = new SubastaDTO(subasta);

        // Assert
        assertEquals("CANCELADA", dto.getEstado());
    }

    @Test
    void builder_DeberiaCrearDTOCorrectamente() {
        // Act
        SubastaDTO dto = SubastaDTO.builder()
                .id(1L)
                .nombre("Subasta Builder")
                .descripcion("Descripción Builder")
                .precioInicial(200.0)
                .precioActual(250.0)
                .aumentoMinimo(20.0)
                .fechaCreacion(fechaCreacion)
                .fechaCierre(fechaCierre)
                .estado("ACTIVA")
                .userId(1L)
                .build();

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("Subasta Builder", dto.getNombre());
        assertEquals("Descripción Builder", dto.getDescripcion());
        assertEquals(200.0, dto.getPrecioInicial());
        assertEquals(250.0, dto.getPrecioActual());
        assertEquals(20.0, dto.getAumentoMinimo());
        assertEquals(fechaCreacion, dto.getFechaCreacion());
        assertEquals(fechaCierre, dto.getFechaCierre());
        assertEquals("ACTIVA", dto.getEstado());
        assertEquals(1L, dto.getUserId());
    }

    @Test
    void settersAndGetters_DeberiaFuncionarCorrectamente() {
        // Arrange
        SubastaDTO dto = new SubastaDTO();

        // Act
        dto.setId(2L);
        dto.setNombre("Subasta Setter");
        dto.setDescripcion("Descripción Setter");
        dto.setPrecioInicial(300.0);
        dto.setPrecioActual(350.0);
        dto.setAumentoMinimo(30.0);
        dto.setFechaCreacion(fechaCreacion);
        dto.setFechaCierre(fechaCierre);
        dto.setEstado("FINALIZADA");
        dto.setUserId(2L);

        // Assert
        assertEquals(2L, dto.getId());
        assertEquals("Subasta Setter", dto.getNombre());
        assertEquals("Descripción Setter", dto.getDescripcion());
        assertEquals(300.0, dto.getPrecioInicial());
        assertEquals(350.0, dto.getPrecioActual());
        assertEquals(30.0, dto.getAumentoMinimo());
        assertEquals(fechaCreacion, dto.getFechaCreacion());
        assertEquals(fechaCierre, dto.getFechaCierre());
        assertEquals("FINALIZADA", dto.getEstado());
        assertEquals(2L, dto.getUserId());
    }

    @Test
    void equals_ConDTOsIguales_DeberiaRetornarTrue() {
        // Arrange
        SubastaDTO dto1 = new SubastaDTO(subasta);
        SubastaDTO dto2 = new SubastaDTO(subasta);

        // Act & Assert
        assertEquals(dto1, dto2);
    }

    @Test
    void equals_ConDTOsDiferentes_DeberiaRetornarFalse() {
        // Arrange
        SubastaDTO dto1 = new SubastaDTO(subasta);
        
        Subasta subasta2 = Subasta.builder()
                .id(2L)
                .nombre("Subasta Diferente")
                .descripcion("Descripción diferente")
                .precioInicial(200.0)
                .precioActual(200.0)
                .aumentoMinimo(20.0)
                .fechaCreacion(fechaCreacion)
                .fechaCierre(fechaCierre)
                .estado(EstadoSubasta.ACTIVA)
                .user_id(2L)
                .build();
        SubastaDTO dto2 = new SubastaDTO(subasta2);

        // Act & Assert
        assertNotEquals(dto1, dto2);
    }

    @Test
    void hashCode_ConDTOsIguales_DeberiaRetornarMismoHashCode() {
        // Arrange
        SubastaDTO dto1 = new SubastaDTO(subasta);
        SubastaDTO dto2 = new SubastaDTO(subasta);

        // Act & Assert
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_DeberiaContenerInformacionRelevante() {
        // Act
        SubastaDTO dto = new SubastaDTO(subasta);
        String toString = dto.toString();

        // Assert
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("nombre=Subasta Test"));
        assertTrue(toString.contains("precioInicial=100.0"));
        assertTrue(toString.contains("estado=ACTIVA"));
    }
} 