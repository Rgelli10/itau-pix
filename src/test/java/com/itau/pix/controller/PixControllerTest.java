package com.itau.pix.controller;

import com.itau.pix.exception.PixValidadorException;
import com.itau.pix.massas.GeradorMassas;
import com.itau.pix.model.PixModelo;
import com.itau.pix.model.enums.TipoChave;
import com.itau.pix.model.dto.PixRequisicaoDto;
import com.itau.pix.model.dto.PixAlterarRequisicaoDto;
import com.itau.pix.service.PixService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PixController.class)
public class PixControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PixService pixService;

    @Test
    public void testCreatePixKey() throws Exception {
        PixRequisicaoDto requestDto = GeradorMassas.createPixKeyRequestDto();
        PixModelo createdPixKey = GeradorMassas.createPixKey();

        when(pixService.cadastrar(any())).thenReturn(createdPixKey);

        mockMvc.perform(post("/api/v1/pix/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GeradorMassas.asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdPixKey.getId()));
    }

    @Test
    public void testUpdatePixKey() throws Exception {
        UUID id = UUID.randomUUID();
        PixAlterarRequisicaoDto updateRequest = GeradorMassas.createPixKeyUpdateRequestDto();
        PixModelo updatedPixKey = GeradorMassas.createPixKey();
        updatedPixKey.setId(id.toString());

        when(pixService.alterar(eq(id), any(PixAlterarRequisicaoDto.class))).thenReturn(updatedPixKey);

        mockMvc.perform(put("/api/v1/pix/chave/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GeradorMassas.asJsonString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedPixKey.getId()));
    }

    @Test
    public void testSearchPixKeys() throws Exception {
        UUID id = UUID.randomUUID();
        TipoChave tipoChave = TipoChave.EMAIL;
        String numeroAgencia = "1234";
        String numeroConta = "56789012";
        String nomeCorrentista = "João";
        LocalDateTime dataHoraInclusao = LocalDateTime.now().minusDays(1);
        LocalDateTime dataHoraInativacao = LocalDateTime.now();

        // Crie uma lista de PixKey
        List<PixModelo> pixKeys = GeradorMassas.createPixKeyList(3);

        // Configure o mock para retornar a ResponseEntity com a lista de PixKey
        when(pixService.buscar(
                eq(id),
                eq(tipoChave),
                eq(numeroAgencia),
                eq(numeroConta),
                eq(nomeCorrentista),
                eq(dataHoraInclusao),
                eq(dataHoraInativacao)))
                .thenReturn(ResponseEntity.ok(pixKeys));

        // Converta as datas para o formato esperado pelo LocalDateTime
        String dataHoraInclusaoString = dataHoraInclusao.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String dataHoraInativacaoString = dataHoraInativacao.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Execute a requisição GET com os parâmetros
        mockMvc.perform(get("/api/v1/pix/buscar")
                        .param("id", id.toString())
                        .param("tipoChave", tipoChave.name())
                        .param("numeroAgencia", numeroAgencia)
                        .param("numeroConta", numeroConta)
                        .param("nomeCorrentista", nomeCorrentista)
                        .param("dataHoraInclusao", dataHoraInclusaoString)
                        .param("dataHoraInativacao", dataHoraInativacaoString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(pixKeys.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(pixKeys.get(1).getId()))
                .andExpect(jsonPath("$[2].id").value(pixKeys.get(2).getId()));
    }

    @Test
    public void testSearchPixKeysInvalidParameters() throws Exception {
        mockMvc.perform(get("/api/v1/pix/buscar")
                        .param("id", "invalid-uuid")
                        .param("tipoChave", "invalid")
                        .param("numeroAgencia", "12345")
                        .param("numeroConta", "1234")
                        .param("nomeCorrentista", "")
                        .param("dataHoraInclusao", "invalid-date")
                        .param("dataHoraInativacao", "invalid-date"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeactivatePixKey() throws Exception {
        UUID id = UUID.randomUUID();
        PixModelo deactivatedKey = GeradorMassas.createPixKey();
        deactivatedKey.setId(id.toString());
        deactivatedKey.setInativa(true);

        when(pixService.desativar(eq(id))).thenReturn(ResponseEntity.ok(deactivatedKey));

        mockMvc.perform(delete("/api/v1/pix/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deactivatedKey.getId()));
    }

    @Test
    public void testDeactivatePixKeyThrowsExceptio1n() throws Exception {
        UUID id = UUID.fromString(GeradorMassas.createPixKey().getId());
        String errorMessage = "Chave Pix não encontrada ou inválida.";

        when(pixService.desativar(id)).thenThrow(new PixValidadorException(errorMessage));

        mockMvc.perform(delete("/api/v1/pix/" + id))
                .andExpect(status().isUnprocessableEntity());
    }

}