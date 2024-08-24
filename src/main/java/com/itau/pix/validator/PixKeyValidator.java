package com.itau.pix.validator;

import com.itau.pix.exception.PixKeyValidationException;
import com.itau.pix.model.PixKey;
import com.itau.pix.model.TipoChave;
import com.itau.pix.model.dto.PixKeyRequestDto;
import com.itau.pix.model.dto.PixKeyUpdateRequestDto;
import com.itau.pix.repository.PixKeyRepository;
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
public class PixKeyValidator {
    @Autowired
    private PixKeyRepository repository;

    public void validatePixKeyRequest(PixKeyRequestDto request) {
        if (request.getTipoChave() == null || request.getValorChave() == null ||
                request.getTipoConta() == null || request.getNumeroAgencia() == null ||
                request.getNumeroConta() == null || request.getNomeCorrentista() == null) {
            throw new PixKeyValidationException("Todos os campos obrigatórios devem ser informados.");
        }

        switch (request.getTipoChave()) {
            case CELULAR:
                validateCelular(request.getValorChave());
                break;
            case EMAIL:
                validateEmail(request.getValorChave());
                break;
            case CPF:
                validateCpf(request.getValorChave());
                break;
            case CNPJ:
                validateCnpj(request.getValorChave());
                break;
            case ALEATORIA:
                validateChaveAleatoria(request.getValorChave());
                break;
            default:
                throw new PixKeyValidationException("Tipo de chave inválido.");
        }
        validateDuplication(request);
        validateKeyLimit(request);
    }

    public void validatePixKeyUpdateRequest(PixKey existingKey, PixKeyUpdateRequestDto requestUpdate) {
        if (requestUpdate.getId() != null && !existingKey.getId().equals(requestUpdate.getId())) {
            throw new PixKeyValidationException("O ID da chave não pode ser alterado.");
        }

        if (requestUpdate.getTipoChave() != null && !existingKey.getTipoChave().equals(requestUpdate.getTipoChave())) {
            throw new PixKeyValidationException("O tipo da chave não pode ser alterado.");
        }

        if (requestUpdate.getValorChave() != null && !existingKey.getValorChave().equals(requestUpdate.getValorChave())) {
            throw new PixKeyValidationException("O valor da chave não pode ser alterado.");
        }

        if (existingKey.isInativa()) {
            throw new PixKeyValidationException("Não é permitido alterar uma chave inativa.");
        }

        validatePixKeyUpdateFields(requestUpdate);
    }

    private void validatePixKeyUpdateFields(PixKeyUpdateRequestDto request) {
        if (request.getTipoConta() != null) {
            if (!request.getTipoConta().matches("^(corrente|poupança)$")) {
                throw new PixKeyValidationException("Tipo de conta inválido. Deve ser 'corrente' ou 'poupança'.");
            }
            if (request.getTipoConta().length() > 10) {
                throw new PixKeyValidationException("Tipo de conta deve ter no máximo 10 caracteres.");
            }
        }

        if (request.getNumeroAgencia() != null) {
            if (!request.getNumeroAgencia().matches("^\\d{4}$")) {
                throw new PixKeyValidationException("Número da agência inválido. Deve ter exatamente 4 dígitos.");
            }
        }

        if (request.getNumeroConta() != null) {
            if (!request.getNumeroConta().matches("^\\d{8}$")) {
                throw new PixKeyValidationException("Número da conta inválido. Deve ter exatamente 8 dígitos.");
            }
        }

        if (request.getNomeCorrentista() != null) {
            if (request.getNomeCorrentista().isEmpty() || request.getNomeCorrentista().length() > 30) {
                throw new PixKeyValidationException("Nome do correntista inválido. Deve ter no máximo 30 caracteres.");
            }
        }

        if (request.getSobrenomeCorrentista() != null) {
            if (request.getSobrenomeCorrentista().isEmpty() || request.getSobrenomeCorrentista().length() > 45) {
                throw new PixKeyValidationException("Sobrenome do correntista inválido. Deve ter no máximo 45 caracteres.");
            }
        }
    }

    public void validateSearchPixKeysFilters(
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

    public void validatePixKeyIsNotInactive(PixKey key) {
        if (key.isInativa()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A chave já está desativada.");
        }
    }

    public List<PixKey> filterInactiveKeys(List<PixKey> keys) {
        return keys.stream()
                .filter(pixKey -> !pixKey.isInativa())
                .collect(Collectors.toList());
    }

    private void validateCelular(String valorChave) {
        // Verifica se o número começa com o código do Brasil (+55)
        if (!valorChave.startsWith("+55")) {
            throw new PixKeyValidationException("O código do país deve ser +55 para números do Brasil.");
        }

        if (!valorChave.matches("^\\+55\\d{2}9\\d{8}$")) {
            throw new PixKeyValidationException("Número de celular inválido. O formato correto é +55DD9XXXXXXXX.");
        }
    }

    private void validateEmail(String valorChave) {
        if (!valorChave.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$") || valorChave.length() > 77) {
            throw new PixKeyValidationException("Valor de e-mail inválido.");
        }
    }

    private void validateCpf(String valorChave) {
        // Implementar a validação do CPF conforme a regra
        if (!valorChave.matches("^\\d{11}$") || !isValidCpf(valorChave)) {
            throw new PixKeyValidationException("Valor de CPF inválido.");
        }
    }

    private void validateCnpj(String valorChave) {
        // Implementar a validação do CNPJ conforme a regra
        if (!valorChave.matches("^\\d{14}$") || !isValidCnpj(valorChave)) {
            throw new PixKeyValidationException("Valor de CNPJ inválido.");
        }
    }

    private void validateChaveAleatoria(String valorChave) {
        if (!valorChave.matches("^[a-zA-Z0-9]{36}$")) {
            throw new PixKeyValidationException("Valor de chave aleatória inválido.");
        }
    }

    private void validateDuplication(PixKeyRequestDto request) {
        boolean existsValorChave = repository.existsByValorChave(request.getValorChave());

        if (existsValorChave) {
            throw new PixKeyValidationException("Já existe uma chave cadastrada com esse valor.");
        }

        boolean existsTipoChaveParaConta = repository.existsByTipoChaveAndNumeroConta(
                request.getTipoChave().toString(),
                request.getNumeroConta()
        );

        if (existsTipoChaveParaConta) {
            throw new PixKeyValidationException("Já existe uma chave desse tipo cadastrada para essa conta.");
        }

        // Verifica se o valor da chave já existe para outro correntista
        boolean existsForOtherCustomer = repository.existsByValorChaveAndNomeCorrentista(
                request.getValorChave(),
                request.getNumeroConta()
        );

        if (existsForOtherCustomer) {
            throw new PixKeyValidationException("Já existe uma chave cadastrada com esse valor para outro correntista.");
        }

    }

    private void validateKeyLimit(PixKeyRequestDto request) {
        long count = repository.countByNumeroAgenciaAndNumeroConta(
                request.getNumeroAgencia(),
                request.getNumeroConta()
        );

        if (request.isPessoaFisica() && count >= 5) {
            throw new PixKeyValidationException("Limite de 5 chaves atingido para conta de pessoa física.");
        } else if (request.isPessoaJuridica() && count >= 20) {
            throw new PixKeyValidationException("Limite de 20 chaves atingido para conta de pessoa jurídica.");
        }
    }

    private boolean isValidCpf(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", "");

        // Verifica se o CPF tem 11 dígitos ou se é uma sequência de números repetidos
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            char dig10, dig11;
            int sm, i, r, num, peso;

            // Cálculo do primeiro dígito verificador
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

            // Cálculo do segundo dígito verificador
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

            // Verifica se os dígitos calculados conferem com os dígitos informados
            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
        } catch (InputMismatchException e) {
            return false;
        }
    }

    private boolean isValidCnpj(String cnpj) {
        cnpj = cnpj.replaceAll("[^\\d]", "");

        // Verifica se o CNPJ tem 14 dígitos
        if (cnpj.length() != 14) {
            return false;
        }

        try {
            char dig13, dig14;
            int sm, i, r, num, peso;

            // Cálculo do primeiro dígito verificador
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

            // Cálculo do segundo dígito verificador
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

            // Verifica se os dígitos calculados conferem com os dígitos informados
            return (dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13));
        } catch (InputMismatchException e) {
            return false;
        }
    }
}
