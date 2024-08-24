package com.itau.pix.validator;

import com.itau.pix.exception.PixKeyValidationException;
import com.itau.pix.massas.GeradorMassas;
import com.itau.pix.model.PixKey;
import com.itau.pix.model.TipoChave;
import com.itau.pix.model.dto.PixKeyRequestDto;
import com.itau.pix.model.dto.PixKeyUpdateRequestDto;
import com.itau.pix.repository.PixKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PixKeyValidatorTest {

    @Mock
    private PixKeyRepository repository;

    @InjectMocks
    private PixKeyValidator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidatePixKeyRequestValid() {
        PixKeyRequestDto requestDto = GeradorMassas.createPixKeyRequestDto();

        when(repository.existsByValorChave(requestDto.getValorChave())).thenReturn(false);
        assertEquals(requestDto.getTipoChave(), TipoChave.EMAIL);
        assertDoesNotThrow(() -> validator.validatePixKeyRequest(requestDto));
        requestDto.setTipoChave(TipoChave.CPF);
        requestDto.setValorChave("97670138068");
        assertEquals(requestDto.getTipoChave(), TipoChave.CPF);
        assertDoesNotThrow(() -> validator.validatePixKeyRequest(requestDto));
        requestDto.setTipoChave(TipoChave.CNPJ);
        requestDto.setValorChave("45076971000153");
        assertEquals(requestDto.getTipoChave(), TipoChave.CNPJ);
        assertDoesNotThrow(() -> validator.validatePixKeyRequest(requestDto));
        requestDto.setTipoChave(TipoChave.ALEATORIA);
        requestDto.setValorChave("417122439961664439189826754968636099");
        assertEquals(requestDto.getTipoChave(), TipoChave.ALEATORIA);
        assertDoesNotThrow(() -> validator.validatePixKeyRequest(requestDto));
        requestDto.setTipoChave(TipoChave.CELULAR);
        requestDto.setValorChave("+5511912345678");
        assertEquals(requestDto.getTipoChave(), TipoChave.CELULAR);
        assertDoesNotThrow(() -> validator.validatePixKeyRequest(requestDto));
    }

    @Test
    public void testValidatePixKeyRequestInvalid() {
        PixKeyRequestDto requestDto = GeradorMassas.createInvalidPixKeyRequestDto();

        Exception exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyRequest(requestDto);
        });
        assertTrue(exception.getMessage().contains("Todos os campos obrigatórios devem ser informados."));
    }

    @Test
    public void testValidatePixKeyRequestDuplicatedValue() {
        PixKeyRequestDto requestDto = GeradorMassas.createDuplicatedPixKeyRequestDto();

        when(repository.existsByValorChave(requestDto.getValorChave())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            validator.validatePixKeyRequest(requestDto);
        });

        assertTrue(exception.getMessage().contains("Já existe uma chave cadastrada com esse valor."));
    }

    @Test
    public void testValidatePixKeyUpdateRequestInvalidTipoConta() {
        PixKey existingKey = new PixKey();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixKeyUpdateRequestDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setTipoConta("inválido");

        PixKeyValidationException exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyUpdateRequest(existingKey, requestDto);
        });
        assertTrue(exception.getMessage().contains("Tipo de conta inválido. Deve ser 'corrente' ou 'poupança'."));
    }

    @Test
    public void testValidatePixKeyChavesInvalidas() {
        PixKeyRequestDto requestDto = GeradorMassas.createPixKeyRequestDto();
        requestDto.setTipoChave(TipoChave.ALEATORIA);
        requestDto.setValorChave("445555");
        PixKeyValidationException exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyRequest(requestDto);
        });
        assertTrue(exception.getMessage().contains("Valor de chave aleatória inválido."));

        requestDto.setTipoChave(TipoChave.CELULAR);
        requestDto.setValorChave("+5566391110");
        exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyRequest(requestDto);
        });
        assertTrue(exception.getMessage().contains("Número de celular inválido. O formato correto é +55DD9XXXXXXXX."));
//
        requestDto.setTipoChave(TipoChave.CPF);
        requestDto.setValorChave("445555");
        exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyRequest(requestDto);
        });
        assertTrue(exception.getMessage().contains("Valor de CPF inválido."));

        requestDto.setTipoChave(TipoChave.CNPJ);
        requestDto.setValorChave("445555");
        exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyRequest(requestDto);
        });
        assertTrue(exception.getMessage().contains("Valor de CNPJ inválido."));

        requestDto.setTipoChave(TipoChave.CELULAR);
        requestDto.setValorChave("1166391110");
        exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyRequest(requestDto);
        });
        assertTrue(exception.getMessage().contains("O código do país deve ser +55 para números do Brasil."));

        requestDto.setTipoChave(TipoChave.EMAIL);
        requestDto.setValorChave("sadsa.cm");
        exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyRequest(requestDto);
        });
        assertTrue(exception.getMessage().contains("Valor de e-mail inválido."));
    }

    @Test
    public void testValidatePixKeyUpdateId() {
        PixKey existingKey = new PixKey();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixKeyUpdateRequestDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setId("123");

        PixKeyValidationException exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyUpdateRequest(existingKey, requestDto);
        });
        assertTrue(exception.getMessage().contains("O ID da chave não pode ser alterado."));
    }

    @Test
    public void testValidatePixKeyUpdateChaveInativa() {
        PixKey existingKey = new PixKey();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(true);

        PixKeyUpdateRequestDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setNumeroConta("12345678");

        PixKeyValidationException exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyUpdateRequest(existingKey, requestDto);
        });
        assertTrue(exception.getMessage().contains("Não é permitido alterar uma chave inativa."));
    }

    @Test
    public void testValidatePixKeyUpdateRequestInvalidNumeroAgencia() {
        PixKey existingKey = new PixKey();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixKeyUpdateRequestDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setNumeroAgencia("123");

        PixKeyValidationException exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyUpdateRequest(existingKey, requestDto);
        });

        assertTrue(exception.getMessage().contains("Número da agência inválido. Deve ter exatamente 4 dígitos."));
    }

    @Test
    public void testValidatePixKeyUpdateInvalidChaveAleatoria() {
        PixKey existingKey = new PixKey();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixKeyUpdateRequestDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setValorChave("123");

        PixKeyValidationException exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyUpdateRequest(existingKey, requestDto);
        });

        assertTrue(exception.getMessage().contains("O valor da chave não pode ser alterado."));
    }

    @Test
    public void testValidatePixKeyUpdateInvalidTipoChave() {
        PixKey existingKey = new PixKey();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixKeyUpdateRequestDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setTipoChave(TipoChave.CELULAR);

        PixKeyValidationException exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyUpdateRequest(existingKey, requestDto);
        });

        assertTrue(exception.getMessage().contains("O tipo da chave não pode ser alterado."));
    }

    @Test
    public void testValidatePixKeyRequestInvalidNumeroConta() {
        PixKey existingKey = new PixKey();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixKeyUpdateRequestDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setNumeroConta("123");

        PixKeyValidationException exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyUpdateRequest(existingKey, requestDto);
        });

        assertTrue(exception.getMessage().contains("Número da conta inválido. Deve ter exatamente 8 dígitos."));
    }

    @Test
    public void testValidatePixKeyRequestInvalidNomeCorrentista() {
        PixKey existingKey = new PixKey();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixKeyUpdateRequestDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setNomeCorrentista("");

        PixKeyValidationException exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyUpdateRequest(existingKey, requestDto);
        });
        assertTrue(exception.getMessage().contains("Nome do correntista inválido. Deve ter no máximo 30 caracteres."));
    }

    @Test
    public void testValidatePixKeyRequestInvalidSobrenomeCorrentista() {
        PixKey existingKey = new PixKey();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixKeyUpdateRequestDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setSobrenomeCorrentista("");

        PixKeyValidationException exception = assertThrows(PixKeyValidationException.class, () -> {
            validator.validatePixKeyUpdateRequest(existingKey, requestDto);
        });
        assertTrue(exception.getMessage().contains("Sobrenome do correntista inválido. Deve ter no máximo 45 caracteres."));
    }

}
