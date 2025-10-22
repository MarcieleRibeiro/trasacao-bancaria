package br.com.lancamentobancario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApplicationTests {

    @Test
    @DisplayName("Deve carregar o contexto da aplicação com sucesso")
    void contextLoads() {
        Application.main(new String[] {});
        assertNotNull(SpringApplication.class, "O contexto do Spring deve ser carregado");
    }
}
