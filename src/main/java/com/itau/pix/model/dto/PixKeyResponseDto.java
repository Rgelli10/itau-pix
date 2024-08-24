package com.itau.pix.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PixKeyResponseDto {

    private String id;
    private LocalDateTime dataRegistro;

}
