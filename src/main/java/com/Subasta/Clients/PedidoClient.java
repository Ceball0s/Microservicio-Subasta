package com.Subasta.Clients;

//public package com.Subasta.Clients;

import com.Subasta.DTOs.CrearPedidoRequest;
import com.Subasta.DTOs.PedidoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Pedido_Rastreo")
public interface PedidoClient {

    @PostMapping("/crear")
    PedidoResponse crearPedido(@RequestBody CrearPedidoRequest request);
}

