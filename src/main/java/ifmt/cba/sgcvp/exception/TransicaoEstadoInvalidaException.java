package ifmt.cba.sgcvp.exception;

public class TransicaoEstadoInvalidaException extends Exception {

    public TransicaoEstadoInvalidaException() {
        super("Transicao de estado invalida");
    }

    public TransicaoEstadoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
