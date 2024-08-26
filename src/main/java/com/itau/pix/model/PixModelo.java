package com.itau.pix.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itau.pix.model.enums.TipoChave;
import com.itau.pix.model.enums.TipoCorrentista;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "pix_keys")
public class PixModelo {

    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    private TipoChave tipoChave;
    private String valorChave;
    private String tipoConta;
    private String numeroAgencia;
    private String numeroConta;
    private String nomeCorrentista;
    private String sobrenomeCorrentista;
    private LocalDateTime dataHoraInclusao;
    private LocalDateTime dataHoraInativacao;
    @JsonIgnore
    private boolean inativa;
    @Enumerated(EnumType.STRING)
    private TipoCorrentista tipoCorrentista;
}
