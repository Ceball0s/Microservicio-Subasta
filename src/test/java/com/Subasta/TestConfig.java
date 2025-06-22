package com.Subasta;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.Subasta.Clients.ChatClient;
import com.Subasta.Clients.OfertaClient;
import com.Subasta.Clients.PedidoClient;
import com.Subasta.DTOs.ChatRoomResponse;
import com.Subasta.DTOs.OfertaDTO;
import com.Subasta.DTOs.PedidoResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public ChatClient chatClient() {
        ChatClient mockChatClient = mock(ChatClient.class);
        ChatRoomResponse mockResponse = new ChatRoomResponse();
        mockResponse.setRoomId("test-room-id");
        when(mockChatClient.crearSalaChat(anyString())).thenReturn(mockResponse);
        return mockChatClient;
    }

    @Bean
    @Primary
    public OfertaClient ofertaClient() {
        OfertaClient mockOfertaClient = mock(OfertaClient.class);
        OfertaDTO mockOferta = new OfertaDTO();
        mockOferta.setId(1L);
        mockOferta.setUserId(1L);
        mockOferta.setMonto(150.0);
        when(mockOfertaClient.obtenerMejorOfertaPorSubasta(anyLong())).thenReturn(mockOferta);
        return mockOfertaClient;
    }

    @Bean
    @Primary
    public PedidoClient pedidoClient() {
        PedidoClient mockPedidoClient = mock(PedidoClient.class);
        PedidoResponse mockResponse = new PedidoResponse();
        mockResponse.setTrackingId("test-tracking-id");
        mockResponse.setEstadoRastreo("CREADO");
        when(mockPedidoClient.crearPedido(any())).thenReturn(mockResponse);
        return mockPedidoClient;
    }
} 