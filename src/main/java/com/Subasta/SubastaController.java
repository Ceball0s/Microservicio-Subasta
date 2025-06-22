package com.Subasta;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Subasta.DTOs.AgregarRequest;
import com.Subasta.DTOs.SubastaDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/subasta")
@CrossOrigin(origins = {"http://localhost:5173"})
@RequiredArgsConstructor
public class SubastaController {

    private final SubastaService subastaService;

    @GetMapping("/recomendaciones")
    public ResponseEntity<List<SubastaDTO>> obtenerRecomendaciones(@RequestHeader("user-id") Long userId) {
        List<SubastaDTO> subastas;

        if (userId == null || userId <= 0) {
            subastas = subastaService.obtenerRecomendacionesGenericas();
        } else {
            //int usuarioId = userId.intValue();
            //subastas = subastaService.obtenerRecomendacionesPersonalizadas(usuarioId);
            subastas = subastaService.obtenerRecomendacionesGenericas();
        }

        return ResponseEntity.ok(subastas);
    }

    @PostMapping("/agregar")
    public ResponseEntity<SubastaDTO> agregarSubasta(
            @RequestBody AgregarRequest request,
            @RequestHeader("user-id") Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Acceso denegado
        }

        if (request.getAumentoMinimo() == null || request.getAumentoMinimo() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null); // o lanza una excepción personalizada
        }

        int usuarioId = userId.intValue();
        SubastaDTO subastaGuardado = subastaService.agregarSubasta(request, usuarioId);
        return ResponseEntity.ok(subastaGuardado);
    }


    @GetMapping("/{subastaId}")
    public ResponseEntity<SubastaDTO> consultarSubasta(@PathVariable int subastaId) {
        SubastaDTO subasta = subastaService.obtenerSubastaPorId(subastaId);
        return ResponseEntity.ok(subasta);
    }

    @PutMapping("/{subastaId}")
    public ResponseEntity<SubastaDTO> modificarSubasta(@PathVariable int subastaId,
                                                       @RequestBody AgregarRequest request,
                                                       @RequestHeader("user-id") Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Acceso denegado
        }

        int usuarioId = userId.intValue();
        SubastaDTO subastaModificada = subastaService.modificarSubasta(subastaId, request, usuarioId);

        return ResponseEntity.ok(subastaModificada);
    }

    @PutMapping("/finalizar/{subastaId}")
    public ResponseEntity<SubastaDTO> cerrarSubasta(@PathVariable int subastaId,
                                                    @RequestHeader("user-id") Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Acceso denegado
        }

        int usuarioId = userId.intValue();
        SubastaDTO subastaCerrada = subastaService.cerrarSubasta(subastaId, usuarioId);
        return ResponseEntity.ok(subastaCerrada);
    }

    @PutMapping("/cancelar/{subastaId}")
    public ResponseEntity<SubastaDTO> cancelarSubasta(@PathVariable int subastaId,
                                                      @RequestHeader("user-id") Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Acceso denegado
        }

        int usuarioId = userId.intValue();
        SubastaDTO subastaCancelada = subastaService.cancelarSubasta(subastaId, usuarioId);
        return ResponseEntity.ok(subastaCancelada);
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<SubastaDTO>> obtenerSubastasPorUsuario(@RequestHeader("user-id") Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Acceso denegado
        }

        int usuarioId = userId.intValue();
        List<SubastaDTO> subastas = subastaService.obtenerSubastasPorUsuario(usuarioId);
        return ResponseEntity.ok(subastas);
    }
}