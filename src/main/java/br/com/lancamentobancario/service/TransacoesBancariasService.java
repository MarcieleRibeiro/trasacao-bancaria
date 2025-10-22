package br.com.lancamentobancario.service;

import br.com.lancamentobancario.adapters.input.output.BancoRepository;
import br.com.lancamentobancario.adapters.input.output.entity.ContaEntidade;
import br.com.lancamentobancario.dto.request.LancamentoRequest;
import br.com.lancamentobancario.dto.response.SaldoResponse;
import br.com.lancamentobancario.dto.response.TransacaoResponse;
import br.com.lancamentobancario.exception.RecursoNaoEncontradoException;
import br.com.lancamentobancario.exception.SaldoInsuficienteException;
import br.com.lancamentobancario.exception.TransacaoDuplicadaException;
import br.com.lancamentobancario.model.TipoLancamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class TransacoesBancariasService {

    private final BancoRepository bancoRepository;

    public TransacoesBancariasService(BancoRepository bancoRepository) {
        this.bancoRepository = bancoRepository;
    }

    @Transactional
    public TransacaoResponse registrarLancamentos(List<LancamentoRequest> requests, String chaveIdempotenciaCabecalho) {
        if (requests == null || requests.isEmpty())
            throw new IllegalArgumentException("A lista de lançamentos não pode estar vazia.");

        String contaCorrente = requests.get(0).getContaCorrente();
        ContaEntidade conta = bancoRepository.buscarPorContaCorrente(contaCorrente)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada: " + contaCorrente));

        BigDecimal saldoAtual = conta.getSaldo() == null ? BigDecimal.ZERO : conta.getSaldo();
        BigDecimal totalCredito = BigDecimal.ZERO;
        BigDecimal totalDebito = BigDecimal.ZERO;

        for (LancamentoRequest request : requests) {
            String chaveFinal = request.getChaveIdempotencia() != null
                    ? request.getChaveIdempotencia()
                    : chaveIdempotenciaCabecalho;

            if (chaveFinal != null && bancoRepository.buscarPorChaveIdempotencia(chaveFinal).isPresent()) {
                throw new TransacaoDuplicadaException("Transação duplicada detectada para chave: " + chaveFinal);
            }

            TipoLancamento tipo = request.getTipo();
            BigDecimal valor = request.getValor();

            if (valor == null || tipo == null)
                throw new IllegalArgumentException("Tipo e valor do lançamento são obrigatórios.");

            if (tipo == TipoLancamento.CREDITO) {
                saldoAtual = saldoAtual.add(valor);
                totalCredito = totalCredito.add(valor);
            } else if (tipo == TipoLancamento.DEBITO) {
                if (saldoAtual.compareTo(valor) < 0) {
                    throw new SaldoInsuficienteException("Saldo insuficiente para débito de R$ " + valor);
                }
                saldoAtual = saldoAtual.subtract(valor);
                totalDebito = totalDebito.add(valor);
            }

            request.setChaveIdempotencia(chaveFinal);
            bancoRepository.salvarLancamento(request);
        }

        conta.setSaldo(saldoAtual);
        bancoRepository.salvar(conta);

        return gerarResponse(conta, totalCredito, totalDebito, saldoAtual);
    }

    public SaldoResponse consultarSaldoPorContaCorrente(String contaCorrente) {
        ContaEntidade conta = bancoRepository.buscarPorContaCorrente(contaCorrente)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada: " + contaCorrente));

        return new SaldoResponse(conta.getIdConta(), conta.getContaCorrente(), conta.getSaldo(), conta.getVersao());
    }

    private TransacaoResponse gerarResponse(ContaEntidade conta, BigDecimal totalCredito, BigDecimal totalDebito, BigDecimal saldoAtual) {
        Instant agora = Instant.now();
        ZoneId zoneBr = ZoneId.of("America/Sao_Paulo");

        String data = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", new Locale("pt", "BR"))
                .format(agora.atZone(zoneBr));

        String hora = DateTimeFormatter.ofPattern("HH'h'mm").format(agora.atZone(zoneBr));

        return new TransacaoResponse(
                UUID.randomUUID().toString(),
                data,
                hora,
                totalCredito,
                totalDebito,
                saldoAtual
        );
    }
}
