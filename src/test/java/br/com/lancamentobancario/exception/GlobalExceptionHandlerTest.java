package br.com.lancamentobancario.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void deveTratarSaldoInsuficienteException() {
        SaldoInsuficienteException ex = new SaldoInsuficienteException("Saldo insuficiente para débito");
        ResponseEntity<Map<String, Object>> response = handler.handleSaldoInsuficiente(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Saldo insuficiente", response.getBody().get("erro"));
        assertEquals("Saldo insuficiente para débito", response.getBody().get("mensagem"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void deveTratarTransacaoDuplicadaException() {
        TransacaoDuplicadaException ex = new TransacaoDuplicadaException("Transação já registrada");
        ResponseEntity<Map<String, Object>> response = handler.handleTransacaoDuplicada(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Transação duplicada", response.getBody().get("erro"));
        assertEquals("Transação já registrada", response.getBody().get("mensagem"));
    }

    @Test
    void deveTratarRecursoNaoEncontradoException() {
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException("Conta não encontrada");
        ResponseEntity<Map<String, Object>> response = handler.handleRecursoNaoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Recurso não encontrado", response.getBody().get("erro"));
        assertEquals("Conta não encontrada", response.getBody().get("mensagem"));
    }

    @Test
    void deveTratarIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Campo obrigatório ausente");
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida", response.getBody().get("erro"));
        assertEquals("Campo obrigatório ausente", response.getBody().get("mensagem"));
    }

    @Test
    void deveTratarExceptionGenerica() {
        Exception ex = new Exception("Erro inesperado no servidor");
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno no servidor", response.getBody().get("erro"));
        assertEquals("Erro inesperado no servidor", response.getBody().get("mensagem"));
    }
}
