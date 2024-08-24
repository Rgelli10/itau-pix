package com.itau.pix.validator;

import com.itau.pix.exception.PixValidadorException;
import com.itau.pix.model.PixModelo;
import com.itau.pix.model.enums.TipoChave;
import com.itau.pix.model.dto.PixRequisicaoDto;
import com.itau.pix.model.dto.PixAlterarRequisicaoDto;
import com.itau.pix.repository.PixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PixValidador {
    @Autowired
    private PixRepository repository;

    public void validadorRequisicao(PixRequisicaoDto requisicao) {
        if (requisicao.getTipoChave() == null || requisicao.getValorChave() == null ||
                requisicao.getTipoConta() == null || requisicao.getNumeroAgencia() == null ||
                requisicao.getNumeroConta() == null || requisicao.getNomeCorrentista() == null) {
            throw new PixValidadorException("Todos os campos obrigatórios devem ser informados.");
        }

        switch (requisicao.getTipoChave()) {
            case CELULAR:
                validadorCelular(requisicao.getValorChave());
                break;
            case EMAIL:
                validadorEmail(requisicao.getValorChave());
                break;
            case CPF:
                validadorCpf(requisicao.getValorChave());
                break;
            case CNPJ:
                validadorCnpj(requisicao.getValorChave());
                break;
            case ALEATORIA:
                validadorChaveAleatoria(requisicao.getValorChave());
                break;
            default:
                throw new PixValidadorException("Tipo de chave inválido.");
        }
        validadorChaveDuplicada(requisicao);
        validadorLimiteChave(requisicao);
    }

    public void validadorRequisicaoAlterar(PixModelo chaveExiste, PixAlterarRequisicaoDto requisicaoAlterar) {
        if (requisicaoAlterar.getId() != null && !chaveExiste.getId().equals(requisicaoAlterar.getId())) {
            throw new PixValidadorException("O ID da chave não pode ser alterado.");
        }

        if (requisicaoAlterar.getTipoChave() != null && !chaveExiste.getTipoChave().equals(requisicaoAlterar.getTipoChave())) {
            throw new PixValidadorException("O tipo da chave não pode ser alterado.");
        }

        if (requisicaoAlterar.getValorChave() != null && !chaveExiste.getValorChave().equals(requisicaoAlterar.getValorChave())) {
            throw new PixValidadorException("O valor da chave não pode ser alterado.");
        }

        if (chaveExiste.isInativa()) {
            throw new PixValidadorException("Não é permitido alterar uma chave inativa.");
        }

        validadorCampo(requisicaoAlterar);
    }

    private void validadorCampo(PixAlterarRequisicaoDto requisicao) {
        if (requisicao.getTipoConta() != null) {
            if (!requisicao.getTipoConta().matches("^(corrente|poupança)$")) {
                throw new PixValidadorException("Tipo de conta inválido. Deve ser 'corrente' ou 'poupança'.");
            }
            if (requisicao.getTipoConta().length() > 10) {
                throw new PixValidadorException("Tipo de conta deve ter no máximo 10 caracteres.");
            }
        }

        if (requisicao.getNumeroAgencia() != null) {
            if (!requisicao.getNumeroAgencia().matches("^\\d{4}$")) {
                throw new PixValidadorException("Número da agência inválido. Deve ter exatamente 4 dígitos.");
            }
        }

        if (requisicao.getNumeroConta() != null) {
            if (!requisicao.getNumeroConta().matches("^\\d{8}$")) {
                throw new PixValidadorException("Número da conta inválido. Deve ter exatamente 8 dígitos.");
            }
        }

        if (requisicao.getNomeCorrentista() != null) {
            if (requisicao.getNomeCorrentista().isEmpty() || requisicao.getNomeCorrentista().length() > 30) {
                throw new PixValidadorException("Nome do correntista inválido. Deve ter no máximo 30 caracteres.");
            }
        }

        if (requisicao.getSobrenomeCorrentista() != null) {
            if (requisicao.getSobrenomeCorrentista().isEmpty() || requisicao.getSobrenomeCorrentista().length() > 45) {
                throw new PixValidadorException("Sobrenome do correntista inválido. Deve ter no máximo 45 caracteres.");
            }
        }
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
        if (id != null) {
            if (tipoChave != null || numeroAgencia != null || numeroConta != null || nomeCorrentista != null ||
                    dataHoraInclusao != null || dataHoraInativacao != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se informar o ID, nenhum outro filtro pode ser usado.");
            }
        }
    }

    public void validadorChaveAtiva(PixModelo chave) {
        if (chave.isInativa()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A chave já está desativada.");
        }
    }

    public List<PixModelo> filtroChaveInativa(List<PixModelo> chave) {
        return chave.stream()
                .filter(pixKey -> !pixKey.isInativa())
                .collect(Collectors.toList());
    }

    private void validadorCelular(String valorChave) {
        if (!valorChave.startsWith("+55")) {
            throw new PixValidadorException("O código do país deve ser +55 para números do Brasil.");
        }

        if (!valorChave.matches("^\\+55\\d{2}9\\d{8}$")) {
            throw new PixValidadorException("Número de celular inválido. O formato correto é +55DD9XXXXXXXX.");
        }
    }

    private void validadorEmail(String valorChave) {
        if (!valorChave.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$") || valorChave.length() > 77) {
            throw new PixValidadorException("Valor de e-mail inválido.");
        }
    }

    private void validadorCpf(String valorChave) {
        if (!valorChave.matches("^\\d{11}$") || !isValidCpf(valorChave)) {
            throw new PixValidadorException("Valor de CPF inválido.");
        }
    }

    private void validadorCnpj(String valorChave) {
        if (!valorChave.matches("^\\d{14}$") || !isValidCnpj(valorChave)) {
            throw new PixValidadorException("Valor de CNPJ inválido.");
        }
    }

    private void validadorChaveAleatoria(String valorChave) {
        if (!valorChave.matches("^[a-zA-Z0-9]{36}$")) {
            throw new PixValidadorException("Valor de chave aleatória inválido.");
        }
    }

    private void validadorChaveDuplicada(PixRequisicaoDto requisicao) {
        boolean existsValorChave = repository.existsByValorChave(requisicao.getValorChave());

        if (existsValorChave) {
            throw new PixValidadorException("Já existe uma chave cadastrada com esse valor.");
        }

        boolean existeTipoChaveParaConta = repository.existsByTipoChaveAndNumeroConta(
                requisicao.getTipoChave().toString(),
                requisicao.getNumeroConta()
        );

        if (existeTipoChaveParaConta) {
            throw new PixValidadorException("Já existe uma chave desse tipo cadastrada para essa conta.");
        }

        boolean existeChaveOutraConta = repository.existsByValorChaveAndNomeCorrentista(
                requisicao.getValorChave(),
                requisicao.getNumeroConta()
        );

        if (existeChaveOutraConta) {
            throw new PixValidadorException("Já existe uma chave cadastrada com esse valor para outro correntista.");
        }

    }

    private void validadorLimiteChave(PixRequisicaoDto requisicao) {
        long qtdChave = repository.countByNumeroAgenciaAndNumeroConta(
                requisicao.getNumeroAgencia(),
                requisicao.getNumeroConta()
        );

        if (requisicao.isPessoaFisica() && qtdChave >= 5) {
            throw new PixValidadorException("Limite de 5 chaves atingido para conta de pessoa física.");
        } else if (requisicao.isPessoaJuridica() && qtdChave >= 20) {
            throw new PixValidadorException("Limite de 20 chaves atingido para conta de pessoa jurídica.");
        }
    }

    private boolean isValidCpf(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            char dig10, dig11;
            int sm, i, r, num, peso;

            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm += (num * peso);
                peso--;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig10 = '0';
            } else {
                dig10 = (char) (r + 48);
            }

            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm += (num * peso);
                peso--;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig11 = '0';
            } else {
                dig11 = (char) (r + 48);
            }

            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
        } catch (InputMismatchException e) {
            return false;
        }
    }

    private boolean isValidCnpj(String cnpj) {
        cnpj = cnpj.replaceAll("[^\\d]", "");

        if (cnpj.length() != 14) {
            return false;
        }

        try {
            char dig13, dig14;
            int sm, i, r, num, peso;

            sm = 0;
            peso = 2;
            for (i = 11; i >= 0; i--) {
                num = (int) (cnpj.charAt(i) - 48);
                sm += (num * peso);
                peso = peso == 9 ? 2 : peso + 1;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1)) {
                dig13 = '0';
            } else {
                dig13 = (char) ((11 - r) + 48);
            }

            sm = 0;
            peso = 2;
            for (i = 12; i >= 0; i--) {
                num = (int) (cnpj.charAt(i) - 48);
                sm += (num * peso);
                peso = peso == 9 ? 2 : peso + 1;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1)) {
                dig14 = '0';
            } else {
                dig14 = (char) ((11 - r) + 48);
            }

            return (dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13));
        } catch (InputMismatchException e) {
            return false;
        }
    }
}
