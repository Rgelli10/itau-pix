package com.itau.pix.repository;

import com.itau.pix.massas.GeradorMassas;
import com.itau.pix.model.PixKey;
import com.itau.pix.model.TipoChave;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
public class PixKeyRepositoryTest {

    @Autowired
    private PixKeyRepository pixKeyRepository;

    @Test
    public void testExistsByValorChave() {
        PixKey pixKey = new PixKey();
        pixKey.setValorChave("teste@exemplo.com");
        pixKeyRepository.save(pixKey);

        boolean exists = pixKeyRepository.existsByValorChave("teste@exemplo.com");
        assertTrue(exists);
    }

    @Test
    public void testFindByNumeroAgenciaAndNumeroConta() {
        PixKey pixKey = new PixKey();
        pixKey.setNumeroAgencia("1234");
        pixKey.setNumeroConta("12345678");

        pixKeyRepository.save(pixKey);

        List<PixKey> keys = pixKeyRepository.findByNumeroAgenciaAndNumeroConta("1234", "12345678");
        assertThat(keys).isNotEmpty();
        assertThat(keys.get(0).getNumeroAgencia()).isEqualTo("1234");
        assertThat(keys.get(0).getNumeroConta()).isEqualTo("12345678");
    }
}
