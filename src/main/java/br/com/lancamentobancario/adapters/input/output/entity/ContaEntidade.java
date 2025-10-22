package br.com.lancamentobancario.adapters.input.output.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "conta")
public class ContaEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_conta", nullable = false, updatable = false)
    private UUID idConta;

    @Column(name = "conta_corrente", unique = true, nullable = false, length = 20)
    private String contaCorrente;

    @Version
    @Column(nullable = false)
    private long versao;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    public ContaEntidade() {}

    public ContaEntidade(String contaCorrente, BigDecimal saldo) {
        this.contaCorrente = contaCorrente;
        this.saldo = saldo;
    }

    public UUID getIdConta() {
        return idConta;
    }

    public void setIdConta(UUID idConta) {
        this.idConta = idConta;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public long getVersao() {
        return versao;
    }

    public void setVersao(long versao) {
        this.versao = versao;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
