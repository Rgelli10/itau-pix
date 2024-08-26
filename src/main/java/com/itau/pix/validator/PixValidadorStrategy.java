package com.itau.pix.validator;

import com.itau.pix.exception.NaoAutorizadoException;
import com.itau.pix.exception.NaoEncontradoException;
import com.itau.pix.exception.RequisicaoInvalidaException;
import com.itau.pix.exception.ValidacaoException;
import com.itau.pix.model.PixModelo;
import com.itau.pix.model.enums.TipoChave;
import com.itau.pix.model.dto.PixRequisicaoDto;
import com.itau.pix.model.dto.PixAlterarRequisicaoDto;
import com.itau.pix.model.enums.TipoCorrentista;
import com.itau.pix.repository.PixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PixValidadorStrategy {

    private final ValidadorPixFactory validador;
    private final PixRepository repository;

    @Autowired
    public PixValidadorStrategy(ValidadorPixFactory validationFactory, PixRepository repository) {
        this.validador = validationFactory;
        this.repository = repository;
    }

    public void validadorRequisicao(PixRequisicaoDto requisicao) {
        ValidadorPix strategy = validador.getStrategy(requisicao.getTipoChave());
        strategy.validate(requisicao);
        validarCamposObrigatorios(requisicao);

        validarLimiteChave(requisicao);
        validarDuplicidade(requisicao);
    }

    public void validadorRequisicaoAlterar(PixModelo chaveExiste, PixAlterarRequisicaoDto requisicaoAlterar) {
        validarImutabilidadeCampos(chaveExiste, requisicaoAlterar);

        if (chaveExiste.isInativa()) {
            throw new ValidacaoException("Não é permitido alterar uma chave inativa.");
        }

        validarCamposAlteraveis(requisicaoAlterar);
    }

    public void validadorBuscaFiltros(
            UUID id,
            TipoChave tipoChave,
            String numeroAgencia,
            String numeroConta,
            String nomeCorrentista,
            LocalDateTime dataHoraInclusao,
            LocalDateTime dataHoraInativacao
    ) {
        if (id != null && (tipoChave != null || numeroAgencia != null || numeroConta != null ||
                nomeCorrentista != null || dataHoraInclusao != null || dataHoraInativacao != null)) {
            throw new RequisicaoInvalidaException("Se informar o ID, nenhum outro filtro pode ser usado.");
        }
    }

    public void validadorChaveAtiva(PixModelo chave) {
        if (chave.isInativa()) {
            throw new ValidacaoException("A chave já está desativada.");
        }
    }

    public List<PixModelo> filtroChaveInativa(List<PixModelo> chaves) {
        return chaves.stream()
                .filter(pixKey -> !pixKey.isInativa())
                .collect(Collectors.toList());
    }

    private void validarCamposObrigatorios(PixRequisicaoDto requisicao) {
        if (requisicao.getTipoChave() == null || requisicao.getValorChave() == null ||
                requisicao.getTipoConta() == null || requisicao.getNumeroAgencia() == null ||
                requisicao.getNumeroConta() == null || requisicao.getNomeCorrentista() == null) {
            throw new ValidacaoException("Todos os campos obrigatórios devem ser informados.");
        }
    }

    private void validarImutabilidadeCampos(PixModelo chaveExiste, PixAlterarRequisicaoDto requisicaoAlterar) {
        if (requisicaoAlterar.getId() != null && !chaveExiste.getId().equals(requisicaoAlterar.getId())) {
            throw new ValidacaoException("O ID da chave não pode ser alterado.");
        }

        if (requisicaoAlterar.getTipoChave() != null && !chaveExiste.getTipoChave().equals(requisicaoAlterar.getTipoChave())) {
            throw new ValidacaoException("O tipo da chave não pode ser alterado.");
        }

        if (requisicaoAlterar.getValorChave() != null && !chaveExiste.getValorChave().equals(requisicaoAlterar.getValorChave())) {
            throw new ValidacaoException("O valor da chave não pode ser alterado.");
        }
    }

    private void validarCamposAlteraveis(PixAlterarRequisicaoDto requisicao) {
        if (requisicao.getTipoConta() != null) {
            if (!requisicao.getTipoConta().matches("^(corrente|poupanca)$")) {
                throw new RequisicaoInvalidaException("Tipo de conta inválido. Deve ser 'corrente' ou 'poupanca'.");
            }
        }

        if (requisicao.getNumeroAgencia() != null && !requisicao.getNumeroAgencia().matches("^\\d{4}$")) {
            throw new RequisicaoInvalidaException("Número da agência inválido. Deve ter exatamente 4 dígitos.");
        }

        if (requisicao.getNumeroConta() != null && !requisicao.getNumeroConta().matches("^\\d{8}$")) {
            throw new RequisicaoInvalidaException("Número da conta inválido. Deve ter exatamente 8 dígitos.");
        }

        if (requisicao.getNomeCorrentista() != null) {
            String nome = requisicao.getNomeCorrentista().trim();
            if (nome.isEmpty() || nome.length() > 30) {
                throw new RequisicaoInvalidaException("Nome do correntista inválido. Não pode ser em branco e deve ter no máximo 30 caracteres.");
            }
        }

        if (requisicao.getSobrenomeCorrentista() != null) {
            String sobrenome = requisicao.getSobrenomeCorrentista().trim();
            if (sobrenome.length() > 45) {
                throw new RequisicaoInvalidaException("Sobrenome do correntista inválido. Deve ter no máximo 45 caracteres.");
            }
        }
    }

    private void validarDuplicidade(PixRequisicaoDto requisicao) {
        if (repository.existsByValorChave(requisicao.getValorChave())) {
            throw new ValidacaoException("Já existe uma chave cadastrada com esse valor.");
        }

        if (requisicao.getTipoCorrentista() == TipoCorrentista.FISICA) {
            if (repository.existsByTipoChaveAndNumeroConta(
                    requisicao.getTipoChave().name(),
                    requisicao.getNumeroConta())) {
                throw new ValidacaoException("Já existe uma chave desse tipo cadastrada para essa conta.");
            }

            if (repository.existsByValorChaveAndNomeCorrentista(
                    requisicao.getValorChave(),
                    requisicao.getNomeCorrentista())) {
                throw new ValidacaoException("Já existe uma chave cadastrada com esse valor para outro correntista.");
            }
        }
    }

    private void validarLimiteChave(PixRequisicaoDto requisicao) {
        long qtdChave = repository.countByNumeroAgenciaAndNumeroConta(
                requisicao.getNumeroAgencia(),
                requisicao.getNumeroConta()
        );

        if (requisicao.getTipoCorrentista() == TipoCorrentista.FISICA && qtdChave >= 5) {
            throw new NaoEncontradoException("Limite de 5 chaves atingido para conta de pessoa física.");
        } else if (requisicao.getTipoCorrentista() == TipoCorrentista.JURIDICA && qtdChave >= 20) {
            throw new NaoAutorizadoException("Limite de 20 chaves atingido para conta de pessoa jurídica.");
        }
    }
}
