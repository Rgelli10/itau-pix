package com.itau.pix.service;

import com.itau.pix.exception.PixValidadorException;
import com.itau.pix.model.PixModelo;
import com.itau.pix.model.enums.TipoChave;
import com.itau.pix.model.dto.PixRequisicaoDto;
import com.itau.pix.model.dto.PixAlterarRequisicaoDto;
import com.itau.pix.repository.PixRepository;
import com.itau.pix.validator.PixValidador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PixService {

    @Autowired
    private PixRepository repository;
    @Autowired
    private PixValidador validator;

    public PixModelo cadastrar(PixRequisicaoDto requisicao) {
        validator.validadorRequisicao(requisicao);

        if (requisicao.getId() == null) {
            requisicao.setId(UUID.randomUUID().toString());
        }

        PixModelo pixKey = new PixModelo();
        pixKey.setId(requisicao.getId());
        pixKey.setTipoChave(requisicao.getTipoChave());
        pixKey.setValorChave(requisicao.getValorChave());
        pixKey.setTipoConta(requisicao.getTipoConta());
        pixKey.setNumeroAgencia(requisicao.getNumeroAgencia());
        pixKey.setNumeroConta(requisicao.getNumeroConta());
        pixKey.setNomeCorrentista(requisicao.getNomeCorrentista());
        pixKey.setSobrenomeCorrentista(requisicao.getSobrenomeCorrentista());
        pixKey.setDataHoraInclusao(LocalDateTime.now());
        pixKey.setInativa(false);

        return repository.save(pixKey);
    }

    public PixModelo alterar(UUID id, PixAlterarRequisicaoDto requisicaoAlterar) {
        PixModelo ChaveExiste = repository.findById(id)
                .orElseThrow(() -> new PixValidadorException("ID não encontrado."));

        validator.validadorRequisicaoAlterar(ChaveExiste, requisicaoAlterar);

        if (requisicaoAlterar.getTipoConta() != null) ChaveExiste.setTipoConta(requisicaoAlterar.getTipoConta());
        if (requisicaoAlterar.getNumeroAgencia() != null) ChaveExiste.setNumeroAgencia(requisicaoAlterar.getNumeroAgencia());
        if (requisicaoAlterar.getNumeroConta() != null) ChaveExiste.setNumeroConta(requisicaoAlterar.getNumeroConta());
        if (requisicaoAlterar.getNomeCorrentista() != null) ChaveExiste.setNomeCorrentista(requisicaoAlterar.getNomeCorrentista());
        if (requisicaoAlterar.getSobrenomeCorrentista() != null) ChaveExiste.setSobrenomeCorrentista(requisicaoAlterar.getSobrenomeCorrentista());

        return repository.save(ChaveExiste);
    }

    public ResponseEntity<List<PixModelo>> buscar(
            UUID id,
            TipoChave tipoChave,
            String numeroAgencia,
            String numeroConta,
            String nomeCorrentista,
            LocalDateTime dataHoraInclusao,
            LocalDateTime dataHoraInativacao
    ) {
        validator.validadorBuscaFiltros(id, tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInclusao, dataHoraInativacao);

        List<PixModelo> resulado;
        if (tipoChave != null) {
            if (numeroAgencia != null && numeroConta != null) {
                if (dataHoraInclusao != null) {
                    resulado = repository.findByTipoChaveAndNumeroAgenciaAndNumeroContaAndNomeCorrentistaAndDataHoraInclusao(
                            tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInclusao);
                } else if (dataHoraInativacao != null) {
                    resulado = repository.findByTipoChaveAndNumeroAgenciaAndNumeroContaAndNomeCorrentistaAndDataHoraInativacao(
                            tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInativacao);
                } else {
                    resulado = repository.findByTipoChaveAndNumeroAgenciaAndNumeroConta(tipoChave, numeroAgencia, numeroConta);
                }
            } else {
                resulado = repository.findByTipoChave(tipoChave);
            }
        } else if (numeroAgencia != null && numeroConta != null) {
            resulado = repository.findByNumeroAgenciaAndNumeroConta(numeroAgencia, numeroConta);
        } else if (nomeCorrentista != null) {
            resulado = repository.findByNomeCorrentista(nomeCorrentista);
        } else if (dataHoraInclusao != null) {
            resulado = repository.findByDataHoraInclusao(dataHoraInclusao);
        } else if (dataHoraInativacao != null) {
            resulado = repository.findByDataHoraInativacao(dataHoraInativacao);
        } else {
            resulado = new ArrayList<>();
        }

        resulado = validator.filtroChaveInativa(resulado);

        if (resulado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        return ResponseEntity.ok(resulado);
    }

    public ResponseEntity<PixModelo> desativar(UUID id) {
        PixModelo chaveExiste = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID não encontrado."));

        validator.validadorChaveAtiva(chaveExiste);

        chaveExiste.setInativa(true);
        chaveExiste.setDataHoraInativacao(LocalDateTime.now());

        PixModelo chaveDesativada = repository.save(chaveExiste);

        return ResponseEntity.ok(chaveDesativada);
    }
}