package ifmt.cba.sgcvp.exception;

public class EstoqueInsuficienteException extends Exception {

    public EstoqueInsuficienteException() {
        super("Estoque insuficiente");
    }

    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
