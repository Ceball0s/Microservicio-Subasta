package com.Subasta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgregarRequest {
    private String nombre;
    private String descripcion;
    private Double precioInicial;
    private Double aumentoMinimo; // ✅ Campo necesario para la lógica de ofertas
    private Date fechaCierre;
}
