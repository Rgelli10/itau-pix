package com.itau.pix.service;

import com.itau.pix.exception.NaoEncontradoException;
import com.itau.pix.model.PixModelo;
import com.itau.pix.model.enums.TipoChave;
import com.itau.pix.model.dto.PixRequisicaoDto;
import com.itau.pix.model.dto.PixAlterarRequisicaoDto;
import com.itau.pix.repository.PixRepository;
import com.itau.pix.validator.PixValidadorStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PixService {
    @Autowired
    private PixRepository repository;
    @Autowired
    private PixValidadorStrategy validador;

    public PixModelo cadastrar(PixRequisicaoDto requisicao) {
        validador.validadorRequisicao(requisicao);

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
                .orElseThrow(() -> new NaoEncontradoException("ID não encontrado."));

        validador.validadorRequisicaoAlterar(ChaveExiste, requisicaoAlterar);

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

        validador.validadorBuscaFiltros(id, tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInclusao, dataHoraInativacao);

        List<PixModelo> resultado;
        if (tipoChave != null) {
            if (numeroAgencia != null && numeroConta != null) {
                if (dataHoraInclusao != null) {
                    resultado = repository.findByTipoChaveAndNumeroAgenciaAndNumeroContaAndNomeCorrentistaAndDataHoraInclusao(
                            tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInclusao);
                } else if (dataHoraInativacao != null) {
                    resultado = repository.findByTipoChaveAndNumeroAgenciaAndNumeroContaAndNomeCorrentistaAndDataHoraInativacao(
                            tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInativacao);
                } else {
                    resultado = repository.findByTipoChaveAndNumeroAgenciaAndNumeroConta(tipoChave, numeroAgencia, numeroConta);
                }
            } else {
                resultado = repository.findByTipoChave(tipoChave);
            }
        } else if (numeroAgencia != null && numeroConta != null) {
            resultado = repository.findByNumeroAgenciaAndNumeroConta(numeroAgencia, numeroConta);
        } else if (nomeCorrentista != null) {
            resultado = repository.findByNomeCorrentista(nomeCorrentista);
        } else if (dataHoraInclusao != null) {
            resultado = repository.findByDataHoraInclusao(dataHoraInclusao);
        } else if (dataHoraInativacao != null) {
            resultado = repository.findByDataHoraInativacao(dataHoraInativacao);
        } else {
            resultado = new ArrayList<>();
        }

        resultado = validador.filtroChaveInativa(resultado);

        if (resultado.isEmpty()) {
            throw new NaoEncontradoException("Nenhuma chave encontrada com os critérios fornecidos.");
        }

        return ResponseEntity.ok(resultado);
    }

    public ResponseEntity<PixModelo> desativar(UUID id) {
        PixModelo chaveExiste = repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("ID não encontrado."));

        validador.validadorChaveAtiva(chaveExiste);

        chaveExiste.setInativa(true);
        chaveExiste.setDataHoraInativacao(LocalDateTime.now());

        PixModelo chaveDesativada = repository.save(chaveExiste);

        return ResponseEntity.ok(chaveDesativada);
    }
}