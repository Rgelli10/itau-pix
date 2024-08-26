package com.itau.pix.controller;

import com.itau.pix.model.PixModelo;
import com.itau.pix.model.enums.TipoChave;
import com.itau.pix.model.dto.PixRequisicaoDto;
import com.itau.pix.model.dto.PixAlterarRequisicaoDto;
import com.itau.pix.service.PixService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequestMapping("/api/v1/pix")
public class PixController {

    @Autowired
    private PixService pixService;

    @PostMapping("/cadastrar")
    public ResponseEntity<Map<String, String>> cadastrarChavePix(@Valid @RequestBody PixRequisicaoDto requisicao) {
        PixModelo createdKey = pixService.cadastrar(requisicao);
        Map<String, String> response = new HashMap<>();
        response.put("id", createdKey.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/chave/{id}")
    public ResponseEntity<PixModelo> alterarChavePix(
            @PathVariable UUID id,
            @Valid @RequestBody PixAlterarRequisicaoDto requisicaoAlterar) {
        PixModelo alterarChave = pixService.alterar(id, requisicaoAlterar);
        return ResponseEntity.status(HttpStatus.OK).body(alterarChave);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<PixModelo>> consultarChavePix(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) TipoChave tipoChave,
            @RequestParam(required = false) String numeroAgencia,
            @RequestParam(required = false) String numeroConta,
            @RequestParam(required = false) String nomeCorrentista,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDateTime dataHoraInclusao,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDateTime dataHoraInativacao
    ) {
        return pixService.buscar(id, tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInclusao, dataHoraInativacao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PixModelo> desativarChavePix(@PathVariable UUID id) {
        return pixService.desativar(id);
    }
}
