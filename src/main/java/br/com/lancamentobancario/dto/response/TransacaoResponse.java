package br.com.lancamentobancario.dto.response;

import java.math.BigDecimal;

public record TransacaoResponse(
        String idTransacao,
        String dataTransacao,
        String horario,
        BigDecimal valorCreditado,
        BigDecimal valorDebitado,
        BigDecimal saldoEmConta
) {}