package com.itau.pix.service;

import com.itau.pix.exception.PixKeyValidationException;
import com.itau.pix.model.PixKey;
import com.itau.pix.model.TipoChave;
import com.itau.pix.model.dto.PixKeyRequestDto;
import com.itau.pix.model.dto.PixKeyUpdateRequestDto;
import com.itau.pix.repository.PixKeyRepository;
import com.itau.pix.validator.PixKeyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PixKeyService {

    @Autowired
    private PixKeyRepository repository;

    @Autowired
    private PixKeyValidator validator;

    public PixKey registerPixKey(PixKeyRequestDto request) {
        validator.validatePixKeyRequest(request);

        if (request.getId() == null) {
            request.setId(UUID.randomUUID().toString());
        }

        PixKey pixKey = new PixKey();
        pixKey.setId(request.getId());
        pixKey.setTipoChave(request.getTipoChave());
        pixKey.setValorChave(request.getValorChave());
        pixKey.setTipoConta(request.getTipoConta());
        pixKey.setNumeroAgencia(request.getNumeroAgencia());
        pixKey.setNumeroConta(request.getNumeroConta());
        pixKey.setNomeCorrentista(request.getNomeCorrentista());
        pixKey.setSobrenomeCorrentista(request.getSobrenomeCorrentista());
        pixKey.setDataHoraInclusao(LocalDateTime.now());
        pixKey.setInativa(false);

        return repository.save(pixKey);
    }

    public PixKey updatePixKey(UUID id, PixKeyUpdateRequestDto updateRequest) {
        PixKey existingKey = repository.findById(id)
                .orElseThrow(() -> new PixKeyValidationException("ID não encontrado."));

        validator.validatePixKeyUpdateRequest(existingKey, updateRequest);

        if (updateRequest.getTipoConta() != null) existingKey.setTipoConta(updateRequest.getTipoConta());
        if (updateRequest.getNumeroAgencia() != null) existingKey.setNumeroAgencia(updateRequest.getNumeroAgencia());
        if (updateRequest.getNumeroConta() != null) existingKey.setNumeroConta(updateRequest.getNumeroConta());
        if (updateRequest.getNomeCorrentista() != null) existingKey.setNomeCorrentista(updateRequest.getNomeCorrentista());
        if (updateRequest.getSobrenomeCorrentista() != null) existingKey.setSobrenomeCorrentista(updateRequest.getSobrenomeCorrentista());

        return repository.save(existingKey);
    }

    public ResponseEntity<List<PixKey>> searchPixKeys(
            UUID id,
            TipoChave tipoChave,
            String numeroAgencia,
            String numeroConta,
            String nomeCorrentista,
            LocalDateTime dataHoraInclusao,
            LocalDateTime dataHoraInativacao
    ) {
        validator.validateSearchPixKeysFilters(id, tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInclusao, dataHoraInativacao);

        List<PixKey> result;
        if (tipoChave != null) {
            if (numeroAgencia != null && numeroConta != null) {
                if (dataHoraInclusao != null) {
                    result = repository.findByTipoChaveAndNumeroAgenciaAndNumeroContaAndNomeCorrentistaAndDataHoraInclusao(
                            tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInclusao);
                } else if (dataHoraInativacao != null) {
                    result = repository.findByTipoChaveAndNumeroAgenciaAndNumeroContaAndNomeCorrentistaAndDataHoraInativacao(
                            tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInativacao);
                } else {
                    result = repository.findByTipoChaveAndNumeroAgenciaAndNumeroConta(tipoChave, numeroAgencia, numeroConta);
                }
            } else {
                result = repository.findByTipoChave(tipoChave);
            }
        } else if (numeroAgencia != null && numeroConta != null) {
            result = repository.findByNumeroAgenciaAndNumeroConta(numeroAgencia, numeroConta);
        } else if (nomeCorrentista != null) {
            result = repository.findByNomeCorrentista(nomeCorrentista);
        } else if (dataHoraInclusao != null) {
            result = repository.findByDataHoraInclusao(dataHoraInclusao);
        } else if (dataHoraInativacao != null) {
            result = repository.findByDataHoraInativacao(dataHoraInativacao);
        } else {
            result = new ArrayList<>();
        }

        result = validator.filterInactiveKeys(result);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        return ResponseEntity.ok(result);
    }

    public ResponseEntity<PixKey> deactivatePixKey(UUID id) {
        PixKey existingKey = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID não encontrado."));

        validator.validatePixKeyIsNotInactive(existingKey);

        existingKey.setInativa(true);
        existingKey.setDataHoraInativacao(LocalDateTime.now());

        PixKey updatedKey = repository.save(existingKey);

        return ResponseEntity.ok(updatedKey);
    }
}