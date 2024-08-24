package com.itau.pix.controller;

import com.itau.pix.exception.PixKeyValidationException;
import com.itau.pix.model.PixKey;
import com.itau.pix.model.TipoChave;
import com.itau.pix.model.dto.PixKeyRequestDto;
import com.itau.pix.model.dto.PixKeyUpdateRequestDto;
import com.itau.pix.service.PixKeyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequestMapping("/api/v1/pix")
public class PixController {

    @Autowired
    private PixKeyService pixService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> createPixKey(@Valid @RequestBody PixKeyRequestDto request) {
        PixKey createdKey = pixService.registerPixKey(request);
        Map<String, String> response = new HashMap<>();
        response.put("id", createdKey.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/chave/{id}")
    public ResponseEntity<PixKey> updatePixKey(
            @PathVariable UUID id,
            @Valid @RequestBody PixKeyUpdateRequestDto updateRequest) {
        PixKey updatedKey = pixService.updatePixKey(id, updateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedKey);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PixKey>> searchPixKeys(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) TipoChave tipoChave,
            @RequestParam(required = false) String numeroAgencia,
            @RequestParam(required = false) String numeroConta,
            @RequestParam(required = false) String nomeCorrentista,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDateTime dataHoraInclusao,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDateTime dataHoraInativacao
    ) {
        return pixService.searchPixKeys(id, tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInclusao, dataHoraInativacao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivatePixKey(@PathVariable UUID id) {
        try {
            PixKey deactivatedKey = pixService.deactivatePixKey(id).getBody();
            return ResponseEntity.ok(deactivatedKey);
        } catch (PixKeyValidationException ex) {
            return ResponseEntity.unprocessableEntity().body(ex.getMessage());
        }
    }
}
