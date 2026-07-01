package ifmt.cba.sgcvp.exception;

// Indica tentativa de mudanca de status fora do fluxo permitido.
public class TransicaoEstadoInvalidaException extends Exception {

    // Cria a excecao com mensagem padrao de transicao invalida.
    public TransicaoEstadoInvalidaException() {
        super("Transicao de estado invalida");
    }

    // Cria a excecao com mensagem especifica de transicao invalida.
    public TransicaoEstadoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
