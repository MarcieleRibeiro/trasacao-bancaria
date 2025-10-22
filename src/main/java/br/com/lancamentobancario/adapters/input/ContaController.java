package br.com.lancamentobancario.adapters.input;

import br.com.lancamentobancario.dto.request.LancamentoRequest;
import br.com.lancamentobancario.dto.response.SaldoResponse;
import br.com.lancamentobancario.dto.response.TransacaoResponse;
import br.com.lancamentobancario.service.TransacoesBancariasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transacao")
public class ContaController {

    private final TransacoesBancariasService transacoesBancariasService;

    public ContaController(TransacoesBancariasService transacoesBancariasService) {
        this.transacoesBancariasService = transacoesBancariasService;
    }

    @PostMapping("/lancamentos")
    public ResponseEntity<TransacaoResponse> criarLancamentos(
            @RequestBody List<LancamentoRequest> requests,
            @RequestHeader(value = "Idempotency-Key", required = false) String chaveIdempotencia) {

        TransacaoResponse response = transacoesBancariasService.registrarLancamentos(requests, chaveIdempotencia);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{contaCorrente}/saldo")
    public ResponseEntity<SaldoResponse> obterSaldo(@PathVariable String contaCorrente) {
        SaldoResponse response = transacoesBancariasService.consultarSaldoPorContaCorrente(contaCorrente);
        return ResponseEntity.ok(response);
    }
}
