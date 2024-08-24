package com.itau.pix.repository;

import com.itau.pix.model.PixModelo;
import com.itau.pix.model.enums.TipoChave;
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
public interface PixRepository extends MongoRepository<PixModelo, UUID> {
    boolean existsByValorChave(String valorChave);
    boolean existsByTipoChaveAndNumeroConta(String tipoChave, String numeroConta);
    boolean existsByValorChaveAndNomeCorrentista(String valorChave, String conta);
    long countByNumeroAgenciaAndNumeroConta(String numeroAgencia, String numeroConta);
    List<PixModelo> findByTipoChaveAndNumeroAgenciaAndNumeroConta(TipoChave tipoChave, String numeroAgencia, String numeroConta);
    List<PixModelo> findByTipoChaveAndNumeroAgenciaAndNumeroContaAndNomeCorrentistaAndDataHoraInclusao(
            TipoChave tipoChave, String numeroAgencia, String numeroConta, String nomeCorrentista, LocalDateTime dataHoraInclusao);
    List<PixModelo> findByTipoChaveAndNumeroAgenciaAndNumeroContaAndNomeCorrentistaAndDataHoraInativacao(
            TipoChave tipoChave, String numeroAgencia, String numeroConta, String nomeCorrentista, LocalDateTime dataHoraInativacao);
    List<PixModelo> findByTipoChave(TipoChave tipoChave);
    List<PixModelo> findByNumeroAgenciaAndNumeroConta(String numeroAgencia, String numeroConta);
    List<PixModelo> findByNomeCorrentista(String nomeCorrentista);
    List<PixModelo> findByDataHoraInclusao(LocalDateTime dataHoraInclusao);
    List<PixModelo> findByDataHoraInativacao(LocalDateTime dataHoraInativacao);

}
