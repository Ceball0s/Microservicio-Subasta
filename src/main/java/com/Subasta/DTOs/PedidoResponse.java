package com.Subasta.DTOs;

import lombok.Data;
import java.util.Date;

@Data
public class PedidoResponse {
    private String trackingId;
    private Long idComprador;
    private Long idVendedor;
    private String productoNombre;
    private String productoDescripcion;
    private Double precioFinal;
    private Date fechaCreacion;
    private String estadoRastreo;
}
