package br.com.lancamentobancario.exception;

public class TransacaoDuplicadaException extends RuntimeException {

    public TransacaoDuplicadaException(String mensagem) {
        super(mensagem);
    }
}