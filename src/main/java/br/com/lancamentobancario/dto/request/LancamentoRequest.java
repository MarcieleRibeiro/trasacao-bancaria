package br.com.lancamentobancario.dto.request;

import br.com.lancamentobancario.model.TipoLancamento;

import java.math.BigDecimal;
import java.util.UUID;

public class LancamentoRequest {

    private UUID idLancamento;
    private String contaCorrente;
    private TipoLancamento tipo;
    private BigDecimal valor;
    private String chaveIdempotencia;

    public LancamentoRequest() {
    }

    public LancamentoRequest(UUID idLancamento, String contaCorrente, TipoLancamento tipo, BigDecimal valor, String chaveIdempotencia) {
        this.idLancamento = idLancamento;
        this.contaCorrente = contaCorrente;
        this.tipo = tipo;
        this.valor = valor;
        this.chaveIdempotencia = chaveIdempotencia;
    }

    public UUID getIdLancamento() {
        return idLancamento;
    }

    public void setIdLancamento(UUID idLancamento) {
        this.idLancamento = idLancamento;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoLancamento tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getChaveIdempotencia() {
        return chaveIdempotencia;
    }

    public void setChaveIdempotencia(String chaveIdempotencia) {
        this.chaveIdempotencia = chaveIdempotencia;
    }
}
