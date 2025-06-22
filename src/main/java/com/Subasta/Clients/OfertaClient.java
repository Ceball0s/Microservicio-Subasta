package com.Subasta.Clients;

import com.Subasta.DTOs.OfertaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "oferta")
public interface OfertaClient {

    @GetMapping("/subasta/mejor/{subastaId}")
    OfertaDTO obtenerMejorOfertaPorSubasta(@PathVariable("subastaId") Long subastaId);
}
