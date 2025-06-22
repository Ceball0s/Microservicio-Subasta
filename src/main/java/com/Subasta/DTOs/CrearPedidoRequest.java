package com.Subasta.DTOs;

import lombok.Data;

@Data
public class CrearPedidoRequest {
    private Long idComprador;
    private Long idVendedor;
    private String nombreProducto;
    private String descripcionProducto;
    private Double precioFinal;
}
