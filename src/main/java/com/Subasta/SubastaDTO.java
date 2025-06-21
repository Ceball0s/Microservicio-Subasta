package com.Subasta;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.Subasta.Models.Subasta;
import com.Subasta.Models.EstadoSubasta;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date fechaCreacion;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date fechaCierre;
    private String estado;
    private Long userId;

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
