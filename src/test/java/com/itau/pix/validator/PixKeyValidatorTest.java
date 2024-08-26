package com.itau.pix.validator;

import com.itau.pix.exception.PixValidadorException;
import com.itau.pix.massas.GeradorMassas;
import com.itau.pix.model.PixModelo;
import com.itau.pix.model.enums.TipoChave;
import com.itau.pix.model.dto.PixRequisicaoDto;
import com.itau.pix.model.dto.PixAlterarRequisicaoDto;
import com.itau.pix.repository.PixRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PixKeyValidatorTest {

    @Mock
    private PixRepository repository;

    @InjectMocks
    private PixValidadorStrategy validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidatePixKeyRequestValid() {
        PixRequisicaoDto requestDto = GeradorMassas.createPixKeyRequestDto();

        when(repository.existsByValorChave(requestDto.getValorChave())).thenReturn(false);
        assertEquals(requestDto.getTipoChave(), TipoChave.EMAIL);
        assertDoesNotThrow(() -> validator.validadorRequisicao(requestDto));
        requestDto.setTipoChave(TipoChave.CPF);
        requestDto.setValorChave("97670138068");
        assertEquals(requestDto.getTipoChave(), TipoChave.CPF);
        assertDoesNotThrow(() -> validator.validadorRequisicao(requestDto));
        requestDto.setTipoChave(TipoChave.CNPJ);
        requestDto.setValorChave("45076971000153");
        assertEquals(requestDto.getTipoChave(), TipoChave.CNPJ);
        assertDoesNotThrow(() -> validator.validadorRequisicao(requestDto));
        requestDto.setTipoChave(TipoChave.ALEATORIA);
        requestDto.setValorChave("417122439961664439189826754968636099");
        assertEquals(requestDto.getTipoChave(), TipoChave.ALEATORIA);
        assertDoesNotThrow(() -> validator.validadorRequisicao(requestDto));
        requestDto.setTipoChave(TipoChave.CELULAR);
        requestDto.setValorChave("+5511912345678");
        assertEquals(requestDto.getTipoChave(), TipoChave.CELULAR);
        assertDoesNotThrow(() -> validator.validadorRequisicao(requestDto));
    }

    @Test
    public void testValidatePixKeyRequestInvalid() {
        PixRequisicaoDto requestDto = GeradorMassas.createInvalidPixKeyRequestDto();

        Exception exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicao(requestDto);
        });
        assertTrue(exception.getMessage().contains("Todos os campos obrigatórios devem ser informados."));
    }

    @Test
    public void testValidatePixKeyRequestDuplicatedValue() {
        PixRequisicaoDto requestDto = GeradorMassas.createDuplicatedPixKeyRequestDto();

        when(repository.existsByValorChave(requestDto.getValorChave())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            validator.validadorRequisicao(requestDto);
        });

        assertTrue(exception.getMessage().contains("Já existe uma chave cadastrada com esse valor."));
    }

    @Test
    public void testValidatePixKeyUpdateRequestInvalidTipoConta() {
        PixModelo existingKey = new PixModelo();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixAlterarRequisicaoDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setTipoConta("inválido");

        PixValidadorException exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicaoAlterar(existingKey, requestDto);
        });
        assertTrue(exception.getMessage().contains("Tipo de conta inválido. Deve ser 'corrente' ou 'poupança'."));
    }

    @Test
    public void testValidatePixKeyChavesInvalidas() {
        PixRequisicaoDto requestDto = GeradorMassas.createPixKeyRequestDto();
        requestDto.setTipoChave(TipoChave.ALEATORIA);
        requestDto.setValorChave("445555");
        PixValidadorException exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicao(requestDto);
        });
        assertTrue(exception.getMessage().contains("Valor de chave aleatória inválido."));

        requestDto.setTipoChave(TipoChave.CELULAR);
        requestDto.setValorChave("+5566391110");
        exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicao(requestDto);
        });
        assertTrue(exception.getMessage().contains("Número de celular inválido. O formato correto é +55DD9XXXXXXXX."));
//
        requestDto.setTipoChave(TipoChave.CPF);
        requestDto.setValorChave("445555");
        exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicao(requestDto);
        });
        assertTrue(exception.getMessage().contains("Valor de CPF inválido."));

        requestDto.setTipoChave(TipoChave.CNPJ);
        requestDto.setValorChave("445555");
        exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicao(requestDto);
        });
        assertTrue(exception.getMessage().contains("Valor de CNPJ inválido."));

        requestDto.setTipoChave(TipoChave.CELULAR);
        requestDto.setValorChave("1166391110");
        exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicao(requestDto);
        });
        assertTrue(exception.getMessage().contains("O código do país deve ser +55 para números do Brasil."));

        requestDto.setTipoChave(TipoChave.EMAIL);
        requestDto.setValorChave("sadsa.cm");
        exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicao(requestDto);
        });
        assertTrue(exception.getMessage().contains("Valor de e-mail inválido."));
    }

    @Test
    public void testValidatePixKeyUpdateId() {
        PixModelo existingKey = new PixModelo();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixAlterarRequisicaoDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setId("123");

        PixValidadorException exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicaoAlterar(existingKey, requestDto);
        });
        assertTrue(exception.getMessage().contains("O ID da chave não pode ser alterado."));
    }

    @Test
    public void testValidatePixKeyUpdateChaveInativa() {
        PixModelo existingKey = new PixModelo();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(true);

        PixAlterarRequisicaoDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setNumeroConta("12345678");

        PixValidadorException exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicaoAlterar(existingKey, requestDto);
        });
        assertTrue(exception.getMessage().contains("Não é permitido alterar uma chave inativa."));
    }

    @Test
    public void testValidatePixKeyUpdateRequestInvalidNumeroAgencia() {
        PixModelo existingKey = new PixModelo();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixAlterarRequisicaoDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setNumeroAgencia("123");

        PixValidadorException exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicaoAlterar(existingKey, requestDto);
        });

        assertTrue(exception.getMessage().contains("Número da agência inválido. Deve ter exatamente 4 dígitos."));
    }

    @Test
    public void testValidatePixKeyUpdateInvalidChaveAleatoria() {
        PixModelo existingKey = new PixModelo();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixAlterarRequisicaoDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setValorChave("123");

        PixValidadorException exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicaoAlterar(existingKey, requestDto);
        });

        assertTrue(exception.getMessage().contains("O valor da chave não pode ser alterado."));
    }

    @Test
    public void testValidatePixKeyUpdateInvalidTipoChave() {
        PixModelo existingKey = new PixModelo();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixAlterarRequisicaoDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setTipoChave(TipoChave.CELULAR);

        PixValidadorException exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicaoAlterar(existingKey, requestDto);
        });

        assertTrue(exception.getMessage().contains("O tipo da chave não pode ser alterado."));
    }

    @Test
    public void testValidatePixKeyRequestInvalidNumeroConta() {
        PixModelo existingKey = new PixModelo();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixAlterarRequisicaoDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setNumeroConta("123");

        PixValidadorException exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicaoAlterar(existingKey, requestDto);
        });

        assertTrue(exception.getMessage().contains("Número da conta inválido. Deve ter exatamente 8 dígitos."));
    }

    @Test
    public void testValidatePixKeyRequestInvalidNomeCorrentista() {
        PixModelo existingKey = new PixModelo();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixAlterarRequisicaoDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setNomeCorrentista("");

        PixValidadorException exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicaoAlterar(existingKey, requestDto);
        });
        assertTrue(exception.getMessage().contains("Nome do correntista inválido. Deve ter no máximo 30 caracteres."));
    }

    @Test
    public void testValidatePixKeyRequestInvalidSobrenomeCorrentista() {
        PixModelo existingKey = new PixModelo();
        existingKey.setId(GeradorMassas.createPixKeyUpdateRequestDto().getId());
        existingKey.setTipoChave(TipoChave.CPF);
        existingKey.setValorChave("123.456.789-00");
        existingKey.setTipoConta("corrente");
        existingKey.setNumeroAgencia("5678");
        existingKey.setNumeroConta("87654321");
        existingKey.setNomeCorrentista("Maria");
        existingKey.setSobrenomeCorrentista("Oliveira");
        existingKey.setInativa(false);

        PixAlterarRequisicaoDto requestDto = GeradorMassas.createPixKeyUpdateRequestDto();
        requestDto.setSobrenomeCorrentista("");

        PixValidadorException exception = assertThrows(PixValidadorException.class, () -> {
            validator.validadorRequisicaoAlterar(existingKey, requestDto);
        });
        assertTrue(exception.getMessage().contains("Sobrenome do correntista inválido. Deve ter no máximo 45 caracteres."));
    }

}
