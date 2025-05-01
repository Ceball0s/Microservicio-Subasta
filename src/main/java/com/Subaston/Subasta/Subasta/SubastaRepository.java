package com.Subasta;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubastaRepository extends JpaRepository<Subasta, Long> {

    // Subastas genéricas
    List<Subasta> findTop10ByOrderByFechaCreacionDesc();

    // Subastas abiertas
    @Query("SELECT s FROM Subasta s WHERE s.fechaCierre > CURRENT_TIMESTAMP")
    List<Subasta> findSubastasAbiertas();

    // Buscar subasta por ID
    Optional<Subasta> findById(long id);

    // Buscar subastas por user_id
    @Query("SELECT s FROM Subasta s WHERE s.user_id = :userId")
    List<Subasta> findByUserId(@Param("userId") Long userId);
}