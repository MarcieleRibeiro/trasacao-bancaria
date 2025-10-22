package br.com.lancamentobancario.adapters.input.output.entity;

import br.com.lancamentobancario.model.TipoLancamento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lancamento")
public class LancamentoEntidade {

    @Id
    @Column(name = "id_lancamento", nullable = false)
    private UUID idLancamento;

    @Column(name = "conta_corrente", nullable = false)
    private String contaCorrente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoLancamento tipo;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "chave_idempotencia", unique = true)
    private String chaveIdempotencia;

    @Column(name = "data_ocorrencia", nullable = false)
    private LocalDateTime dataOcorrencia;


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

    public void setChaveIdempotencia(String chaveIdempotencia) {
        this.chaveIdempotencia = chaveIdempotencia;
    }

    public void setDataOcorrencia(LocalDateTime dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }
}
