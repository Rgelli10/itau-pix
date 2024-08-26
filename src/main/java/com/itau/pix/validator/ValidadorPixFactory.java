package com.itau.pix.validator;

import com.itau.pix.model.enums.TipoChave;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ValidadorPixFactory {
    private final Map<TipoChave, ValidadorPix> strategies;
    @Autowired
    public ValidadorPixFactory(List<ValidadorPix> strategyList) {
        strategies = strategyList.stream()
                .collect(Collectors.toMap(strategy -> {
                    if (strategy instanceof ValidadorCelular) return TipoChave.CELULAR;
                    if (strategy instanceof ValidadorEmail) return TipoChave.EMAIL;
                    if (strategy instanceof ValidadorCPF) return TipoChave.CPF;
                    if (strategy instanceof ValidadorCNPJ) return TipoChave.CNPJ;
                    if (strategy instanceof ValidadorChaveAleatoria) return TipoChave.ALEATORIA;
                    throw new IllegalArgumentException("Tipo de chave não suportado.");
                }, strategy -> strategy));
    }

    public ValidadorPix getStrategy(TipoChave tipoChave) {
        return strategies.get(tipoChave);
    }
}
