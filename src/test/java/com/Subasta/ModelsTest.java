package com.Subasta;

import com.Subasta.Models.EstadoSubasta;
import com.Subasta.Models.Subasta;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class ModelsTest {

    @Test
    void testSubastaModelCompleto() {
        Date ahora = new Date();
        Date manana = new Date(ahora.getTime() + 86400000);

        // Crear dos instancias para probar equals y hashCode
        Subasta subasta1 = Subasta.builder()
                .id(1L)
                .nombre("Test Subasta")
                .descripcion("Descripción de prueba")
                .precioInicial(100.0)
                .precioActual(120.0)
                .aumentoMinimo(10.0)
                .fechaCreacion(ahora)
                .fechaCierre(manana)
                .estado(EstadoSubasta.ACTIVA)
                .user_id(1L)
                .build();

        Subasta subasta2 = Subasta.builder()
                .id(1L)
                .nombre("Test Subasta")
                .descripcion("Descripción de prueba")
                .precioInicial(100.0)
                .precioActual(120.0)
                .aumentoMinimo(10.0)
                .fechaCreacion(ahora)
                .fechaCierre(manana)
                .estado(EstadoSubasta.ACTIVA)
                .user_id(1L)
                .build();
        
        Subasta subasta3 = new Subasta(3L, "Otra Subasta", EstadoSubasta.FINALIZADA, "Otra Desc", 200.0, 200.0, 20.0, ahora, manana, 2L);

        // Probar getters
        assertEquals(1L, subasta1.getId());
        assertEquals("Test Subasta", subasta1.getNombre());
        assertEquals("Descripción de prueba", subasta1.getDescripcion());
        assertEquals(100.0, subasta1.getPrecioInicial());
        assertEquals(120.0, subasta1.getPrecioActual());
        assertEquals(10.0, subasta1.getAumentoMinimo());
        assertEquals(ahora, subasta1.getFechaCreacion());
        assertEquals(manana, subasta1.getFechaCierre());
        assertEquals(EstadoSubasta.ACTIVA, subasta1.getEstado());
        assertEquals(1L, subasta1.getUser_id());

        // Probar setters
        subasta3.setId(4L);
        assertEquals(4L, subasta3.getId());
        subasta3.setNombre("Nombre Cambiado");
        assertEquals("Nombre Cambiado", subasta3.getNombre());
        subasta3.setDescripcion("Desc Cambiada");
        assertEquals("Desc Cambiada", subasta3.getDescripcion());
        subasta3.setPrecioInicial(250.0);
        assertEquals(250.0, subasta3.getPrecioInicial());
        subasta3.setPrecioActual(260.0);
        assertEquals(260.0, subasta3.getPrecioActual());
        subasta3.setAumentoMinimo(25.0);
        assertEquals(25.0, subasta3.getAumentoMinimo());
        subasta3.setFechaCreacion(manana);
        assertEquals(manana, subasta3.getFechaCreacion());
        subasta3.setFechaCierre(ahora);
        assertEquals(ahora, subasta3.getFechaCierre());
        subasta3.setEstado(EstadoSubasta.CANCELADA);
        assertEquals(EstadoSubasta.CANCELADA, subasta3.getEstado());
        subasta3.setUser_id(3L);
        assertEquals(3L, subasta3.getUser_id());

        // Probar equals, hashCode y toString
        assertEquals(subasta1, subasta2);
        assertNotEquals(subasta1, subasta3);
        assertEquals(subasta1.hashCode(), subasta2.hashCode());
        assertNotEquals(subasta1.hashCode(), subasta3.hashCode());
        assertNotNull(subasta1.toString());
        
        // Probar constructores
        Subasta subastaVacia = new Subasta();
        assertNotNull(subastaVacia);
    }

    @Test
    void testEstadoSubastaEnum() {
        assertEquals("ACTIVA", EstadoSubasta.ACTIVA.name());
        assertEquals("FINALIZADA", EstadoSubasta.FINALIZADA.name());
        assertEquals("CANCELADA", EstadoSubasta.CANCELADA.name());
        assertEquals(3, EstadoSubasta.values().length);
    }
    
    @Test
    void testDTOsCompleto() {
        Date ahora = new Date();
        Subasta subasta = new Subasta(1L, "Original", EstadoSubasta.ACTIVA, "Desc", 100.0, 120.0, 10.0, ahora, ahora, 1L);

        // Test SubastaDTO
        SubastaDTO dto1 = new SubastaDTO(subasta);
        dto1.setPrecioActual(130.0); // Probar setter
        
        SubastaDTO dto2 = new SubastaDTO(subasta);

        SubastaDTO dto3 = SubastaDTO.builder().id(3L).nombre("Diferente").build();

        assertEquals(130.0, dto1.getPrecioActual());
        assertEquals(dto1.getId(), dto2.getId()); // Probar getter
        assertNotEquals(dto1, dto2); // No son iguales por el cambio de precio
        assertEquals(dto1.hashCode(), dto1.hashCode()); // consistencia
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertNotNull(dto1.toString());

        // Test AgregarRequest
        AgregarRequest req1 = new AgregarRequest("Req1", "Desc1", 1.0, 1.0, ahora);
        AgregarRequest req2 = new AgregarRequest("Req1", "Desc1", 1.0, 1.0, ahora);
        AgregarRequest req3 = new AgregarRequest("Req3", "Desc3", 3.0, 3.0, ahora);

        assertEquals("Req1", req1.getNombre()); // getter
        req1.setNombre("Req1 Modificado");
        assertEquals("Req1 Modificado", req1.getNombre()); // setter

        assertNotEquals(req1, req2); // Son diferentes por el nombre modificado
        assertEquals(req2, req2); // reflexivo
        assertEquals(req2.hashCode(), req2.hashCode());
        assertNotNull(req1.toString());
    }
} 