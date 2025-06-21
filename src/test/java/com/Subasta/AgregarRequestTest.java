package com.Subasta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AgregarRequestTest {

    private AgregarRequest agregarRequest;
    private Date fechaCierre;

    @BeforeEach
    void setUp() {
        fechaCierre = new Date(System.currentTimeMillis() + 86400000); // +1 día
        
        agregarRequest = AgregarRequest.builder()
                .nombre("Subasta Test")
                .descripcion("Descripción de prueba")
                .precioInicial(100.0)
                .aumentoMinimo(10.0)
                .fechaCierre(fechaCierre)
                .build();
    }

    @Test
    void builder_DeberiaCrearRequestCorrectamente() {
        // Assert
        assertEquals("Subasta Test", agregarRequest.getNombre());
        assertEquals("Descripción de prueba", agregarRequest.getDescripcion());
        assertEquals(100.0, agregarRequest.getPrecioInicial());
        assertEquals(10.0, agregarRequest.getAumentoMinimo());
        assertEquals(fechaCierre, agregarRequest.getFechaCierre());
    }

    @Test
    void settersAndGetters_DeberiaFuncionarCorrectamente() {
        // Arrange
        AgregarRequest request = new AgregarRequest();

        // Act
        request.setNombre("Subasta Setter");
        request.setDescripcion("Descripción Setter");
        request.setPrecioInicial(200.0);
        request.setAumentoMinimo(20.0);
        request.setFechaCierre(fechaCierre);

        // Assert
        assertEquals("Subasta Setter", request.getNombre());
        assertEquals("Descripción Setter", request.getDescripcion());
        assertEquals(200.0, request.getPrecioInicial());
        assertEquals(20.0, request.getAumentoMinimo());
        assertEquals(fechaCierre, request.getFechaCierre());
    }

    @Test
    void constructorSinParametros_DeberiaCrearInstanciaVacia() {
        // Act
        AgregarRequest request = new AgregarRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getNombre());
        assertNull(request.getDescripcion());
        assertNull(request.getPrecioInicial());
        assertNull(request.getAumentoMinimo());
        assertNull(request.getFechaCierre());
    }

    @Test
    void constructorConParametros_DeberiaCrearInstanciaConValores() {
        // Act
        AgregarRequest request = new AgregarRequest(
                "Subasta Constructor",
                "Descripción Constructor",
                300.0,
                30.0,
                fechaCierre
        );

        // Assert
        assertEquals("Subasta Constructor", request.getNombre());
        assertEquals("Descripción Constructor", request.getDescripcion());
        assertEquals(300.0, request.getPrecioInicial());
        assertEquals(30.0, request.getAumentoMinimo());
        assertEquals(fechaCierre, request.getFechaCierre());
    }

    @Test
    void equals_ConRequestsIguales_DeberiaRetornarTrue() {
        // Arrange
        AgregarRequest request1 = AgregarRequest.builder()
                .nombre("Subasta Igual")
                .descripcion("Descripción igual")
                .precioInicial(100.0)
                .aumentoMinimo(10.0)
                .fechaCierre(fechaCierre)
                .build();

        AgregarRequest request2 = AgregarRequest.builder()
                .nombre("Subasta Igual")
                .descripcion("Descripción igual")
                .precioInicial(100.0)
                .aumentoMinimo(10.0)
                .fechaCierre(fechaCierre)
                .build();

        // Act & Assert
        assertEquals(request1, request2);
    }

    @Test
    void equals_ConRequestsDiferentes_DeberiaRetornarFalse() {
        // Arrange
        AgregarRequest request1 = AgregarRequest.builder()
                .nombre("Subasta 1")
                .precioInicial(100.0)
                .aumentoMinimo(10.0)
                .build();

        AgregarRequest request2 = AgregarRequest.builder()
                .nombre("Subasta 2")
                .precioInicial(200.0)
                .aumentoMinimo(20.0)
                .build();

        // Act & Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void hashCode_ConRequestsIguales_DeberiaRetornarMismoHashCode() {
        // Arrange
        AgregarRequest request1 = AgregarRequest.builder()
                .nombre("Subasta Hash")
                .precioInicial(100.0)
                .aumentoMinimo(10.0)
                .build();

        AgregarRequest request2 = AgregarRequest.builder()
                .nombre("Subasta Hash")
                .precioInicial(100.0)
                .aumentoMinimo(10.0)
                .build();

        // Act & Assert
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void toString_DeberiaContenerInformacionRelevante() {
        // Act
        String toString = agregarRequest.toString();

        // Assert
        assertTrue(toString.contains("nombre=Subasta Test"));
        assertTrue(toString.contains("precioInicial=100.0"));
        assertTrue(toString.contains("aumentoMinimo=10.0"));
    }

    @Test
    void validacionCamposObligatorios_DeberiaPermitirValoresValidos() {
        // Arrange & Act
        AgregarRequest request = AgregarRequest.builder()
                .nombre("Subasta Válida")
                .descripcion("Descripción válida")
                .precioInicial(50.0)
                .aumentoMinimo(5.0)
                .build();

        // Assert
        assertNotNull(request.getNombre());
        assertNotNull(request.getDescripcion());
        assertTrue(request.getPrecioInicial() > 0);
        assertTrue(request.getAumentoMinimo() > 0);
    }

    @Test
    void validacionPrecioInicial_DeberiaPermitirValoresPositivos() {
        // Arrange & Act
        AgregarRequest request = AgregarRequest.builder()
                .nombre("Subasta Precio")
                .precioInicial(0.01)
                .aumentoMinimo(0.01)
                .build();

        // Assert
        assertTrue(request.getPrecioInicial() > 0);
    }

    @Test
    void validacionAumentoMinimo_DeberiaPermitirValoresPositivos() {
        // Arrange & Act
        AgregarRequest request = AgregarRequest.builder()
                .nombre("Subasta Aumento")
                .precioInicial(100.0)
                .aumentoMinimo(0.01)
                .build();

        // Assert
        assertTrue(request.getAumentoMinimo() > 0);
    }
} 