package com.Subasta.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.Subasta.Models.EstadoSubasta;
import com.Subasta.Models.Subasta;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubastaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precioInicial;
    private Double precioActual;
    private Double aumentoMinimo; // ✅ nuevo campo
    private Date fechaCreacion;
    private Date fechaCierre;
    private String estado;
    private Long userId;
    private String chatRoomId;


    public SubastaDTO(Subasta subasta) {
        this.id = subasta.getId();
        this.nombre = subasta.getNombre();
        this.descripcion = subasta.getDescripcion();
        this.precioInicial = subasta.getPrecioInicial();
        this.precioActual = subasta.getPrecioActual();
        this.aumentoMinimo = subasta.getAumentoMinimo(); // ✅ nuevo campo
        this.fechaCreacion = subasta.getFechaCreacion();
        this.fechaCierre = subasta.getFechaCierre();
        this.userId = subasta.getUser_id();
        this.chatRoomId = subasta.getChatRoomId();

        // O puedes usar: this.estado = subasta.getEstado().name();
        if (subasta.getEstado() == EstadoSubasta.ACTIVA) {
            this.estado = "ACTIVA";
        } else if (subasta.getEstado() == EstadoSubasta.FINALIZADA) {
            this.estado = "FINALIZADA";
        } else {
            this.estado = "CANCELADA";
        }
    }
}
