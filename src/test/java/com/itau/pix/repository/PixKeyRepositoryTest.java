package com.itau.pix.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
public class PixKeyRepositoryTest {

    @Autowired
    private PixRepository pixKeyRepository;

//    @Test
//    public void testExistsByValorChave() {
//        PixKey pixKey = new PixKey();
//        pixKey.setValorChave("teste@exemplo.com");
//        pixKeyRepository.save(pixKey);
//
//        boolean exists = pixKeyRepository.existsByValorChave("teste@exemplo.com");
//        assertTrue(exists);
//    }
//
//    @Test
//    public void testFindByNumeroAgenciaAndNumeroConta() {
//        PixKey pixKey = new PixKey();
//        pixKey.setNumeroAgencia("1234");
//        pixKey.setNumeroConta("12345678");
//
//        pixKeyRepository.save(pixKey);
//
//        List<PixKey> keys = pixKeyRepository.findByNumeroAgenciaAndNumeroConta("1234", "12345678");
//        assertThat(keys).isNotEmpty();
//        assertThat(keys.get(0).getNumeroAgencia()).isEqualTo("1234");
//        assertThat(keys.get(0).getNumeroConta()).isEqualTo("12345678");
//    }
}
