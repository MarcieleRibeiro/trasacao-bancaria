package br.com.lancamentobancario.service;

import br.com.lancamentobancario.adapters.input.output.BancoRepository;
import br.com.lancamentobancario.adapters.input.output.entity.ContaEntidade;
import br.com.lancamentobancario.adapters.input.output.entity.LancamentoEntidade;
import br.com.lancamentobancario.dto.request.LancamentoRequest;
import br.com.lancamentobancario.dto.response.TransacaoResponse;
import br.com.lancamentobancario.exception.RecursoNaoEncontradoException;
import br.com.lancamentobancario.exception.SaldoInsuficienteException;
import br.com.lancamentobancario.exception.TransacaoDuplicadaException;
import br.com.lancamentobancario.model.TipoLancamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransacoesBancariasServiceTest {

    private BancoRepository bancoRepository;
    private TransacoesBancariasService service;

    @BeforeEach
    void setup() {
        bancoRepository = mock(BancoRepository.class);
        service = new TransacoesBancariasService(bancoRepository);
    }

    private ContaEntidade criarConta(String contaCorrente, BigDecimal saldo) {
        ContaEntidade conta = new ContaEntidade();
        conta.setIdConta(UUID.randomUUID());
        conta.setContaCorrente(contaCorrente);
        conta.setSaldo(saldo);
        conta.setVersao(0L);
        return conta;
    }

    @Test
    @DisplayName("Deve processar várias transações na mesma requisição")
    void deveProcessarVariasTransacoesNaMesmaRequisicao() {
        ContaEntidade conta = criarConta("33333-3", BigDecimal.valueOf(1000));

        when(bancoRepository.buscarPorContaCorrente("33333-3")).thenReturn(Optional.of(conta));
        when(bancoRepository.buscarPorChaveIdempotencia(any())).thenReturn(Optional.empty());

        LancamentoRequest credito1 = new LancamentoRequest();
        credito1.setContaCorrente("33333-3");
        credito1.setTipo(TipoLancamento.CREDITO);
        credito1.setValor(BigDecimal.valueOf(200));
        credito1.setChaveIdempotencia("multi-001");

        LancamentoRequest debito1 = new LancamentoRequest();
        debito1.setContaCorrente("33333-3");
        debito1.setTipo(TipoLancamento.DEBITO);
        debito1.setValor(BigDecimal.valueOf(150));
        debito1.setChaveIdempotencia("multi-002");

        LancamentoRequest credito2 = new LancamentoRequest();
        credito2.setContaCorrente("33333-3");
        credito2.setTipo(TipoLancamento.CREDITO);
        credito2.setValor(BigDecimal.valueOf(100));
        credito2.setChaveIdempotencia("multi-003");

        List<LancamentoRequest> transacoes = List.of(credito1, debito1, credito2);

        TransacaoResponse resp = service.registrarLancamentos(transacoes, null);

        assertNotNull(resp);
        assertEquals(BigDecimal.valueOf(1150), resp.saldoEmConta(), "O saldo final deve ser 1150.00");

        verify(bancoRepository, times(3)).salvarLancamento(any(LancamentoRequest.class));
        verify(bancoRepository, atLeastOnce()).salvar(conta);
    }


    @Test
    @DisplayName("Deve registrar um lançamento de crédito corretamente")
    void deveRegistrarCredito() {
        ContaEntidade conta = criarConta("12345-6", BigDecimal.valueOf(1000));

        when(bancoRepository.buscarPorContaCorrente("12345-6")).thenReturn(Optional.of(conta));
        when(bancoRepository.buscarPorChaveIdempotencia(any())).thenReturn(Optional.empty());

        LancamentoRequest req = new LancamentoRequest();
        req.setContaCorrente("12345-6");
        req.setTipo(TipoLancamento.CREDITO);
        req.setValor(BigDecimal.valueOf(500));
        req.setChaveIdempotencia("abc001");

        TransacaoResponse resp = service.registrarLancamentos(List.of(req), null);

        assertNotNull(resp);
        assertEquals(BigDecimal.valueOf(1500), resp.saldoEmConta());
        verify(bancoRepository, times(1)).salvarLancamento(req);
    }

    @Test
    @DisplayName("Deve registrar um lançamento de débito corretamente")
    void deveRegistrarDebito() {
        ContaEntidade conta = criarConta("98765-4", BigDecimal.valueOf(800));

        when(bancoRepository.buscarPorContaCorrente("98765-4")).thenReturn(Optional.of(conta));
        when(bancoRepository.buscarPorChaveIdempotencia(any())).thenReturn(Optional.empty());

        LancamentoRequest req = new LancamentoRequest();
        req.setContaCorrente("98765-4");
        req.setTipo(TipoLancamento.DEBITO);
        req.setValor(BigDecimal.valueOf(300));
        req.setChaveIdempotencia("abc002");

        TransacaoResponse resp = service.registrarLancamentos(List.of(req), null);

        assertNotNull(resp);
        assertEquals(BigDecimal.valueOf(500), resp.saldoEmConta());
        verify(bancoRepository).salvarLancamento(req);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar debitar valor maior que o saldo")
    void deveLancarExcecaoSaldoInsuficiente() {
        ContaEntidade conta = criarConta("11111-1", BigDecimal.valueOf(200));

        when(bancoRepository.buscarPorContaCorrente("11111-1")).thenReturn(Optional.of(conta));
        when(bancoRepository.buscarPorChaveIdempotencia(any())).thenReturn(Optional.empty());

        LancamentoRequest req = new LancamentoRequest();
        req.setContaCorrente("11111-1");
        req.setTipo(TipoLancamento.DEBITO);
        req.setValor(BigDecimal.valueOf(500));
        req.setChaveIdempotencia("abc003");

        assertThrows(SaldoInsuficienteException.class, () -> service.registrarLancamentos(List.of(req), null));
    }

    @Test
    @DisplayName("Deve lançar exceção se a conta não for encontrada")
    void deveLancarExcecaoContaNaoEncontrada() {
        when(bancoRepository.buscarPorContaCorrente("99999-9")).thenReturn(Optional.empty());

        LancamentoRequest req = new LancamentoRequest();
        req.setContaCorrente("99999-9");
        req.setTipo(TipoLancamento.CREDITO);
        req.setValor(BigDecimal.valueOf(100));
        req.setChaveIdempotencia("abc004");

        assertThrows(RecursoNaoEncontradoException.class, () -> service.registrarLancamentos(List.of(req), null));
    }

    @Test
    @DisplayName("Deve lançar exceção se a transação for duplicada")
    void deveLancarExcecaoTransacaoDuplicada() {
        ContaEntidade conta = criarConta("55555-5", BigDecimal.valueOf(1000));
        LancamentoEntidade lancamentoExistente = new LancamentoEntidade();

        when(bancoRepository.buscarPorContaCorrente("55555-5")).thenReturn(Optional.of(conta));
        when(bancoRepository.buscarPorChaveIdempotencia("abc005")).thenReturn(Optional.of(lancamentoExistente));

        LancamentoRequest req = new LancamentoRequest();
        req.setContaCorrente("55555-5");
        req.setTipo(TipoLancamento.CREDITO);
        req.setValor(BigDecimal.valueOf(200));
        req.setChaveIdempotencia("abc005");

        assertThrows(TransacaoDuplicadaException.class, () -> service.registrarLancamentos(List.of(req), null));
    }

    @Test
    @DisplayName("Deve retornar o saldo corretamente quando a conta existe")
    void deveConsultarSaldoPorContaCorrenteComSucesso() {
        ContaEntidade conta = criarConta("44444-4", BigDecimal.valueOf(2500));

        when(bancoRepository.buscarPorContaCorrente("44444-4"))
                .thenReturn(Optional.of(conta));

        var response = service.consultarSaldoPorContaCorrente("44444-4");

        assertNotNull(response);
        assertEquals(conta.getIdConta(), response.idConta());
        assertEquals(conta.getContaCorrente(), response.contaCorrente());
        assertEquals(conta.getSaldo(), response.saldo());
        assertEquals(conta.getVersao(), response.versao());
        verify(bancoRepository).buscarPorContaCorrente("44444-4");
    }

    @Test
    @DisplayName("Deve lançar exceção ao consultar saldo de uma conta inexistente")
    void deveLancarExcecaoAoConsultarContaInexistente() {
        when(bancoRepository.buscarPorContaCorrente("99999-9"))
                .thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.consultarSaldoPorContaCorrente("99999-9"));

        verify(bancoRepository).buscarPorContaCorrente("99999-9");
    }
}
