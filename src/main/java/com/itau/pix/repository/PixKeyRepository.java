package com.itau.pix.repository;

import com.itau.pix.model.PixKey;
import com.itau.pix.model.TipoChave;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@EnableMongoRepositories(
        basePackages = "com.itau.pix.repository.mongo",
        mongoTemplateRef = "mongoTemplate"
)
public interface PixKeyRepository extends MongoRepository<PixKey, UUID> {
    boolean existsByValorChave(String valorChave);
    boolean existsByTipoChaveAndNumeroConta(String tipoChave, String numeroConta);
    boolean existsByValorChaveAndNomeCorrentista(String valorChave, String conta);
    long countByNumeroAgenciaAndNumeroConta(String numeroAgencia, String numeroConta);
    List<PixKey> findByTipoChaveAndNumeroAgenciaAndNumeroConta(TipoChave tipoChave, String numeroAgencia, String numeroConta);
    List<PixKey> findByTipoChaveAndNumeroAgenciaAndNumeroContaAndNomeCorrentistaAndDataHoraInclusao(
            TipoChave tipoChave, String numeroAgencia, String numeroConta, String nomeCorrentista, LocalDateTime dataHoraInclusao);
    List<PixKey> findByTipoChaveAndNumeroAgenciaAndNumeroContaAndNomeCorrentistaAndDataHoraInativacao(
            TipoChave tipoChave, String numeroAgencia, String numeroConta, String nomeCorrentista, LocalDateTime dataHoraInativacao);
    List<PixKey> findByTipoChave(TipoChave tipoChave);
    List<PixKey> findByNumeroAgenciaAndNumeroConta(String numeroAgencia, String numeroConta);
    List<PixKey> findByNomeCorrentista(String nomeCorrentista);
    List<PixKey> findByDataHoraInclusao(LocalDateTime dataHoraInclusao);
    List<PixKey> findByDataHoraInativacao(LocalDateTime dataHoraInativacao);

}
