package com.Subasta;

import com.Subasta.Models.EstadoSubasta;
import com.Subasta.Models.Subasta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SubastaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubastaRepository subastaRepository;

    private Subasta subasta1;
    private Subasta subasta2;
    private Subasta subasta3;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada prueba
        entityManager.clear();

        // Crear subastas de prueba
        subasta1 = Subasta.builder()
                .nombre("Subasta 1")
                .descripcion("Descripción 1")
                .precioInicial(100.0)
                .precioActual(100.0)
                .aumentoMinimo(10.0)
                .fechaCreacion(new Date())
                .fechaCierre(new Date())
                .estado(EstadoSubasta.ACTIVA)
                .user_id(1L)
                .build();

        subasta2 = Subasta.builder()
                .nombre("Subasta 2")
                .descripcion("Descripción 2")
                .precioInicial(200.0)
                .precioActual(200.0)
                .aumentoMinimo(20.0)
                .fechaCreacion(new Date())
                .fechaCierre(new Date())
                .estado(EstadoSubasta.ACTIVA)
                .user_id(2L)
                .build();

        subasta3 = Subasta.builder()
                .nombre("Subasta 3")
                .descripcion("Descripción 3")
                .precioInicial(300.0)
                .precioActual(300.0)
                .aumentoMinimo(30.0)
                .fechaCreacion(new Date())
                .fechaCierre(new Date())
                .estado(EstadoSubasta.FINALIZADA)
                .user_id(1L)
                .build();
    }

    @Test
    void save_DeberiaGuardarSubasta() {
        // Act
        Subasta subastaGuardada = subastaRepository.save(subasta1);

        // Assert
        assertNotNull(subastaGuardada.getId());
        assertEquals(subasta1.getNombre(), subastaGuardada.getNombre());
        assertEquals(subasta1.getPrecioInicial(), subastaGuardada.getPrecioInicial());
        assertEquals(EstadoSubasta.ACTIVA, subastaGuardada.getEstado());
        assertEquals(subasta1.getPrecioInicial(), subastaGuardada.getPrecioActual());
    }

    @Test
    void findById_ConIdExistente_DeberiaRetornarSubasta() {
        // Arrange
        Subasta subastaGuardada = entityManager.persistAndFlush(subasta1);

        // Act
        Optional<Subasta> resultado = subastaRepository.findById(subastaGuardada.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(subasta1.getNombre(), resultado.get().getNombre());
        assertEquals(subasta1.getPrecioInicial(), resultado.get().getPrecioInicial());
    }

    @Test
    void findById_ConIdInexistente_DeberiaRetornarEmpty() {
        // Act
        Optional<Subasta> resultado = subastaRepository.findById(999L);

        // Assert
        assertFalse(resultado.isPresent());
    }

    @Test
    void findTop10ByOrderByFechaCreacionDesc_DeberiaRetornarSubastasOrdenadas() {
        // Arrange
        entityManager.persistAndFlush(subasta1);
        entityManager.persistAndFlush(subasta2);
        entityManager.persistAndFlush(subasta3);

        // Act
        List<Subasta> resultado = subastaRepository.findTop10ByOrderByFechaCreacionDesc();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() >= 3);
        // Verificar que están ordenadas por fecha de creación descendente
        for (int i = 0; i < resultado.size() - 1; i++) {
            assertTrue(resultado.get(i).getFechaCreacion().compareTo(resultado.get(i + 1).getFechaCreacion()) >= 0);
        }
    }

    @Test
    void findByUserId_ConUsuarioExistente_DeberiaRetornarSubastasDelUsuario() {
        // Arrange
        entityManager.persistAndFlush(subasta1); // user_id = 1L
        entityManager.persistAndFlush(subasta2); // user_id = 2L
        entityManager.persistAndFlush(subasta3); // user_id = 1L

        // Act
        List<Subasta> resultado = subastaRepository.findByUserId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        resultado.forEach(subasta -> assertEquals(1L, subasta.getUser_id()));
    }

    @Test
    void findByUserId_ConUsuarioInexistente_DeberiaRetornarListaVacia() {
        // Act
        List<Subasta> resultado = subastaRepository.findByUserId(999L);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void save_ConSubastaModificada_DeberiaActualizarSubasta() {
        // Arrange
        Subasta subastaGuardada = entityManager.persistAndFlush(subasta1);
        String nuevoNombre = "Subasta Modificada";
        subastaGuardada.setNombre(nuevoNombre);

        // Act
        Subasta subastaActualizada = subastaRepository.save(subastaGuardada);

        // Assert
        assertEquals(nuevoNombre, subastaActualizada.getNombre());
        assertEquals(subastaGuardada.getId(), subastaActualizada.getId());
    }

    @Test
    void save_DeberiaInicializarCamposPorDefecto() {
        // Act
        Subasta subastaGuardada = subastaRepository.save(subasta1);

        // Assert
        assertEquals(EstadoSubasta.ACTIVA, subastaGuardada.getEstado());
        assertEquals(subasta1.getPrecioInicial(), subastaGuardada.getPrecioActual());
    }

    @Test
    void findAll_DeberiaRetornarTodasLasSubastas() {
        // Arrange
        entityManager.persistAndFlush(subasta1);
        entityManager.persistAndFlush(subasta2);
        entityManager.persistAndFlush(subasta3);

        // Act
        List<Subasta> resultado = subastaRepository.findAll();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() >= 3);
    }

    @Test
    void delete_DeberiaEliminarSubasta() {
        // Arrange
        Subasta subastaGuardada = entityManager.persistAndFlush(subasta1);

        // Act
        subastaRepository.delete(subastaGuardada);
        entityManager.flush();

        // Assert
        Optional<Subasta> subastaEliminada = subastaRepository.findById(subastaGuardada.getId());
        assertFalse(subastaEliminada.isPresent());
    }
} 