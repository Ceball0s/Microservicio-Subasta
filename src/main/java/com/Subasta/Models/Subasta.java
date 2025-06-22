package com.Subasta.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subasta", schema = "public")
public class Subasta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSubasta estado;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private Double precioInicial;

    @Column()
    private Double precioActual;

    @Column(name = "aumento_minimo", nullable = false)
    private Double aumentoMinimo;  // <<-- Nuevo atributo

    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "fecha_cierre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCierre;

    @Column(name = "user_id", nullable = false)
    private Long user_id; // Identificador del usuario propietario de la subasta

    @Column(name = "chat_room_id")
    private String chatRoomId;

    @PrePersist
    public void initializeFields() {
        this.estado = EstadoSubasta.ACTIVA; // Estado inicial de la subasta
        this.precioActual = this.precioInicial; // Inicializa precioActual igual al precioInicial
    }

    // Constructor personalizado sin chatRoomId
    public Subasta(Long id, String nombre, EstadoSubasta estado, String descripcion,
                Double precioInicial, Double precioActual, Double aumentoMinimo,
                Date fechaCreacion, Date fechaCierre, Long user_id) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.descripcion = descripcion;
        this.precioInicial = precioInicial;
        this.precioActual = precioActual;
        this.aumentoMinimo = aumentoMinimo;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
        this.user_id = user_id;
    }
}
