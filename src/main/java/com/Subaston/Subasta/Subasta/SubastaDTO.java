package com.Subasta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

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
    private Date fechaCreacion;
    private Date fechaCierre;
    private String estado;
    private Long userId;

    public SubastaDTO(Subasta subasta) {
        this.id = subasta.getId();
        this.nombre = subasta.getNombre();
        this.descripcion = subasta.getDescripcion();
        this.precioInicial = subasta.getPrecioInicial();
        this.precioActual = subasta.getPrecioActual();
        this.fechaCreacion = subasta.getFechaCreacion();
        this.fechaCierre = subasta.getFechaCierre();
        this.userId = subasta.getUser_id();

        if (subasta.getEstado() == Subasta.EstadoSubasta.ACTIVA) {
            this.estado = "ACTIVA";
        } else if (subasta.getEstado() == Subasta.EstadoSubasta.FINALIZADA) {
            this.estado = "FINALIZADA";
        } else {
            this.estado = "CANCELADA";
        }
    }
}