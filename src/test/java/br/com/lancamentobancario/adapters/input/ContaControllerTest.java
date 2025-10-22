package br.com.lancamentobancario.adapters.input;

import br.com.lancamentobancario.dto.request.LancamentoRequest;
import br.com.lancamentobancario.dto.response.SaldoResponse;
import br.com.lancamentobancario.dto.response.TransacaoResponse;
import br.com.lancamentobancario.model.TipoLancamento;
import br.com.lancamentobancario.service.TransacoesBancariasService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ContaController.class)
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransacoesBancariasService transacoesBancariasService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar múltiplos lançamentos com sucesso")
    void deveCriarMultiplosLancamentosComSucesso() throws Exception {
        List<LancamentoRequest> requests = List.of(
                new LancamentoRequest(UUID.randomUUID(), "0001-01", TipoLancamento.CREDITO, BigDecimal.valueOf(500), "abc123"),
                new LancamentoRequest(UUID.randomUUID(), "0001-01", TipoLancamento.DEBITO, BigDecimal.valueOf(200), "abc124")
        );

        TransacaoResponse response = new TransacaoResponse(
                UUID.randomUUID().toString(),
                "quarta-feira, 22/10/2025",
                "15h45",
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(1300)
        );

        when(transacoesBancariasService.registrarLancamentos(any(), eq("abcHeader"))).thenReturn(response);

        mockMvc.perform(post("/transacao/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", "abcHeader")
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorCreditado").value(500))
                .andExpect(jsonPath("$.valorDebitado").value(200))
                .andExpect(jsonPath("$.saldoEmConta").value(1300));
    }

    @Test
    @DisplayName("Deve retornar saldo de uma conta existente")
    void deveConsultarSaldoPorContaCorrenteComSucesso() throws Exception {
        SaldoResponse response = new SaldoResponse(
                UUID.randomUUID(),
                "0001-01",
                BigDecimal.valueOf(1500),
                1L
        );

        when(transacoesBancariasService.consultarSaldoPorContaCorrente("0001-01")).thenReturn(response);

        mockMvc.perform(get("/transacao/0001-01/saldo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contaCorrente").value("0001-01"))
                .andExpect(jsonPath("$.saldo").value(1500))
                .andExpect(jsonPath("$.versao").value(1));
    }
}
