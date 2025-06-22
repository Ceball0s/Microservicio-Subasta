package com.Subasta;

import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Subasta.Clients.ChatClient;
import com.Subasta.Clients.OfertaClient;
import com.Subasta.Clients.PedidoClient;
import com.Subasta.DTOs.AgregarRequest;
import com.Subasta.DTOs.CrearPedidoRequest;
import com.Subasta.DTOs.OfertaDTO;
import com.Subasta.DTOs.SubastaDTO;
import com.Subasta.Models.*;
@Service
public class SubastaService {

    private final SubastaRepository subastaRepository;
    
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private OfertaClient ofertaClient;

    @Autowired
    private PedidoClient pedidoClient;

    public SubastaService(SubastaRepository subastaRepository) {
        this.subastaRepository = subastaRepository;
    }

    // Recomendaciones genéricas
    public List<SubastaDTO> obtenerRecomendacionesGenericas() {
        return subastaRepository.findTop10ByOrderByFechaCreacionDesc()
                .stream()
                .map(SubastaDTO::new)
                .collect(Collectors.toList());
    }

    // Crear una nueva subasta
    public SubastaDTO agregarSubasta(AgregarRequest request, int usuarioId) {
        Subasta subasta = Subasta.builder()
            .nombre(request.getNombre())
            .descripcion(request.getDescripcion())
            .precioInicial(request.getPrecioInicial())
            .aumentoMinimo(request.getAumentoMinimo()) // ✅ nuevo campo
            .user_id((long) usuarioId)
            .fechaCreacion(new Date())
            .fechaCierre(request.getFechaCierre() != null ? request.getFechaCierre() : calcularFechaCierrePorDefecto())
            .build();
        
        // Crear sala de chat
        String chatRoomId = chatClient.crearSalaChat(String.valueOf(usuarioId)).getRoomId();
        subasta.setChatRoomId(chatRoomId);
        Subasta subastaGuardado = subastaRepository.save(subasta);
        return new SubastaDTO(subastaGuardado);
    }


    // Modificar una subasta existente
    public SubastaDTO modificarSubasta(int subastaId, AgregarRequest request, int usuarioId) {
        Subasta subastaExistente = subastaRepository.findById((long) subastaId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Subasta no encontrada"));

        if (!subastaExistente.getUser_id().equals((long) usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para modificar esta subasta");
        }

        if (request.getNombre() != null) {
            subastaExistente.setNombre(request.getNombre());
        }
        if (request.getDescripcion() != null) {
            subastaExistente.setDescripcion(request.getDescripcion());
        }
        if (request.getPrecioInicial() != null) {
            subastaExistente.setPrecioInicial(request.getPrecioInicial());
            if (subastaExistente.getPrecioActual() < subastaExistente.getPrecioInicial()) {
                subastaExistente.setPrecioActual(request.getPrecioInicial());
            }
        }
        if (request.getFechaCierre() != null) {
            subastaExistente.setFechaCierre(request.getFechaCierre());
        }

        Subasta subastaModificada = subastaRepository.save(subastaExistente);
        return new SubastaDTO(subastaModificada);
    }

    // Cerrar una subasta
    public SubastaDTO cerrarSubasta(int subastaId, int usuarioId) {
        Subasta subastaExistente = subastaRepository.findById((long) subastaId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Subasta no encontrada"));

        if (!subastaExistente.getUser_id().equals((long) usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para cerrar esta subasta");
        }

        if (subastaExistente.getEstado() != EstadoSubasta.ACTIVA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La subasta no está activa y no se puede cerrar");
        }

        subastaExistente.setEstado(EstadoSubasta.FINALIZADA);
        Subasta subasta = subastaRepository.save(subastaExistente);
        // 🔗 Obtener mejor oferta desde el microservicio de Oferta
        OfertaDTO mejorOferta = ofertaClient.obtenerMejorOfertaPorSubasta(subasta.getId());

        // 📨 Crear pedido en Pedido_Rastreo
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setIdComprador(mejorOferta.getUserId());
        request.setIdVendedor(subasta.getUser_id());
        request.setNombreProducto(subasta.getNombre());
        request.setDescripcionProducto(subasta.getDescripcion());
        request.setPrecioFinal(mejorOferta.getMonto());

        pedidoClient.crearPedido(request);
        return new SubastaDTO(subasta);
    }

    // Cancelar una subasta
    public SubastaDTO cancelarSubasta(int subastaId, int usuarioId) {
        Subasta subastaExistente = subastaRepository.findById((long) subastaId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Subasta no encontrada"));

        if (!subastaExistente.getUser_id().equals((long) usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para cancelar esta subasta");
        }

        if (subastaExistente.getEstado() != EstadoSubasta.ACTIVA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La subasta no está activa y no se puede cancelar");
        }

        subastaExistente.setEstado(EstadoSubasta.CANCELADA);
        Subasta subastaCancelada = subastaRepository.save(subastaExistente);
        return new SubastaDTO(subastaCancelada);
    }

    // Obtener subastas por usuario
    public List<SubastaDTO> obtenerSubastasPorUsuario(int usuarioId) {
        List<Subasta> subastas = subastaRepository.findByUserId((long) usuarioId);
        return subastas.stream().map(SubastaDTO::new).collect(Collectors.toList());
    }

    // Obtener una subasta por ID
    public SubastaDTO obtenerSubastaPorId(int subastaId) {
        Subasta subasta = subastaRepository.findById((long) subastaId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Subasta no encontrada"));
        return new SubastaDTO(subasta);
    }

    // Calcular la fecha de cierre por defecto
    private Date calcularFechaCierrePorDefecto() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 7); // Por defecto, la subasta cierra en 7 días
        return calendar.getTime();
    }
}