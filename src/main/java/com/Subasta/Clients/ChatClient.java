package com.Subasta.Clients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import com.Subasta.DTOs.ChatRoomResponse;

@FeignClient(name = "Microservicio-Chat")
public interface ChatClient {

    @PostMapping("/room")
    ChatRoomResponse crearSalaChat(@RequestHeader("X-User-Id") String userId);
}
