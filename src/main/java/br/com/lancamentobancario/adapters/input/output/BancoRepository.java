package br.com.lancamentobancario.adapters.input.output;

import br.com.lancamentobancario.adapters.input.output.entity.ContaEntidade;
import br.com.lancamentobancario.adapters.input.output.entity.LancamentoEntidade;
import br.com.lancamentobancario.dto.request.LancamentoRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class BancoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<ContaEntidade> buscarPorContaCorrente(String contaCorrente) {
        return entityManager
                .createQuery("SELECT c FROM ContaEntidade c WHERE c.contaCorrente = :contaCorrente", ContaEntidade.class)
                .setParameter("contaCorrente", contaCorrente)
                .getResultStream()
                .findFirst();
    }

    public Optional<LancamentoEntidade> buscarPorChaveIdempotencia(String chaveIdempotencia) {
        return entityManager
                .createQuery("SELECT l FROM LancamentoEntidade l WHERE l.chaveIdempotencia = :chave", LancamentoEntidade.class)
                .setParameter("chave", chaveIdempotencia)
                .getResultStream()
                .findFirst();
    }

    @Transactional
    public void salvar(ContaEntidade conta) {
        entityManager.merge(conta);
    }

    @Transactional
    public void salvarLancamento(LancamentoRequest lancamentoRequest) {
        LancamentoEntidade entidade = new LancamentoEntidade();
        entidade.setIdLancamento(java.util.UUID.randomUUID());
        entidade.setContaCorrente(lancamentoRequest.getContaCorrente());
        entidade.setTipo(lancamentoRequest.getTipo());
        entidade.setValor(lancamentoRequest.getValor());
        entidade.setChaveIdempotencia(lancamentoRequest.getChaveIdempotencia());
        entidade.setDataOcorrencia(LocalDateTime.now());

        entityManager.merge(entidade);
    }
}
