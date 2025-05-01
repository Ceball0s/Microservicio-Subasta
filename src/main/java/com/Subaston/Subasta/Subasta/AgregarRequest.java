package com.Subasta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import com.Subasta.Subasta.EstadoSubasta;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgregarRequest {
    private String nombre;
    private String descripcion;
    private Double precioInicial;
    private Date fechaCierre;
    private Double precioActual;
    private EstadoSubasta estado;
}