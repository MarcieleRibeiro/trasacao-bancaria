package br.com.lancamentobancario.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record SaldoResponse(
        UUID idConta,
        String contaCorrente,
        BigDecimal saldo,
        Long versao
) {}
