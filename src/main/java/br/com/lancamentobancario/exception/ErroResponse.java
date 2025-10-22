package br.com.lancamentobancario.exception;

import java.time.LocalDateTime;

public record ErroResponse(
        String erro,
        String detalhe,
        int status,
        LocalDateTime timestamp
) {}
