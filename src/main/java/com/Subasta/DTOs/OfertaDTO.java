package com.Subasta.DTOs;

import lombok.Data;

@Data
public class OfertaDTO {
    private Long id;
    private Long subastaId;
    private Long userId;
    private Double monto;
    private String fecha; // si lo necesitas, o usa LocalDateTime
}
