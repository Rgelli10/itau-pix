package com.itau.pix.service;

import com.itau.pix.exception.PixValidadorException;
import com.itau.pix.model.PixModelo;
import com.itau.pix.model.enums.TipoChave;
import com.itau.pix.model.dto.PixRequisicaoDto;
import com.itau.pix.model.dto.PixAlterarRequisicaoDto;
import com.itau.pix.repository.PixRepository;
import com.itau.pix.validator.PixValidador;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PixServiceTest {

    @InjectMocks
    private PixService pixKeyService;

    @Mock
    private PixRepository pixKeyRepository;

    @Mock
    private PixValidador pixKeyValidator;

    @Test
    public void testRegisterPixKey() {
        PixRequisicaoDto requestDto = new PixRequisicaoDto();
        requestDto.setId(null);
        requestDto.setTipoChave(TipoChave.EMAIL);
        requestDto.setValorChave("test@example.com");
        requestDto.setTipoConta("corrente");
        requestDto.setNumeroAgencia("1234");
        requestDto.setNumeroConta("12345678");
        requestDto.setNomeCorrentista("João");
        requestDto.setSobrenomeCorrentista("Silva");
        requestDto.setPessoaFisica(true);

        PixModelo pixKey = new PixModelo();
        pixKey.setId(UUID.randomUUID().toString());
        when(pixKeyRepository.save(any(PixModelo.class))).thenReturn(pixKey);

        PixModelo createdKey = pixKeyService.cadastrar(requestDto);

        assertNotNull(createdKey);
        assertNotNull(createdKey.getId());
        verify(pixKeyRepository, times(1)).save(any(PixModelo.class));
    }

    @Test
    public void testUpdatePixKey() {
        UUID id = UUID.randomUUID();
        PixAlterarRequisicaoDto updateRequest = new PixAlterarRequisicaoDto();
        updateRequest.setTipoChave(TipoChave.CPF);

        PixModelo existingKey = new PixModelo();
        existingKey.setId(id.toString());

        when(pixKeyRepository.findById(id)).thenReturn(Optional.of(existingKey));
        when(pixKeyRepository.save(any(PixModelo.class))).thenReturn(existingKey);

        PixModelo updatedKey = pixKeyService.alterar(id, updateRequest);

        assertNotNull(updatedKey);
        assertEquals(id.toString(), updatedKey.getId());
        verify(pixKeyRepository, times(1)).findById(id);
        verify(pixKeyRepository, times(1)).save(any(PixModelo.class));
    }

//    @Test
//    public void testSearchPixKeysWithResults() {
//        UUID id = UUID.randomUUID();
//        TipoChave tipoChave = TipoChave.EMAIL;
//        String numeroAgencia = "1234";
//        String numeroConta = "56789012";
//        String nomeCorrentista = "Carlos";
//        LocalDateTime dataHoraInclusao = LocalDateTime.now().minusDays(1);
//        LocalDateTime dataHoraInativacao = LocalDateTime.now();
//
//        // Cria uma lista de resultados fictícios
//        List<PixKey> pixKeys = GeradorMassas.createPixKeyList(1);
//
//        // Configura o mock para retornar uma lista com um item
//        when(pixKeyRepository.findByTipoChaveAndNumeroAgenciaAndNumeroContaAndNomeCorrentistaAndDataHoraInclusao(
//                eq(tipoChave), eq(numeroAgencia), eq(numeroConta), eq(nomeCorrentista), eq(dataHoraInclusao)))
//                .thenReturn(pixKeys);
//
//        // Configura o mock para o validador se necessário
//        // when(validator.validateSearchPixKeysFilters(...)).thenReturn(...);
//
//        // Chama o método do serviço
//        ResponseEntity<List<PixKey>> response = pixKeyService.searchPixKeys(
//                id, tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInclusao, dataHoraInativacao);
//
//        // Verifica a resposta
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(pixKeys, response.getBody());
//    }

    @Test
    public void testSearchPixKeysWithNoResults() {
        UUID id = UUID.randomUUID();
        TipoChave tipoChave = TipoChave.EMAIL;
        String numeroAgencia = "1234";
        String numeroConta = "56789012";
        String nomeCorrentista = "Carlos";
        LocalDateTime dataHoraInclusao = LocalDateTime.now().minusDays(1);
        LocalDateTime dataHoraInativacao = LocalDateTime.now();

        when(pixKeyRepository.findByTipoChaveAndNumeroAgenciaAndNumeroContaAndNomeCorrentistaAndDataHoraInclusao(
                eq(tipoChave), eq(numeroAgencia), eq(numeroConta), eq(nomeCorrentista), eq(dataHoraInclusao)))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<PixModelo>> response = pixKeyService.buscar(
                id, tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInclusao, dataHoraInativacao);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    public void testSearchPixKeysWithInvalidFilters() {
        UUID id = UUID.randomUUID();
        TipoChave tipoChave = TipoChave.EMAIL;
        String numeroAgencia = "invalid"; // Parâmetro inválido
        String numeroConta = "56789012";
        String nomeCorrentista = "Carlos";
        LocalDateTime dataHoraInclusao = LocalDateTime.now().minusDays(1);
        LocalDateTime dataHoraInativacao = LocalDateTime.now();

        doThrow(new PixValidadorException("Número da agência inválido"))
                .when(pixKeyValidator).validadorBuscaFiltros(id, tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInclusao, dataHoraInativacao);

        Exception exception = assertThrows(PixValidadorException.class, () -> {
            pixKeyService.buscar(id, tipoChave, numeroAgencia, numeroConta, nomeCorrentista, dataHoraInclusao, dataHoraInativacao);
        });

        assertTrue(exception.getMessage().contains("Número da agência inválido"));
    }

    @Test
    public void testDeactivatePixKey() {
        UUID id = UUID.randomUUID();
        PixModelo existingKey = new PixModelo();
        existingKey.setId(id.toString());
        when(pixKeyRepository.findById(id)).thenReturn(Optional.of(existingKey));
        when(pixKeyRepository.save(any(PixModelo.class))).thenReturn(existingKey);

        ResponseEntity<PixModelo> response = pixKeyService.desativar(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id.toString(), response.getBody().getId());
        assertTrue(response.getBody().isInativa());
        verify(pixKeyRepository, times(1)).findById(id);
        verify(pixKeyRepository, times(1)).save(any(PixModelo.class));
    }
}